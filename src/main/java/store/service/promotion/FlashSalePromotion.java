package store.service.promotion;

public class FlashSalePromotion implements PromotionStrategy {
    @Override
    public double calculateDiscountedPrice(int quantity, double price) {
        int payableQuantity = (int) Math.ceil(quantity * 0.8);  // 20% 할인 적용
        return payableQuantity * price;
    }

    @Override
    public int getFreeQuantity(int quantity) {
        return 0;
    }

}
