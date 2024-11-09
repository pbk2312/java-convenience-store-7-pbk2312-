package store.handler;

import store.model.Product;
import store.validator.InputValidator;
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
                return userInput.equals("Y");
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
    }

}
