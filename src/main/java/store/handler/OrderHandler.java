package store.handler;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import store.model.Order;
import store.service.OrderService;
import store.util.ParsingUtils;
import store.validator.InputValidator;
import store.view.InputView;
import store.view.OutputView;

public class OrderHandler {

    private static final String YES = "Y";

    private final InputView inputView;
    private final OrderService orderService;
    private final OutputView outputView;

    public OrderHandler(InputView inputView, OrderService orderService, OutputView outputView) {
        this.inputView = inputView;
        this.orderService = orderService;
        this.outputView = outputView;
    }

    public void processProductSelection(Order order) {
        processWithValidation(inputView::inputProductSelection, input -> addProductsToOrder(order, input));
    }

    private void addProductsToOrder(Order order, String input) {
        InputValidator.validateProductSelectionFormat(input);
        Arrays.stream(ParsingUtils.splitProducts(input, ","))
                .forEach(productInfo -> attemptToAddSingleProduct(order, productInfo));
    }

    private void attemptToAddSingleProduct(Order order, String productInfo) {
        String productName = ParsingUtils.extractProductName(productInfo);
        int quantity = ParsingUtils.extractQuantity(productInfo);

        try {
            orderService.addProductToOrder(order, productName, quantity);
        } catch (IllegalArgumentException e) {
            outputView.printErrorMessage(e.getMessage());
        }
    }

    public void processMembershipChoice(Order order) {
        processWithValidation(inputView::inputMembershipChoice, input -> applyMembershipDiscount(order, input));
    }

    private void applyMembershipDiscount(Order order, String input) {
        InputValidator.validateYesOrNo(input);
        orderService.applyMembershipDiscount(order, input.equalsIgnoreCase(YES));
    }

    public boolean confirmAdditionalPurchase() {
        return processWithValidationAndReturn(inputView::inputAdditionalPurchase, this::convertYesOrNoToBoolean);
    }

    private boolean convertYesOrNoToBoolean(String input) {
        InputValidator.validateYesOrNo(input);
        return input.equalsIgnoreCase(YES);
    }

    private <T> T processWithValidationAndReturn(Supplier<String> inputSupplier,
                                                 Function<String, T> validationFunction) {
        while (true) {
            try {
                String input = promptForInput(inputSupplier);
                return validationFunction.apply(input);
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void processWithValidation(Supplier<String> inputSupplier, Consumer<String> validationFunction) {
        while (true) {
            try {
                String input = promptForInput(inputSupplier);
                validationFunction.accept(input);
                break;
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private String promptForInput(Supplier<String> inputSupplier) {
        String input = inputSupplier.get();
        InputValidator.validateNotEmpty(input);
        return input;
    }

}
