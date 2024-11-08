package store.service.promotion;

public class TwoPlusOnePromotion implements PromotionStrategy {
    @Override
    public double calculateDiscountedPrice(int quantity, double price) {
        int payableQuantity = (2 * quantity) / 3;
        return payableQuantity * price;
    }


    @Override
    public int getFreeQuantity(int quantity) {
        return quantity / 3;  // 구매한 수량의 3분의 1이 무료 증정
    }

}
