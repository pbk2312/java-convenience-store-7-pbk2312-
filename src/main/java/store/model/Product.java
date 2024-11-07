package store.model;

public class Product {
    private final String name;
    private final double price;
    private int stock;
    private final Promotion promotion;

    public Product(String name, double price, int stock) {
        this(name, price, stock, null);
    }

    public Product(String name, double price, int stock, Promotion promotion) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.promotion = promotion;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    // 재고를 직접 차감하지 않고 서비스에서 관리
    public void adjustStock(int quantity) {
        this.stock -= quantity;
    }

}
