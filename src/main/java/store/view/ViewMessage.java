package store.view;

public enum ViewMessage {

    INPUT_PRODUCT_SELECTION("구매할 상품과 수량을 입력해 주세요. (예: [콜라-10],[사이다-3])"),
    INPUT_MEMBERSHIP_CHOICE("멤버십 할인을 받으시겠습니까? (Y/N)"),
    INPUT_ADDITIONAL_PURCHASE("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)"),
    PROMOTION_ADD("현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)"),
    PROMOTION_LACK("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)");

    private final String message;

    ViewMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
