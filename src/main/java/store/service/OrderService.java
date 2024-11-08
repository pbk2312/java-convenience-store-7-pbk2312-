package store.service;

import store.model.Inventory;
import store.model.Order;
import store.model.Product;
import store.view.ErrorMessage;

public class OrderService {

    private final Inventory inventory;
    private final ProductService productService;
    private final PricingService pricingService;
    private static final double MEMBERSHIP_DISCOUNT_RATE = 0.3;
    private static final double MAX_MEMBERSHIP_DISCOUNT = 8000.0;

    public OrderService(Inventory inventory, ProductService productService, PricingService pricingService) {
        this.inventory = inventory;
        this.productService = productService;
        this.pricingService = pricingService;
    }

    public Order createOrder() {
        return new Order();
    }

    public void addProductToOrder(Order order, String productName, int quantity) {
        Product product = inventory.getProductByName(productName)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NON_EXISTENT_PRODUCT.getMessage()));

        productService.deductStock(product, quantity);
        order.addProduct(product, quantity);
        addFreeItemsToOrder(order, product, quantity);
    }

    private void addFreeItemsToOrder(Order order, Product product, int quantity) {
        int freeQuantity = productService.calculateFreeItems(product, quantity);
        if (freeQuantity > 0) {
            order.getFreeItems().put(product, freeQuantity);
        }
    }

    public void applyMembershipDiscount(Order order, boolean isMembership) {
        order.setMembership(isMembership);
    }

    public double calculateFinalTotal(Order order) {
        double eventDiscount = calculateEventDiscount(order);
        double membershipDiscount = calculateMembershipDiscount(order.getTotalBeforeDiscount() - eventDiscount,
                order.isMembership());
        return finalizeTotal(order, eventDiscount, membershipDiscount);
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

    private double calculateMembershipDiscount(double amountAfterEventDiscount, boolean isMembership) {
        if (!isMembership) {
            return 0.0;
        }
        double discount = amountAfterEventDiscount * MEMBERSHIP_DISCOUNT_RATE;
        return Math.min(discount, MAX_MEMBERSHIP_DISCOUNT);
    }

    private double finalizeTotal(Order order, double eventDiscount, double membershipDiscount) {
        order.setEventDiscount(eventDiscount);
        order.setMembershipDiscount(membershipDiscount);
        double finalTotal = order.getTotalBeforeDiscount() - eventDiscount - membershipDiscount;
        order.setFinalTotal(finalTotal);
        return finalTotal;
    }

}
