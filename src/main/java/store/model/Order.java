package store.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import store.service.PricingService;

public class Order {
    private static final double MEMBERSHIP_DISCOUNT_RATE = 0.3;
    private static final double MAX_MEMBERSHIP_DISCOUNT = 8000.0;

    private final Map<Product, Integer> orderedProducts;
    private final Map<Product, Integer> freeItems;
    private boolean isMembership;
    private int totalQuantity;
    private double totalBeforeDiscount;
    private double finalTotal;
    private double eventDiscount;
    private double membershipDiscount;

    private final PricingService pricingService;

    public Order(PricingService pricingService) {
        this.orderedProducts = new HashMap<>();
        this.freeItems = new HashMap<>();
        this.isMembership = false;
        this.totalQuantity = 0; // 초기화
        this.totalBeforeDiscount = 0.0;
        this.finalTotal = 0.0;
        this.eventDiscount = 0.0;
        this.membershipDiscount = 0.0;
        this.pricingService = pricingService;
    }

    public void addProduct(Product product, int quantity) {
        orderedProducts.put(product, quantity);
        totalBeforeDiscount += product.getPrice() * quantity;
        totalQuantity += quantity; // 총 구매 수량 업데이트
        calculateFreeItems(product, quantity);
    }

    private void calculateFreeItems(Product product, int quantity) {
        if (product.getPromotion() != null && product.getPromotion().isActive(LocalDate.now())) {
            int freeQuantity = product.getPromotion().getFreeQuantity(quantity, LocalDate.now());
            if (freeQuantity > 0) {
                freeItems.put(product, freeQuantity);
            }
        }
    }

    public void setMembership(boolean isMembership) {
        this.isMembership = isMembership;
    }

    public double calculateTotal() {
        double discount = 0.0;

        for (Map.Entry<Product, Integer> entry : orderedProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double discountedPrice = pricingService.calculateFinalPrice(product, quantity);
            discount += (product.getPrice() * quantity) - discountedPrice;
        }

        eventDiscount = discount;
        membershipDiscount = calculateMembershipDiscount(totalBeforeDiscount - discount);
        finalTotal = totalBeforeDiscount - eventDiscount - membershipDiscount;
        return finalTotal;
    }

    private double calculateMembershipDiscount(double amountAfterPromotionDiscount) {
        if (isMembership) {
            double discount = amountAfterPromotionDiscount * MEMBERSHIP_DISCOUNT_RATE;
            return Math.min(discount, MAX_MEMBERSHIP_DISCOUNT);
        }
        return 0.0;
    }

    public Map<Product, Integer> getOrderedProducts() {
        return orderedProducts;
    }

    public Map<Product, Integer> getFreeItems() {
        return freeItems;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public double getTotalBeforeDiscount() {
        return totalBeforeDiscount;
    }

    public double getEventDiscount() {
        return eventDiscount;
    }

    public double getMembershipDiscount() {
        return membershipDiscount;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

}
