package store.config;

import store.controller.StoreController;
import store.handler.InputHandler;
import store.handler.OrderHandler;
import store.loader.InventoryLoader;
import store.model.Inventory;
import store.service.MembershipDiscountCalculator;
import store.service.OrderService;
import store.service.PricingService;
import store.service.ProductService;
import store.service.PromotionProcessor;
import store.service.PromotionService;
import store.service.StockManager;
import store.validator.StockValidator;
import store.view.InputView;
import store.view.OutputView;

public class AppConfig {

    public Inventory inventory() {
        Inventory inventory = Inventory.getInstance();
        inventory.clear();
        InventoryLoader loader = InventoryLoader.getInstance();
        loader.loadProducts(inventory, loader.loadPromotions());
        return inventory;
    }

    public InputHandler inputHandler() {
        return new InputHandler(inputView(), outputView());
    }

    public ProductService productService() {
        return new ProductService();
    }

    public PricingService pricingService() {
        return new PricingService();
    }

    public StockManager stockManager() {
        return new StockManager(inventory(), productService());
    }

    public PromotionService promotionService() {
        return new PromotionService(productService());
    }

    public OrderService orderService() {
        return new OrderService(
                stockManager(),
                promotionProcessor(),
                stockValidator(),
                pricingService(),
                membershipDiscountCalculator(), inputHandler()
        );
    }


    public PromotionProcessor promotionProcessor() {
        return new PromotionProcessor(stockManager(), promotionService(), inputHandler());
    }

    public StockValidator stockValidator() {
        return new StockValidator();
    }

    public MembershipDiscountCalculator membershipDiscountCalculator() {
        return new MembershipDiscountCalculator();
    }

    public InputView inputView() {
        return new InputView();
    }

    public OutputView outputView() {
        return new OutputView();
    }

    public OrderHandler orderHandler() {
        return new OrderHandler(inputView(), orderService(), outputView());
    }

    public StoreController createStoreController() {
        return new StoreController(inventory(), orderHandler(), orderService(), outputView());
    }


}
