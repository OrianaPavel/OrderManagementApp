package presentation;

import bll.ClientsBLL;
import bll.OrdersBLL;
import bll.ProductBLL;

import model.Clients;
import model.Orders;
import model.Product;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

/**
 * Clasa Controller ofera functionalitate GUI, controleaza toate componentele vizuale din GUI.
 */
public class Controller implements ActionListener {
/*TODO:...
*/
    private View view;
    private ClientsBLL clientsBLL;
    private OrdersBLL ordersBLL;
    private ProductBLL productBLL;

    public Controller(View theView){
        clientsBLL = new ClientsBLL();
        ordersBLL = new OrdersBLL();
        productBLL = new ProductBLL();
        view = theView;
        view.getChooseUserMenu().addActionListener(this);
        view.getChooseEmployeeMenu().addActionListener(this);
        view.getBackButtonToMainEmployee().addActionListener(this);
        view.getBackButtonToMainUser().addActionListener(this);
        view.getPlaceOrderB().addActionListener(this);
        view.getViewOrders().addActionListener(this);
        view.getBackToUserMenuFromPlaceOrder().addActionListener(this);
        view.getBackToUserMenuFromViewOrders().addActionListener(this);
        view.getSubmit().addActionListener(this);
        CRUDAction c = new CRUDAction();

        view.getViewCRUD().getComboBoxOPSelection().addActionListener(c);
        view.getViewCRUD().getSearchID().addActionListener(c);
        view.getViewCRUD().getSubmit().addActionListener(c);
        view.getViewCRUD().getBack().addActionListener(this);

        view.getProductCRUDButton().addActionListener(this);
        view.getClientCRUDButton().addActionListener(this);
    }
    private void createBill(Orders order,Product product,Clients client){
        /*TODO:
        * clientdata(cu secunda)
        */
        String file = client.getId()+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) +  ".txt";
        float total = product.getPrice() * order.getQuantity();
        try {
            File myObj = new File(file);
            myObj.createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            FileWriter writer = new FileWriter(file);
            writer.write("Bill to: \n" +
                            "Name: " + client.getName() + "\n" +
                            "Address: " + client.getAddress() + "\n\n" +
                            "Products: \n" +
                            "Quantity: " + order.getQuantity() + "\n" +
                            "Product name: " + product.getProductName() + "\n" +
                            "Price/Unit: " + product.getPrice() + "$\n" +
                            "Total: " + total + "$\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        view.getContentPane().removeAll();
        if(e.getSource() == view.getChooseEmployeeMenu())
            view.add(view.getEmployeeMenu());
        else if(e.getSource() == view.getChooseUserMenu())
            view.add(view.getUserMenu());
        else if(e.getSource() == view.getBackButtonToMainEmployee() || e.getSource() == view.getBackButtonToMainUser())
            view.add(view.getMainMenu());
        else if(e.getSource() == view.getClientCRUDButton()){
            view.getViewCRUD().initObjectFields("clients");
            view.add(view.getViewCRUD());
        }
        else if(e.getSource() == view.getProductCRUDButton()){
            view.getViewCRUD().initObjectFields("product");
            view.add(view.getViewCRUD());
        }
        else if(e.getSource() == view.getViewCRUD().getBack())
            view.add(view.getEmployeeMenu());
        else if(e.getSource() == view.getBackToUserMenuFromPlaceOrder() || e.getSource() == view.getBackToUserMenuFromViewOrders() )
            view.add(view.getUserMenu());
        else if(e.getSource() == view.getPlaceOrderB())
            view.add(view.getPlaceOrderPanel());
        else if(e.getSource() == view.getViewOrders()){
            view.setTabelData(ordersBLL.convertListToTable(ordersBLL.findAllOrders()));
            view.initScrollPane();
        }
        else if(e.getSource() == view.getSubmit()){
            try {
                Orders order = view.getObject();
                Product product = productBLL.findProductById(order.getIdProduct());
                Clients client = clientsBLL.findClientById(order.getIdClient());
                if (product == null || client == null)
                    JOptionPane.showMessageDialog(null, "Invalid data");
                else if (product.getStock() < order.getQuantity())
                    JOptionPane.showMessageDialog(null, "Insufficient stock");
                else {
                    ordersBLL.insertOrder(order);
                    product.setStock(product.getStock() - order.getQuantity());
                    productBLL.updateProduct(product);
                    JOptionPane.showMessageDialog(null, "Order was successfully submitted!");
                    createBill(order,product,client);
                }
            }catch(Exception er){
                JOptionPane.showMessageDialog(null, "Invalid data");
            }
            view.add(view.getPlaceOrderPanel());
        }
        view.revalidate();
        view.repaint();
    }


    private class CRUDAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == view.getViewCRUD().getComboBoxOPSelection()){
                int selectedIndex = ((JComboBox)e.getSource()).getSelectedIndex();
                view.getViewCRUD().reinit();
                if(selectedIndex == 0) // Add
                    view.getViewCRUD().setContentAdd();
                else if(selectedIndex == 3 || selectedIndex == 1) //Update
                    view.getViewCRUD().setContentUpdateOrDelete(selectedIndex);
                else if(selectedIndex == 2){
                    JTable table;
                    if(view.getViewCRUD().getType().equals("clients"))
                       table = clientsBLL.convertListToTable(clientsBLL.findAllClients());
                    else
                        table = productBLL.convertListToTable(productBLL.findAllProducts());
                    view.getViewCRUD().setViewALL(table);
                    view.getViewCRUD().setContentViewAll();
                }

            }
            else if(e.getSource() == view.getViewCRUD().getSearchID() && (view.getViewCRUD().getOperation() == 3 || view.getViewCRUD().getOperation() == 1)){
                view.getViewCRUD().getInsertIDText().setEditable(false);
                try {
                    int id = Integer.parseInt(view.getViewCRUD().getInsertIDText().getText());
                    Object object = null;
                    if(view.getViewCRUD().getType().equals("clients")) {
                        try {
                            object = clientsBLL.findClientById(id);
                        }catch (NoSuchElementException er) {
                            JOptionPane.showMessageDialog(null,"Invalid ID");
                        }
                    }
                    else {
                        try {
                            object = productBLL.findProductById(id);
                        }catch (NoSuchElementException er) {
                            JOptionPane.showMessageDialog(null,"Invalid ID");
                        }
                    }
                    if(object != null) {
                        view.getViewCRUD().setValueFields(object);
                        view.getViewCRUD().setContent();
                    }
                }catch (NumberFormatException er){
                    JOptionPane.showMessageDialog(null,"Invalid format");
                }
            }
            else if(e.getSource() == view.getViewCRUD().getSubmit()){
                submitButtonAction();
            }
            view.getViewCRUD().revalidate();
            view.getViewCRUD().repaint();
        }
        private void submitButtonAction(){
            switch(view.getViewCRUD().getOperation()){
                case 0:
                    try {
                        if(view.getViewCRUD().getType().equals("clients"))
                            clientsBLL.insertClient((Clients)view.getViewCRUD().createObject());
                        else
                            productBLL.insertProduct((Product)view.getViewCRUD().createObject());
                        JOptionPane.showMessageDialog(null,"Data was inserted successfully!");
                    }catch (Exception er){
                        er.printStackTrace();
                        JOptionPane.showMessageDialog(null,"Invalid data");
                    }
                    break;
                case 1:
                    try {
                        Object a;
                        if (view.getViewCRUD().getType().equals("clients"))
                            a = clientsBLL.deleteClient((Clients) view.getViewCRUD().createObject());
                        else
                            a = productBLL.deleteProduct((Product) view.getViewCRUD().createObject());
                        JOptionPane.showMessageDialog(null,"Data deleted successfully!");
                    }catch(Exception er){
                        JOptionPane.showMessageDialog(null,"Error id not found");
                    }
                    break;
                case 3:
                    try {
                        if(view.getViewCRUD().getType().equals("clients"))
                            clientsBLL.updateClient((Clients)view.getViewCRUD().createObject());
                        else
                            productBLL.updateProduct((Product)view.getViewCRUD().createObject());
                        JOptionPane.showMessageDialog(null,"Data updated successfully!");
                    }catch (Exception er){
                        er.printStackTrace();
                        JOptionPane.showMessageDialog(null,"Invalid data");
                    }
                    break;
            }
        }
    }
}
