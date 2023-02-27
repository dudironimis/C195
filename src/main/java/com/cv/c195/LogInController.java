package com.cv.c195;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for "login.fxml"
 * @author Chris Vachon
 */
public class LogInController implements Initializable {
    @FXML
    Label labelZone;
    @FXML
    PasswordField textFieldPW;
    @FXML
    Label labelUser;
    @FXML
    Label labelPW;
    @FXML
    TextField textFieldUser;
    @FXML
    Button buttonSignIn;

    /**
     * Translates the labels to english or French, depending on the location of the local machine
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resourceBundle = ResourceBundle.getBundle("Languages");
        labelZone.setText(ZoneId.systemDefault().getId());
        labelUser.setText(resourceBundle.getString("Username"));
        labelPW.setText(resourceBundle.getString("Password"));
        buttonSignIn.setText(resourceBundle.getString("SignIn"));

    }

    /**
     * Attempts to sign in to the main application. If the username and password don't match what is found in the connected
     * database, an error appears. The error will be in English or French, depending on the location of the local machine.
     * If the sign in information does match, the main application is shown.
     * Lambda expression used to close the database before the program is ended.
     */
    public void signInButtonPushed(){

        if(DatabaseController.logIn(textFieldUser.getText(),textFieldPW.getText())){
            try{
                FileWriter loginActivity = new FileWriter("login_activity.txt", true);
                loginActivity.write(" Success" + "\n\n");
                loginActivity.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            //Closes Log In page on successful log in
            Stage stage = (Stage) buttonSignIn.getScene().getWindow();
            stage.close();
            //Opens main page
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/main.fxml"));
                Parent parent = (Parent) fxmlLoader.load();
                stage = new Stage();
                stage.setScene(new Scene(parent));
                stage.show();
                //Closes database before ending the program
                stage.setOnCloseRequest(we -> DatabaseController.closeDatabase());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else{
            try{
                FileWriter loginActivity = new FileWriter("login_activity.txt", true);
                loginActivity.write(" Fail" + "\n\n");
                loginActivity.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Languages");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(resourceBundle.getString("DBErrorTitle"));
            alert.setHeaderText(resourceBundle.getString("DBErrorHText"));
            alert.setContentText(resourceBundle.getString("DBErrorCText"));
            alert.showAndWait();
        }
    }
}