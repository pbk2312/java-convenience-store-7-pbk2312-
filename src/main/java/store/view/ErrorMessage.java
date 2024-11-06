package store.view;

public enum ErrorMessage {

    INVALID_FORMAT("올바른 형식으로 입력해 주세요. 예: [콜라-10],[사이다-3]"),
    NON_EXISTENT_PRODUCT("존재하지 않는 상품입니다. 다시 입력해 주세요."),
    INVALID_QUANTITY("재고가 부족하거나 잘못된 수량입니다. 다시 입력해 주세요."),
    INVALID_YES_NO("Y 또는 N을 입력해 주세요.");

    private static final String ERROR_PREFIX = "[ERROR] ";
    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return ERROR_PREFIX + message;
    }

}
