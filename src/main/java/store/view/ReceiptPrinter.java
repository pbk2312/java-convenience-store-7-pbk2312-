package store.view;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import store.model.Order;
import store.model.Product;
import store.model.ProductSummary;

public class ReceiptPrinter {

    private final NumberFormat numberFormat;

    public ReceiptPrinter() {
        this.numberFormat = NumberFormat.getNumberInstance(Locale.KOREA);
    }

    public void printReceipt(Order order) {
        System.out.println("==============W 편의점================");
        printPurchaseDetails(order);
        printFreeItemsSection(order);
        printAmountSummary(order);
        System.out.println("=====================================");
    }

    // 구매 내역을 출력하는 메서드
    private void printPurchaseDetails(Order order) {
        System.out.println("상품명\t\t수량\t금액");

        Map<String, ProductSummary> orderSummary = order.getAggregatedOrderSummary();

        // 가져온 요약 정보를 사용하여 출력
        orderSummary.forEach((name, summary) -> {
            System.out.printf("%s\t\t%d\t%s%n",
                    name,
                    summary.getTotalQuantity(),
                    formatPrice(summary.getTotalPrice()));
        });
    }

    // 증정품 섹션을 출력하는 메서드
    private void printFreeItemsSection(Order order) {
        Map<Product, Integer> freeItems = order.getFreeItems();
        if (!freeItems.isEmpty()) {
            System.out.println("=============증정===============");
            freeItems.forEach((product, quantity) ->
                    System.out.printf("%s\t\t%d%n", product.getName(), quantity)
            );
        }
    }

    private void printAmountSummary(Order order) {
        System.out.println("====================================");
        System.out.printf("총구매액\t\t%d\t%s%n", order.getTotalQuantity(), formatPrice(order.getTotalBeforeDiscount()));
        System.out.printf("행사할인\t\t\t-%s%n", formatPrice(order.getEventDiscount()));
        System.out.printf("멤버십할인\t\t\t-%s%n", formatPrice(order.getMembershipDiscount()));
        System.out.printf("내실돈\t\t\t %s%n", formatPrice(order.getFinalTotal()));
    }

    private String formatPrice(double price) {
        return numberFormat.format(price);
    }

}
