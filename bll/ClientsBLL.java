package bll;


import dao.ClientsDAO;
import model.Clients;
import model.Product;


import javax.swing.*;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Clients business logic reprezinta layer-ul intermediar intre accesul la baza de date si interfata grafica
 */
public class ClientsBLL {
    private ClientsDAO clientsDAO;

    public ClientsBLL() {
        clientsDAO = new ClientsDAO();

    }
    public JTable convertListToTable(List<Clients> a) {
        JTable st = clientsDAO.convertListToTable(a);
        if (st == null) {
            throw new NoSuchElementException("The clients table is empty");
        }
        return st;
    }
    public Clients findClientById(int id) {
        Clients st = clientsDAO.findById(id);
        if (st == null) {
            throw new NoSuchElementException("The client with id =" + id + " was not found!");
        }
        return st;
    }
    public List<Clients> findAllClients() {
        List<Clients> st = clientsDAO.findAll();
        if (st == null) {
            throw new NoSuchElementException("The client table is empty");
        }
        return st;
    }
    public Clients insertClient(Clients clients) {
        Clients st = clientsDAO.insert(clients);
        if (st == null) {
            throw new NoSuchElementException("The client with id ="+ clients.getId()  + " was not inserted!");
        }
        return st;
    }

    public Clients updateClient(Clients clients) {
        Clients st = clientsDAO.update(clients);
        if (st == null) {
            throw new NoSuchElementException("The client with id ="+ clients.getId()  + " was not updated!");
        }
        return st;
    }
    public Clients deleteClient(Clients clients) {
        Clients st = clientsDAO.delete(clients);
        if (st == null) {
            throw new NoSuchElementException("The client with id ="+ clients.getId()  + " was not deleted!");
        }
        return st;
    }
}
