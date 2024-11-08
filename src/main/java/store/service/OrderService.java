package store.service;

import java.time.LocalDate;
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
        Product product = inventory.getProductList().stream()
                .filter(p -> p.getName().equals(productName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NON_EXISTENT_PRODUCT.getMessage()));

        productService.deductStock(product, quantity);
        order.addProduct(product, quantity);

        // 무료 아이템 계산
        int freeQuantity = calculateFreeItems(product, quantity);
        if (freeQuantity > 0) {
            order.getFreeItems().put(product, freeQuantity);
        }
    }

    public int calculateFreeItems(Product product, int quantity) {
        if (product.getPromotion() != null && product.getPromotion().isActive(LocalDate.now())) {
            return product.getPromotion().getFreeQuantity(quantity, LocalDate.now());
        }
        return 0;
    }

    public void applyMembershipDiscount(Order order, boolean isMembership) {
        order.setMembership(isMembership);
    }

    public double calculateFinalTotal(Order order) {
        double discount = 0.0;

        for (var entry : order.getOrderedProducts().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double discountedPrice = pricingService.calculateFinalPrice(product, quantity);
            discount += (product.getPrice() * quantity) - discountedPrice;
        }

        order.setEventDiscount(discount);
        double membershipDiscount = calculateMembershipDiscount(order.getTotalBeforeDiscount() - discount,
                order.isMembership());
        order.setMembershipDiscount(membershipDiscount);
        double finalTotal = order.getTotalBeforeDiscount() - discount - membershipDiscount;
        order.setFinalTotal(finalTotal);
        return finalTotal;
    }

    private double calculateMembershipDiscount(double amountAfterPromotionDiscount, boolean isMembership) {
        if (isMembership) {
            double discount = amountAfterPromotionDiscount * MEMBERSHIP_DISCOUNT_RATE;
            return Math.min(discount, MAX_MEMBERSHIP_DISCOUNT);
        }
        return 0.0;
    }

}
