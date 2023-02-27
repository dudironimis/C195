package com.cv.c195;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for "Reports.fxml"
 * @author Chris Vachon
 */
public class ReportsController implements Initializable {

    @FXML
    private ComboBox<String> reportsComboBox;
    @FXML
    private Button runReportButton;

    private ObservableList<String> reports = FXCollections.observableArrayList();

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
     * Populates the reports combo box
     * Lambda expressions for the run report button.
     * This makes the code more concise as they all only need a single line of code each as functions.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        reports.add("Appointments by Month and Type");
        reports.add("Contact's Schedules");
        reports.add("Total Customer Appointments");
        reportsComboBox.setItems(reports);

        runReportButton.setOnAction(actionEvent ->
                openNewScene("FXML/" + reportsComboBox.getSelectionModel().getSelectedItem() + ".fxml"));
    }
}
