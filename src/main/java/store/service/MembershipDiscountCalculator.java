package store.service;

public class MembershipDiscountCalculator {
    private static final double MEMBERSHIP_DISCOUNT_RATE = 0.3;
    private static final double MAX_MEMBERSHIP_DISCOUNT = 8000.0;

    public double calculate(double amountAfterEventDiscount, boolean isMembership) {
        if (!isMembership) {
            return 0.0;
        }
        double discount = amountAfterEventDiscount * MEMBERSHIP_DISCOUNT_RATE;
        return Math.min(discount, MAX_MEMBERSHIP_DISCOUNT);
    }

}
