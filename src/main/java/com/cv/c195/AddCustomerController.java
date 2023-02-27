package com.cv.c195;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller for "Add Customer.fxml"
 * @author Chris Vachon
 */
public class AddCustomerController implements Initializable {

    @FXML
    TextField addId;
    @FXML
    TextField addName;
    @FXML
    TextField addAddress;
    @FXML
    TextField addZip;

    @FXML
    TextField addPhone;
    @FXML
    ComboBox<String> addCountry;
    @FXML
    ComboBox<String> addState;
    private ObservableList<String> countries = FXCollections.observableArrayList();
    private ObservableList<String> states = FXCollections.observableArrayList();

    /**
     * Runs after the country is chosen from the country combo box.
     * First-Level Division combo box is populated with divisions found in that country from the connected database.
     */
    @FXML
    public void addCountryChosen(){
        states.clear();
        addState.getItems().clear();
        DatabaseController.query("SELECT * FROM countries WHERE Country = '" + addCountry.getSelectionModel().getSelectedItem() + "'");
        ResultSet qr = DatabaseController.getQueryResults();
        int countryId;
        try{
            qr.next();
            countryId = qr.getInt("Country_ID");
            qr.close();
            DatabaseController.closeResultSet();
            DatabaseController.closeStatement();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        DatabaseController.query("SELECT Division FROM first_level_Divisions WHERE Country_ID = " + countryId);
        ResultSet qr2 = DatabaseController.getQueryResults();
        try {
            while (qr2.next()) {
                states.add(qr2.getString("Division"));
            }
            addState.getItems().addAll(states);
            qr2.close();
            DatabaseController.closeResultSet();
            DatabaseController.closeStatement();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    /**
     * Populates the country combo box with countries found in the connected database.
     * Populates the ID text field with the next id available in the connected database.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseController.query("SELECT * FROM countries");
        ResultSet qr = DatabaseController.getQueryResults();

        try{
            while (qr.next()){
                countries.add(qr.getString("Country"));

            }
            qr.close();
            DatabaseController.closeResultSet();
            DatabaseController.closeStatement();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        addCountry.getItems().addAll(countries);


        if(DatabaseController.query("SELECT MAX(CUSTOMER_ID) FROM CUSTOMERS")){
            ResultSet queryMax = DatabaseController.getQueryResults();
           try {
                queryMax.next();
                Integer x = queryMax.getInt("MAX(CUSTOMER_ID)")+1;
                addId.setText(x.toString());
               queryMax.close();
               DatabaseController.closeResultSet();
               DatabaseController.closeStatement();
            } catch (SQLException e) {
               System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * Function is run when the "Add Customer" button is pressed.
     * All data filled out in the Add Customer form is added to the main window records table and the connected database.
     * Error boxes appear when form isn't completely and properly filled out.
     * When the form is filled out completely and properly, the window is closed.
     */
    @FXML
    public void addCustomerButtonPressed(){

        int id = Integer.parseInt(addId.getText());
        String state;
        boolean fail = false;


        if(addName.getText().isEmpty() ||
            addAddress.getText().isEmpty() ||
            addZip.getText().isEmpty() ||
            addPhone.getText().isEmpty() ||
            addCountry.getSelectionModel().getSelectedItem() == null ||
            addState.getSelectionModel().getSelectedItem() == null){
                fail = true;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("All fields must be filled out correctly");
                alert.setContentText("Please fill out all fields with valid inputs to continue");
                alert.showAndWait();
        }

        if(!fail) {
            Customer added = new Customer(id, addName.getText(), addAddress.getText(),
                    addZip.getText(), addPhone.getText(), addState.getSelectionModel().getSelectedItem().toString(),
                    addCountry.getSelectionModel().getSelectedItem().toString());
            DatabaseController.addToCustomerList(added);

            DatabaseController.query("SELECT Division_ID FROM first_level_divisions WHERE Division = '" +
                    addState.getSelectionModel().getSelectedItem().toString() + "'");

            ResultSet rs = DatabaseController.getQueryResults();
            try {
                rs.next();
                state = rs.getString("Division_ID");
                rs.close();
                DatabaseController.closeResultSet();
                DatabaseController.closeStatement();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            DatabaseController.query("INSERT INTO customers (Customer_ID, Customer_Name , Address, Postal_Code, Phone, " +
                    "Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) \n" + "VALUES (" + addId.getText() +
                    ", '" + added.getName() + "', '" + added.getAddress() + "', '" + added.getZip() + "', '" +
                    added.getPhone() + "', now(), '" + DatabaseController.getUser() + "', now(), '" +
                    DatabaseController.getUser() + "', " + state + ")");

            DatabaseController.closeStatement();

            try {
                //Closes page after adding customer
                Stage stage = (Stage) addState.getScene().getWindow();
                stage.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
