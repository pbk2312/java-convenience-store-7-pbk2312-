package store.service;

import java.util.Optional;
import store.handler.InputHandler;
import store.model.Order;
import store.model.Product;
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

    public void addProductToOrder(Order order, String productName, int quantity) {
        Optional<Product> promotionProductOpt = stockManager.getPromotionProduct(productName);
        Product regularProduct = stockManager.getRegularProduct(productName);

        int totalAvailableStock = promotionProductOpt.map(Product::getStock).orElse(0) + regularProduct.getStock();

        if (totalAvailableStock < quantity) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_QUANTITY.getMessage());
        }

        if (promotionProductOpt.isEmpty()) {
            processOrderWithoutPromotion(order, regularProduct, quantity);
        } else {
            handlePromotion(order, promotionProductOpt.get(), regularProduct, quantity);
        }
    }

    private void handlePromotion(Order order, Product promotionProduct, Product regularProduct, int quantity) {
        PromotionStatus status = PromotionStatus.checkStock(promotionProduct.getStock(), quantity);
        if (status == PromotionStatus.FULL) {
            handleFullPromotion(order, promotionProduct, quantity);
            return;
        }
        processPartialPromotion(order, promotionProduct, regularProduct, quantity);
    }

    private void processOrderWithoutPromotion(Order order, Product regularProduct, int quantity) {
        stockManager.deductStock(regularProduct, quantity);
        order.addProduct(regularProduct, quantity);
    }

    private void handleFullPromotion(Order order, Product promotionProduct, int quantity) {
        stockManager.deductStock(promotionProduct, quantity);
        order.addProduct(promotionProduct, quantity);
        promotionService.addFreeItems(order, promotionProduct, quantity);
    }


    private void processPartialPromotion(Order order, Product promotionProduct, Product regularProduct,
                                         int requestedQuantity) {
        int promotionalStock = promotionProduct.getStock();

        // 프로모션 할인이 적용되는 수량 계산 (예: 2+1 프로모션에서 3개 중 2개는 할인이 적용됨)
        int fullPromotionQuantity = (requestedQuantity / 3) * 2;

        int usedPromotionQuantity = Math.min(promotionalStock, requestedQuantity);

        // 프로모션 재고를 모두 사용하여 주문에 추가
        if (usedPromotionQuantity > 0) {
            addOnlyPromotionalProducts(order, promotionProduct, usedPromotionQuantity);
        }

        // 프로모션 재고를 모두 사용한 후, 요청된 수량에서 프로모션 재고를 뺀 남은 수량을 일반 재고에서 처리
        int remainingQuantity = requestedQuantity - usedPromotionQuantity;
        if (remainingQuantity > 0) {
            if (confirmPurchaseWithoutPromotion(regularProduct, requestedQuantity - fullPromotionQuantity)) {
                stockManager.deductStock(regularProduct, remainingQuantity);
                order.addProduct(regularProduct, remainingQuantity);
            }
        }
    }

    // 프로모션 재고를 주문에 추가하고 무료 품목을 추가
    private void addOnlyPromotionalProducts(Order order, Product promotionProduct, int promotionalStock) {
        stockManager.deductStock(promotionProduct, promotionalStock);
        order.addProduct(promotionProduct, promotionalStock);
        promotionService.addFreeItems(order, promotionProduct, promotionalStock);
    }

    // 프로모션 할인이 적용되지 않는 수량 구매 여부 확인
    private boolean confirmPurchaseWithoutPromotion(Product promotionProduct, int remainingQuantity) {
        return inputHandler.confirmPurchaseWithoutPromotion(promotionProduct, remainingQuantity);
    }

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

    private double calculateMembershipDiscount(double amountAfterEventDiscount, boolean isMembership) {
        return isMembership ? calculateActualMembershipDiscount(amountAfterEventDiscount) : 0.0;
    }

    private double calculateActualMembershipDiscount(double amountAfterEventDiscount) {
        double discount = amountAfterEventDiscount * MEMBERSHIP_DISCOUNT_RATE;
        return Math.min(discount, MAX_MEMBERSHIP_DISCOUNT);
    }

    private double calculateProductDiscount(Product product, int quantity) {
        double originalPrice = product.getPrice() * quantity;
        double discountedPrice = pricingService.calculateFinalPrice(product, quantity);
        return originalPrice - discountedPrice;
    }

    private void finalizeTotal(Order order, double eventDiscount, double membershipDiscount) {
        order.setEventDiscount(eventDiscount);
        order.setMembershipDiscount(membershipDiscount);
        double finalTotal = order.getTotalBeforeDiscount() - eventDiscount - membershipDiscount;
        order.setFinalTotal(finalTotal);
    }

    private enum PromotionStatus {
        FULL, PARTIAL;

        public static PromotionStatus checkStock(int promotionalStock, int requestedQuantity) {
            return (promotionalStock >= requestedQuantity) ? FULL : PARTIAL;
        }
    }

}
