package store.model;

public class OnePlusOnePromotion implements PromotionStrategy {

    @Override
    public double calculateDiscountedPrice(int quantity, double price) {
        int payableQuantity = (quantity + 1) / 2;
        return payableQuantity * price;
    }

}
