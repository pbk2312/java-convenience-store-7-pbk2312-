package store.model;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import store.service.promotion.PromotionStrategy;
import store.view.ErrorMessage;

public class Promotion {

    private final PromotionStrategy strategy;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String description;

    public Promotion(PromotionStrategy strategy, LocalDate startDate, LocalDate endDate, String description) {
        validateDates(startDate, endDate);
        this.strategy = strategy;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_PROMOTION_DATES.getMessage());
        }
    }

    public boolean isActive(LocalDate currentDate) {
        return !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate);
    }

    public double calculateDiscountedPrice(int quantity, double price) {
        return isActive(DateTimes.now().toLocalDate()) ? strategy.calculateDiscountedPrice(quantity, price)
                : price * quantity;
    }

    public int getFreeQuantity(int quantity) {
        return isActive(DateTimes.now().toLocalDate()) ? strategy.getFreeQuantity(quantity) : 0;
    }

    public String getDescription() {
        return description;
    }

}
