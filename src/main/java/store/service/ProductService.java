package store.service;

import store.model.Product;
import store.view.ErrorMessage;

public class ProductService {

    // 재고 차감
    public void deductStock(Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new IllegalStateException(ErrorMessage.INVALID_QUANTITY.getMessage());
        }
        product.adjustStock(quantity);
    }

}
