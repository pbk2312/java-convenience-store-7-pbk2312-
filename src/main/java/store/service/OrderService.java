package store.service;

import store.model.Inventory;
import store.model.Order;
import store.model.Product;
import store.view.ErrorMessage;

public class OrderService {

    private final Inventory inventory;
    private final ProductService productService;
    private final PricingService pricingService;

    public OrderService(Inventory inventory, ProductService productService, PricingService pricingService) {
        this.inventory = inventory;
        this.productService = productService;
        this.pricingService = pricingService;
    }

    public Order createOrder() {
        return new Order(pricingService);
    }

    public void addProductToOrder(Order order, String productName, int quantity) {
        Product product = inventory.getProductList().stream()
                .filter(p -> p.getName().equals(productName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NON_EXISTENT_PRODUCT.getMessage()));

        productService.deductStock(product, quantity);  // 재고 차감
        order.addProduct(product, quantity);
    }

    public void applyMembershipDiscount(Order order, boolean isMembership) {
        order.setMembership(isMembership);
    }

    public double calculateFinalTotal(Order order) {
        return order.calculateTotal();
    }


}
