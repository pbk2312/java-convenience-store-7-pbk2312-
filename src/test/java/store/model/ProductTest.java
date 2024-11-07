package store.model;

import static org.assertj.core.api.Assertions.assertThat;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class ProductTest {

    @Test
    public void testProductCreation() {
        Product product = new Product("노트북", 1500.00, 10);
        assertThat(product.getName()).isEqualTo("노트북");
        assertThat(product.getPrice()).isEqualTo(1500.00);
        assertThat(product.getStock()).isEqualTo(10);
    }

    @Test
    public void testDeductStock() {
        Product product = new Product("노트북", 1500.00, 10);
        product.deductStock(3);
        assertThat(product.getStock()).isEqualTo(7);
    }

    @Test
    public void testIsPromotionActive() {
        // 현재 날짜 기준으로 프로모션이 활성화된 상태인지 확인
        LocalDate today = DateTimes.now().toLocalDate();
        Product product = new Product("노트북", 1500.00, 10, "1+1", today.minusDays(1), today.plusDays(1));
        assertThat(product.isPromotionActive()).isTrue();
    }

    @Test
    public void testCalculatePromotionPrice_OnePlusOne() {
        Product product = new Product("노트북", 1500.00, 10, "1+1", LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1));
        double price = product.calculatePromotionPrice(3); // 1+1 조건으로, 2개 가격만 지불
        assertThat(price).isEqualTo(3000.00);
    }

}
