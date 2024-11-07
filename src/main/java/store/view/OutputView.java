package store.view;

import java.text.NumberFormat;
import java.util.Locale;
import store.model.Inventory;
import store.model.Product;

public class OutputView {

    public void displayWelcomeMessage() {
        System.out.println(ViewMessage.WELCOME_MESSAGE.getMessage());
    }

    public void displayProductList(Inventory inventory) {
        displayWelcomeMessage();
        inventory.getProductList().forEach(this::displayProduct);
    }

    private void displayProduct(Product product) {
        String formattedPrice = formatPrice(product.getPrice());
        String promotionText = product.getPromotion() != null ? " " + product.getPromotion().getDescription() : "";
        String stockText = product.getStock() > 0 ? product.getStock() + "개" : "재고 없음";

        System.out.printf("- %s %s원 %s%s%n",
                product.getName(),
                formattedPrice,
                stockText,
                promotionText);
    }

    public void promptProductSelection() {
        System.out.println(ViewMessage.INPUT_PRODUCT_SELECTION.getMessage());
    }

    public void promptMembershipChoice() {
        System.out.println(ViewMessage.INPUT_MEMBERSHIP_CHOICE.getMessage());
    }

    public void promptAdditionalPurchase() {
        System.out.println(ViewMessage.INPUT_ADDITIONAL_PURCHASE.getMessage());
    }

    public void promptPromotionAdd(String productName, int additionalQuantity) {
        System.out.printf(ViewMessage.PROMOTION_ADD.getMessage(), productName, additionalQuantity);
    }

    public void promptPromotionLack(String productName, int quantity) {
        System.out.printf(ViewMessage.PROMOTION_LACK.getMessage(), productName, quantity);
    }

    private String formatPrice(double price) {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(price);
    }

}
