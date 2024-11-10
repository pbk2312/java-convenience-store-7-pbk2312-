package store.service;

import store.model.Product;

public class PricingService {


    public double calculateFinalPrice(Product product, int quantity) {
        if (product.getPromotion() != null) {
            int freeQuantity = product.getPromotion().getFreeQuantity(quantity);
            int paidQuantity = quantity - freeQuantity;

            return product.getPrice() * paidQuantity;
        }
        return product.getPrice() * quantity;
    }

}

