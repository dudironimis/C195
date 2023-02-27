package com.cv.c195;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller for "Total Customer Appointments.fxml"
 * @author Chris Vachon
 */
public class CustomerAppointmentsReportController implements Initializable {

    @FXML
    private BarChart customerAppointmentsBarChart;

    /**
     * Populates the bar graph with the number of appointments each customer has had found in the connected database.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        XYChart.Series customerAppointmentSeries = new XYChart.Series();

        ArrayList<String> customers = new ArrayList<>();
        ArrayList<Integer> customersTotal = new ArrayList<>();


        if(DatabaseController.query("""
                SELECT appointments.Customer_ID, customers.Customer_Name FROM Appointments
                INNER JOIN customers on appointments.Customer_ID = customers.Customer_ID
                """)){
            ResultSet qr = DatabaseController.getQueryResults();

            try{
                while(qr.next()){

                    if(customers.contains(qr.getString("Customer_Name"))){
                        int ind = customers.indexOf(qr.getString("Customer_Name"));
                        customersTotal.set(ind, customersTotal.get(ind)+1);
                    }
                    else{
                        customers.add(qr.getString("Customer_Name"));
                        customersTotal.add(1);
                    }
                }

                qr.close();
                DatabaseController.closeResultSet();
                DatabaseController.closeStatement();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        for(int i = 0; i < customers.size(); i++){
            customerAppointmentSeries.getData().add(new XYChart.Data<String, Integer>(customers.get(i), customersTotal.get(i)));
        }

        customerAppointmentsBarChart.getData().addAll(customerAppointmentSeries);
    }
}
