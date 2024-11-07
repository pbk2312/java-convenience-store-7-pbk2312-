package store.model;

public interface PromotionStrategy {
    double calculateDiscountedPrice(int quantity, double price);

    String getType();

}
