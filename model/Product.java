package model;

public class Product {
    private int id;
    private String productName;
    private float price;
    private int stock;

    public Product(){

    }
    public Product( String product_name, float price, int stock) {
        super();
        this.productName = product_name;
        this.price = price;
        this.stock = stock;
    }
    public Product(int id, String product_name, float price, int stock) {
        super();
        this.id = id;
        this.productName = product_name;
        this.price = price;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", productName='" + productName + ", price=" + price + ", stock=" + stock + '}';
    }
}
