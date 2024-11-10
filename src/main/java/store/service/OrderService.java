package store.service;

import store.handler.InputHandler;
import store.model.Order;
import store.model.Product;
import store.service.promotion.PromotionStrategy;
import store.view.ErrorMessage;

public class OrderService {

    private final StockManager stockManager;
    private final PromotionService promotionService;
    private final InputHandler inputHandler;
    private final PricingService pricingService;
    private static final double MEMBERSHIP_DISCOUNT_RATE = 0.3;
    private static final double MAX_MEMBERSHIP_DISCOUNT = 8000.0;

    public OrderService(StockManager stockManager, PromotionService promotionService,
                        PricingService pricingService, InputHandler inputHandler) {
        this.stockManager = stockManager;
        this.promotionService = promotionService;
        this.pricingService = pricingService;
        this.inputHandler = inputHandler;
    }

    public Order createOrder() {
        return new Order();
    }

    // 주문에 상품을 추가하고 프로모션 적용 여부와 재고를 확인
    public void addProductToOrder(Order order, String productName, int quantity) {
        Product promotionProduct = stockManager.getPromotionProduct(productName).orElse(null);
        Product regularProduct = stockManager.getRegularProduct(productName);

        validateStockAvailability(promotionProduct, regularProduct, quantity);

        if (promotionProduct != null) {
            processWithPromotion(order, promotionProduct, regularProduct, quantity);
            return;
        }
        processWithoutPromotion(order, regularProduct, quantity);
    }

