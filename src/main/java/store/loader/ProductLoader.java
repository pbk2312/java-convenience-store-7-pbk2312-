package store.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import store.model.FlashSalePromotion;
import store.model.Inventory;
import store.model.OnePlusOnePromotion;
import store.model.Product;
import store.model.Promotion;
import store.model.TwoPlusOnePromotion;
import store.util.ParsingUtils;
import store.view.ErrorMessage;

public class ProductLoader {

    private static final String PRODUCT_FILE_PATH = "src/main/resources/products.md";
    private static final String PROMOTION_FILE_PATH = "src/main/resources/promotions.md";

    public void loadProducts(Inventory inventory, Map<String, Promotion> promotions) {
        Map<String, Integer> productCount = countProductOccurrences();
        processFile(PRODUCT_FILE_PATH, line -> addProductToInventory(inventory, promotions, productCount, line));
    }

    private void addProductToInventory(Inventory inventory, Map<String, Promotion> promotions,
                                       Map<String, Integer> productCount, String line) {
        Product product = parseProduct(line, promotions);
        inventory.addProduct(product);
        addZeroStockProductIfNeeded(inventory, product, productCount);
    }

    private Map<String, Integer> countProductOccurrences() {
        Map<String, Integer> productCount = new HashMap<>();
        processFile(PRODUCT_FILE_PATH, line -> {
            String productName = ParsingUtils.splitProducts(line, ",")[0];
            productCount.put(productName, productCount.getOrDefault(productName, 0) + 1);
        });
        return productCount;
    }

    private void processFile(String filePath, LineProcessor processor) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.lines().skip(1).forEach(processor::process);
        } catch (IOException e) {
            logError(filePath.equals(PRODUCT_FILE_PATH) ? ErrorMessage.PRODUCT_LOAD_ERROR
                    : ErrorMessage.PROMOTION_LOAD_ERROR, e);
        }
    }

    private Product parseProduct(String line, Map<String, Promotion> promotions) {
        String[] parts = ParsingUtils.splitProducts(line, ",");
        String name = parts[0];
        double price = ParsingUtils.parseDouble(parts[1]);
        int quantity = ParsingUtils.parseInt(parts[2]);
        Promotion promotion = getPromotionIfExists(parts[3], promotions);
        return new Product(name, price, quantity, promotion);
    }

    private Promotion getPromotionIfExists(String promotionName, Map<String, Promotion> promotions) {
        return ParsingUtils.isNullValue(promotionName) ? null : promotions.get(promotionName);
    }

    public Map<String, Promotion> loadPromotions() {
        Map<String, Promotion> promotions = new HashMap<>();
        processFile(PROMOTION_FILE_PATH, line -> addPromotionFromLine(promotions, line));
        return promotions;
    }

    private void addPromotionFromLine(Map<String, Promotion> promotions, String line) {
        String[] parts = ParsingUtils.splitProducts(line, ",");
        String name = parts[0];
        int buy = ParsingUtils.parseInt(parts[1]);
        int get = ParsingUtils.parseInt(parts[2]);
        LocalDate startDate = ParsingUtils.parseLocalDate(parts[3]);
        LocalDate endDate = ParsingUtils.parseLocalDate(parts[4]);
        promotions.put(name, createPromotion(buy, get, startDate, endDate, name));
    }

    private Promotion createPromotion(int buy, int get, LocalDate startDate, LocalDate endDate, String name) {
        if ("반짝할인".equals(name)) {
            return new Promotion(new FlashSalePromotion(), startDate, endDate, "반짝할인");
        }
        if (buy == 1 && get == 1) {
            return new Promotion(new OnePlusOnePromotion(), startDate, endDate, "MD추천상품");
        }
        if (buy == 2 && get == 1) {
            return new Promotion(new TwoPlusOnePromotion(), startDate, endDate, "탄산2+1");
        }
        return null;
    }

    private void logError(ErrorMessage errorMessage, IOException e) {
        System.err.println(errorMessage.getMessage() + ": " + e.getMessage());
    }

    private void addZeroStockProductIfNeeded(Inventory inventory, Product product, Map<String, Integer> productCount) {
        if (product.getPromotion() != null && productCount.get(product.getName()) == 1) {
            Product zeroStockProduct = new Product(product.getName(), product.getPrice(), 0, null);
            inventory.addProduct(zeroStockProduct);
        }
    }

    @FunctionalInterface
    interface LineProcessor {
        void process(String line);
    }

}
