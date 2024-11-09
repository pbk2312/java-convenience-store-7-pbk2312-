package store.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Inventory {

    private static Inventory instance;  // 싱글톤

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

    public Optional<Product> getProductByName(String productName) {
        return productList.stream()
                .filter(product -> product.getName().equals(productName))
                .findFirst();
    }


    public void adjustProductStock(Product product, int quantity) {
        // 차감된 재고 상태를 직접 가져오기
        Product updatedProduct = new Product(product.getName(), product.getPrice(), product.getStock(),
                product.getPromotion());

        for (int i = 0; i < productList.size(); i++) {
            Product p = productList.get(i);
            if (p.getName().equals(product.getName()) && p.hasPromotion() == product.hasPromotion()) {
                productList.set(i, updatedProduct);
                break;
            }
        }
    }
}
