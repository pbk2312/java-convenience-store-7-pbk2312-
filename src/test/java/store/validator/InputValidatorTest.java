package store.validator;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import store.view.ErrorMessage;

class InputValidatorTest {

    @ParameterizedTest
    @DisplayName("잘못된 형식 및 빈 값에 따른 예외 발생 여부 테스트")
    @CsvSource({
            "'', EMPTY_INPUT",                           // 빈 입력
            "'[-10]', INVALID_FORMAT",                   // 상품명이 빈 경우
            "'[우아한돼지=10]', INVALID_FORMAT",               // 형식 오류 - 잘못된 구분자
            "'[우아한유한-10][합격-3]', INVALID_FORMAT",     // 형식 오류 - 쉼표 없이 구분
            "'[우아한사이다],[성공-3]', INVALID_FORMAT",     // 형식 오류 - 하이픈 없는 경우
            "'[우아한합격--15]', INVALID_FORMAT"               // 잘못된 구분자
    })
    void shouldThrowExceptionForInvalidProductSelectionFormat(String input, String expectedErrorCode) {
        assertThatThrownBy(() -> InputValidator.validateProductSelectionFormat(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.valueOf(expectedErrorCode).getMessage());
    }

    @Test
    @DisplayName("정상 형식의 입력 값에 대해 예외가 발생하지 X")
    void shouldNotThrowExceptionForValidProductSelectionFormat() {
        String validInput = "[콜라-2],[사이다-3]";

        assertThatNoException()
                .isThrownBy(() -> InputValidator.validateProductSelectionFormat(validInput));
    }

    @ParameterizedTest
    @DisplayName("Y/N 외 다른 값 또는 비어 있는 입력 시 예외 발생")
    @CsvSource({
            "'우아한테크코스', INVALID_YES_NO",
            "'maybe', INVALID_YES_NO",
            "'', EMPTY_INPUT",
            "'y', INVALID_YES_NO", // 소문자는 허용 X
            "'n', INVALID_YES_NO" // 소문자는 허용 X
    })
    void shouldThrowErrorForInvalidYesOrNoInput(String input, String errorCode) {
        assertThatThrownBy(() -> InputValidator.validateYesOrNo(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.valueOf(errorCode).getMessage());
    }

    @ParameterizedTest
    @DisplayName("Y/N 정상 입력 값에 대해 예외가 발생하지 X")
    @CsvSource({
            "'Y'",
            "'N'"
    })
    void shouldNotThrowExceptionForValidYesOrNoInput(String input) {
        assertThatNoException()
                .isThrownBy(() -> InputValidator.validateYesOrNo(input));
    }

}
