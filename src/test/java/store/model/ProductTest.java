package store.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import store.view.ErrorMessage;

public class ProductTest {

    @Test
    public void testProductCreation() {
        Product product = new Product("우아한돼지들", 1500.00, 10);
        assertThat(product.getName()).isEqualTo("우아한돼지들");
        assertThat(product.getPrice()).isEqualTo(1500.00);
        assertThat(product.getStock()).isEqualTo(10);
    }

    @Test
    public void testDeductStock() {
        Product product = new Product("우아한돼지들", 1500.00, 10);
        product.deductStock(3);
        assertThat(product.getStock()).isEqualTo(7);
    }

    @Test
    public void testDeductStock_InsufficientStock_ShouldThrowException() {
        Product product = new Product("우아한돼지들", 1500.00, 2);
        assertThatThrownBy(() -> product.deductStock(3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(ErrorMessage.INVALID_QUANTITY.getMessage());
    }

    @Test
    public void testCalculateFinalPrice_NoPromotion() {
        Product product = new Product("우아한돼지들", 1500.00, 10);
        double price = product.calculateFinalPrice(3); // 프로모션 없음
        assertThat(price).isEqualTo(4500.00);
    }

    @Test
    public void testCalculateFinalPrice_OnePlusOnePromotion() {
        Promotion onePlusOnePromotion = new Promotion(
                new OnePlusOnePromotion(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)
        );
        Product product = new Product("우아한돼지들", 1500.00, 10, onePlusOnePromotion);
        double price = product.calculateFinalPrice(3); // 1+1 조건으로, 2개 가격만 지불
        assertThat(price).isEqualTo(3000.00);
    }

    @Test
    public void testCalculateFinalPrice_TwoPlusOnePromotion() {
        Promotion twoPlusOnePromotion = new Promotion(
                new TwoPlusOnePromotion(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(1)
        );
        Product product = new Product("우아한돼지들", 1000.00, 10, twoPlusOnePromotion);
        double price = product.calculateFinalPrice(3); // 2+1 조건으로, 2개 가격만 지불
        assertThat(price).isEqualTo(2000.00);
    }

    @Test
    public void testCalculateFinalPrice_PromotionInactive() {
        Promotion expiredPromotion = new Promotion(
                new OnePlusOnePromotion(), LocalDate.now().minusDays(10), LocalDate.now().minusDays(1)
        );
        Product product = new Product("우아한돼지들", 1500.00, 10, expiredPromotion);
        double price = product.calculateFinalPrice(3); // 프로모션이 만료됨
        assertThat(price).isEqualTo(4500.00); // 기본 가격
    }

}
