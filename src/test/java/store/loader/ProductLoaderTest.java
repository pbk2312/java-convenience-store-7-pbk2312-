package store.loader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.model.Inventory;
import store.model.Promotion;

public class ProductLoaderTest {

    private ProductLoader productLoader;
    private Inventory inventory;

    @BeforeEach
    public void setUp() {
        productLoader = new ProductLoader();
        inventory = new Inventory();
    }

    @Test
    public void testLoadProducts_FileLoadsSuccessfully() {
        Map<String, Promotion> promotions = productLoader.loadPromotions();

        // 파일 로딩에 예외가 발생하지 않는지 확인
        assertThatNoException().isThrownBy(() -> productLoader.loadProducts(inventory, promotions));

        // 인벤토리가 비어있지 않고, 제품이 로드되었는지 확인
        assertThat(inventory.getProductList()).isNotEmpty();
    }

    @Test
    public void testLoadPromotions_FileLoadsSuccessfully() {
        // promotions.md 파일에서 프로모션을 로드하는지 확인
        Map<String, Promotion> promotions = productLoader.loadPromotions();

        // 프로모션 데이터가 로드되었는지 확인
        assertThat(promotions).isNotEmpty();

        // 특정 프로모션이 로드되었는지 확인
        assertThat(promotions).containsKey("탄산2+1");
        assertThat(promotions.get("탄산2+1").getDescription()).isEqualTo("탄산2+1");

        assertThat(promotions).containsKey("반짝할인");
        assertThat(promotions.get("반짝할인").getDescription()).isEqualTo("반짝할인");
    }

}
