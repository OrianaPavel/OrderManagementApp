package presentation;

import model.Clients;
import model.Product;

import javax.swing.*;
import java.awt.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *  Contine componente vizuale din GUI care permit angajatului sa interactioneze cu baza de date
 *  pentru a putea efectua operatiile CRUD pentru tabele Product si Client
 */
public class ViewCRUD extends JPanel{
    private JButton back = new JButton("Back");
    private JComboBox comboBoxOPSelection = new JComboBox();
    private JPanel header = new JPanel();


    private JPanel insertOrUpdate = new JPanel();
    private JButton searchID = new JButton("Search ID");
    private JLabel selectIDLabel = new JLabel("Select ID");
    private JTextField insertIDText= new JTextField(15);
    private JPanel content = new JPanel();
    private List<JLabel> fields = new ArrayList<>();
    private List<JTextField> values = new ArrayList<>();
    private JButton submit = new JButton("Submit");

    private JScrollPane viewAllScroll = new JScrollPane();
    private JTable viewALL = new JTable();

    private String[] ops;

    private GridBagConstraints gbc = new GridBagConstraints();
    private String type;
    private int operation;
    public  ViewCRUD(){
        initObjectFields("clients");

        this.setLayout(new GridBagLayout());

        gbc.gridx = 1; gbc.gridy = 1;
        viewAllScroll.add(viewALL);

        insertOrUpdate.add(selectIDLabel);
        insertOrUpdate.add(insertIDText);
        insertOrUpdate.add(searchID);

    }
    private void clean(){
        gbc.gridx = 1; gbc.gridy = 1;
        this.removeAll();
        header.removeAll();
        fields.clear();
        values.clear();
    }
    protected void reinit(){
        insertIDText.setEditable(true);
        gbc.gridx = 1; gbc.gridy = 1;
        this.removeAll();
        this.add(header,gbc);
    }
    public void initObjectFields(String type){
        clean();
        this.type = type;
        ops = new String[]{"Add " + type, "Delete " + type, "View all " + type, "Update " + type};
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>( ops );
        comboBoxOPSelection.setModel(model);
        initFieldNames();

        header.add(back);
        header.add(comboBoxOPSelection);
        this.add(header,gbc);
    }
    private void initFieldNames(){
        if(type.equals("clients")){
            fields.add(new JLabel("name"));
            fields.add(new JLabel("address"));
        }else if(type.equals("product")){
            fields.add(new JLabel("productName"));
            fields.add(new JLabel("price"));
            fields.add(new JLabel("stock"));
        }
        for(JLabel field:fields)
            values.add(new JTextField(10));
    }
    public void setValueFields(Object object){
        int cnt = 0;
        for(Field field:object.getClass().getDeclaredFields()) {
            try {
                if(field.getName().equals("id"))
                    continue;
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), object.getClass());
                Method method = propertyDescriptor.getReadMethod();
                values.get(cnt++).setText(method.invoke(object).toString());
            } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
                e.printStackTrace();
            }
        }
    }
    protected void setContentAdd() {
        operation = 0;
        for(JTextField value:values)
            value.setText("");
        setContent();
        gbc.gridy = 2;
        this.add(content,gbc);
    }
    protected void setContentUpdateOrDelete(int operation) {
        this.operation = operation;
        gbc.gridy = 2;
        this.add(insertOrUpdate,gbc);
    }
    public void setContentViewAll(){
        viewALL.setEnabled(false);
        viewAllScroll = new JScrollPane(viewALL);
        gbc.gridy = 2;
        this.add(viewAllScroll,gbc);
    }
    public void setContent(){
        content.removeAll();
        content.setLayout(new GridLayout(fields.size()+1,2));
        for(int i = 0; i < fields.size();i++){
            content.add(fields.get(i));
            content.add(values.get(i));
        }
        content.add(submit);
        gbc.gridy = 3;
        this.add(content,gbc);
    }
    public Object createObject() throws IllegalArgumentException{

        if(type.equals("clients")) {
            if(insertIDText.getText().equals(""))
                return new Clients(values.get(0).getText(), values.get(1).getText());
            int id = Integer.parseInt(insertIDText.getText());
            return new Clients(id, values.get(0).getText(), values.get(1).getText());
        }
        if(Integer.parseInt(values.get(2).getText()) < 0 || Float.parseFloat(values.get(1).getText()) < 0 )
            throw new IllegalArgumentException("Invalid input!");
        if(insertIDText.getText().equals(""))
            return new Product(values.get(0).getText(),Float.parseFloat(values.get(1).getText()),Integer.parseInt(values.get(2).getText()));
        int id = Integer.parseInt(insertIDText.getText());
        return new Product(id,values.get(0).getText(),Float.parseFloat(values.get(1).getText()),Integer.parseInt(values.get(2).getText()));
    }

    public List<JTextField> getValues() {
        return values;
    }

    public JPanel getHeader() {
        return header;
    }

    public JPanel getContent() {
        return content;
    }

    public String getType() {
        return type;
    }

    public JTextField getInsertIDText() {
        return insertIDText;
    }

    public JButton getSearchID() {
        return searchID;
    }

    public JPanel getInsertOrUpdate() {
        return insertOrUpdate;
    }

    public JComboBox getComboBoxOPSelection() {
        return comboBoxOPSelection;
    }

    public JButton getBack() {
        return back;
    }

    public JButton getSubmit() {
        return submit;
    }

    public void setViewALL(JTable viewALL) {
        this.viewALL = viewALL;
    }

    public int getOperation() {
        return operation;
    }
}
