package bll;

import dao.ProductDAO;
import model.Product;

import javax.swing.*;
import java.util.List;
import java.util.NoSuchElementException;
/**
 * Product business logic reprezinta layer-ul intermediar intre accesul la baza de date si interfata grafica
 */
public class ProductBLL {
    private ProductDAO productDAO;

    public ProductBLL() {
        productDAO = new ProductDAO();

    }

    public Product findProductById(int id) {
        Product st = productDAO.findById(id);
        if (st == null) {
            throw new NoSuchElementException("The product with id =" + id + " was not found!");
        }
        return st;
    }
    public JTable convertListToTable(List<Product> a) {
        JTable st = productDAO.convertListToTable(a);
        if (st == null) {
            throw new NoSuchElementException("The product table is empty");
        }
        return st;
    }
    public List<Product> findAllProducts() {
        List<Product> st = productDAO.findAll();
        if (st == null) {
            throw new NoSuchElementException("The product table is empty");
        }
        return st;
    }
    public Product insertProduct(Product product) {
        Product st = productDAO.insert(product);
        if (st == null) {
            throw new NoSuchElementException("The product with id ="+ product.getId()  + " was not inserted!");
        }
        return st;
    }

    public Product updateProduct(Product product) {
        Product st = productDAO.update(product);
        if (st == null) {
            throw new NoSuchElementException("The product with id ="+ product.getId()  + " was not updated!");
        }
        return st;
    }
    public Product deleteProduct(Product product) {
        Product st = productDAO.delete(product);
        if (st == null) {
            throw new NoSuchElementException("The product with id ="+ product.getId()  + " was not deleted!");
        }
        return st;
    }


}
