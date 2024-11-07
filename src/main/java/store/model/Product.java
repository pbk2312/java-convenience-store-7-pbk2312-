package store.model;

import store.view.ErrorMessage;

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

    public void deductStock(int quantity) {
        if (quantity > stock) {
            throw new IllegalStateException(ErrorMessage.INVALID_QUANTITY.getMessage());
        }
        stock -= quantity;
    }

    public double calculateFinalPrice(int quantity) {
        if (promotion != null) {
            return promotion.calculateDiscountedPrice(quantity, price);
        }
        return price * quantity;
    }

}
