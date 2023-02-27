package com.cv.c195;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

/**
 * Controller for "Contact's Schedules.fxml"
 * @author Chris Vachon
 */
public class ScheduleReportController implements Initializable {

    @FXML
    private TabPane schedulePane;
    private ObservableList<ObservableList<Appointment>> appointmentsList = FXCollections.observableArrayList();

    /**
     * Creates a tab for each contact found in the connected database.
     * Each tab is labeled with the contacts name, and has their schedule filled out.
     * The schedules are in chronological order.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DatabaseController.query("""
                SELECT contacts.Contact_Name, appointments.Appointment_ID, appointments.Title, appointments.Type,
                appointments.Description, appointments.Start, appointments.End, appointments.Customer_ID,
                appointments.Location, customers.Customer_Name, appointments.user_id
                FROM contacts
                LEFT JOIN appointments ON contacts.Contact_ID = appointments.Contact_ID
                LEFT JOIN customers ON customers.Customer_ID = appointments.Customer_ID
                ORDER BY contacts.Contact_Name desc,
                appointments.start asc""");
        ResultSet qr = DatabaseController.getQueryResults();

        try{
            String currentContact = "";
            ObservableList<Appointment> appointment;
            int aListNum = -1;
            while(qr.next()){
                Appointment appt = null;
                //Convert times from UTC to local for schedule
                if(qr.getTimestamp("Start") != null) {
                    LocalDateTime st = qr.getTimestamp("Start").toLocalDateTime();
                    LocalDateTime ed = qr.getTimestamp("End").toLocalDateTime();
                    ZonedDateTime zdt = ZonedDateTime.of(st, ZoneId.systemDefault());
                    ZonedDateTime zdtUTC = ZonedDateTime.of(st, ZoneId.of("UTC"));
                    int offsetUTC = zdt.getOffset().compareTo(zdtUTC.getOffset());
                    st = st.minusSeconds(offsetUTC);
                    ed = ed.minusSeconds(offsetUTC);
                    //Create appointment to go into observable list
                    appt = new Appointment(qr.getInt("Appointment_Id"), qr.getString("Customer_Name"),
                            qr.getInt("Customer_Id"), qr.getString("Contact_Name"), qr.getString("Title"),
                            qr.getString("Description"), qr.getString("Location"), qr.getString("Type"),
                            st, ed, qr.getInt("User_Id"));
                }
                    //If the Contact's name has changed, start a new tab, tableview, and observable list
                    if (!qr.getString("Contact_Name").equals(currentContact)) {
                        aListNum++;
                        currentContact = qr.getString("Contact_Name");
                        Tab x = new Tab();
                        appointment = FXCollections.observableArrayList();
                        appointmentsList.add(appointment);
                        x.setText(qr.getString("Contact_Name"));
                        TableView<Appointment> appointments = new TableView<>();
                        TableColumn<Appointment, Integer> appointmentId = new TableColumn<>("Appointment ID");
                        appointmentId.setCellValueFactory(new PropertyValueFactory<>("id"));
                        TableColumn<Appointment, String> appointmentTitle = new TableColumn<>("Title");
                        appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
                        TableColumn<Appointment, String> appointmentDescription = new TableColumn<>("Description");
                        appointmentDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
                        TableColumn<Appointment, String> appointmentStart = new TableColumn<>("Start");
                        appointmentStart.setCellValueFactory(new PropertyValueFactory<>("startStr"));
                        TableColumn<Appointment, String> appointmentEnd = new TableColumn<>("End");
                        appointmentEnd.setCellValueFactory(new PropertyValueFactory<>("endStr"));
                        TableColumn<Appointment, Integer> appointmentCustomerId = new TableColumn<>("Customer ID");
                        appointmentCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
                        appointmentsList.get(aListNum).add(appt);
                        appointments.setItems(appointmentsList.get(aListNum));
                        appointments.getColumns().setAll(appointmentId, appointmentTitle, appointmentDescription,
                                appointmentStart, appointmentEnd, appointmentCustomerId);
                        x.setContent(appointments);
                        schedulePane.getTabs().add(x);
                    }
                    else {
                        appointmentsList.get(aListNum).add(appt);
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
}
