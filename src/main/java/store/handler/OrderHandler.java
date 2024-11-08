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

    private final InputView inputView;
    private final OrderService orderService;
    private final OutputView outputView;

    public OrderHandler(InputView inputView, OrderService orderService, OutputView outputView) {
        this.inputView = inputView;
        this.orderService = orderService;
        this.outputView = outputView;
    }

    
    public void processProductSelection(Order order) {
        executeWithValidation(inputView::inputProductSelection, input -> validateAndAddProductsToOrder(order, input));
    }


    private void validateAndAddProductsToOrder(Order order, String input) {
        InputValidator.validateProductSelectionFormat(input);
        Arrays.stream(ParsingUtils.splitProducts(input, ","))
                .forEach(productInfo -> addSingleProductToOrder(order, productInfo));
    }


    private void addSingleProductToOrder(Order order, String productInfo) {
        String productName = ParsingUtils.extractProductName(productInfo);
        int quantity = ParsingUtils.extractQuantity(productInfo);
        try {
            orderService.addProductToOrder(order, productName, quantity);
        } catch (IllegalStateException e) {
            outputView.printErrorMessage(e.getMessage());
        }
    }


    public void processMembershipChoice(Order order) {
        executeWithValidation(inputView::inputMembershipChoice, input -> applyMembershipDiscountIfValid(order, input));
    }


    private void applyMembershipDiscountIfValid(Order order, String input) {
        InputValidator.validateYesOrNo(input);
        orderService.applyMembershipDiscount(order, input.equalsIgnoreCase("Y"));
    }


    public boolean confirmAdditionalPurchase() {
        return executeWithValidationAndReturn(inputView::inputAdditionalPurchase, this::convertYesOrNoToBoolean);
    }


    private boolean convertYesOrNoToBoolean(String input) {
        InputValidator.validateYesOrNo(input);
        return input.equalsIgnoreCase("Y");
    }


    private <T> T executeWithValidationAndReturn(Supplier<String> inputSupplier,
                                                 Function<String, T> validationAndProcessing) {
        while (true) {
            try {
                String input = promptAndValidateInput(inputSupplier);
                return validationAndProcessing.apply(input);
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void executeWithValidation(Supplier<String> inputSupplier, Consumer<String> validationAndProcessing) {
        while (true) {
            try {
                String input = promptAndValidateInput(inputSupplier);
                validationAndProcessing.accept(input);
                return;
            } catch (IllegalArgumentException e) {
                outputView.printErrorMessage(e.getMessage());
            }
        }
    }


    private String promptAndValidateInput(Supplier<String> inputSupplier) {
        String input = inputSupplier.get();
        InputValidator.validateNotEmpty(input);
        return input;
    }

}
