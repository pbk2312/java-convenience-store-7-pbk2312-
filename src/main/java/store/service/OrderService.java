package store.service;

import java.util.Optional;
import store.handler.InputHandler;
import store.model.Order;
import store.model.Product;
import store.service.promotion.OnePlusOnePromotion;
import store.validator.StockValidator;

public class OrderService {
    private final StockManager stockManager;
    private final PromotionProcessor promotionProcessor;
    private final StockValidator stockValidator;
    private final PricingService pricingService;
    private final MembershipDiscountCalculator membershipDiscountCalculator;
    private final InputHandler inputHandler;

    public OrderService(StockManager stockManager, PromotionProcessor promotionProcessor,
                        StockValidator stockValidator, PricingService pricingService,
                        MembershipDiscountCalculator membershipDiscountCalculator, InputHandler inputHandler) {
        this.stockManager = stockManager;
        this.promotionProcessor = promotionProcessor;
        this.stockValidator = stockValidator;
        this.pricingService = pricingService;
        this.membershipDiscountCalculator = membershipDiscountCalculator;
        this.inputHandler = inputHandler;
    }

    public Order createOrder() {
        return new Order();
    }

    public void addProductToOrder(Order order, String productName, int quantity) {
        Optional<Product> promotionProductOpt = stockManager.getPromotionProduct(productName);
        Product regularProduct = stockManager.getRegularProduct(productName);

        if (promotionProductOpt.isPresent()) {
            Product promotionProduct = promotionProductOpt.get();
            validateAndDeductStock(order, promotionProduct, regularProduct, quantity);
            handleFreeItems(order, promotionProduct, quantity);
            promotionProcessor.applyPartialPromotion(order, promotionProduct, regularProduct, quantity);
        } else {
            validateAndDeductStock(order, null, regularProduct, quantity);
        }
    }

    private void validateAndDeductStock(Order order, Product promotionProduct, Product regularProduct, int quantity) {
        stockValidator.validateStockAvailability(promotionProduct, regularProduct, quantity);

        if (promotionProduct == null) {
            stockManager.deductStock(regularProduct, quantity);
            order.addProduct(regularProduct, quantity);
        }
    }

    private void handleFreeItems(Order order, Product promotionProduct, int quantity) {
        if (promotionProduct == null || !(promotionProduct.getPromotion()
                .getStrategy() instanceof OnePlusOnePromotion)) {
            return;
        }

        if (quantity % 2 == 1) {
            boolean addFreeItem = inputHandler.confirmAddFreePromotionItem(promotionProduct, 1);
            if (addFreeItem) {
                order.getFreeItems().put(promotionProduct, 1);
            }
        }
    }

    public void applyMembershipDiscount(Order order, boolean isMembership) {
        order.setMembership(isMembership);
    }


    public void calculateFinalTotal(Order order) {
        double eventDiscount = calculateEventDiscount(order);

        double nonPromotionalTotal = order.getOrderedProducts().entrySet().stream()
                .filter(entry -> !entry.getKey().hasPromotion())
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();

        double membershipDiscount = 0.0;
        if (nonPromotionalTotal > 0) {
            membershipDiscount = membershipDiscountCalculator.calculate(nonPromotionalTotal, order.isMembership());
        }

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
