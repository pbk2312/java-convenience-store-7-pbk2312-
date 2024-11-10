package store.handler;

import store.model.Product;
import store.validator.InputValidator;
import store.view.ErrorMessage;
import store.view.InputView;
import store.view.OutputView;

public class InputHandler {

    private final InputView inputView;
    private final OutputView outputView;

    public InputHandler(InputView inputView, OutputView outputView) {
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public boolean confirmPurchaseWithoutPromotion(Product promotionProduct, int nonDiscountedQuantity) {
        while (true) {
            try {
                String userInput = inputView.inputPromotionLack(promotionProduct.getName(), nonDiscountedQuantity)
                        .trim();
                InputValidator.validateYesOrNo(userInput);

                if (userInput.equals("N")) {
                    throw new IllegalArgumentException(ErrorMessage.No_PURCHASE.getMessage());
                }
                return true;
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
    }

    public boolean confirmAddFreePromotionItem(Product promotionProduct, int freeQuantity) {
        while (true) {
            try {
                String userInput = inputView.inputPromotionAdd(promotionProduct.getName(), freeQuantity).trim();
                InputValidator.validateYesOrNo(userInput);
                if (userInput.equals("N")) {
                    throw new IllegalArgumentException(ErrorMessage.MUST_GET_ONE_MORE.getMessage());
                }
                return true;
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
    }

}
