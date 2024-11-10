package store.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Order {

    private final Map<Product, Integer> orderedProducts;
    private final Map<Product, Integer> freeItems;
    private boolean isMembership;
    private int totalQuantity;
    private double totalBeforeDiscount;
    private double eventDiscount;
    private double membershipDiscount;
    private double finalTotal;

    public Order() {
        this.orderedProducts = new HashMap<>();
        this.freeItems = new HashMap<>();
        this.isMembership = false;
        this.totalQuantity = 0;
        this.totalBeforeDiscount = 0.0;
        this.eventDiscount = 0.0;
        this.membershipDiscount = 0.0;
        this.finalTotal = 0.0;
    }

    public void addProduct(Product product, int quantity) {
        orderedProducts.put(product, quantity);
        totalBeforeDiscount += product.getPrice() * quantity;
        totalQuantity += quantity;
    }

    public void setEventDiscount(double discount) {
        this.eventDiscount = discount;
    }

    public void setMembershipDiscount(double discount) {
        this.membershipDiscount = discount;
    }

    public void setFinalTotal(double total) {
        this.finalTotal = total;
    }

    public Map<Product, Integer> getOrderedProducts() {
        return orderedProducts;
    }

    public Map<Product, Integer> getFreeItems() {
        return freeItems;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public double getTotalBeforeDiscount() {
        return totalBeforeDiscount;
    }

    public double getEventDiscount() {
        return eventDiscount;
    }

    public double getMembershipDiscount() {
        return membershipDiscount;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public boolean isMembership() {
        return isMembership;
    }

    public void setMembership(boolean isMembership) {
        this.isMembership = isMembership;
    }

    public Map<String, ProductSummary> getAggregatedOrderSummary() {
        Map<String, ProductSummary> summaryMap = new LinkedHashMap<>();
        orderedProducts.forEach((product, quantity) -> updateSummaryMap(summaryMap, product, quantity));
        return summaryMap;
    }

    private void updateSummaryMap(Map<String, ProductSummary> summaryMap, Product product, int quantity) {
        String productName = product.getName();
        double totalProductPrice = calculateTotalProductPrice(product, quantity);

        summaryMap.putIfAbsent(productName, new ProductSummary(0, 0.0));
        ProductSummary summary = summaryMap.get(productName);

        summary.addQuantity(quantity);
        summary.addTotalPrice(totalProductPrice);
    }

    private double calculateTotalProductPrice(Product product, int quantity) {
        return product.getPrice() * quantity;
    }


}