    // 프로모션 및 일반 재고를 합쳐서 요청 수량을 충족하는지 확인
    private void validateStockAvailability(Product promotionProduct, Product regularProduct, int quantity) {
        int promotionalStock = promotionProduct != null ? promotionProduct.getStock() : 0;
        int totalAvailableStock = promotionalStock + regularProduct.getStock();

        if (totalAvailableStock < quantity) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_QUANTITY.getMessage());
        }
    }

    // 주문을 프로모션과 함께 처리하며 재고 상태에 따라 전체 또는 부분 프로모션 적용
    private void processWithPromotion(Order order, Product promotionProduct, Product regularProduct, int quantity) {
        if (PromotionStatus.checkStock(promotionProduct.getStock(), quantity) == PromotionStatus.FULL) {
            applyFullPromotion(order, promotionProduct, quantity);
            return;
        }
        applyPartialPromotion(order, promotionProduct, regularProduct, quantity);
    }

    // 프로모션 없이 주문을 처리하며 재고 차감 후 주문에 추가
    private void processWithoutPromotion(Order order, Product regularProduct, int quantity) {
        stockManager.deductStock(regularProduct, quantity);
        order.addProduct(regularProduct, quantity);
    }

    // 프로모션을 전체 적용하여 재고 차감 및 무료 상품 추가
    private void applyFullPromotion(Order order, Product promotionProduct, int quantity) {
        stockManager.deductStock(promotionProduct, quantity);
        order.addProduct(promotionProduct, quantity);
        promotionService.addFreeItems(order, promotionProduct, quantity);
    }

    // 프로모션 재고 사용 후, 남은 수량을 정가로 구매할지 여부 확인
    private void applyPartialPromotion(Order order, Product promotionProduct, Product regularProduct,
                                       int requestedQuantity) {
        int promotionalStock = promotionProduct.getStock();
        PromotionStrategy strategy = promotionProduct.getPromotion().getStrategy();
        int fullPromotionQuantity = strategy.calculatePayableQuantity(requestedQuantity);

        int usedPromotionQuantity = Math.min(promotionalStock, requestedQuantity);
        applyPromotionalStock(order, promotionProduct, usedPromotionQuantity);

        int remainingQuantity = requestedQuantity - usedPromotionQuantity;
        applyNonPromotionalStock(order, regularProduct, remainingQuantity, requestedQuantity, fullPromotionQuantity);
    }

    // 사용 가능한 프로모션 재고를 주문에 적용
    private void applyPromotionalStock(Order order, Product promotionProduct, int usedPromotionQuantity) {
        if (usedPromotionQuantity > 0) {
            addOnlyPromotionalProducts(order, promotionProduct, usedPromotionQuantity);
        }
    }

    // 남은 수량을 정가로 구매할지 확인하고 정가로 주문에 추가
    private void applyNonPromotionalStock(Order order, Product regularProduct, int remainingQuantity,
                                          int requestedQuantity, int fullPromotionQuantity) {
        if (remainingQuantity > 0 && confirmPurchaseWithoutPromotion(regularProduct,
                requestedQuantity - fullPromotionQuantity)) {
            stockManager.deductStock(regularProduct, remainingQuantity);
            order.addProduct(regularProduct, remainingQuantity);
        }
    }

    // 프로모션 재고만 주문에 추가하고 무료 상품 추가
    private void addOnlyPromotionalProducts(Order order, Product promotionProduct, int promotionalStock) {
        stockManager.deductStock(promotionProduct, promotionalStock);
        order.addProduct(promotionProduct, promotionalStock);
        promotionService.addFreeItems(order, promotionProduct, promotionalStock);
    }

    // 프로모션 할인 없이 정가로 일부 수량을 구매할지 사용자에게 확인 요청
    private boolean confirmPurchaseWithoutPromotion(Product promotionProduct, int remainingQuantity) {
        return inputHandler.confirmPurchaseWithoutPromotion(promotionProduct, remainingQuantity);
    }

    // 멤버십 상태를 주문에 설정하여 멤버십 할인을 적용
    public void applyMembershipDiscount(Order order, boolean isMembership) {
        order.setMembership(isMembership);
    }

    public void calculateFinalTotal(Order order) {
        double eventDiscount = calculateEventDiscount(order);
        double membershipDiscount = calculateMembershipDiscount(order.getTotalBeforeDiscount() - eventDiscount,
                order.isMembership());
        finalizeTotal(order, eventDiscount, membershipDiscount);
    }

    private double calculateEventDiscount(Order order) {
        return order.getOrderedProducts().entrySet().stream()
                .mapToDouble(entry -> calculateProductDiscount(entry.getKey(), entry.getValue()))
                .sum();
    }

    // 멤버십 여부에 따라 멤버십 할인을 계산
    private double calculateMembershipDiscount(double amountAfterEventDiscount, boolean isMembership) {
        return isMembership ? calculateActualMembershipDiscount(amountAfterEventDiscount) : 0.0;
    }

    // 8,000원  멤버십 할인을 계산
    private double calculateActualMembershipDiscount(double amountAfterEventDiscount) {
        double discount = amountAfterEventDiscount * MEMBERSHIP_DISCOUNT_RATE;
        return Math.min(discount, MAX_MEMBERSHIP_DISCOUNT);
    }

    private double calculateProductDiscount(Product product, int quantity) {
        double originalPrice = product.getPrice() * quantity;
        double discountedPrice = pricingService.calculateFinalPrice(product, quantity);
        return originalPrice - discountedPrice;
    }

    // 최종 금액을 주문에 설정하고 이벤트 및 멤버십 할인 정보를 반영
    private void finalizeTotal(Order order, double eventDiscount, double membershipDiscount) {
        order.setEventDiscount(eventDiscount);
        order.setMembershipDiscount(membershipDiscount);
        double finalTotal = order.getTotalBeforeDiscount() - eventDiscount - membershipDiscount;
        order.setFinalTotal(finalTotal);
    }

    // 남은 재고에 따라 프로모션 상태(전체 또는 부분)를 결정하는 enum
    private enum PromotionStatus {
        FULL, PARTIAL;

        public static PromotionStatus checkStock(int promotionalStock, int requestedQuantity) {
            return (promotionalStock >= requestedQuantity) ? FULL : PARTIAL;
        }
    }

}
