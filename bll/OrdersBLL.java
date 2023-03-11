package bll;


import dao.OrdersDAO;
import model.Orders;
import model.Product;

import javax.swing.*;
import java.util.List;
import java.util.NoSuchElementException;
/**
 * Orders business logic reprezinta layer-ul intermediar intre accesul la baza de date si interfata grafica
 */
public class OrdersBLL {
    private OrdersDAO ordersDAO;

    public OrdersBLL() {
        ordersDAO = new OrdersDAO();

    }

    public Orders findOrderById(int id) {
        Orders st = ordersDAO.findById(id);
        if (st == null) {
            throw new NoSuchElementException("The order with id =" + id + " was not found!");
        }
        return st;
    }
    public List<Orders> findAllOrders() {
        List<Orders> st = ordersDAO.findAll();
        if (st == null) {
            throw new NoSuchElementException("The order table is empty");
        }
        return st;
    }
    public Orders insertOrder(Orders orders) {
        Orders st = ordersDAO.insert(orders);
        if (st == null) {
            throw new NoSuchElementException("The order with id ="+ orders.getId()  + " was not inserted!");
        }
        return st;
    }
    public JTable convertListToTable(List<Orders> a) {
        JTable st = ordersDAO.convertListToTable(a);
        if (st == null) {
            throw new NoSuchElementException("The orders table is empty");
        }
        return st;
    }
    public Orders updateOrder(Orders orders) {
        Orders st = ordersDAO.update(orders);
        if (st == null) {
            throw new NoSuchElementException("The order with id ="+ orders.getId()  + " was not updated!");
        }
        return st;
    }
    public Orders deleteOrder(Orders orders) {
        Orders st = ordersDAO.delete(orders);
        if (st == null) {
            throw new NoSuchElementException("The order with id ="+ orders.getId()  + " was not deleted!");
        }
        return st;
    }
}
