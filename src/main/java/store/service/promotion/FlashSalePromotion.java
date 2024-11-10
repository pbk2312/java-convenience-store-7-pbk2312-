package store.service.promotion;

public class FlashSalePromotion implements PromotionStrategy {
    @Override
    public double calculateDiscountedPrice(int quantity, double price) {
        int payableQuantity = (int) Math.ceil(quantity * 0.8);
        return payableQuantity * price;
    }

    @Override
    public int getFreeQuantity(int quantity) {
        return 0;
    }

    @Override
    public int calculatePayableQuantity(int quantity) {
        return (int) Math.ceil(quantity * 0.8);
    }

}
