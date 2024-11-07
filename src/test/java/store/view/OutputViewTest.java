package store.view;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.model.OnePlusOnePromotion;
import store.model.Product;
import store.model.Promotion;
import store.model.TwoPlusOnePromotion;

class OutputViewTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private OutputView outputView;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        outputView = new OutputView();
    }

    @Test
    void displayWelcomeMessage() {
        outputView.displayWelcomeMessage();
        String expectedOutput = "안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.";
        assertThat(outputStreamCaptor.toString().trim()).isEqualTo(expectedOutput);
    }

    @Test
    void displayProducts() {
        List<Product> products = Arrays.asList(
                new Product("콜라", 1000, 10, new Promotion(new TwoPlusOnePromotion(),
                        LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), "탄산2+1")),
                new Product("사이다", 1000, 8, new Promotion(new TwoPlusOnePromotion(),
                        LocalDate.now().minusDays(5), LocalDate.now().plusDays(5), "탄산2+1")),
                new Product("오렌지주스", 1800, 0, new Promotion(new OnePlusOnePromotion(),
                        LocalDate.now().minusDays(10), LocalDate.now().plusDays(10), "MD추천상품")),
                new Product("물", 500, 10, null),
                new Product("비타민워터", 1500, 6, null)
        );

        outputView.displayProducts(products);

        String expectedOutput = String.join(System.lineSeparator(),
                "안녕하세요. W편의점입니다.",
                "현재 보유하고 있는 상품입니다.",
                "- 콜라 1,000원 10개 탄산2+1",
                "- 사이다 1,000원 8개 탄산2+1",
                "- 오렌지주스 1,800원 재고 없음 MD추천상품",
                "- 물 500원 10개",
                "- 비타민워터 1,500원 6개"
        );

        assertThat(outputStreamCaptor.toString().trim()).isEqualTo(expectedOutput);
    }

    @Test
    void displayMessage() {
        outputView.displayMessage(ViewMessage.INPUT_PRODUCT_SELECTION);
        assertThat(outputStreamCaptor.toString().trim()).isEqualTo("구매할 상품과 수량을 입력해 주세요. (예: [콜라-10],[사이다-3])");
    }

    @Test
    void displayFormattedMessage() {
        outputView.displayFormattedMessage(ViewMessage.PROMOTION_ADD, "콜라", 1);
        assertThat(outputStreamCaptor.toString().trim()).isEqualTo("현재 콜라은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)");
    }

}
