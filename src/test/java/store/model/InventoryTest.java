package store.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InventoryTest {

    private Inventory inventory;
    private Product sampleProduct;

    @BeforeEach
    public void setUp() {
        inventory = new Inventory();
        sampleProduct = new Product("콜라", 1000.00, 10);
    }

    @Test
    public void testAddProduct() {
        // 제품 추가
        inventory.addProduct(sampleProduct);

        // 제품이 인벤토리에 추가되었는지 확인
        assertThat(inventory.getProductList()).contains(sampleProduct);
    }

    @Test
    public void testGetProductList() {
        // 여러 제품 추가
        Product product1 = new Product("사이다", 1000.00, 8);
        Product product2 = new Product("물", 500.00, 20);
        inventory.addProduct(sampleProduct);
        inventory.addProduct(product1);
        inventory.addProduct(product2);

        // getProductList가 올바르게 반환되는지 확인
        assertThat(inventory.getProductList()).containsExactly(sampleProduct, product1, product2);
    }

    @Test
    public void testGetProductList_IsEmptyInitially() {
        // 인벤토리가 처음에 비어 있는지 확인
        assertThat(inventory.getProductList()).isEmpty();
    }

}
