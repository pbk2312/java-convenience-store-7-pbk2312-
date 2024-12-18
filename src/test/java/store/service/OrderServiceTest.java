package store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.handler.InputHandler;
import store.model.Inventory;
import store.model.Order;
import store.model.Product;
import store.validator.StockValidator;
import store.view.ErrorMessage;
import store.view.InputView;
import store.view.OutputView;

public class OrderServiceTest {

    private Inventory inventory;
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        inventory = Inventory.getInstance();
        inventory.getProductList().clear();

        ProductService productService = new ProductService();
        StockManager stockManager = new StockManager(inventory, productService);
        PromotionService promotionService = new PromotionService(productService);
        PromotionProcessor promotionProcessor = new PromotionProcessor(stockManager, promotionService,
                new InputHandler(new InputView(), new OutputView()));
        StockValidator stockValidator = new StockValidator();
        PricingService pricingService = new PricingService();
        MembershipDiscountCalculator membershipDiscountCalculator = new MembershipDiscountCalculator();

        orderService = new OrderService(
                stockManager,
                promotionProcessor,
                stockValidator,
                pricingService,
                membershipDiscountCalculator,
                new InputHandler(new InputView(), new OutputView()));

        inventory.addProduct(new Product("우아한 콜라", 1000.0, 10));
        inventory.addProduct(new Product("우아한 사이다", 1000.0, 5));
    }

    @Test
    public void testCreateOrder() {
        Order order = orderService.createOrder();
        assertThat(order).isNotNull();
    }

    @Test
    public void testAddProductToOrder_SuccessfulAddition() {
        Order order = orderService.createOrder();
        orderService.addProductToOrder(order, "우아한 콜라", 3);

        orderService.calculateFinalTotal(order);
        assertThat(order.getFinalTotal()).isEqualTo(3000.0);
        assertThat(inventory.getProductList().stream()
                .filter(product -> product.getName().equals("우아한 콜라"))
                .findFirst().get().getStock()).isEqualTo(7);
    }

    @Test
    public void testAddProductToOrder_ProductNotFound_ShouldThrowException() {
        Order order = orderService.createOrder();
        assertThatThrownBy(() -> orderService.addProductToOrder(order, "없는상품", 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(ErrorMessage.NON_EXISTENT_PRODUCT.getMessage());
    }

    @Test
    public void testAddProductToOrder_InsufficientStock_ShouldThrowException() {
        Order order = orderService.createOrder();
        assertThatThrownBy(() -> orderService.addProductToOrder(order, "우아한 사이다", 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(ErrorMessage.INVALID_QUANTITY.getMessage());
    }

    @Test
    public void testApplyMembershipDiscount() {
        Order order = orderService.createOrder();
        orderService.addProductToOrder(order, "우아한 콜라", 5);

        orderService.applyMembershipDiscount(order, true);
        orderService.calculateFinalTotal(order);
        assertThat(order.getFinalTotal()).isEqualTo(3500.0);
    }

    @Test
    public void testCalculateFinalTotal() {
        Order order = orderService.createOrder();
        orderService.addProductToOrder(order, "우아한 콜라", 3);
        orderService.addProductToOrder(order, "우아한 사이다", 2);

        orderService.calculateFinalTotal(order);
        assertThat(order.getFinalTotal()).isEqualTo(5000.0);
    }

}
