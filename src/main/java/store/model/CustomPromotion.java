package store.model;

public class CustomPromotion implements PromotionStrategy {

    @Override
    public double calculateDiscountedPrice(int quantity, double price) {
        return price * quantity;
    }

    @Override
    public String getType() {
        return "반짝할인";
    }

}
