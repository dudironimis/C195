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
 * Controller for "Appointments by Month and Type.fxml"
 * @author Chris Vachon
 */
public class AppointmentReportController implements Initializable {

    @FXML
    private BarChart<String, Integer> monthBarChart;
    @FXML
    private BarChart<String, Integer> typesBarChart;

    /**
     * Populates the month bar chart with the number of appointments for each month found in the connected database.
     * Populates the type bar chart with the number of appointments for each type found in the connected database.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        XYChart.Series<String, Integer> monthSeries = new XYChart.Series<>();
        XYChart.Series<String, Integer> typeSeries = new XYChart.Series<>();

        int[] monthTotals = new int[12];
        ArrayList<String> types = new ArrayList<>();
        ArrayList<Integer> typesTotal = new ArrayList<>();
        
        for(int i = 0; i < 12; i++){
            monthTotals[i] = 0;
        }

        if(DatabaseController.query("SELECT * FROM Appointments")){
            ResultSet qr = DatabaseController.getQueryResults();

            try{
                while(qr.next()){
                    int monthVal = qr.getTimestamp("Start").toLocalDateTime().getMonthValue();
                    monthTotals[monthVal-1]++;

                    if(types.contains(qr.getString("Type"))){
                        int ind = types.indexOf(qr.getString("Type"));
                        typesTotal.set(ind, typesTotal.get(ind)+1);
                    }
                    else{
                        types.add(qr.getString("Type"));
                        typesTotal.add(1);
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

        for(int i = 0; i < types.size(); i++){
            typeSeries.getData().add(new XYChart.Data<>(types.get(i), typesTotal.get(i)));
        }

        monthSeries.getData().add(new XYChart.Data<>("January", monthTotals[0]));
        monthSeries.getData().add(new XYChart.Data<>("February", monthTotals[1]));
        monthSeries.getData().add(new XYChart.Data<>("March", monthTotals[2]));
        monthSeries.getData().add(new XYChart.Data<>("April", monthTotals[3]));
        monthSeries.getData().add(new XYChart.Data<>("May", monthTotals[4]));
        monthSeries.getData().add(new XYChart.Data<>("June", monthTotals[5]));
        monthSeries.getData().add(new XYChart.Data<>("July", monthTotals[6]));
        monthSeries.getData().add(new XYChart.Data<>("August", monthTotals[7]));
        monthSeries.getData().add(new XYChart.Data<>("September", monthTotals[8]));
        monthSeries.getData().add(new XYChart.Data<>("October", monthTotals[9]));
        monthSeries.getData().add(new XYChart.Data<>("November", monthTotals[10]));
        monthSeries.getData().add(new XYChart.Data<>("December", monthTotals[11]));

        monthBarChart.getData().addAll(monthSeries);
        typesBarChart.getData().addAll(typeSeries);
    }
}
