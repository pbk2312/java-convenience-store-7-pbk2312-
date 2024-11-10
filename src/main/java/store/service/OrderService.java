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

    private void validateStockAvailability(Product promotionProduct, Product regularProduct, int quantity) {
        int promotionalStock = promotionProduct != null ? promotionProduct.getStock() : 0;
        int totalAvailableStock = promotionalStock + regularProduct.getStock();

        if (totalAvailableStock < quantity) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_QUANTITY.getMessage());
        }
    }

    private void processWithPromotion(Order order, Product promotionProduct, Product regularProduct, int quantity) {
        if (PromotionStatus.checkStock(promotionProduct.getStock(), quantity) == PromotionStatus.FULL) {
            applyFullPromotion(order, promotionProduct, quantity);
            return;
        }
        applyPartialPromotion(order, promotionProduct, regularProduct, quantity);
    }

    private void processWithoutPromotion(Order order, Product regularProduct, int quantity) {
        stockManager.deductStock(regularProduct, quantity);
        order.addProduct(regularProduct, quantity);
    }

    private void applyFullPromotion(Order order, Product promotionProduct, int quantity) {
        stockManager.deductStock(promotionProduct, quantity);
        order.addProduct(promotionProduct, quantity);
        promotionService.addFreeItems(order, promotionProduct, quantity);
    }

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

    private void applyPromotionalStock(Order order, Product promotionProduct, int usedPromotionQuantity) {
        if (usedPromotionQuantity > 0) {
            addOnlyPromotionalProducts(order, promotionProduct, usedPromotionQuantity);
        }
    }

    private void applyNonPromotionalStock(Order order, Product regularProduct, int remainingQuantity,
                                          int requestedQuantity, int fullPromotionQuantity) {
        if (remainingQuantity > 0 && confirmPurchaseWithoutPromotion(regularProduct,
                requestedQuantity - fullPromotionQuantity)) {
            stockManager.deductStock(regularProduct, remainingQuantity);
            order.addProduct(regularProduct, remainingQuantity);
        }
    }

    private void addOnlyPromotionalProducts(Order order, Product promotionProduct, int promotionalStock) {
        stockManager.deductStock(promotionProduct, promotionalStock);
        order.addProduct(promotionProduct, promotionalStock);
        promotionService.addFreeItems(order, promotionProduct, promotionalStock);
    }

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
