package store.view;

public enum ErrorMessage {

    INVALID_FORMAT("올바른 형식으로 입력해 주세요. 예: [콜라-10],[사이다-3]"),
    NON_EXISTENT_PRODUCT("존재하지 않는 상품입니다. 다시 입력해 주세요."),
    INVALID_QUANTITY("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요."),
    INVALID_YES_NO("Y 또는 N을 입력해 주세요."),
    EMPTY_INPUT("입력이 비어 있습니다. 값을 입력해 주세요."),
    ZERO_QUANTITY("수량은 0보다 커야 합니다."),
    PRODUCT_LOAD_ERROR("제품 정보를 불러오는 중 오류가 발생했습니다."),
    PROMOTION_LOAD_ERROR("프로모션 정보를 불러오는 중 오류가 발생했습니다."),
    INVALID_PROMOTION_DATES("프로모션 시작일은 종료일보다 이전이어야 합니다."),
    No_PURCHASE("이 기회를 놓치면 다시는 안 와! 꼭 잡아야 해!"),
    MUST_GET_ONE_MORE("안돼! 넌 무조건 1개 더받아야해!");
    private static final String ERROR_PREFIX = "[ERROR] ";
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return ERROR_PREFIX + message;
    }

}
