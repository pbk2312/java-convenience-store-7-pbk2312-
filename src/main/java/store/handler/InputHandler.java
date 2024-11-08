package store.handler;

import store.model.Order;
import store.service.OrderService;
import store.util.ParsingUtils;
import store.validator.InputValidator;
import store.view.InputView;
import store.view.OutputView;

public class InputHandler {

    private final InputView inputView;
    private final OrderService orderService;
    private final OutputView outputView;

    public InputHandler(InputView inputView, OrderService orderService, OutputView outputView) {
        this.inputView = inputView;
        this.orderService = orderService;
        this.outputView = outputView;
    }

    public void handleProductSelection(Order order) {
        while (true) {
            try {
                String input = inputView.inputProductSelection();
                InputValidator.validateProductSelectionFormat(input);
                addProductsToOrder(order, input);
                break;
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void addProductsToOrder(Order order, String input) {
        String[] products = ParsingUtils.splitProducts(input, ",");
        for (String productInfo : products) {
            String productName = ParsingUtils.extractProductName(productInfo);
            int quantity = ParsingUtils.extractQuantity(productInfo);
            try {
                orderService.addProductToOrder(order, productName, quantity);
            } catch (IllegalStateException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
    }


    public void handleMembershipChoice(Order order) {
        while (true) {
            try {
                String input = inputView.inputMembershipChoice();
                InputValidator.validateYesOrNo(input);
                orderService.applyMembershipDiscount(order, input.equals("Y"));
                break;
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
    }

    public boolean handleAdditionalPurchase() {
        while (true) {
            try {
                String input = inputView.inputAdditionalPurchase();
                InputValidator.validateYesOrNo(input);
                return input.equals("Y");
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
    }

}

