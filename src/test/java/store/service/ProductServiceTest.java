package store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.model.OnePlusOnePromotion;
import store.model.Product;
import store.model.Promotion;
import store.view.ErrorMessage;

public class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductService();
    }

    @Test
    public void testDeductStock_SufficientStock() {
        Product product = new Product("콜라", 1000.0, 5);
        productService.deductStock(product, 3);

        assertThat(product.getStock()).isEqualTo(2);
    }

    @Test
    public void testDeductStock_InsufficientStock_ShouldThrowException() {
        Product product = new Product("콜라", 1000.0, 2);
        assertThatThrownBy(() -> productService.deductStock(product, 3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(ErrorMessage.INVALID_QUANTITY.getMessage());
    }

    @Test
    public void testCalculateFinalPrice_NoPromotion() {
        Product product = new Product("콜라", 1000.0, 10);
        double price = productService.calculateFinalPrice(product, 3);
        assertThat(price).isEqualTo(3000.0);
    }

    @Test
    public void testCalculateFinalPrice_WithPromotion() {
        Promotion promotion = new Promotion(
                new OnePlusOnePromotion(),
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1),
                "1+1"
        );
        Product product = new Product("콜라", 1000.0, 10, promotion);
        double price = productService.calculateFinalPrice(product, 3);
        assertThat(price).isEqualTo(2000.0); // 1+1 프로모션 적용으로 2개 가격
    }

}
