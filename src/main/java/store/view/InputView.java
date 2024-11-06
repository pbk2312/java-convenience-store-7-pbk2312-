package store.view;

import camp.nextstep.edu.missionutils.Console;

public class InputView {

    public String inputProductSelection() {
        return readInput(ViewMessage.INPUT_PRODUCT_SELECTION.getMessage());
    }

    public String inputMembershipChoice() {
        return readInput(ViewMessage.INPUT_MEMBERSHIP_CHOICE.getMessage());
    }

    public String inputAdditionalPurchase() {
        return readInput(ViewMessage.INPUT_ADDITIONAL_PURCHASE.getMessage());
    }

    public String inputPromotionAdd(String productName, int quantity) {
        String message = String.format(ViewMessage.PROMOTION_ADD.getMessage(), productName, quantity);
        return readInput(message);
    }

    public String inputPromotionLack(String productName, int quantity) {
        String message = String.format(ViewMessage.PROMOTION_LACK.getMessage(), productName, quantity);
        return readInput(message);
    }


    // 콘솔 자원 해제
    public void close() {
        Console.close();
    }

    private String readInput(String message) {
        System.out.println(message);
        return Console.readLine();
    }
}
