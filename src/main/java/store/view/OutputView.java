package store.view;

import java.util.List;
import store.model.Product;

public class OutputView {

    public void displayWelcomeMessage() {
        System.out.print(ViewMessage.WELCOME_MESSAGE.getMessage());
    }

    public void displayProducts(List<Product> products) {
        displayWelcomeMessage();
        products.forEach(product -> System.out.println(formatProductInfo(product)));
    }

    private String formatProductInfo(Product product) {
        String promotionInfo = product.getPromotion() != null ? product.getPromotion().getDescription() : "";
        String stockInfo = product.getStock() > 0 ? product.getStock() + "개" : "재고 없음";
        return String.format("- %s %,d원 %s%s", product.getName(), (int) product.getPrice(), stockInfo,
                promotionInfo.isEmpty() ? "" : " " + promotionInfo);
    }

    public void displayMessage(ViewMessage message) {
        System.out.println(message.getMessage());
    }

    public void displayFormattedMessage(ViewMessage message, Object... args) {
        System.out.printf(message.getMessage(), args);
        System.out.println();
    }

}
