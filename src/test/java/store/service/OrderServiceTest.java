package store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.model.Inventory;
import store.model.Order;
import store.model.Product;
import store.view.ErrorMessage;

public class OrderServiceTest {

    private Inventory inventory;
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        ProductService productService = new ProductService();
        PricingService pricingService = new PricingService();

        inventory = Inventory.getInstance();
        inventory.getProductList().clear();
        orderService = new OrderService(inventory, productService, pricingService);

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

        double finalTotal = orderService.calculateFinalTotal(order);
        assertThat(finalTotal).isEqualTo(3000.0);
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
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(ErrorMessage.INVALID_QUANTITY.getMessage());
    }

    @Test
    public void testApplyMembershipDiscount() {
        Order order = orderService.createOrder();
        orderService.addProductToOrder(order, "우아한 콜라", 5);

        orderService.applyMembershipDiscount(order, true);
        double finalTotal = orderService.calculateFinalTotal(order);
        assertThat(finalTotal).isEqualTo(3500.0); // 5000 - 1500 (30% discount)
    }

    @Test
    public void testCalculateFinalTotal() {
        Order order = orderService.createOrder();
        orderService.addProductToOrder(order, "우아한 콜라", 3);
        orderService.addProductToOrder(order, "우아한 사이다", 2);

        double finalTotal = orderService.calculateFinalTotal(order);
        assertThat(finalTotal).isEqualTo(5000.0); // 3000 + 2000
    }


}
