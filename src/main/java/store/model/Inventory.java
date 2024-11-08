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

    // 특정 이름의 프로모션 제품을 반환
    public Optional<Product> getPromotionProductByName(String productName) {
        return productList.stream()
                .filter(product -> product.getName().equals(productName))
                .filter(Product::hasPromotion) // 프로모션이 있는 제품만 필터링
                .findFirst();
    }

    // 특정 이름의 일반 제품을 반환
    public Optional<Product> getRegularProductByName(String productName) {
        return productList.stream()
                .filter(product -> product.getName().equals(productName))
                .filter(product -> !product.hasPromotion()) // 프로모션이 없는 제품만 필터링
                .findFirst();
    }

}
