package store.view;

import java.text.NumberFormat;
import java.util.Locale;
import store.model.Inventory;
import store.model.Order;
import store.model.Product;

public class OutputView {

    private final ReceiptPrinter receiptPrinter;

    public OutputView() {
        this.receiptPrinter = new ReceiptPrinter();
    }

    public void displayWelcomeMessage() {
        System.out.println(ViewMessage.WELCOME_MESSAGE.getMessage());
    }

    public void displayProductList(Inventory inventory) {
        displayWelcomeMessage();
        inventory.getProductList().forEach(this::displayProduct);
    }

    private void displayProduct(Product product) {
        System.out.printf("- %s %s원 %s%s%n",
                product.getName(),
                formatPrice(product.getPrice()),
                getStockText(product),
                getPromotionText(product));
    }

    private String formatPrice(double price) {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(price);
    }

    private String getPromotionText(Product product) {
        return product.getPromotion() != null ? " " + product.getPromotion().getDescription() : "";
    }

    private String getStockText(Product product) {
        return product.getStock() > 0 ? product.getStock() + "개" : "재고 없음";
    }

    public void printErrorMessage(String errorMessage) {
        System.out.println(errorMessage);
    }

    // 영수증 출력을 ReceiptPrinter에서 불러옴
    public void printReceipt(Order order) {
        receiptPrinter.printReceipt(order);
    }

}
