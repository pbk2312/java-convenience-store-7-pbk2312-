package store.model;

public class TwoPlusOnePromotion implements PromotionStrategy {

    @Override
    public double calculateDiscountedPrice(int quantity, double price) {
        int payableQuantity = (quantity / 3) * 2 + (quantity % 3);
        return payableQuantity * price;
    }

}
