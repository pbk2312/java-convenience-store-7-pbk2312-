package store.util;

public class ParsingUtils {

    // 상품 분리
    public static String[] splitProducts(String input, String delimiter) {
        return input.split(delimiter);
    }

    // 상품명 추출
    public static String extractProductName(String item) {
        return item.substring(1, item.indexOf('-'));
    }

    // 수량 추출
    public static int extractQuantity(String item) {
        return Integer.parseInt(item.substring(item.indexOf('-') + 1, item.length() - 1));
    }

    // 인스턴스화 방지
    private ParsingUtils() {
    }

}
