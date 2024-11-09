package store.service;

import java.util.Optional;
import store.model.Inventory;
import store.model.Product;
import store.view.ErrorMessage;

public class StockManager {
    private final Inventory inventory;
    private final ProductService productService;

    public StockManager(Inventory inventory, ProductService productService) {
        this.inventory = inventory;
        this.productService = productService;
    }

    public Optional<Product> getPromotionProduct(String productName) {
        return inventory.getPromotionProductByName(productName);
    }

    public Product getRegularProduct(String productName) {
        return inventory.getRegularProductByName(productName)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NON_EXISTENT_PRODUCT.getMessage()));
    }

    public void deductStock(Product product, int quantity) {
        productService.deductStock(product, quantity);
        inventory.adjustProductStock(product);
    }

}
