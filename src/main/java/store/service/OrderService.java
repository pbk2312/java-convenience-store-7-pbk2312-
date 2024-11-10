package store.service;

import store.model.Order;
import store.model.Product;
import store.validator.StockValidator;

public class OrderService {
    private final StockManager stockManager;
    private final PromotionProcessor promotionProcessor;
    private final StockValidator stockValidator;
    private final PricingService pricingService;
    private final MembershipDiscountCalculator membershipDiscountCalculator;

    public OrderService(StockManager stockManager, PromotionProcessor promotionProcessor,
                        StockValidator stockValidator, PricingService pricingService,
                        MembershipDiscountCalculator membershipDiscountCalculator) {
        this.stockManager = stockManager;
        this.promotionProcessor = promotionProcessor;
        this.stockValidator = stockValidator;
        this.pricingService = pricingService;
        this.membershipDiscountCalculator = membershipDiscountCalculator;
    }

    public Order createOrder() {
        return new Order();
    }

    public void addProductToOrder(Order order, String productName, int quantity) {
        Product promotionProduct = stockManager.getPromotionProduct(productName).orElse(null);
        Product regularProduct = stockManager.getRegularProduct(productName);

        stockValidator.validateStockAvailability(promotionProduct, regularProduct, quantity);

        if (promotionProduct != null) {
            promotionProcessor.applyPartialPromotion(order, promotionProduct, regularProduct, quantity);
        } else {
            stockManager.deductStock(regularProduct, quantity);
            order.addProduct(regularProduct, quantity);
        }
    }

    public void applyMembershipDiscount(Order order, boolean isMembership) {
        order.setMembership(isMembership);
    }

    public void calculateFinalTotal(Order order) {
        double eventDiscount = calculateEventDiscount(order);
        double membershipDiscount = membershipDiscountCalculator.calculate(
                order.getTotalBeforeDiscount() - eventDiscount, order.isMembership());
        finalizeTotal(order, eventDiscount, membershipDiscount);
    }

    private double calculateEventDiscount(Order order) {
        return order.getOrderedProducts().entrySet().stream()
                .mapToDouble(entry -> calculateProductDiscount(entry.getKey(), entry.getValue()))
                .sum();
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

}
