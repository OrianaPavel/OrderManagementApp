package model;

public class Orders {
    private int id;
    private int idClient;
    private int idProduct;
    private int quantity;

    public Orders(){

    }
    public Orders( int idClient, int idProduct, int quantity) {
        super();
        this.idClient = idClient;
        this.idProduct = idProduct;
        this.quantity = quantity;
    }
    public Orders(int id, int idClient, int idProduct, int quantity) {
        super();
        this.id = id;
        this.idClient = idClient;
        this.idProduct = idProduct;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Orders{" + "id=" + id + ", idClient=" + idClient + ", idProduct=" + idProduct + ", quantity=" + quantity + '}';
    }
}
