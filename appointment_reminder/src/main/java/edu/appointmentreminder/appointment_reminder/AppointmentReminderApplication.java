package edu.appointmentreminder.appointment_reminder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;


public class AppointmentReminderApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("MainMenu-view.fxml"));
            stage.setTitle("Appointment Reminder");
            stage.setScene(new Scene(root,1170,660));
            stage.setMinHeight(660);
            stage.setMinWidth(1170);
            stage.setX(Screen.getPrimary().getBounds().getMaxX());
            stage.setY(Screen.getPrimary().getBounds().getMaxY());
            stage.show();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) {
        launch();
    }
}
