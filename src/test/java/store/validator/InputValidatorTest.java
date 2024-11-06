package store.validator;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import store.view.ErrorMessage;

class InputValidatorTest {

    @ParameterizedTest
    @DisplayName("다양한 입력 값에 따른 예외 발생 여부 테스트")
    @CsvSource({
            "'', EMPTY_INPUT",                           // 빈 입력
            "'[-10]', INVALID_FORMAT",                   // 상품명이 빈 경우
            "'[콜라-0]', ZERO_QUANTITY",                 // 수량이 0인 경우
            "'[콜라=10]', INVALID_FORMAT",               // 형식 오류 - 잘못된 구분자
            "'[콜라-10][사이다-3]', INVALID_FORMAT",     // 형식 오류 - 쉼표 없이 구분
            "'[콜라10],[사이다-3]', INVALID_FORMAT",     // 형식 오류 - 하이픈 없는 경우
            "'[콜라-10],[사이다-0]', ZERO_QUANTITY",     // 여러 상품 중 하나의 수량이 0인 경우
            "'[없는상품-3]', NON_EXISTENT_PRODUCT",      // 존재하지 않는 상품
            "'[콜라-15]', INVALID_QUANTITY",             // 재고보다 많은 수량
            "'[콜라--15]', INVALID_FORMAT"               // 잘못된 구분자
    })
    void shouldThrowExceptionForInvalidProductSelection(String input, String expectedErrorCode) {
        // 샘플 상품 재고 구성
        Map<String, Integer> productInventory = new HashMap<>();
        productInventory.put("콜라", 10);
        productInventory.put("사이다", 5);

        assertThatThrownBy(() -> InputValidator.validateProductSelection(input, productInventory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.valueOf(expectedErrorCode).getMessage());
    }

    @Test
    @DisplayName("정상 입력 값에 대해 예외가 발생하지 않아야 한다")
    void shouldNotThrowExceptionForValidProductSelection() {
        Map<String, Integer> productInventory = new HashMap<>();
        productInventory.put("콜라", 10);
        productInventory.put("사이다", 5);

        String validInput = "[콜라-2],[사이다-3]";

        assertThatNoException()
                .isThrownBy(() -> InputValidator.validateProductSelection(validInput, productInventory));
    }

    @ParameterizedTest
    @DisplayName("Y/N 외 다른 값 또는 비어 있는 입력 시 예외 발생")
    @CsvSource({
            "'우아한테크코스', INVALID_YES_NO",
            "'maybe', INVALID_YES_NO",
            "'', EMPTY_INPUT"
    })
    void shouldThrowErrorForInvalidYesOrNoInput(String input, String errorCode) {
        assertThatThrownBy(() -> InputValidator.validateYesOrNo(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.valueOf(errorCode).getMessage());
    }

}
