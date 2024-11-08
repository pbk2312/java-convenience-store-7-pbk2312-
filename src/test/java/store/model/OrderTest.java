package store.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderTest {

    private Order order;

    @BeforeEach
    public void setUp() {
        order = new Order();
    }

    @Test
    public void testAddProduct() {
        Product product = new Product("우아한 콜라", 1000.0, 10);
        order.addProduct(product, 3);

        assertThat(order.getTotalBeforeDiscount()).isEqualTo(3000.0);
        assertThat(order.getTotalQuantity()).isEqualTo(3);
        assertThat(order.getOrderedProducts().get(product)).isEqualTo(3);
    }

    @Test
    public void testAddProductWithMultipleProducts() {
        Product product1 = new Product("우아한 콜라", 1000.0, 10);
        Product product2 = new Product("우아한 사이다", 1200.0, 5);
        order.addProduct(product1, 3);
        order.addProduct(product2, 2);

        assertThat(order.getTotalBeforeDiscount()).isEqualTo(5400.0);
        assertThat(order.getTotalQuantity()).isEqualTo(5);
        assertThat(order.getOrderedProducts().get(product1)).isEqualTo(3);
        assertThat(order.getOrderedProducts().get(product2)).isEqualTo(2);
    }

}
