package com.cv.c195;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.ResultSet;
import java.time.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for "main.fxml"
 * @author Chris Vachon
 */
public class MainController implements Initializable {
    @FXML
    private TableView<Customer> recordsTable;
    @FXML
    private TableColumn<Customer, Integer> customerID;
    @FXML
    private TableColumn<Customer, String> customerName;
    @FXML
    private TableColumn<Customer, String> customerAddress;
    @FXML
    private TableColumn<Customer, String> customerState;
    @FXML
    private TableColumn<Customer, String> customerZip;
    @FXML
    private TableColumn<Customer, String> customerCountry;
    @FXML
    private TableColumn<Customer, String> customerPhone;
    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<Appointment, Integer> appointmentID;
    @FXML
    private TableColumn<Appointment, Integer> appointmentCustomerId;
    @FXML
    private TableColumn<Appointment, Integer> appointmentUserId;
    @FXML
    private TableColumn<Appointment, String> appointmentContactName;
    @FXML
    private TableColumn<Appointment, String> appointmentTitle;
    @FXML
    private TableColumn<Appointment, String> appointmentDescription;
    @FXML
    private TableColumn<Appointment, String> appointmentLocation;
    @FXML
    private TableColumn<Appointment, String> appointmentType;
    @FXML
    private TableColumn<Appointment, String> appointmentStart;
    @FXML
    private TableColumn<Appointment, String> appointmentEnd;
    @FXML
    private RadioButton weekRadio;
    @FXML
    private RadioButton monthRadio;
    @FXML
    private Button reportsButton;
    @FXML
    private Button addAppointmentButton;
    @FXML
    private Button addCustomerButton;
    private static Customer customer;
    private static Appointment appointment;

    /**
     * The selected customer from the records table is deleted from the table and the database. If it's successful, a
     * success alert pops up. If it does not, an error alert pops up with the reason for failure
     */
    @FXML
    public void onDeleteCustomerButtonClick(){
        boolean deletable = true;

        for(Appointment appointment: DatabaseController.getAllAppointments()){
                if(recordsTable.getSelectionModel().getSelectedItem().getId() == appointment.getCustomerId()) {
                    deletable = false;
                    break;
                }
            }
        if(deletable) {
            DatabaseController.query("DELETE FROM customers WHERE Customer_ID = " + recordsTable.getSelectionModel().getSelectedItem().getId());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Customer was successfully deleted");
            alert.setContentText("Good-bye" + recordsTable.getSelectionModel().getSelectedItem().getName());
            alert.showAndWait();
            DatabaseController.deleteCustomerList(recordsTable.getSelectionModel().getSelectedItem());
            DatabaseController.closeStatement();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete All?");
            alert.setHeaderText("All appointments for the customer must be deleted before the customer is deleted");
            alert.setContentText("Would you like to delete all appointments and the customer?");
            Optional<ButtonType> result = alert.showAndWait();

            if(result.get() == ButtonType.OK){
                DatabaseController.query("DELETE FROM appointments WHERE Customer_ID = " + recordsTable.getSelectionModel().getSelectedItem().getId());

                ObservableList<Appointment> found = FXCollections.observableArrayList();

                for(Appointment appointments : DatabaseController.getAllAppointments()){
                    if(appointments.getCustomerId() == recordsTable.getSelectionModel().getSelectedItem().getId()){
                        found.add(appointments);
                    }
                }
                DatabaseController.closeStatement();
                DatabaseController.getAllAppointments().removeAll(found);
                DatabaseController.getWeeklyAppointments().removeAll(found);
                DatabaseController.getMonthlyAppointments().removeAll(found);
                DatabaseController.query("DELETE FROM customers WHERE Customer_ID = " + recordsTable.getSelectionModel().getSelectedItem().getId());
                DatabaseController.closeStatement();
                DatabaseController.deleteCustomerList(recordsTable.getSelectionModel().getSelectedItem());

            }
        }

    }

