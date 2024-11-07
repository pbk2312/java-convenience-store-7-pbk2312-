package store.service;

import store.model.Inventory;
import store.model.Order;
import store.model.Product;
import store.view.ErrorMessage;

public class OrderService {

    private final Inventory inventory;
    private final ProductService productService;

    public OrderService(Inventory inventory, ProductService productService) {
        this.inventory = inventory;
        this.productService = productService;
    }

    public Order createOrder() {
        return new Order(productService);
    }

    public void addProductToOrder(Order order, String productName, int quantity) {
        Product product = inventory.getProductList().stream()
                .filter(p -> p.getName().equals(productName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.PRODUCT_NOT_FOUND.getMessage()));

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
