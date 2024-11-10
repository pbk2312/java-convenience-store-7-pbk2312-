package store.service.promotion;

public class OnePlusOnePromotion implements PromotionStrategy {
    @Override
    public double calculateDiscountedPrice(int quantity, double price) {
        int payableQuantity = (quantity + 1) / 2;
        return payableQuantity * price;
    }

    @Override
    public int getFreeQuantity(int quantity) {
        return quantity / 2;
    }

    @Override
    public int calculatePayableQuantity(int quantity) {
        return (quantity + 1) / 2;
    }

}
