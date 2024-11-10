package store.controller;

import store.handler.OrderHandler;
import store.model.Inventory;
import store.model.Order;
import store.service.OrderService;
import store.view.OutputView;

public class StoreController {
    private final OrderHandler orderHandler;
    private final OrderService orderService;
    private final OutputView outputView;
    private final Inventory inventory;

    public StoreController(Inventory inventory, OrderHandler orderHandler, OrderService orderService,
                           OutputView outputView) {
        this.inventory = inventory;
        this.orderHandler = orderHandler;
        this.orderService = orderService;
        this.outputView = outputView;
    }

    public void start() {
        // 상품 목록과 프로모션 정보를 화면에 출력
        outputView.displayProductList(inventory);

        // 첫 번째 주문 생성 및 처리
        Order order = orderService.createOrder();
        processOrder(order);

        // 추가 구매 여부 확인 및 반복 처리
        boolean additionalPurchase = orderHandler.confirmAdditionalPurchase();
        while (additionalPurchase) {
            outputView.displayProductList(inventory);

            // 추가 주문 생성 및 처리
            order = orderService.createOrder();
            processOrder(order);

            additionalPurchase = orderHandler.confirmAdditionalPurchase();
        }
    }

    private void processOrder(Order order) {
        // 주문에 상품 추가
        orderHandler.processProductSelection(order);

        // 멤버십 할인 적용 여부 확인
        orderHandler.processMembershipChoice(order);

        // 최종 결제 금액 계산 및 영수증 출력
        orderService.calculateFinalTotal(order);
        outputView.printReceipt(order);
    }

}
