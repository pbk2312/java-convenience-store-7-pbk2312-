package store.validator;

import java.util.Map;
import store.util.ParsingUtils;
import store.view.ErrorMessage;

public class InputValidator {

    private static final String ITEM_FORMAT_REGEX = "\\[(\\p{IsAlphabetic}+)-([0-9]\\d*)]";

    public static void validateProductSelection(String input, Map<String, Integer> productInventory) {
        validateNotEmpty(input);
        String[] items = ParsingUtils.splitItems(input, ",");
        for (String item : items) {
            validateSingleItem(item, productInventory);
        }
    }

    private static void validateSingleItem(String item, Map<String, Integer> productInventory) {
        validateFormat(item);
        String productName = ParsingUtils.extractProductName(item);
        int quantity = ParsingUtils.extractQuantity(item);

        validateQuantityNotZero(quantity);
        validateProductExistence(productName, productInventory);
        validateStockAvailability(productName, quantity, productInventory);
    }

    private static void validateFormat(String item) {
        if (!isValidFormat(item)) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_FORMAT.getMessage());
        }
    }

    private static void validateQuantityNotZero(int quantity) {
        if (quantity == 0) {
            throw new IllegalArgumentException(ErrorMessage.ZERO_QUANTITY.getMessage());
        }
    }

    private static void validateProductExistence(String productName, Map<String, Integer> productInventory) {
        if (!productInventory.containsKey(productName)) {
            throw new IllegalArgumentException(ErrorMessage.NON_EXISTENT_PRODUCT.getMessage());
        }
    }

    private static void validateStockAvailability(String productName, int quantity,
                                                  Map<String, Integer> productInventory) {
        int stock = productInventory.getOrDefault(productName, 0);
        if (quantity > stock) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_QUANTITY.getMessage());
        }
    }

    private static boolean isValidFormat(String item) {
        return item.matches(ITEM_FORMAT_REGEX);
    }

    private static void validateNotEmpty(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(ErrorMessage.EMPTY_INPUT.getMessage());
        }
    }

    public static void validateYesOrNo(String input) {
        validateNotEmpty(input);
        if (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("N")) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_YES_NO.getMessage());
        }
    }

}
