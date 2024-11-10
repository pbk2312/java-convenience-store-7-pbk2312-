package store.model;

public class ProductSummary {
    private int totalQuantity;
    private double totalPrice;

    public ProductSummary(int quantity, double price) {
        this.totalQuantity = quantity;
        this.totalPrice = price;
    }

    public void addQuantity(int quantity) {
        this.totalQuantity += quantity;
    }

    public void addTotalPrice(double price) {
        this.totalPrice += price;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

}
