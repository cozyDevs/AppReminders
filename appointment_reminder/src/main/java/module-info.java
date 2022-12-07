module edu.appointmentreminder.appointment_reminder {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires twilio;
    requires java.dotenv;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.poi;

    opens edu.appointmentreminder.appointment_reminder to javafx.fxml;
    exports edu.appointmentreminder.appointment_reminder;
}