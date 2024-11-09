package store.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Inventory {

    private static Inventory instance;  // 싱글톤 인스턴스

    private final List<Product> productList = new ArrayList<>();

    private Inventory() {
    }

    public static synchronized Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }

    public void clear() {
        productList.clear();
    }

    public void addProduct(Product product) {
        productList.add(product);
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void adjustProductStock(Product product) {
        productList.replaceAll(p ->
                p.getName().equals(product.getName()) && p.hasPromotion() == product.hasPromotion()
                        ? new Product(product.getName(), product.getPrice(), product.getStock(), product.getPromotion())
                        : p
        );
    }


    public Optional<Product> getPromotionProductByName(String productName) {
        return productList.stream()
                .filter(product -> product.getName().equals(productName) && product.hasPromotion())
                .findFirst();
    }

    public Optional<Product> getRegularProductByName(String productName) {
        return productList.stream()
                .filter(product -> product.getName().equals(productName) && !product.hasPromotion())
                .findFirst();
    }

}
