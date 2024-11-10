package store.service.promotion;

public class TwoPlusOnePromotion implements PromotionStrategy {
    @Override
    public double calculateDiscountedPrice(int quantity, double price) {
        int payableQuantity = (2 * quantity) / 3;
        return payableQuantity * price;
    }


    @Override
    public int getFreeQuantity(int quantity) {
        return quantity / 3;
    }

    @Override
    public int calculatePayableQuantity(int quantity) {
        return (2 * quantity) / 3;
    }
    
}
