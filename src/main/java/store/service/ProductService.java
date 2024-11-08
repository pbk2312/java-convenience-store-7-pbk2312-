package store.service;

import camp.nextstep.edu.missionutils.DateTimes;
import store.model.Product;
import store.view.ErrorMessage;

public class ProductService {

    public void deductStock(Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new IllegalStateException(ErrorMessage.INVALID_QUANTITY.getMessage());
        }
        product.adjustStock(quantity);
    }

    public int calculateFreeItems(Product product, int quantity) {
        if (product.getPromotion() != null && product.getPromotion().isActive(DateTimes.now().toLocalDate())) {
            return product.getPromotion().getFreeQuantity(quantity);
        }
        return 0;
    }

}
