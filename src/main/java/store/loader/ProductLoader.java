package store.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import store.model.OnePlusOnePromotion;
import store.model.Product;
import store.model.Promotion;
import store.model.TwoPlusOnePromotion;
import store.util.ParsingUtils;
import store.view.ErrorMessage;

public class ProductLoader {

    private static final String PRODUCT_FILE_PATH = "src/main/resources/products.md";
    private static final String PROMOTION_FILE_PATH = "src/main/resources/promotions.md";

    public List<Product> loadProducts(Map<String, Promotion> promotions) {
        List<Product> products = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PRODUCT_FILE_PATH))) {
            br.lines().skip(1).forEach(line -> products.add(parseProduct(line, promotions)));
        } catch (IOException e) {
            logError(ErrorMessage.PRODUCT_LOAD_ERROR, e);
        }
        return products;
    }

    public Map<String, Promotion> loadPromotions() {
        Map<String, Promotion> promotions = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PROMOTION_FILE_PATH))) {
            br.lines().skip(1).forEach(line -> addPromotion(line, promotions));
        } catch (IOException e) {
            logError(ErrorMessage.PROMOTION_LOAD_ERROR, e);
        }
        return promotions;
    }

    private Product parseProduct(String line, Map<String, Promotion> promotions) {
        String[] parts = ParsingUtils.splitProducts(line, ",");
        String name = parts[0];
        double price = ParsingUtils.parseDouble(parts[1]);
        int quantity = ParsingUtils.parseInt(parts[2]);
        Promotion promotion = ParsingUtils.isNullValue(parts[3]) ? null : promotions.get(parts[3]);
        return new Product(name, price, quantity, promotion);
    }

    private void addPromotion(String line, Map<String, Promotion> promotions) {
        String[] parts = ParsingUtils.splitProducts(line, ",");
        String name = parts[0];
        int buy = ParsingUtils.parseInt(parts[1]);
        int get = ParsingUtils.parseInt(parts[2]);
        LocalDate startDate = ParsingUtils.parseLocalDate(parts[3]);
        LocalDate endDate = ParsingUtils.parseLocalDate(parts[4]);
        promotions.put(name, createPromotion(buy, get, startDate, endDate));
    }

    private Promotion createPromotion(int buy, int get, LocalDate startDate, LocalDate endDate) {
        if (buy == 1 && get == 1) {
            return new Promotion(new OnePlusOnePromotion(), startDate, endDate);
        }
        if (buy == 2 && get == 1) {
            return new Promotion(new TwoPlusOnePromotion(), startDate, endDate);
        }
        return null;
    }

    private void logError(ErrorMessage errorMessage, IOException e) {
        System.err.println(errorMessage.getMessage() + ": " + e.getMessage());
    }

}