    /**
     * The Update Customer window appears with the information from the selected customer entered into form.
     */
    @FXML
    public void onUpdateCustomerButtonClick(){
        customer = recordsTable.getSelectionModel().getSelectedItem();
        openNewScene("FXML/Update Customer.fxml");
    }

    /**
     * The Update Appointment window appears with the information from the selected customer entered into form.
     */
    @FXML
    public void onUpdateAppointmentButtonClick(){
        appointment = appointmentTable.getSelectionModel().getSelectedItem();
        openNewScene("FXML/Update Appointment.fxml");
    }

    /**
     * The selected appointment from the records table is deleted from the table and the database. If it's successful, a
     * success alert pops up.
     */
    @FXML
    public void onDeleteAppointmentButtonClick(){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Appointment " + appointmentTable.getSelectionModel().getSelectedItem().getId() + " was canceled");
        alert.setContentText("Please call to reschedule the " + appointmentTable.getSelectionModel().getSelectedItem().getType());
        alert.showAndWait();

        DatabaseController.query("DELETE FROM appointments WHERE Appointment_ID = " + appointmentTable.getSelectionModel().getSelectedItem().getId());
        DatabaseController.deleteAppointmentList(appointmentTable.getSelectionModel().getSelectedItem());
        DatabaseController.closeStatement();
    }

    /**
     * Function chooses which set of appointments to show in the table.
     * If the radio button labeled "week" is pressed, the appointments table shows the appointments for the next 7 days.
     * If the radio button labeled "month" is pressed, the appointments table shows the appointments for the next 30 days.
     * If the radio button labeled "all" is pressed, the appointments table shows all the appointments.
     */
    @FXML
    public void weekMonthToggle(){
        if (weekRadio.isSelected()){
            appointmentTable.setItems(DatabaseController.getWeeklyAppointments());
        }
        else if (monthRadio.isSelected()){
            appointmentTable.setItems(DatabaseController.getMonthlyAppointments());
        }
        else{
            appointmentTable.setItems(DatabaseController.getAllAppointments());
        }
    }

    /**
     * Opens a new window of the chosen FXML file.
     * @param str - The name of the FXML file to be shown
     */
    public void openNewScene(String str){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(str));
            Parent parent = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return The customer that is selected when the "Update Customer" button is pressed.
     */
    public static Customer getCustomer(){return customer;}

    /**
     *
     * @return The appointment that is selected when the "Update Appointment" button is pressed.
     */
    public static Appointment getAppointment(){return appointment;}

