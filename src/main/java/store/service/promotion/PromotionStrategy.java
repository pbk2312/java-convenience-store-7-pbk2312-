package store.service.promotion;

public interface PromotionStrategy {
    double calculateDiscountedPrice(int quantity, double price);

    int getFreeQuantity(int quantity);

}
