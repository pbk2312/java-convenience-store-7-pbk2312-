package store.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import store.view.ErrorMessage;

public class PromotionTest {

    private Promotion promotion;
    private PromotionStrategy strategy;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    public void setUp() {
        // 기본 프로모션 설정
        strategy = new OnePlusOnePromotion();
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 12, 31);
        promotion = new Promotion(strategy, startDate, endDate, "1+1 Promotion");
    }

    @Test
    public void testPromotionCreationValidDates() {
        // 올바른 시작일과 종료일 설정 시 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> new Promotion(strategy, startDate, endDate, "1+1 Promotion"));
    }

    @Test
    public void testPromotionCreationInvalidDates() {
        LocalDate invalidStartDate = LocalDate.of(2025, 1, 1);
        LocalDate invalidEndDate = LocalDate.of(2024, 12, 31);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Promotion(strategy, invalidStartDate, invalidEndDate, "Invalid Promotion"));
        assertThat(exception.getMessage()).isEqualTo(ErrorMessage.INVALID_PROMOTION_DATES.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "2024-01-01, true",  // 시작일과 동일한 날짜
            "2024-06-15, true",  // 기간 중간 날짜
            "2024-12-31, true",  // 종료일과 동일한 날짜
            "2023-12-31, false", // 시작일 이전 날짜
            "2025-01-01, false"  // 종료일 이후 날짜
    })
    public void testIsActive(LocalDate testDate, boolean expectedResult) {
        assertThat(promotion.isActive(testDate)).isEqualTo(expectedResult);
    }

    @Test
    public void testCalculateDiscountedPriceWhenActive() {
        double price = 1000.0;
        int quantity = 2;
        double discountedPrice = promotion.calculateDiscountedPrice(quantity, price);
        assertThat(discountedPrice).isEqualTo(1000.0); // 1+1 적용 시 2개 중 1개만 결제
    }

    @Test
    public void testCalculateDiscountedPriceWhenInactive() {
        double price = 1000.0;
        int quantity = 2;

        Promotion inactivePromotion = new Promotion(strategy, startDate, endDate, "1+1 Promotion") {
            @Override
            public boolean isActive(LocalDate date) {
                return false;
            }
        };

        double originalPrice = inactivePromotion.calculateDiscountedPrice(quantity, price);
        assertThat(originalPrice).isEqualTo(price * quantity);
    }

    @ParameterizedTest
    @CsvSource({
            "2, 1", // 프로모션 활성화 상태에서 2개 구매 시 1개 무료
            "3, 1", // 프로모션 활성화 상태에서 3개 구매 시 1개 무료
            "4, 2"  // 프로모션 활성화 상태에서 4개 구매 시 2개 무료
    })
    public void testGetFreeQuantityWhenActive(int quantity, int expectedFreeQuantity) {
        // 프로모션이 활성 상태일 때 무료 제공 수량 확인
        assertThat(promotion.getFreeQuantity(quantity)).isEqualTo(expectedFreeQuantity);
    }

    @Test
    public void testGetFreeQuantityWhenInactive() {
        Promotion inactivePromotion = new Promotion(strategy, startDate, endDate, "1+1 Promotion") {
            @Override
            public boolean isActive(LocalDate date) {
                return false;
            }
        };

        int quantity = 2;
        int freeQuantity = inactivePromotion.getFreeQuantity(quantity);
        assertThat(freeQuantity).isEqualTo(0);
    }

}
