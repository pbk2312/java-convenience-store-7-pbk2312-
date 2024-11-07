package store.model;

public record Member(String name, boolean isMember) {
    private static final double DISCOUNT_RATE = 0.3;
    private static final double MAX_DISCOUNT_AMOUNT = 8000.0;

    public double applyMembershipDiscount(double amount) {
        if (!isMember) {
            return amount;
        }
        double discount = amount * DISCOUNT_RATE;
        return Math.min(discount, MAX_DISCOUNT_AMOUNT); // 최대 할인 금액 적용
    }

}
