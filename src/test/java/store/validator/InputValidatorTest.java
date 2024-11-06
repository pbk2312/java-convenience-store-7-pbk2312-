package store.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InputValidatorTest {

    @Test
    @DisplayName("올바른 형식의 상품 및 수량 입력을 검증")
    void shouldPassValidProductSelection() {
        String input = "[콜라-10],[사이다-3]";
        InputValidator.validateProductSelection(input);
    }

    @Test
    @DisplayName("잘못된 형식의 상품 및 수량 입력 시 예외 발생")
    void shouldThrowErrorForInvalidProductSelectionFormat() {
        String input = "콜라10, 사이다-3";
        assertThatThrownBy(() -> InputValidator.validateProductSelection(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR] 올바른 형식으로 입력해 주세요");
    }

    @Test
    @DisplayName("존재하지 않는 상품 입력 시 예외 발생")
    void shouldThrowErrorForNonExistentProduct() {
        Map<String, Integer> productInventory = new HashMap<>();
        productInventory.put("콜라", 10);

        assertThatThrownBy(() -> InputValidator.validateProductExistence("우아한테크코스", productInventory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR] 존재하지 않는 상품입니다");
    }

    @Test
    @DisplayName("유효하지 않은 수량 입력 시 예외 발생")
    void shouldThrowErrorForInvalidQuantity() {
        Map<String, Integer> productInventory = new HashMap<>();
        productInventory.put("콜라", 5);

        assertThatThrownBy(() -> InputValidator.validateQuantity("콜라", 10, productInventory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR] 재고가 부족하거나 잘못된 수량입니다");
    }

    @Test
    @DisplayName("Y/N 외 다른 값 입력 시 예외 발생")
    void shouldThrowErrorForInvalidYesOrNoInput() {
        String input = "우아한테크코스";
        assertThatThrownBy(() -> InputValidator.validateYesOrNo(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("[ERROR] Y 또는 N을 입력해 주세요");
    }

}
