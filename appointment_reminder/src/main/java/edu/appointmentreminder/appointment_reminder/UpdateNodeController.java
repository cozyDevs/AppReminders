package edu.appointmentreminder.appointment_reminder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UpdateNodeController {
    private Stage oldStage;
    private Stage currentStage;
    @FXML
    Label lineLabel;
    @FXML
    TextField firstNameTextField;
    @FXML
    TextField lastNameTextField;
    @FXML
    TextField appointmentTimeTextField;
    @FXML
    DatePicker appointmentDateDatePicker;
    @FXML
    TextField phoneNumberTextField;
    private  AppointmentDataNode appointmentDataNode;
    private static boolean[] daysInAdvanced;

    @FXML
    public void updateNode(ActionEvent event){
        appointmentDataNode.setDateTime(ZonedDateTime.of(appointmentDateDatePicker.getValue(), LocalTime.parse(appointmentTimeTextField.getText(),DateTimeFormatter.ofPattern("hh:mm a")),appointmentDataNode.getDateTime().getZone()));
        appointmentDataNode.setFirstName(firstNameTextField.getText());
        appointmentDataNode.setLastName(lastNameTextField.getText());
        appointmentDataNode.setPhoneNumber(Long.parseLong(phoneNumberTextField.getText()));
        if(daysInAdvanced[0]){
            SendAppointments.sendAppointment(appointmentDataNode,1);
        }
        if(daysInAdvanced[1]){
            SendAppointments.sendAppointment(appointmentDataNode,3);
        }
        if(daysInAdvanced[2]){
            SendAppointments.sendAppointment(appointmentDataNode,7);
        }
        Stage currentStage = ((Stage)((Node)event.getSource()).getScene().getWindow());
        currentStage.close();

    }

    public void uploadNode(AppointmentDataNode appointmentDataNode, boolean[] daysInAdvanced, Stage oldSstage){
        this.appointmentDataNode = appointmentDataNode;
        this.daysInAdvanced = daysInAdvanced;
        this.oldStage = oldStage;
        lineLabel.setText("Line #"+appointmentDataNode.getLine());
        firstNameTextField.setText(appointmentDataNode.getFirstName());
        lastNameTextField.setText(appointmentDataNode.getLastName());
        appointmentTimeTextField.setText(appointmentDataNode.getDateTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        ZonedDateTime dateAndTime = appointmentDataNode.getDateTime();
        appointmentDateDatePicker.setValue(dateAndTime.toLocalDate());

    }

}
