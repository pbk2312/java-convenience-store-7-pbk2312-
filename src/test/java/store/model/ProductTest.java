package store.model;

import org.junit.jupiter.api.Test;

public class ProductTest {

    @Test
    public void testProductCreation() {
        Product product = new Product("노트북", 1500.00, 10);
        assertEquals("노트북", product.getName());
        assertEquals(1500.00, product.getPrice());
        assertEquals(10, product.getStock());
    }
}
