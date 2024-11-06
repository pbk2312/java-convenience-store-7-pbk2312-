package store.validator;

import java.util.Map;
import store.view.ErrorMessage;

public class InputValidator {

    public static void validateProductSelection(String input) {
        if (!input.matches("\\[\\w+-\\d+](,\\[\\w+-\\d+])*")) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_FORMAT.getMessage());
        }
    }

    public static void validateProductExistence(String productName, Map<String, Integer> productInventory) {
        if (!productInventory.containsKey(productName)) {
            throw new IllegalArgumentException(ErrorMessage.NON_EXISTENT_PRODUCT.getMessage());
        }
    }

    public static void validateQuantity(String productName, int quantity, Map<String, Integer> productInventory) {
        int stock = productInventory.getOrDefault(productName, 0);
        if (quantity < 1 || quantity > stock) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_QUANTITY.getMessage());
        }
    }

    public static void validateYesOrNo(String input) {
        if (!input.equalsIgnoreCase("Y") && !input.equalsIgnoreCase("N")) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_YES_NO.getMessage());
        }
    }

}
