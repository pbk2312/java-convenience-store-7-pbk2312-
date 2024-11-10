package store.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InventoryTest {

    private Inventory inventory;
    private Product sampleProduct;

    @BeforeEach
    public void setUp() {
        inventory = Inventory.getInstance();
        inventory.getProductList().clear();
        sampleProduct = new Product("콜라", 1000.00, 10);
    }

    @Test
    public void testAddProduct() {
        inventory.addProduct(sampleProduct);

        assertThat(inventory.getProductList()).contains(sampleProduct);
    }

    @Test
    public void testGetProductList() {
        Product product1 = new Product("사이다", 1000.00, 8);
        Product product2 = new Product("물", 500.00, 20);
        inventory.addProduct(sampleProduct);
        inventory.addProduct(product1);
        inventory.addProduct(product2);

        assertThat(inventory.getProductList()).containsExactly(sampleProduct, product1, product2);
    }

    @Test
    public void testGetProductList_IsEmptyInitially() {
        assertThat(inventory.getProductList()).isEmpty();
    }

}
