package store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.model.Product;
import store.view.ErrorMessage;

public class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductService();
    }

    @Test
    public void testDeductStock_SufficientStock() {
        Product product = new Product("우아한 콜라", 1000.0, 5);
        productService.deductStock(product, 3);

        assertThat(product.getStock()).isEqualTo(2);
    }

    @Test
    public void testDeductStock_InsufficientStock_ShouldThrowException() {
        Product product = new Product("우아한 콜라", 1000.0, 2);
        assertThatThrownBy(() -> productService.deductStock(product, 3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(ErrorMessage.INVALID_QUANTITY.getMessage());
    }


}



