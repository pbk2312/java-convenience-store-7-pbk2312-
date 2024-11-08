package store.model;

public class OnePlusOnePromotion implements PromotionStrategy {
    @Override
    public double calculateDiscountedPrice(int quantity, double price) {
        int payableQuantity = (quantity + 1) / 2;
        return payableQuantity * price;
    }


    @Override
    public int getFreeQuantity(int quantity) {
        return quantity / 2;  // 구매한 수량의 절반이 무료 증정
    }

}
