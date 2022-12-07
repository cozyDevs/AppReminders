package edu.appointmentreminder.appointment_reminder;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class AppointmentDataNode {
    private int line;
    private ZonedDateTime dateTime;
    private String firstName;
    private String lastName;
    private long phoneNumber;
    private String SID;
    /**
     * If error occurred then String will be "Error: error message"
     * If no errors occurred or was never sent then String will be null
     */
    private String errorMessage;
    private String status = getStatus();

    /**
     * @param line
     * @param dateTime
     * @param firstName
     * @param lastName
     * @param phoneNumber
     */
    AppointmentDataNode(int line, ZonedDateTime dateTime, String firstName, String lastName, long phoneNumber) {
        this.line = line;
        this.dateTime = dateTime;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
    AppointmentDataNode(int line, ZonedDateTime dateTime, String firstName, String lastName, long phoneNumber,String SID) {
        this.line = line;
        this.dateTime = dateTime;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.SID = SID;
    }

    //-----getters-----\\

    /**
     * @return the row number that the data was collected
     */
    public int getLine() {
        return line;
    }

    /**
     * @return the date and time as ZonedDateTime
     */
    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    /**
     * @return the first name of patient
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return the lastName of patient
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return the phone number of patient
     */
    public long getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @return the status of the message
     */
    public String getStatus() {
        if(SID==null){
            return "NO RECORD OF MESSAGE BEING CREATED";
        }
        else if(SID.startsWith("SM")){
            Twilio.init(SendAppointments.ACCOUNT_SID, SendAppointments.AUTH_TOKEN);
            Message message = Message.fetcher(SID).fetch();
            String dateSent = null;
            String dateUpdated = null;
            if(message.getDateSent()!=null){
                dateSent = message.getDateSent().withZoneSameInstant(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("MM/dd/yyyy - hh:mm:ss a z"));
            }
            if(message.getDateUpdated()!=null){
                dateUpdated = message.getDateUpdated().withZoneSameInstant(ZoneId.of("America/New_York")).format(DateTimeFormatter.ofPattern("MM/dd/yyyy - hh:mm:ss a z"));
            }
            return String.format("Status: %s\t| Sent on: %s\t| Updated on: %s",message.getStatus(),dateSent,dateUpdated);
        }
        else {
            return "CANNOT READ SID";
        }
    }

    /**
     *
     * @return SID id linked to the message
     */
    public String getSID(){return SID;}

    /**
     *
     * @return true if there was an error; false if no error
     */
    public boolean isError(){
        return errorMessage!=null;
    }

    /**
     *
     * @return errorMessage if there is an error
     */
    public String getErrorMessage(){
        if(isError()){
            return errorMessage;
        }
        else {
            return "No Error";
        }
    }
    //-----setters-----\\

    /**
     *
     * @param dateTime date and time of appointment
     */
    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    /**
     *
     * @param firstName of patient
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @param lastName of patient
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @param phoneNumber of patient
     */
    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     *
     * @param SID id linked to message
     */
    public void setSID(String SID) {
        if(SID.startsWith("SM")){
            this.SID = SID;
        }
        else{
            System.out.println("Line: "+line+" - Not a valid SID");
        }

    }

    /**
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage){
            this.errorMessage = errorMessage;

    }
    public void deleteErrorMessage(){
        this.errorMessage = null;
    }


    /**
     * @return message is different depending on if message was successfully sent
     */
    @Override
    public String toString() {
        if(isError()){
            return String.format("Line: %s \t| Date: %s\t| Time: %s\t| Name: %s, %s\t| Number: %s\t| %s", line, dateTime.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")), dateTime.format(DateTimeFormatter.ofPattern("hh:mm a")), lastName, firstName, phoneNumber,errorMessage);
        }else {
            return String.format("Line: %s \t| Date: %s\t| Time: %s\t| Name: %s, %s\t| Number: %s\t| %s", line, dateTime.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")), dateTime.format(DateTimeFormatter.ofPattern("hh:mm a")), lastName, firstName, phoneNumber, getStatus());
        }


    }

}
