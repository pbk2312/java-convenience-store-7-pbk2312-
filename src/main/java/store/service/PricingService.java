package store.service;

import store.model.Product;

public class PricingService {

    public double calculateFinalPrice(Product product, int quantity) {
        if (product.getPromotion() != null) {
            return product.getPromotion().calculateDiscountedPrice(quantity, product.getPrice());
        }
        return product.getPrice() * quantity;
    }

}
