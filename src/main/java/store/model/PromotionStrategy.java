package store.model;

public interface PromotionStrategy {
    double calculateDiscountedPrice(int quantity, double price);

    // 구매한 수량에 따른 증정 수량을 반환
    int getFreeQuantity(int quantity);

}
