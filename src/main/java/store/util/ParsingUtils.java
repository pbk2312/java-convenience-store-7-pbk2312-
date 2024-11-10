package store.util;

import java.time.LocalDate;

public class ParsingUtils {

    public static String[] splitProducts(String input, String delimiter) {
        return input.split(delimiter);
    }

    public static String extractProductName(String product) {
        return product.substring(1, product.indexOf('-'));
    }

    public static int extractQuantity(String product) {
        return Integer.parseInt(product.substring(product.indexOf('-') + 1, product.length() - 1));
    }

    public static double parseDouble(String value) {
        return Double.parseDouble(value);
    }

    public static int parseInt(String value) {
        return Integer.parseInt(value);
    }

    public static LocalDate parseLocalDate(String date) {
        return LocalDate.parse(date);
    }
    
    // 인스턴스화 방지
    private ParsingUtils() {
    }

}
