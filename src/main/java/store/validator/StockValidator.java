package store.validator;


import store.model.Product;
import store.view.ErrorMessage;

public class StockValidator {
    public void validateStockAvailability(Product promotionProduct, Product regularProduct, int quantity) {
        int promotionalStock = promotionProduct != null ? promotionProduct.getStock() : 0;
        int totalAvailableStock = promotionalStock + regularProduct.getStock();

        if (totalAvailableStock < quantity) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_QUANTITY.getMessage());
        }
    }

}
