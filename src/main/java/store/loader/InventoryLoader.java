package store.loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import store.model.Inventory;
import store.model.Product;
import store.model.Promotion;
import store.service.promotion.FlashSalePromotion;
import store.service.promotion.OnePlusOnePromotion;
import store.service.promotion.TwoPlusOnePromotion;
import store.util.ParsingUtils;
import store.view.ErrorMessage;
import store.view.OutputView;

public class InventoryLoader {

    private static final String PRODUCT_FILE_PATH = "src/main/resources/products.md";
    private static final String PROMOTION_FILE_PATH = "src/main/resources/promotions.md";
    private static InventoryLoader instance;
    private final OutputView outputView = new OutputView();

    private InventoryLoader() {
    }

    public static InventoryLoader getInstance() {
        if (instance == null) {
            instance = new InventoryLoader();
        }
        return instance;
    }

    public void loadProducts(Inventory inventory, Map<String, Promotion> promotions) {
        if (inventory.getProductList().isEmpty()) {
            Map<String, Integer> productCount = countProductOccurrences();
            try {
                FileProcessor.processFile(PRODUCT_FILE_PATH,
                        line -> processProductLine(line, inventory, promotions, productCount));
            } catch (IOException e) {
                logError(ErrorMessage.PRODUCT_LOAD_ERROR, e);
            }
        }
    }

    public Map<String, Promotion> loadPromotions() {
        Map<String, Promotion> promotions = new HashMap<>();
        try {
            FileProcessor.processFile(PROMOTION_FILE_PATH, line -> parsePromotion(line, promotions));
        } catch (IOException e) {
            logError(ErrorMessage.PROMOTION_LOAD_ERROR, e);
        }
        return promotions;
    }

    private void processProductLine(String line, Inventory inventory, Map<String, Promotion> promotions,
                                    Map<String, Integer> productCount) {
        Product product = parseProduct(line, promotions);
        inventory.addProduct(product);
        addZeroStockProductIfNeeded(inventory, product, productCount);
    }

    private Product parseProduct(String line, Map<String, Promotion> promotions) {
        String[] parts = ParsingUtils.splitProducts(line, ",");
        String name = parts[0];
        double price = ParsingUtils.parseDouble(parts[1]);
        int quantity = ParsingUtils.parseInt(parts[2]);
        Promotion promotion = promotions.getOrDefault(parts[3], null);
        return new Product(name, price, quantity, promotion);
    }

    private void parsePromotion(String line, Map<String, Promotion> promotions) {
        String[] parts = ParsingUtils.splitProducts(line, ",");
        String name = parts[0];
        int buy = ParsingUtils.parseInt(parts[1]);
        int get = ParsingUtils.parseInt(parts[2]);
        LocalDate startDate = ParsingUtils.parseLocalDate(parts[3]);
        LocalDate endDate = ParsingUtils.parseLocalDate(parts[4]);
        PromotionType promotionType = PromotionType.fromValues(name, buy, get);
        promotions.put(name, PromotionFactory.createPromotion(promotionType, startDate, endDate));
    }

    private void addZeroStockProductIfNeeded(Inventory inventory, Product product, Map<String, Integer> productCount) {
        if (isSinglePromotionProduct(product, productCount)) {
            inventory.addProduct(new Product(product.getName(), product.getPrice(), 0, null));
        }
    }

    private boolean isSinglePromotionProduct(Product product, Map<String, Integer> productCount) {
        return product.getPromotion() != null && productCount.get(product.getName()) == 1;
    }

    private Map<String, Integer> countProductOccurrences() {
        Map<String, Integer> productCount = new HashMap<>();
        try {
            FileProcessor.processFile(PRODUCT_FILE_PATH, line -> updateProductCount(line, productCount));
        } catch (IOException e) {
            logError(ErrorMessage.PRODUCT_LOAD_ERROR, e);
        }
        return productCount;
    }

    private void updateProductCount(String line, Map<String, Integer> productCount) {
        String productName = ParsingUtils.splitProducts(line, ",")[0];
        productCount.put(productName, productCount.getOrDefault(productName, 0) + 1);
    }

    private void logError(ErrorMessage errorMessage, Exception e) {
        outputView.printErrorMessage(errorMessage.getMessage() + ": " + e.getMessage());
    }

    public static class FileProcessor {
        public static void processFile(String filePath, LineProcessor processor) throws IOException {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                br.lines().skip(1).forEach(processor::process);
            }
        }
    }

    private enum PromotionType {
        FLASH_SALE, ONE_PLUS_ONE, TWO_PLUS_ONE, NONE;

        public static PromotionType fromValues(String name, int buy, int get) {
            if ("반짝할인".equals(name)) {
                return FLASH_SALE;
            }
            if (buy == 1 && get == 1) {
                return ONE_PLUS_ONE;
            }
            if (buy == 2 && get == 1) {
                return TWO_PLUS_ONE;
            }
            return NONE;
        }
    }

    private static class PromotionFactory {
        public static Promotion createPromotion(PromotionType type, LocalDate startDate, LocalDate endDate) {
            if (type == PromotionType.FLASH_SALE) {
                return new Promotion(new FlashSalePromotion(), startDate, endDate, "반짝할인");
            }
            if (type == PromotionType.ONE_PLUS_ONE) {
                return new Promotion(new OnePlusOnePromotion(), startDate, endDate, "MD추천상품");
            }
            if (type == PromotionType.TWO_PLUS_ONE) {
                return new Promotion(new TwoPlusOnePromotion(), startDate, endDate, "탄산2+1");
            }
            return null;
        }
    }

    @FunctionalInterface
    interface LineProcessor {
        void process(String line);
    }

}