    /**
     * Populates the records and appointments tables with data found in the connected database.
     * Shows an alert if an appointment is to happen in the next 15 minutes.
     * Lambda expressions for the report button, add appointment button, and the add customer button.
     * This makes the code more concise as they all only need a single line of code each as functions.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Get the tables ready.
        if(DatabaseController.query("""
                SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, first_level_divisions.Division, countries.Country
                FROM customers
                RIGHT JOIN first_level_divisions ON first_level_divisions.Division_ID=customers.Division_ID
                RIGHT JOIN countries ON first_level_divisions.Country_ID=countries.Country_ID;
                """)){
            ResultSet qr = DatabaseController.getQueryResults();
            try {
                while (qr.next()) {
                    Customer x = new Customer(qr.getInt("Customer_ID"), qr.getString("Customer_Name"),
                            qr.getString("Address"), qr.getString("Postal_code"),
                            qr.getString("Phone"), qr.getString("Division"), qr.getString("Country"));
                    //Remove junk before adding to the table
                    if(x.getName() != null) {
                        DatabaseController.addToCustomerList(x);
                    }
                }//while
                qr.close();
                DatabaseController.closeResultSet();
                DatabaseController.closeStatement();
            }//try
            catch(Exception e){
                throw new RuntimeException(e);
            }//catch
        }//if

        if(DatabaseController.query("""
                SELECT appointments.Appointment_ID, appointments.Title, appointments.Description,appointments.Location, 
                appointments.Type, appointments.Start, appointments.End, customers.Customer_Name,appointments.customer_id, 
                contacts.Contact_Name, appointments.user_id
                FROM appointments
                RIGHT JOIN customers ON customers.Customer_ID = appointments.Customer_ID
                RIGHT JOIN contacts on contacts.Contact_ID = appointments.Contact_ID;""")){
            ResultSet qr = DatabaseController.getQueryResults();
            try{
                while(qr.next()){
                    LocalDateTime start = LocalDateTime.now();
                    LocalDateTime end = LocalDateTime.now();
                    if(qr.getTimestamp("Start") != null) {
                        start = qr.getTimestamp("Start").toLocalDateTime();
                        end = qr.getTimestamp("End").toLocalDateTime();
                    }

                    ZonedDateTime zdt = ZonedDateTime.of(start, ZoneId.systemDefault());
                    ZonedDateTime zdtUTC = ZonedDateTime.of(start, ZoneId.of("UTC"));

                    int offsetUTC = zdt.getOffset().compareTo(zdtUTC.getOffset());

                    start = start.minusSeconds(offsetUTC);
                    end = end.minusSeconds(offsetUTC);

                    Appointment x = new Appointment(qr.getInt("Appointment_ID"), qr.getString("Customer_Name"),
                            qr.getInt("Customer_ID"), qr.getString("Contact_Name"),
                            qr.getString("Title"), qr.getString("Description"),
                            qr.getString("Location"), qr.getString("Type"),
                            start, end, qr.getInt("User_ID"));

                    if(x.getId() != 0) {
                        DatabaseController.addToAppointmentList(x);
                    }
                }
                qr.close();
                DatabaseController.closeResultSet();
                DatabaseController.closeStatement();
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
        }

        customerID.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("id"));
        customerName.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
        customerAddress.setCellValueFactory(new PropertyValueFactory<Customer, String>("address"));
        customerState.setCellValueFactory(new PropertyValueFactory<Customer, String>("state"));
        customerZip.setCellValueFactory(new PropertyValueFactory<Customer, String>("zip"));
        customerCountry.setCellValueFactory(new PropertyValueFactory<Customer, String>("country"));
        customerPhone.setCellValueFactory(new PropertyValueFactory<Customer, String>("phone"));

        appointmentID.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("id"));
        appointmentCustomerId.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("customerId"));
        appointmentContactName.setCellValueFactory(new PropertyValueFactory<Appointment, String>("contactName"));
        appointmentTitle.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        appointmentDescription.setCellValueFactory(new PropertyValueFactory<Appointment, String>("description"));
        appointmentLocation.setCellValueFactory(new PropertyValueFactory<Appointment, String>("location"));
        appointmentType.setCellValueFactory(new PropertyValueFactory<Appointment, String>("type"));
        appointmentStart.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startStr"));
        appointmentEnd.setCellValueFactory(new PropertyValueFactory<Appointment, String>("endStr"));
        appointmentUserId.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("userId"));

        recordsTable.setItems(DatabaseController.getAllCustomers());
        weekMonthToggle();

        boolean appt = false;
        for(Appointment appointments : DatabaseController.getWeeklyAppointments()){
            LocalDateTime zdt = appointments.getStart().minusMinutes(15).toLocalDateTime();

            if(zdt.isBefore(LocalDateTime.now()) || zdt.isEqual(LocalDateTime.now())){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("APPOINTMENT SOON");
                alert.setHeaderText("Appointment " + appointments.getId() + " is starting soon");
                alert.setContentText("It starts at " + appointments.getStartStr());
                alert.showAndWait();
                appt = true;
                break;
            }
        }

        if(!appt){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No appointments");
            alert.setHeaderText("There are no appointments in the next 15 minutes.");
            alert.showAndWait();
        }

        reportsButton.setOnAction(actionEvent -> openNewScene("FXML/Reports.fxml"));
        addAppointmentButton.setOnAction(actionEvent -> openNewScene("FXML/Add Appointment.fxml"));
        addCustomerButton.setOnAction(actionEvent -> openNewScene("FXML/Add Customer.fxml"));

    }
}
