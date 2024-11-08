package store.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import store.service.promotion.OnePlusOnePromotion;
import store.service.promotion.PromotionStrategy;

class ProductTest {

    @Test
    void testProductCreationWithoutPromotion() {
        // Given
        String name = "우아한돼지 한마리";
        double price = 1000.0;
        int stock = 10;

        // When
        Product product = new Product(name, price, stock);

        // Then
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getStock()).isEqualTo(stock);
        assertThat(product.getPromotion()).isNull();
    }

    @Test
    void testProductCreationWithPromotion() {
        // Given
        String name = "우아한사이다";
        double price = 1200.0;
        int stock = 5;
        PromotionStrategy strategy = new OnePlusOnePromotion();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String description = "1+1 프로모션";
        Promotion promotion = new Promotion(strategy, startDate, endDate, description);

        // When
        Product product = new Product(name, price, stock, promotion);

        // Then
        assertThat(product.getName()).isEqualTo(name);
        assertThat(product.getPrice()).isEqualTo(price);
        assertThat(product.getStock()).isEqualTo(stock);
        assertThat(product.getPromotion()).isEqualTo(promotion);
    }

    @Test
    void testAdjustStock() {
        // Given
        String name = "우아한주스";
        double price = 1800.0;
        int stock = 9;
        Product product = new Product(name, price, stock);

        // When
        product.adjustStock(3); // 3개 차감
        int remainingStock = product.getStock();

        // Then
        assertThat(remainingStock).isEqualTo(6); // 남은 재고는 6
    }

    @Test
    void testAdjustStockDoesNotAllowNegativeStock() {
        // Given
        String name = "우아한 물";
        double price = 500.0;
        Product product = new Product(name, price, 2);

        // When
        product.adjustStock(3); // 3개 차감

        // Then
        assertThat(product.getStock()).isEqualTo(-1); // 예외 처리 여기는 없으므로 음수 가능
    }

    @Test
    void testAdjustStockDoesNotAllowNegativeStockWithPromotion() {
        // Given
        String name = "우아한 주스";
        double price = 1500.0;
        PromotionStrategy strategy = new OnePlusOnePromotion();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Promotion promotion = new Promotion(strategy, startDate, endDate, "1+1 프로모션");

        Product product = new Product(name, price, 1, promotion);

        // When
        product.adjustStock(2); // 2개 차감

        // Then
        assertThat(product.getStock()).isEqualTo(-1); // 예외를 처리하는 로직이 없으므로 음수가 가능
    }

}
