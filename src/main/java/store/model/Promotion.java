package store.model;

import java.time.LocalDate;
import store.view.ErrorMessage;

public class Promotion {

    private final PromotionStrategy strategy;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String description;

    public Promotion(PromotionStrategy strategy, LocalDate startDate, LocalDate endDate, String description) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_PROMOTION_DATES.getMessage());
        }
        this.strategy = strategy;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    public boolean isActive(LocalDate currentDate) {
        return !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate);
    }

    public double calculateDiscountedPrice(int quantity, double price, LocalDate currentDate) {
        if (!isActive(currentDate)) {
            return price * quantity;
        }
        return strategy.calculateDiscountedPrice(quantity, price);
    }

    public int getFreeQuantity(int quantity, LocalDate currentDate) {
        if (!isActive(currentDate)) {
            return 0;
        }
        return strategy.getFreeQuantity(quantity);
    }

    public String getDescription() {
        return description;
    }
    
}
