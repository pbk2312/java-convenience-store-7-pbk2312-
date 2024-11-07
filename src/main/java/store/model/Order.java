package store.model;

import java.util.HashMap;
import java.util.Map;
import store.service.ProductService;

public class Order {
    private static final double MEMBERSHIP_DISCOUNT_RATE = 0.3;
    private static final double MAX_MEMBERSHIP_DISCOUNT = 8000.0;

    private final Map<Product, Integer> orderedProducts;
    private boolean isMembership;
    private double totalBeforeDiscount;
    private double finalTotal;

    private final ProductService productService;

    public Order(ProductService productService) {
        this.orderedProducts = new HashMap<>();
        this.isMembership = false;
        this.totalBeforeDiscount = 0.0;
        this.finalTotal = 0.0;
        this.productService = productService;
    }

    public void addProduct(Product product, int quantity) {
        orderedProducts.put(product, quantity);
        totalBeforeDiscount += product.getPrice() * quantity;
    }

    public void setMembership(boolean isMembership) {
        this.isMembership = isMembership;
    }

    public double calculateTotal() {
        double discount = 0.0;

        for (Map.Entry<Product, Integer> entry : orderedProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double discountedPrice = productService.calculateFinalPrice(product, quantity);
            discount += (product.getPrice() * quantity) - discountedPrice;
        }

        double membershipDiscount = calculateMembershipDiscount(totalBeforeDiscount - discount);
        finalTotal = totalBeforeDiscount - discount - membershipDiscount;
        return finalTotal;
    }

    private double calculateMembershipDiscount(double amountAfterPromotionDiscount) {
        if (isMembership) {
            double discount = amountAfterPromotionDiscount * MEMBERSHIP_DISCOUNT_RATE;
            return Math.min(discount, MAX_MEMBERSHIP_DISCOUNT);
        }
        return 0.0;
    }

}
