package com.cv.c195;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

/**
 * Controller for "Update Customert.fxml"
 * @author Chris Vachon
 */
public class UpdateCustomerController implements Initializable {

    @FXML
    TextField updateId;
    @FXML
    TextField updateName;
    @FXML
    TextField updateAddress;
    @FXML
    TextField updateZip;
    @FXML
    TextField updatePhone;
    @FXML
    ComboBox<String> updateCountry;
    @FXML
    ComboBox<String> updateState;

    /**
     * Populates all fields in the Update Customer form. The information for the fields is based on the selected
     * customer on the records table of the Main window.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Customer customer = MainController.getCustomer();
        updateId.setText(String.valueOf(customer.getId()));
        updateName.setText(customer.getName());
        updateAddress.setText(customer.getAddress());
        updateZip.setText(customer.getZip());
        updatePhone.setText(customer.getPhone());
        updateCountry.getSelectionModel().select(customer.getCountry());
        updateState.getSelectionModel().select(customer.getState());
    }

    /**
     * Function is run when the "Update Customer" button is pressed.
     * All data filled out in the Update Customer form is updated on the main window records table and the connected database.
     * Error boxes appear when form isn't completely and properly filled out.
     * When the form is filled out completely and properly, the window is closed.
     */
    @FXML
    public void updateCustomerButtonPressed(){

        int id = Integer.parseInt(updateId.getText());
        String state;
        boolean fail = false;

        if(updateName.getText().isEmpty() ||
                updateAddress.getText().isEmpty() ||
                updateZip.getText().isEmpty() ||
                updatePhone.getText().isEmpty() ||
                updateCountry.getSelectionModel().getSelectedItem() == null ||
                updateState.getSelectionModel().getSelectedItem() == null){
            fail = true;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("All fields must be filled out correctly");
            alert.setContentText("Please fill out all fields with valid inputs to continue");
            alert.showAndWait();
        }

        if(!fail) {
            Customer updated = new Customer(id, updateName.getText(),
                    updateAddress.getText(), updateZip.getText(), updatePhone.getText(),
                    updateState.getSelectionModel().getSelectedItem(), updateCountry.getSelectionModel().getSelectedItem());

            DatabaseController.updateCustomerList(MainController.getCustomer(), updated);

            DatabaseController.query("SELECT Division_ID FROM first_level_divisions WHERE Division = '" +
                    updateState.getSelectionModel().getSelectedItem() + "'");

            ResultSet rs = DatabaseController.getQueryResults();
            try {
                rs.next();
                state = rs.getString("Division_ID");
                rs.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            DatabaseController.closeResultSet();
            DatabaseController.closeStatement();

            DatabaseController.query("UPDATE customers\n" +
                    "SET Customer_Name = '" + updateName.getText() + "',\n" +
                    "Address = '" + updateAddress.getText() + "',\n" +
                    "Postal_Code = '" + updateZip.getText() + "',\n" +
                    "Phone = '" + updatePhone.getText() + "',\n" +
                    "Division_ID = " + state + ",\n" +
                    "Last_Update = now(),\n" +
                    "Last_Updated_By = '" + DatabaseController.getUser() + "'\n" +
                    "WHERE Customer_ID = " + updateId.getText());

            DatabaseController.closeStatement();

            try {
                //Closes page after updating customer
                Stage stage = (Stage) updateState.getScene().getWindow();
                stage.close();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
