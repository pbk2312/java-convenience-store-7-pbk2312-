package store.model;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;

public class Promotion {
    private final PromotionStrategy strategy;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Promotion(PromotionStrategy strategy, LocalDate startDate, LocalDate endDate) {
        this.strategy = strategy;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean isActive() {
        LocalDate today = DateTimes.now().toLocalDate();
        return !today.isBefore(startDate) && !today.isAfter(endDate);
    }

    public double calculateDiscountedPrice(int quantity, double price) {
        if (!isActive()) {
            return price * quantity;
        }
        return strategy.calculateDiscountedPrice(quantity, price);
    }

}
