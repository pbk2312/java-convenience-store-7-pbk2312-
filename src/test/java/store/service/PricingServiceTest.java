package store.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import store.model.Product;
import store.model.Promotion;
import store.service.promotion.OnePlusOnePromotion;
import store.service.promotion.PromotionStrategy;
import store.service.promotion.TwoPlusOnePromotion;

public class PricingServiceTest {

    private PricingService pricingService;
    private Product productWithoutPromotion;
    private Product productWithPromotion;
    private Promotion inactivePromotion;

    @BeforeEach
    public void setUp() {
        // given
        pricingService = new PricingService();
        productWithoutPromotion = new Product("Regular Product", 1000.0, 10, null);

        PromotionStrategy onePlusOneStrategy = new OnePlusOnePromotion();
        Promotion activePromotion = new Promotion(onePlusOneStrategy, LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "1+1 Promotion");
        productWithPromotion = new Product("Promoted Product", 1000.0, 10, activePromotion);

        inactivePromotion = new Promotion(onePlusOneStrategy, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31),
                "Inactive Promotion");
    }

    @Test
    public void testCalculateFinalPriceWithoutPromotion() {
        // when
        double finalPrice = pricingService.calculateFinalPrice(productWithoutPromotion, 3);

        // then
        assertThat(finalPrice).isEqualTo(1000.0 * 3);
    }

    @Test
    public void testCalculateFinalPriceWithActivePromotion() {
        // when
        double finalPrice = pricingService.calculateFinalPrice(productWithPromotion, 2);

        // then
        assertThat(finalPrice).isEqualTo(1000.0); // 1+1 적용 시 2개 중 1개만 결제
    }

    @Test
    public void testCalculateFinalPriceWithInactivePromotion() {
        // given
        Product productWithInactivePromotion = new Product("Inactive Promotion Product", 1000.0, 10, inactivePromotion);

        // when
        double finalPrice = pricingService.calculateFinalPrice(productWithInactivePromotion, 2);

        // then
        assertThat(finalPrice).isEqualTo(1000.0 * 2); // 비활성 프로모션이므로 할인이 적용되지 않음
    }

    @Test
    public void testCalculateFinalPriceWithTwoPlusOnePromotion() {
        // given
        PromotionStrategy twoPlusOneStrategy = new TwoPlusOnePromotion();
        Promotion twoPlusOnePromotion = new Promotion(twoPlusOneStrategy, LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31), "2+1 Promotion");
        Product productWithTwoPlusOnePromotion = new Product("2+1 Product", 1000.0, 10, twoPlusOnePromotion);

        // when
        double finalPrice = pricingService.calculateFinalPrice(productWithTwoPlusOnePromotion, 3);

        // then
        assertThat(finalPrice).isEqualTo(2000.0); // 2+1 적용 시 3개 중 2개만 결제
    }

}
