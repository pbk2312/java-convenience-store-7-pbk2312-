package store.service;

import java.util.Optional;
import store.model.Inventory;
import store.model.Order;
import store.model.Product;
import store.view.ErrorMessage;
import store.view.InputView;

public class OrderService {

    private final Inventory inventory;
    private final ProductService productService;
    private final PricingService pricingService;
    private final InputView inputView;
    private static final double MEMBERSHIP_DISCOUNT_RATE = 0.3;
    private static final double MAX_MEMBERSHIP_DISCOUNT = 8000.0;

    public OrderService(Inventory inventory, ProductService productService, PricingService pricingService,
                        InputView inputView) {
        this.inventory = inventory;
        this.productService = productService;
        this.pricingService = pricingService;
        this.inputView = inputView;
    }

    public Order createOrder() {
        return new Order();
    }

    public void addProductToOrder(Order order, String productName, int quantity, InputView inputView) {
        // 일반 재고와 프로모션 재고를 인벤토리에서 각각 가져옴
        Optional<Product> promotionProductOpt = inventory.getPromotionProductByName(productName);
        Product regularProduct = inventory.getRegularProductByName(productName)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.NON_EXISTENT_PRODUCT.getMessage()));

        int totalAvailableStock = promotionProductOpt.map(Product::getStock).orElse(0) + regularProduct.getStock();

        // 전체 재고가 요청한 수량보다 적으면 예외 발생
        if (totalAvailableStock < quantity) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_QUANTITY.getMessage());
        }

        if (promotionProductOpt.isEmpty()) {
            productService.deductStock(regularProduct, quantity);
            order.addProduct(regularProduct, quantity);
        } else {
            Product promotionProduct = promotionProductOpt.get();
            int promotionalStock = promotionProduct.getStock();

            if (promotionalStock < quantity) {
                int remainingQuantity = quantity - promotionalStock;

                // InputView를 사용하여 사용자에게 정가 결제 여부를 묻는 메시지 출력
                String userResponse = inputView.inputPromotionLack(productName, remainingQuantity).trim();

                if (userResponse.equalsIgnoreCase("Y")) {
                    if (promotionalStock > 0) {
                        productService.deductStock(promotionProduct, promotionalStock);
                        order.addProduct(promotionProduct, promotionalStock);
                        addFreeItemsToOrder(order, promotionProduct, promotionalStock);
                    }
                    productService.deductStock(regularProduct, remainingQuantity);
                    order.addProduct(regularProduct, remainingQuantity);
                } else {
                    productService.deductStock(promotionProduct, promotionalStock);
                    order.addProduct(promotionProduct, promotionalStock);
                    addFreeItemsToOrder(order, promotionProduct, promotionalStock);
                }
            } else {
                productService.deductStock(promotionProduct, quantity);
                order.addProduct(promotionProduct, quantity);
                addFreeItemsToOrder(order, promotionProduct, quantity);
            }
        }

        inventory.adjustProductStock(regularProduct);
        promotionProductOpt.ifPresent(inventory::adjustProductStock);
    }


    private void addFreeItemsToOrder(Order order, Product product, int quantity) {
        int freeQuantity = productService.calculateFreeItems(product, quantity);
        if (freeQuantity > 0) {
            order.getFreeItems().put(product, freeQuantity);
        }
    }

    public void applyMembershipDiscount(Order order, boolean isMembership) {
        order.setMembership(isMembership);
    }

    public double calculateFinalTotal(Order order) {
        double eventDiscount = calculateEventDiscount(order);
        double membershipDiscount = calculateMembershipDiscount(order.getTotalBeforeDiscount() - eventDiscount,
                order.isMembership());
        return finalizeTotal(order, eventDiscount, membershipDiscount);
    }

    private double calculateEventDiscount(Order order) {
        return order.getOrderedProducts().entrySet().stream()
                .mapToDouble(entry -> calculateProductDiscount(entry.getKey(), entry.getValue()))
                .sum();
    }

    private double calculateProductDiscount(Product product, int quantity) {
        double originalPrice = product.getPrice() * quantity;
        double discountedPrice = pricingService.calculateFinalPrice(product, quantity);
        return originalPrice - discountedPrice;
    }

    private double calculateMembershipDiscount(double amountAfterEventDiscount, boolean isMembership) {
        if (!isMembership) {
            return 0.0;
        }
        double discount = amountAfterEventDiscount * MEMBERSHIP_DISCOUNT_RATE;
        return Math.min(discount, MAX_MEMBERSHIP_DISCOUNT);
    }

    private double finalizeTotal(Order order, double eventDiscount, double membershipDiscount) {
        order.setEventDiscount(eventDiscount);
        order.setMembershipDiscount(membershipDiscount);
        double finalTotal = order.getTotalBeforeDiscount() - eventDiscount - membershipDiscount;
        order.setFinalTotal(finalTotal);
        return finalTotal;
    }


}
