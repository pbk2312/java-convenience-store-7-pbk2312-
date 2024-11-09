package store.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Inventory {

    private static Inventory instance;
    private final List<Product> productList = new ArrayList<>();


    private Inventory() {
    }

    public static Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
    }

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
                .filter(Product::hasPromotion)
                .findFirst();
    }

    // 특정 이름의 일반 제품을 반환
    public Optional<Product> getRegularProductByName(String productName) {
        return productList.stream()
                .filter(product -> product.getName().equals(productName))
                .filter(product -> !product.hasPromotion())
                .findFirst();
    }

    // 재고 업데이트 메서드
    public void adjustProductStock(Product product, int quantity) {
        // 재고 수량 조정을 위해 새로운 Product 객체 생성
        int newStock = product.getStock() - quantity;
        Product updatedProduct = new Product(product.getName(), product.getPrice(), newStock, product.getPromotion());
        System.out.println("재고 변경 후: " + updatedProduct.getName() + " - 재고: " + updatedProduct.getStock());

        // productList에서 기존 제품을 찾아서 새 객체로 교체
        for (int i = 0; i < productList.size(); i++) {
            Product p = productList.get(i);
            if (p.getName().equals(product.getName()) && p.hasPromotion() == product.hasPromotion()) {
                productList.set(i, updatedProduct); // 기존 객체를 새로운 객체로 대체
                break;
            }
        }
    }

}
