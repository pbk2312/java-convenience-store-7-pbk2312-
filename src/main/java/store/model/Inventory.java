package store.model;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private final List<Product> productList;

    public Inventory() {
        productList = new ArrayList<>();
    }

    public void addProduct(Product product) {
        productList.add(product);
    }

    public List<Product> getProductList() {
        return productList;
    }

}
