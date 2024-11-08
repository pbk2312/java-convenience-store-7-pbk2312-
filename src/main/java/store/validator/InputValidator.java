package store.validator;

import store.util.ParsingUtils;
import store.view.ErrorMessage;

public class InputValidator {

    // "[상품명-수량]" 형식의 입력을 검증하는 정규 표현식 (예: [우아한합격-10])
    private static final String PRODUCT_FORMAT_REGEX = "\\[(\\p{IsAlphabetic}+)-([0-9]\\d*)]";

    public static void validateProductSelectionFormat(String input) {
        validateNotEmpty(input);
        String[] products = ParsingUtils.splitProducts(input, ",");
        for (String product : products) {
            validateFormat(product);
        }
    }

    private static void validateFormat(String product) {
        if (!isValidFormat(product)) {
            logError(ErrorMessage.INVALID_FORMAT);
        }
    }

    private static boolean isValidFormat(String product) {
        return product.matches(PRODUCT_FORMAT_REGEX);
    }

    public static void validateNotEmpty(String input) {
        if (input == null || input.isBlank()) {
            logError(ErrorMessage.EMPTY_INPUT);
        }
    }

    public static void validateYesOrNo(String input) {
        validateNotEmpty(input);
        if (!input.equals("Y") && !input.equals("N")) {
            logError(ErrorMessage.INVALID_YES_NO);
        }
    }

    private static void logError(ErrorMessage errorMessage) {
        throw new IllegalArgumentException(errorMessage.getMessage());
    }

}
