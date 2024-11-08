package store.view;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import store.model.Inventory;
import store.model.Order;
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


    private String formatPrice(double price) {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(price);
    }

    public void printErrorMessage(String errorMessage) {
        System.out.println(errorMessage);
    }

    public void printReceipt(Order order) {
        System.out.println("==============W 편의점================");
        printPurchaseDetails(order);
        printFreeItems(order);
        printAmountInformation(order);
        System.out.println("=====================================");
    }

    private void printPurchaseDetails(Order order) {
        System.out.println("상품명\t\t수량\t금액");
        for (Map.Entry<Product, Integer> entry : order.getOrderedProducts().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            System.out.printf("%s\t\t%d\t%s%n",
                    product.getName(),
                    quantity,
                    formatPrice(product.getPrice() * quantity)
            );
        }
    }

    private void printFreeItems(Order order) {
        Map<Product, Integer> freeItems = order.getFreeItems();
        if (!freeItems.isEmpty()) {
            System.out.println("=============증	정===============");
            for (Map.Entry<Product, Integer> entry : freeItems.entrySet()) {
                System.out.printf("%s\t\t%d%n", entry.getKey().getName(), entry.getValue());
            }
        }
    }

    private void printAmountInformation(Order order) {
        System.out.println("====================================");
        System.out.printf("총구매액\t\t%d\t%s%n", order.getTotalQuantity(), formatPrice(order.getTotalBeforeDiscount()));
        System.out.printf("행사할인\t\t\t-%s%n", formatPrice(order.getEventDiscount()));
        System.out.printf("멤버십할인\t\t\t-%s%n", formatPrice(order.getMembershipDiscount()));
        System.out.printf("내실돈\t\t\t %s%n", formatPrice(order.getFinalTotal()));
    }

}
