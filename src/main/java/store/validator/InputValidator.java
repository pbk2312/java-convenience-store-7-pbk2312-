package store.validator;

import java.util.Map;
import store.util.ParsingUtils;
import store.view.ErrorMessage;

public class InputValidator {

    // "[상품명-수량]" 형식의 입력을 검증하는 정규 표현식 (예: [콜라-10])
    private static final String PRODUCT_FORMAT_REGEX = "\\[(\\p{IsAlphabetic}+)-([0-9]\\d*)]";

    public static void validateProductSelection(String input, Map<String, Integer> productInventory) {
        validateNotEmpty(input);
        String[] products = ParsingUtils.splitProducts(input, ",");
        for (String product : products) {
            validateProductDetails(product, productInventory);
        }
    }

    private static void validateProductDetails(String product, Map<String, Integer> productInventory) {
        validateFormat(product);
        String productName = ParsingUtils.extractProductName(product);
        int quantity = ParsingUtils.extractQuantity(product);

        validateNonZeroQuantity(quantity);
        validateProductExistsInInventory(productName, productInventory);
        validateSufficientStock(productName, quantity, productInventory);
    }

    private static void validateFormat(String product) {
        if (!isValidFormat(product)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_FORMAT.getMessage());
        }
    }

    private static void validateNonZeroQuantity(int quantity) {
        if (quantity == 0) {
            throw new IllegalArgumentException(ErrorMessage.ZERO_QUANTITY.getMessage());
        }
    }

    private static void validateProductExistsInInventory(String productName, Map<String, Integer> productInventory) {
        if (!productInventory.containsKey(productName)) {
            throw new IllegalArgumentException(ErrorMessage.NON_EXISTENT_PRODUCT.getMessage());
        }
    }

    private static void validateSufficientStock(String productName, int quantity,
                                                Map<String, Integer> productInventory) {
        int stock = productInventory.getOrDefault(productName, 0);
        if (quantity > stock) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_QUANTITY.getMessage());
        }
    }

    private static boolean isValidFormat(String product) {
        return product.matches(PRODUCT_FORMAT_REGEX);
    }

    private static void validateNotEmpty(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(ErrorMessage.EMPTY_INPUT.getMessage());
        }
    }

    public static void validateYesOrNo(String input) {
        validateNotEmpty(input);
        if (!input.equals("Y") && !input.equals("N")) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_YES_NO.getMessage());
        }
    }

}
