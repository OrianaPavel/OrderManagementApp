package presentation;

import model.Orders;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Contine si defineste componetele vizuale din GUI care permit utilizatorului sa
 * navigheze intre meniuri si sa efectueze operatiile pentru plasarea unei comenzi si vizualizarea acestora
 */
public class View extends JFrame {
    private JButton chooseUserMenu = new JButton("User");
    private JButton chooseEmployeeMenu = new JButton("Employee");

    private JButton placeOrderB = new JButton("Place order");
    private JButton viewOrders = new JButton("View orders");
    private JButton backToUserMenuFromPlaceOrder = new JButton("Back");
    private JButton backToUserMenuFromViewOrders = new JButton("Back");
    private JTable tabelData = new JTable();
    private JScrollPane ordersScrollPane = new JScrollPane();

    private JPanel mainMenu = new JPanel();
    private JPanel userMenu = new JPanel();
    private JPanel employeeMenu = new JPanel();

    private JButton backButtonToMainUser = new JButton("Back");
    private JButton backButtonToMainEmployee = new JButton("Back");

    private JButton clientCRUDButton = new JButton("Client actions");
    private JButton productCRUDButton = new JButton("Product actions");

    private ViewCRUD viewCRUD;

    private List<JLabel> labels = new ArrayList<>();
    private List<JTextField> values = new ArrayList<>();
    private JPanel placeOrderPanel = new JPanel();
    private JButton submit = new JButton("Submit");
    public View(){
        initMainMenu();
        initEmployeeMenu();
        initUserMenu();
        initfields();
        viewCRUD = new ViewCRUD();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Warehouse");
        this.setVisible(true);
        this.pack();
        this.setSize(600,600);
        this.setResizable(false);
        this.add(mainMenu);
    }

    private void initfields() {
        labels.add(new JLabel("idClient"));
        labels.add(new JLabel("idProductt"));
        labels.add(new JLabel("quantity"));
        for(JLabel label:labels)
            values.add(new JTextField(""));
        placeOrderPanel.setLayout(new GridLayout(4,2));
        placeOrderPanel.setBorder(new EmptyBorder(200,200,200,200));
        for(int i = 0; i < labels.size();i++) {
            placeOrderPanel.add(labels.get(i));
            placeOrderPanel.add(values.get(i));
        }
        placeOrderPanel.add(backToUserMenuFromPlaceOrder);
        placeOrderPanel.add(submit);
    }

    private void initMainMenu(){
        mainMenu.setBorder(new EmptyBorder(230, 230, 230, 230));
        mainMenu.setLayout(new GridLayout(2,1));
        mainMenu.add(chooseUserMenu);
        mainMenu.add(chooseEmployeeMenu);
    }
    private void initUserMenu(){
        userMenu.setBorder(new EmptyBorder(240, 240, 240, 240));
        userMenu.setLayout(new GridLayout(3,1));
        userMenu.add(placeOrderB);
        userMenu.add(viewOrders);
        userMenu.add(backButtonToMainUser);
    }
    private void initEmployeeMenu(){
        employeeMenu.setBorder(new EmptyBorder(240, 220, 220, 240));
        employeeMenu.setLayout(new GridLayout(3,1));
        employeeMenu.add(productCRUDButton);
        employeeMenu.add(clientCRUDButton);
        employeeMenu.add(backButtonToMainEmployee);
    }
    protected void initScrollPane(){
        tabelData.setEnabled(false);
        ordersScrollPane = new JScrollPane(tabelData);
        this.setLayout(new FlowLayout());
        this.add(backToUserMenuFromViewOrders);
        this.add(ordersScrollPane);

    }
    protected Orders getObject(){
        return new Orders(Integer.parseInt(values.get(0).getText()),Integer.parseInt(values.get(1).getText()),Integer.parseInt(values.get(2).getText()));
    }

    public void setTabelData(JTable tabelData) {
        this.tabelData = tabelData;
    }
    public JButton getSubmit() {
        return submit;
    }

    public JPanel getPlaceOrderPanel() {
        return placeOrderPanel;
    }

    public JButton getClientCRUDButton() {
        return clientCRUDButton;
    }

    public JButton getProductCRUDButton() {
        return productCRUDButton;
    }

    public JPanel getMainMenu() {
        return mainMenu;
    }

    public JPanel getUserMenu() {
        return userMenu;
    }

    public JPanel getEmployeeMenu() {
        return employeeMenu;
    }

    public JButton getBackButtonToMainUser() {
        return backButtonToMainUser;
    }

    public JButton getBackButtonToMainEmployee() {
        return backButtonToMainEmployee;
    }

    public JButton getChooseUserMenu() {
        return chooseUserMenu;
    }

    public JButton getChooseEmployeeMenu() {
        return chooseEmployeeMenu;
    }

    public JButton getPlaceOrderB() {
        return placeOrderB;
    }

    public JButton getViewOrders() {
        return viewOrders;
    }

    public ViewCRUD getViewCRUD() {
        return viewCRUD;
    }
    public JButton getBackToUserMenuFromPlaceOrder() {
        return backToUserMenuFromPlaceOrder;
    }

    public JButton getBackToUserMenuFromViewOrders() {
        return backToUserMenuFromViewOrders;
    }
}
