package store.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.loader.ProductLoader;
import store.model.Inventory;
import store.model.Promotion;

public class OutputViewTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private OutputView outputView;
    private Inventory inventory;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        outputView = new OutputView();

        // ProductLoader를 사용하여 파일에서 데이터 로드
        ProductLoader productLoader = new ProductLoader();
        inventory = new Inventory();

        // 프로모션 로드
        Map<String, Promotion> promotions = productLoader.loadPromotions();

        // 제품 목록 로드
        productLoader.loadProducts(inventory, promotions);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(System.out);
    }

    @Test
    public void testDisplayProductList() {
        outputView.displayProductList(inventory);

        String expectedOutput = String.join(System.lineSeparator(),
                "안녕하세요. W편의점입니다.",
                "현재 보유하고 있는 상품입니다.",
                "",
                "- 콜라 1,000원 10개 탄산2+1",
                "- 콜라 1,000원 10개",
                "- 사이다 1,000원 8개 탄산2+1",
                "- 사이다 1,000원 7개",
                "- 오렌지주스 1,800원 9개 MD추천상품",
                "- 오렌지주스 1,800원 재고 없음",
                "- 탄산수 1,200원 5개 탄산2+1",
                "- 탄산수 1,200원 재고 없음",
                "- 물 500원 10개",
                "- 비타민워터 1,500원 6개",
                "- 감자칩 1,500원 5개 반짝할인",
                "- 감자칩 1,500원 5개",
                "- 초코바 1,200원 5개 MD추천상품",
                "- 초코바 1,200원 5개",
                "- 에너지바 2,000원 5개",
                "- 정식도시락 6,400원 8개",
                "- 컵라면 1,700원 1개 MD추천상품",
                "- 컵라면 1,700원 10개",
                ""
        );

        assertThat(outputStreamCaptor.toString().trim()).isEqualTo(expectedOutput.trim());
    }

}
