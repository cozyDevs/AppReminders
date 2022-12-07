package edu.appointmentreminder.appointment_reminder;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

public class MainMenuController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private String csvPath;

    @FXML
    TextField businessNameTextField;
    @FXML
    TextArea sampleOutputTextArea;
    @FXML
    Hyperlink downloadCSVHyperlink;
    @FXML
    RadioButton OneDayAdvancedReminder;
    @FXML
    RadioButton ThreeDayAdvancedReminder;
    @FXML
    RadioButton SevenDayAdvancedReminder;
    @FXML
    Label DaysInAdvancedError;
    @FXML
    Label FilePathError;
    @FXML
    Label BusinessNameError;
    @FXML
    TableView<AppointmentDataNode> tableView;
    @FXML
    TableColumn<AppointmentDataNode,Integer> lineColumn;
    @FXML
    TableColumn<AppointmentDataNode,String> firstNameColumn;
    @FXML
    TableColumn<AppointmentDataNode,String> lastNameColumn;
    @FXML
    TableColumn<AppointmentDataNode, ZonedDateTime> appointmentDateColumn;
    @FXML
    TableColumn<AppointmentDataNode,Long> phoneNumberColumn;
    @FXML
    ProgressIndicator progressIndicator;
    @FXML
    Button submitButton;
    @FXML
    Label SubmissionError;
    @FXML
    protected void uploadCSV(ActionEvent event){
        try{
            FileChooser fileChooser = new FileChooser();
            //fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.csv"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            csvPath = selectedFile.getAbsolutePath();
            if(isFilePathError()){
                throw new Exception("Empty");
            }
            LinkedList<AppointmentDataNode> appointmentDataNodes = AppointmentData.convertFileToLinkedList(csvPath);
            populateTable(appointmentDataNodes);
        }catch (Exception E){
            System.out.println(E.getLocalizedMessage());
            FilePathError.setVisible(true);
        }

    }

    //TODO: downloadCSV needs to download a csv on the users computer. The directoryChooser works but I cant figure out how to copy the file
    @FXML
    protected void downloadCSV(ActionEvent event){
        try{

            //InputStream in = getClass().getResourceAsStream("AppointmentReminderTemplate.csv");

            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Find folder to download csv template");
            File selectedDirectory = directoryChooser.showDialog(stage);

            Path pathIn = Paths.get("/edu/appointmentreminder/appointment_reminder/AppointmentReminderTemplate.csv");
            Path pathOut = Paths.get(selectedDirectory.toURI());
            System.out.println(pathIn);
            Files.copy(pathOut,pathIn,StandardCopyOption.REPLACE_EXISTING);

        }catch (Exception E){
            System.out.println("Error downloading csv");
        }

    }
    @FXML
    protected void changeSampleOutput(KeyEvent keyEvent){
        sampleOutputTextArea.setText(businessNameTextField.getText()+": Appointment Reminder for Smith, John at 12/20/2022 - 01:30 PM EST");
    }


    //javafx doesn't update the scene until after the entire method is called (I think)
    //So setSubmissionError() which does some animation doesn't show in time
    // I don't know how to fix it
    @FXML
    protected void submit(ActionEvent event){
        if(!isDaysInAdvancedError()&&!isBusinessNameError()&&!isFilePathError()){
            setSubmissionError(false);
            try{
                LinkedList<AppointmentDataNode> appointmentDataNodes = loadAppointmentData();
                boolean[] daysInAdvanced = new boolean[]{OneDayAdvancedReminder.isSelected(),ThreeDayAdvancedReminder.isSelected(),SevenDayAdvancedReminder.isSelected()};
                //status scene
                FXMLLoader statusLoader = new FXMLLoader(getClass().getResource("Status-view.fxml"));
                root = statusLoader.load();
                stage = (Stage)(((Node)event.getSource()).getScene().getWindow());
                StatusController statusController = statusLoader.getController();
                statusController.loadAppointmentData(appointmentDataNodes,daysInAdvanced,stage);
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            }catch (Exception e){
                setSubmissionError(true);

                System.out.println(e.getMessage());
            }
        }

    }
    public void setSubmissionError(Boolean submissionError){
        if(submissionError){
            SubmissionError.setVisible(true);
            submitButton.setVisible(true);
            progressIndicator.setVisible(false);
        }else{
            SubmissionError.setVisible(false);
            submitButton.setVisible(false);
            progressIndicator.setVisible(true);
        }
    }
    private boolean isDaysInAdvancedError(){
        if(!OneDayAdvancedReminder.isSelected()&&!ThreeDayAdvancedReminder.isSelected()&&!SevenDayAdvancedReminder.isSelected()){
            DaysInAdvancedError.setVisible(true);
            return true;
        }
        DaysInAdvancedError.setVisible(false);
        return false;
    }
    private boolean isBusinessNameError(){
        if(businessNameTextField.getText().isEmpty()){
           BusinessNameError.setVisible(true);
            return true;
        }
        BusinessNameError.setVisible(false);
        return false;
    }
    private boolean isFilePathError(){
        if(csvPath==null||!csvPath.contains(".csv")){
            FilePathError.setVisible(true);
            return true;
        }
        FilePathError.setVisible(false);
        return false;
    }
    private void populateTable(LinkedList<AppointmentDataNode> appointmentDataNodes){
        try{
            lineColumn.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, Integer>("line"));
            firstNameColumn.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, String>("firstName"));
            lastNameColumn.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, String>("lastName"));
            phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, Long>("phoneNumber"));
            appointmentDateColumn.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, ZonedDateTime>("dateTime"));
            ObservableList<AppointmentDataNode> appointmentDataNodes1 = FXCollections.observableArrayList();
            for(AppointmentDataNode appointmentDataNode : appointmentDataNodes){
                appointmentDataNodes1.add(appointmentDataNode);
            }
            tableView.setItems(appointmentDataNodes1);
        }
        catch (Exception E){
            System.out.println(E.getMessage());
        }

    }
    public LinkedList<AppointmentDataNode> loadAppointmentData() throws Exception {
        LinkedList<AppointmentDataNode> appointmentDataNodes = AppointmentData.convertFileToLinkedList(csvPath);
        SendAppointments.setMessage(businessNameTextField.getText()+": "+SendAppointments.getMessage());
        if(OneDayAdvancedReminder.isSelected()){
            SendAppointments.sendAppointments(appointmentDataNodes,1);
        }

        if(ThreeDayAdvancedReminder.isSelected()){
            SendAppointments.sendAppointments(appointmentDataNodes,3);
        }

        if(SevenDayAdvancedReminder.isSelected()){
            SendAppointments.sendAppointments(appointmentDataNodes,7);
        }
        return appointmentDataNodes;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
