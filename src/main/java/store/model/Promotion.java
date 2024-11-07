package store.model;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import store.view.ErrorMessage;

public class Promotion {
    private final PromotionStrategy strategy;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String description; // 한글 설명 추가

    public Promotion(PromotionStrategy strategy, LocalDate startDate, LocalDate endDate, String description) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_PROMOTION_DATES.getMessage());
        }
        this.strategy = strategy;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
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

    public String getDescription() { // 한글 설명 반환 메서드
        return description;
    }

}
