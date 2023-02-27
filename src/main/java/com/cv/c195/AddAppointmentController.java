package com.cv.c195;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Controller for "Add Appointment.fxml"
 * @author Chris Vachon
 */
public class AddAppointmentController implements Initializable {

    @FXML
    private TextField addId;
    @FXML
    private TextField addTitle;
    @FXML
    private TextField addDescription;
    @FXML
    private TextField addType;
    @FXML
    private TextField addStartHour;
    @FXML
    private TextField addStartMinute;
    @FXML
    private TextField addEndHour;
    @FXML
    private TextField addEndMinute;
    @FXML
    private TextField addCustomerId;
    @FXML
    private TextField addUserId;
    @FXML
    private DatePicker addDate;
    @FXML
    private ComboBox<String> addLocation;
    @FXML
    private ComboBox<String> addContact;
    @FXML
    private RadioButton addStartAM;
    @FXML
    private RadioButton addStartPM;
    @FXML
    private RadioButton addEndAM;
    @FXML
    private RadioButton addEndPM;

    private ObservableList<String> contacts = FXCollections.observableArrayList();
    private ObservableList<String> locations = FXCollections.observableArrayList();

    /**
     * Function is run when the "Add Appointments" button is pressed.
     * All data filled out in the Add Appointment form is added to the main window appointments table and the connected database.
     * Error boxes appear when form isn't completely and properly filled out.
     * When the form is filled out completely and properly, the window is closed.
     */
    @FXML
    public void addAppointmentButtonPressed(){

        int day = 0, month= 0, year = 0, startHour = 25, startMinute = 61, endHour = 25, endMinute = 60, customerId = 0, userId = 0, id = 0, contactId = 0;
        String title = null, description = null, location = null, contact = null, type = null, customerName = null;
        boolean fail = false;
        boolean customerConflict = false;
        boolean userConflict = true;

        try {
            day = addDate.getValue().getDayOfMonth();
            month = addDate.getValue().getMonthValue();
            year = addDate.getValue().getYear();
            startHour = Integer.parseInt(addStartHour.getText());
            startMinute = Integer.parseInt(addStartMinute.getText());
            endHour = Integer.parseInt(addEndHour.getText());
            endMinute = Integer.parseInt(addEndMinute.getText());
            customerId = Integer.parseInt(addCustomerId.getText());
            userId = Integer.parseInt(addUserId.getText());
            id = Integer.parseInt(addId.getText());
            title = addTitle.getText();
            description = addDescription.getText();
            location = addLocation.getSelectionModel().getSelectedItem();
            contact = addContact.getSelectionModel().getSelectedItem();
            type = addType.getText();
        }
        catch(Exception e){
            fail = true;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("All fields must be filled out correctly");
            alert.setContentText("Please fill out all fields with valid inputs to continue");
            alert.showAndWait();
        }

        if(fail){
            fail = false;
        }
        else {

            if(startHour > 12 || startHour < 1 || startMinute > 59 || startMinute < 0 ||
                    endHour > 12 || endHour < 1 || endMinute > 59 || endMinute < 0 ){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Time");
                alert.setContentText("All hours must be between 1 and 12. All Minutes must be between 0 and 59.");
                alert.showAndWait();
            }
            else {
                if (addStartPM.isSelected() && startHour != 12) {
                    startHour += 12;
                }
                if (addEndPM.isSelected() && endHour != 12) {
                    endHour += 12;
                }
                if(addStartAM.isSelected() && startHour == 12){
                    startHour = 0;
                }
                if(addEndAM.isSelected() && endHour == 12){
                    endHour = 0;
                }



                LocalDateTime startDt = LocalDateTime.of(year, month, day, startHour, startMinute);
                LocalDateTime endDt = LocalDateTime.of(year, month, day, endHour, endMinute);

                if (DatabaseController.query("SELECT customer_name FROM customers WHERE customer_id = " + customerId)) {
                    ResultSet qr = DatabaseController.getQueryResults();
                    try {
                        if (!qr.next()) {
                            customerConflict = true;
                        } else {
                            customerName = qr.getString("Customer_Name");
                        }
                        qr.close();
                        DatabaseController.closeResultSet();
                        DatabaseController.closeStatement();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                if (DatabaseController.query("SELECT Contact_ID FROM contacts WHERE Contact_Name = '" + contact + "'")) {
                    ResultSet qr = DatabaseController.getQueryResults();
                    try {
                        qr.next();
                        contactId = qr.getInt("Contact_ID");
                        qr.close();
                        DatabaseController.closeResultSet();
                        DatabaseController.closeStatement();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                if (DatabaseController.query("SELECT User_ID FROM Users")) {
                    ResultSet qr = DatabaseController.getQueryResults();
                    try {
                        while (qr.next()) {
                            if (qr.getInt("User_ID") == userId) {
                                userConflict = false;
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                Appointment x = new Appointment(id, customerName, customerId, contact, title, description, location, type, startDt, endDt, userId);

                boolean scheduleConflict = false;
                for (Appointment appointments : DatabaseController.getAllAppointments()) {
                    //if the start time of the new appointment falls inside an existing appointment,
                    // or the start time of an existing appointment falls inside the new one,
                    // and they have the same customer, there is a conflict.
                    if ((appointments.getStartEST().isBefore(x.getStartEST()) &&
                            appointments.getEndEST().isAfter(x.getStartEST())) ||
                            (x.getStartEST().isBefore(appointments.getStartEST()) &&
                                    x.getEndEST().isAfter(appointments.getStartEST()))
                            || appointments.getStartEST().equals(x.getStartEST()) &&
                            appointments.getCustomerName().equals(x.getCustomerName())) {
                        scheduleConflict = true;
                        break;
                    }
                }

                if (x.getStartEST().isAfter(x.getEndEST())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("You cannot end an appointment before it has begun!");
                    alert.showAndWait();
                } else if (scheduleConflict) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("You are trying to set an appointment that conflicts with another.");
                    alert.showAndWait();
                } else if (x.getStartEST().getHour() >= 22 || x.getEndEST().getHour() >= 22 || x.getStartEST().getHour() < 8 || x.getEndEST().getHour() < 8) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("You are trying to set an appointment outside of office hours.");
                    alert.setContentText("Please keep the appointments within 8:00 AM and 10:00 PM EST");
                    alert.showAndWait();
                } else if (userConflict || customerConflict) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid input!");
                    alert.setContentText("Please verify the Customer ID and User ID");
                    alert.showAndWait();
                } else {
                    DatabaseController.addToAppointmentList(x);

                    DatabaseController.query("INSERT INTO appointments " +
                            "VALUES(" + id + ", '" + title + "', '" + description + "', '" + location + "', '" + type + "', '" +
                            x.getStartStrUTC() + "', '" + x.getEndStrUTC() + "', now(), '" + DatabaseController.getUser() + "', " +
                            "now(), '" + DatabaseController.getUser() + "', " + customerId + ", " + userId + ", " + contactId + ")");

                    DatabaseController.closeStatement();
                    try {
                        //Closes page after updating customer
                        Stage stage = (Stage) addLocation.getScene().getWindow();
                        stage.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * Populates the location and contacts combo boxes with contacts and locations found in the connected database.
     * Populates the ID text field with the next id available in the connected database.
     * Populates the User ID text field with the user currently logged in.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(DatabaseController.query("SELECT * FROM contacts")) {
            ResultSet qr = DatabaseController.getQueryResults();
            try {
                while (qr.next()) {
                    contacts.add(qr.getString("Contact_Name"));
                }
                addContact.setItems(contacts);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if(DatabaseController.query("SELECT * FROM first_level_divisions")) {
            ResultSet qr = DatabaseController.getQueryResults();
            try {
                while (qr.next()) {
                    locations.add(qr.getString("Division"));
                }
                addLocation.setItems(locations);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (DatabaseController.query("SELECT MAX(Appointment_ID) FROM appointments")) {
            ResultSet queryMax = DatabaseController.getQueryResults();
            try {
                queryMax.next();
                Integer x = queryMax.getInt("MAX(Appointment_ID)") + 1;
                addId.setText(x.toString());
                queryMax.close();
                DatabaseController.closeResultSet();
                DatabaseController.closeStatement();
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        if(DatabaseController.query("SELECT User_Id FROM users WHERE User_Name = '" + DatabaseController.getUser() + "'")) {
            ResultSet qr = DatabaseController.getQueryResults();
            try {
                    qr.next();
                    addUserId.setText(qr.getString("User_Id"));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
