package store.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Inventory {

    private final List<Product> productList = new ArrayList<>();

    public void addProduct(Product product) {
        productList.add(product);
    }

    public List<Product> getProductList() {
        return productList;
    }

    public Optional<Product> getProductByName(String productName) {
        return productList.stream()
                .filter(product -> product.getName().equals(productName))
                .findFirst();
    }

}
