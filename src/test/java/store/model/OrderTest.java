package store.model;

import static org.assertj.core.api.Assertions.assertThat;

import camp.nextstep.edu.missionutils.DateTimes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.service.ProductService;

public class OrderTest {

    private Order order;

    @BeforeEach
    public void setUp() {
        ProductService productService = new ProductService();
        order = new Order(productService);
    }

    @Test
    public void testAddProduct() {
        Product product = new Product("콜라", 1000.0, 10);
        order.addProduct(product, 3);
        assertThat(order.calculateTotal()).isEqualTo(3000.0);
    }

    @Test
    public void testCalculateTotal_WithPromotion() {
        Promotion promotion = new Promotion(
                new OnePlusOnePromotion(),
                DateTimes.now().toLocalDate().minusDays(1),
                DateTimes.now().toLocalDate().plusDays(1),
                "1+1"
        );
        Product product = new Product("콜라", 1000.0, 10, promotion);
        order.addProduct(product, 3);  // 1+1 프로모션

        double total = order.calculateTotal();
        assertThat(total).isEqualTo(2000.0);  // 2개 가격 적용됨
    }

    @Test
    public void testCalculateTotal_WithMembershipDiscount() {
        Product product = new Product("에너지바", 2000.0, 10);
        order.addProduct(product, 5);

        order.setMembership(true);
        double total = order.calculateTotal();
        assertThat(total).isEqualTo(7000.0);  // 10000 - 멤버십 할인 3000
    }

    @Test
    public void testCalculateTotal_WithMaxMembershipDiscount() {
        Product product = new Product("고가 제품", 5000.0, 10);
        order.addProduct(product, 10); // 총 50000원

        order.setMembership(true);
        double total = order.calculateTotal();
        assertThat(total).isEqualTo(42000.0);  // 최대 할인 8000원
    }

    @Test
    public void testCalculateTotal_WithPromotionAndMembershipDiscount() {
        Promotion promotion = new Promotion(
                new TwoPlusOnePromotion(),
                DateTimes.now().toLocalDate().minusDays(1),
                DateTimes.now().toLocalDate().plusDays(1),
                "2+1"
        );
        Product product = new Product("사이다", 1000.0, 10, promotion);
        order.addProduct(product, 6); // 4개 가격만 적용

        order.setMembership(true);
        double total = order.calculateTotal();
        assertThat(total).isEqualTo(2800.0);  // 4 * 1000 - 멤버십 할인
    }

    @Test
    public void testCalculateTotal_WithoutMembershipDiscount() {
        Product product = new Product("초코바", 1500.0, 5);
        order.addProduct(product, 3);

        order.setMembership(false);
        double total = order.calculateTotal();
        assertThat(total).isEqualTo(4500.0);  // 멤버십 할인 미적용
    }

}
