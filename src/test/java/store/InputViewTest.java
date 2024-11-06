package store;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InputViewTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private InputView inputView;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        inputView = new InputView();
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
        inputView.close();
    }

    @Test
    @DisplayName("상품 및 수량 입력")
    void shouldDisplayProductSelectionMessageAndReadInput() {
        String input = "[콜라-10],[사이다-3]";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        String result = inputView.inputProductSelection();

        assertThat(outputStreamCaptor.toString().trim()).isEqualTo("구매할 상품과 수량을 입력해 주세요. (예: [콜라-10],[사이다-3])");
        assertThat(result).isEqualTo(input);
    }

    @Test
    @DisplayName("멤버십 할인 선택 메시지를 출력하고 입력 받기")
    void shouldDisplayMembershipChoiceMessageAndReadInput() {
        String input = "Y";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        String result = inputView.inputMembershipChoice();

        assertThat(outputStreamCaptor.toString().trim()).isEqualTo("멤버십 할인을 받으시겠습니까? (Y/N)");
        assertThat(result).isEqualTo(input);
    }

    @Test
    @DisplayName("추가 구매 선택 메시지를 출력하고 입력을 받기")
    void shouldDisplayAdditionalPurchaseMessageAndReadInput() {
        String input = "N";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        String result = inputView.inputAdditionalPurchase();

        assertThat(outputStreamCaptor.toString().trim()).isEqualTo("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)");
        assertThat(result).isEqualTo(input);
    }

    @Test
    @DisplayName("프로모션 혜택에 대한 추가 수량 안내 메시지를 출력하고 입력을 받기")
    void shouldDisplayPromotionAddMessageAndReadInput() {
        String input = "Y";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        String result = inputView.inputPromotionAdd("콜라", 1);

        assertThat(outputStreamCaptor.toString().trim()).isEqualTo("현재 콜라은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)");
        assertThat(result).isEqualTo(input);
    }

    @Test
    @DisplayName("프로모션 재고가 부족하여 정가 결제 여부 안내 메시지를 출력하고 입력을 받기")
    void shouldDisplayPromotionLackMessageAndReadInput() {
        String input = "N";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        String result = inputView.inputPromotionLack("사이다", 2);

        assertThat(outputStreamCaptor.toString().trim()).isEqualTo(
                "현재 사이다 2개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)");
        assertThat(result).isEqualTo(input);
    }

}
