package store.service;

import camp.nextstep.edu.missionutils.DateTimes;
import store.model.Product;

public class PricingService {

    public double calculateFinalPrice(Product product, int quantity) {
        if (product.getPromotion() != null) {
            return product.getPromotion()
                    .calculateDiscountedPrice(quantity, product.getPrice(), DateTimes.now().toLocalDate());
        }
        return product.getPrice() * quantity;
    }

}
