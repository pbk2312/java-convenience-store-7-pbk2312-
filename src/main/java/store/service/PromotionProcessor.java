package store.service;

import store.handler.InputHandler;
import store.model.Order;
import store.model.Product;
import store.service.promotion.PromotionStrategy;

public class PromotionProcessor {
    private final StockManager stockManager;
    private final PromotionService promotionService;
    private final InputHandler inputHandler;

    public PromotionProcessor(StockManager stockManager, PromotionService promotionService, InputHandler inputHandler) {
        this.stockManager = stockManager;
        this.promotionService = promotionService;
        this.inputHandler = inputHandler;
    }


    public void applyPartialPromotion(Order order, Product promotionProduct, Product regularProduct, int quantity) {
        int promotionalStock = promotionProduct.getStock();
        PromotionStrategy strategy = promotionProduct.getPromotion().getStrategy();
        int fullPromotionQuantity = strategy.calculatePayableQuantity(quantity);

        int usedPromotionQuantity = Math.min(promotionalStock, quantity);
        applyPromotionalStock(order, promotionProduct, usedPromotionQuantity);

        int remainingQuantity = quantity - usedPromotionQuantity;
        applyNonPromotionalStock(order, regularProduct, remainingQuantity, quantity, fullPromotionQuantity);
    }

    private void applyPromotionalStock(Order order, Product promotionProduct, int usedPromotionQuantity) {
        if (usedPromotionQuantity > 0) {
            stockManager.deductStock(promotionProduct, usedPromotionQuantity);
            order.addProduct(promotionProduct, usedPromotionQuantity);
            promotionService.addFreeItems(order, promotionProduct, usedPromotionQuantity);
        }
    }

    private void applyNonPromotionalStock(Order order, Product regularProduct, int remainingQuantity,
                                          int requestedQuantity, int fullPromotionQuantity) {
        if (remainingQuantity > 0 && confirmPurchaseWithoutPromotion(regularProduct,
                requestedQuantity - fullPromotionQuantity)) {
            stockManager.deductStock(regularProduct, remainingQuantity);
            order.addProduct(regularProduct, remainingQuantity);
        }
    }

    private boolean confirmPurchaseWithoutPromotion(Product product, int remainingQuantity) {
        return inputHandler.confirmPurchaseWithoutPromotion(product, remainingQuantity);
    }

}
