package edu.appointmentreminder.appointment_reminder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class StatusController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private LinkedList<AppointmentDataNode> patients;
    //success
    @FXML
    TableView<AppointmentDataNode> successTable;
    @FXML
    TableColumn<AppointmentDataNode,Integer> successLine;
    @FXML
    TableColumn<AppointmentDataNode,String> successFirstName;
    @FXML
    TableColumn<AppointmentDataNode,String> successLastName;
    @FXML
    TableColumn<AppointmentDataNode, ZonedDateTime> successAppointmentDate;
    @FXML
    TableColumn<AppointmentDataNode,Long> successPhoneNumber;
    @FXML
    TableColumn<AppointmentDataNode, String> successStatus;
    //failure
    @FXML
    TableView<AppointmentDataNode> failureTable;
    @FXML
    TableColumn<AppointmentDataNode,Integer> failureLine;
    @FXML
    TableColumn<AppointmentDataNode,String> failureFirstName;
    @FXML
    TableColumn<AppointmentDataNode,String> failureLastName;
    @FXML
    TableColumn<AppointmentDataNode, ZonedDateTime> failureAppointmentDate;
    @FXML
    TableColumn<AppointmentDataNode,Long> failurePhoneNumber;
    @FXML
    TableColumn<AppointmentDataNode,String> failureErrorMessage;
    private boolean[] daysInAdvanced;

    @FXML
    protected void onResubmitButtonReleased(ActionEvent event) {
        populateTable(patients);
        failureTable.refresh();
        successTable.refresh();
    }

    //TODO: Make Status column display status and not SID
    //TODO: For some reason the css is being removed for when populateTable is called, so it needs to reapply the css or something like that
    protected void populateTable(LinkedList<AppointmentDataNode>appointmentDataNodes){
        try{
            LinkedList<AppointmentDataNode>[] successAndFailure = AppointmentData.getSuccessAndErrorList(appointmentDataNodes);
            successLine.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, Integer>("line"));
            successFirstName.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, String>("firstName"));
            successLastName.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, String>("lastName"));
            successPhoneNumber.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, Long>("phoneNumber"));
            successAppointmentDate.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, ZonedDateTime>("dateTime"));
            successStatus.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, String>("status"));

            failureLine.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, Integer>("line"));
            failureFirstName.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, String>("firstName"));
            failureLastName.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, String>("lastName"));
            failurePhoneNumber.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, Long>("phoneNumber"));
            failureAppointmentDate.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, ZonedDateTime>("dateTime"));
            failureErrorMessage.setCellValueFactory(new PropertyValueFactory<AppointmentDataNode, String>("errorMessage"));


            ObservableList<AppointmentDataNode> appointmentDataNodesSuccess = FXCollections.observableArrayList();
            ObservableList<AppointmentDataNode> appointmentDataNodesFailure = FXCollections.observableArrayList();
            for(AppointmentDataNode appointmentDataNode : successAndFailure[0]){
                appointmentDataNodesSuccess.add(appointmentDataNode);
            }
            for(AppointmentDataNode appointmentDataNode : successAndFailure[1]){
                appointmentDataNodesFailure.add(appointmentDataNode);
            }
            successTable.setItems(appointmentDataNodesSuccess);
            failureTable.setItems(appointmentDataNodesFailure);
            successTable.applyCss();
            failureTable.applyCss();
        }
        catch (Exception E){
            System.out.println(E.getMessage());
        }
    }

    public void loadAppointmentData(LinkedList<AppointmentDataNode> appointmentDataNodes,boolean[] daysInAdvanced,Stage stage){
        this.daysInAdvanced = daysInAdvanced;
        this.stage = stage;
        patients = appointmentDataNodes;
        populateTable(appointmentDataNodes);
    }

    //TODO: complete updateNode
    public void updateNode() {
        try {
            AppointmentDataNode appointmentDataNode = failureTable.getSelectionModel().getSelectedItem();
            //when user hits submit on this new stage than the following lines will update appointmentDataNode with the new information
            FXMLLoader updateLoader = new FXMLLoader(getClass().getResource("UpdateNode-view.fxml"));
            root = updateLoader.load();
            UpdateNodeController updateNodeController = updateLoader.getController();
            updateNodeController.uploadNode(appointmentDataNode,daysInAdvanced,stage);

            stage = new Stage();
            stage.setTitle("Line #"+ appointmentDataNode.getLine());
            stage.setScene(new Scene(root,500,500));
            stage.setMinHeight(500);
            stage.setMinWidth(500);
            stage.setX(Screen.getPrimary().getBounds().getMaxX()/2);
            stage.setY(Screen.getPrimary().getBounds().getMaxY()/2);
            stage.show();

            populateTable(patients);
            successTable.refresh();
            failureTable.refresh();
        }catch (Exception e){

        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
