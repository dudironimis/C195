package com.cv.c195;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * Controller for "Update Appointment.fxml"
 * @author Chris Vachon
 */
public class UpdateAppointmentController implements Initializable {
    @FXML
    private TextField updateId;
    @FXML
    private TextField updateTitle;
    @FXML
    private TextField updateDescription;
    @FXML
    private TextField updateType;
    @FXML
    private TextField updateStartHour;
    @FXML
    private TextField updateStartMinute;
    @FXML
    private TextField updateEndHour;
    @FXML
    private TextField updateEndMinute;
    @FXML
    private TextField updateCustomerId;
    @FXML
    private TextField updateUserId;
    @FXML
    private DatePicker updateDate;
    @FXML
    private ComboBox<String> updateLocation;
    @FXML
    private ComboBox<String> updateContact;
    @FXML
    private RadioButton updateStartAM;
    @FXML
    private RadioButton updateStartPM;
    @FXML
    private RadioButton updateEndAM;
    @FXML
    private RadioButton updateEndPM;

    private ObservableList<String> contacts = FXCollections.observableArrayList();
    private ObservableList<String> locations = FXCollections.observableArrayList();

    /**
     * Function is run when the "Update Appointments" button is pressed.
     * All data filled out in the Update Appointment form is updated on the main window appointments table and the connected database.
     * Error boxes appear when form isn't completely and properly filled out.
     * When the form is filled out completely and properly, the window is closed.
     */
    @FXML
    public void updateAppointmentButtonPressed(){
        int day = 0, month= 0, year = 0, startHour = 25, startMinute = 61, endHour = 25, endMinute = 60, customerId = 0, userId = 0, id = 0, contactId = 0;
        String title = null, description = null, location = null, contact = null, type = null, customerName = null;
        boolean fail = false;
        boolean customerConflict = false;
        boolean userConflict = true;

        try {
            day = updateDate.getValue().getDayOfMonth();
            month = updateDate.getValue().getMonthValue();
            year = updateDate.getValue().getYear();
            startHour = Integer.parseInt(updateStartHour.getText());
            startMinute = Integer.parseInt(updateStartMinute.getText());
            endHour = Integer.parseInt(updateEndHour.getText());
            endMinute = Integer.parseInt(updateEndMinute.getText());
            customerId = Integer.parseInt(updateCustomerId.getText());
            userId = Integer.parseInt(updateUserId.getText());
            id = Integer.parseInt(updateId.getText());
            title = updateTitle.getText();
            description = updateDescription.getText();
            location = updateLocation.getSelectionModel().getSelectedItem();
            contact = updateContact.getSelectionModel().getSelectedItem();
            type = updateType.getText();
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
        else{
            if(startHour > 12 || startHour < 1 || startMinute > 59 || startMinute < 0 ||
                    endHour > 12 || endHour < 1 || endMinute > 59 || endMinute < 0 ){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Time");
                alert.setContentText("All hours must be between 1 and 12. All Minutes must be between 0 and 59.");
                alert.showAndWait();
            }
            else {
                if (updateStartPM.isSelected() && startHour != 12) {
                    startHour += 12;
                }
                if (updateEndPM.isSelected() && endHour != 12) {
                    endHour += 12;
                }
                if (updateStartAM.isSelected() && startHour == 12) {
                    startHour = 0;
                }
                if (updateEndAM.isSelected() && endHour == 12) {
                    endHour = 0;
                }

                LocalDateTime startDt = LocalDateTime.of(year, month, day, startHour, startMinute);
                LocalDateTime endDt = LocalDateTime.of(year, month, day, endHour, endMinute);

                if (DatabaseController.query("SELECT customer_name FROM customers WHERE customer_id = " + customerId)) {
                    ResultSet qr = DatabaseController.getQueryResults();
                    try {
                        if (!qr.next()) {
                            customerConflict = true;
                            customerName = null;
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

                Appointment updated = new Appointment(id, customerName, customerId, contact, title, description, location, type, startDt, endDt, userId);

                boolean conflict = false;
                for (Appointment appointments : DatabaseController.getAllAppointments()) {
                    //if the start time of the new appointment falls inside an existing appointment,
                    // or the start time of an existing appointment falls inside the new one,
                    // and they have the same contact, there is a conflict.
                    if (((appointments.getStartEST().isBefore(updated.getStartEST()) && appointments.getEndEST().isAfter(updated.getStartEST())) ||
                            (updated.getStartEST().isBefore(appointments.getStartEST()) && updated.getEndEST().isAfter(appointments.getStartEST()))
                            || appointments.getStartEST().equals(updated.getStartEST())) &&
                            appointments.getCustomerName().equals(updated.getCustomerName()) &&
                            appointments.getId() != updated.getId()) {
                        conflict = true;
                        break;
                    }
                }

                if (updated.getEndEST().isBefore(updated.getStartEST())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("You cannot end an appointment before it has begun!");
                    alert.showAndWait();
                } else if (conflict) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("You are trying to set an appointment that conflicts with another.");
                    alert.showAndWait();
                } else if (updated.getStartEST().getHour() >= 22 || updated.getEndEST().getHour() >= 22 || updated.getStartEST().getHour() < 8 || updated.getEndEST().getHour() < 8) {
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
                    DatabaseController.updateAppointmentList(MainController.getAppointment(), updated);

                    DatabaseController.query("UPDATE appointments\n" +
                            "SET Title = '" + title + "',\n" +
                            "Description = '" + description + "',\n" +
                            "Location = '" + location + "',\n" +
                            "Type = '" + type + "',\n" +
                            "Start = '" + updated.getStartStrUTC() + "',\n" +
                            "End = '" + updated.getEndStrUTC() + "',\n" +
                            "Last_update = now(),\n" +
                            "Last_Updated_By = '" + DatabaseController.getUser() + "',\n" +
                            "Customer_ID = " + customerId + ",\n" +
                            "User_ID = " + userId + ",\n" +
                            "Contact_ID = " + contactId + "\n" +
                            "WHERE Appointment_ID = " + id);

                    DatabaseController.closeStatement();

                    try {
                        //Closes page after updating customer
                        Stage stage = (Stage) updateLocation.getScene().getWindow();
                        stage.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * Populates all fields in the Update Appointment form. The information for the fields is based on the selected
     * appointment on the appointment table of the Main window.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Appointment appointment = MainController.getAppointment();

        if(DatabaseController.query("SELECT * FROM contacts")) {
            ResultSet qr = DatabaseController.getQueryResults();
            try {
                while (qr.next()) {
                    contacts.add(qr.getString("Contact_Name"));
                }
                updateContact.setItems(contacts);
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
                updateLocation.setItems(locations);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if(DatabaseController.query("SELECT User_Id FROM users WHERE User_Name = '" + DatabaseController.getUser() + "'")) {
            ResultSet qr = DatabaseController.getQueryResults();
            try {
                qr.next();
                updateUserId.setText(qr.getString("User_Id"));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if(appointment.getStart().getHour() > 12){
            updateStartHour.setText(String.valueOf(appointment.getStart().getHour() - 12));
            updateStartPM.setSelected(true);
        }
        else{
            updateStartHour.setText(String.valueOf(appointment.getStart().getHour()));
            updateStartAM.setSelected(true);
            if(appointment.getStart().getHour() == 12){
                updateStartPM.setSelected(true);
            }
        }

        if(appointment.getEnd().getHour() > 12){
            updateEndHour.setText(String.valueOf(appointment.getEnd().getHour() - 12));
            updateEndPM.setSelected(true);
        }
        else{
            updateEndHour.setText(String.valueOf(appointment.getEnd().getHour()));
            updateEndAM.setSelected(true);
            if(appointment.getEnd().getHour() == 12){
                updateStartPM.setSelected(true);
            }
        }

        if(appointment.getStart().getMinute() < 10){
            updateStartMinute.setText("0"+String.valueOf(appointment.getStart().getMinute()));
        }
        else {
            updateStartMinute.setText(String.valueOf(appointment.getStart().getMinute()));
        }
        if(appointment.getEnd().getMinute() < 10) {
            updateEndMinute.setText("0"+String.valueOf(appointment.getEnd().getMinute()));
        }
        else{
            updateEndMinute.setText(String.valueOf(appointment.getEnd().getMinute()));
        }
        updateId.setText(String.valueOf(appointment.getId()));
        updateTitle.setText(appointment.getTitle());
        updateDescription.setText(appointment.getDescription());
        updateType.setText(appointment.getType());
        updateCustomerId.setText(String.valueOf(appointment.getCustomerId()));
        updateContact.setValue(appointment.getContactName());
        updateLocation.setValue(appointment.getLocation());
        updateDate.setValue(appointment.getStart().toLocalDate());
    }
}
