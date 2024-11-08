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
    private LocalDate currentDate;

    @BeforeEach
    public void setUp() {
        // 기본 프로모션
        strategy = new OnePlusOnePromotion();
        startDate = LocalDate.of(2024, 1, 1);
        endDate = LocalDate.of(2024, 12, 31);
        currentDate = LocalDate.of(2024, 6, 15);
        promotion = new Promotion(strategy, startDate, endDate, "1+1 Promotion");
    }

    @Test
    public void testPromotionCreationValidDates() {
        // 올바른 시작일과 종료일 설정 시 예외가 발생하지 않아야 함
        assertDoesNotThrow(() -> new Promotion(strategy, startDate, endDate, "1+1 Promotion"));
    }

    @Test
    public void testPromotionCreationInvalidDates() {
        // 시작일이 종료일 이후일 때 IllegalArgumentException 예외 발생
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
        // 주어진 날짜가 프로모션 활성화 기간 내에 있는지 여부 확인
        assertThat(promotion.isActive(testDate)).isEqualTo(expectedResult);
    }

    @Test
    public void testCalculateDiscountedPriceWhenActive() {
        // 프로모션이 활성 상태일 때 할인가가 적용됨
        double price = 1000.0;
        int quantity = 2;
        double discountedPrice = promotion.calculateDiscountedPrice(quantity, price, currentDate);
        assertThat(discountedPrice).isEqualTo(1000.0); // 1+1 적용 시 2개 중 1개만 결제
    }

    @Test
    public void testCalculateDiscountedPriceWhenInactive() {
        // 프로모션이 비활성 상태일 때 원래 금액이 반환됨
        LocalDate inactiveDate = LocalDate.of(2023, 12, 31);
        double price = 1000.0;
        int quantity = 2;
        double originalPrice = promotion.calculateDiscountedPrice(quantity, price, inactiveDate);
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
        assertThat(promotion.getFreeQuantity(quantity, currentDate)).isEqualTo(expectedFreeQuantity);
    }

    @Test
    public void testGetFreeQuantityWhenInactive() {
        // 프로모션이 비활성 상태일 때 무료 제공 수량은 0이어야 함
        LocalDate inactiveDate = LocalDate.of(2023, 12, 31);
        int quantity = 2;
        int freeQuantity = promotion.getFreeQuantity(quantity, inactiveDate);
        assertThat(freeQuantity).isEqualTo(0);
    }

}
