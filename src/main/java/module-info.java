module com.cv.c195 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.cv.c195 to javafx.fxml;
    exports com.cv.c195;
}