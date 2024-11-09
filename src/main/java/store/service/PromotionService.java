package store.service;

import store.model.Order;
import store.model.Product;

public class PromotionService {
    private final ProductService productService;

    public PromotionService(ProductService productService) {
        this.productService = productService;
    }

    public void addFreeItems(Order order, Product product, int quantity) {
        int freeQuantity = productService.calculateFreeItems(product, quantity);
        if (freeQuantity > 0) {
            order.getFreeItems().put(product, freeQuantity);
        }
    }

}
