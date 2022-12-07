package edu.appointmentreminder.appointment_reminder;

import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;
import io.github.cdimascio.dotenv.Dotenv;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import java.time.ZonedDateTime;

public class SendAppointments {

    private static Dotenv dotenv = Dotenv.load();
    public static final String ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = dotenv.get("TWILIO_AUTH_TOKEN");

    private static String message = "Appointment Reminder for #lastName#, #firstName# at #appointmentDateTime#"; //default message
    public static String getMessage() {
        return message;
    }
    public static void setMessage(String message) {
        SendAppointments.message = message;
    }

    /**
     * Iterates through linkedList of appointmentNodes and uses an int daysInAdvanced to call method sendAppointment()
     * @param appointmentDataNodes appointmentDataNodes which holds ALL the row/patient data
     * @param daysInAdvanced      the amount of days the user wants to remind the patient before their appointment
     */
    public static void sendAppointments(LinkedList<AppointmentDataNode> appointmentDataNodes, int daysInAdvanced) {
        Iterator dataNodes = appointmentDataNodes.iterator();
        while (dataNodes.hasNext()) {
            AppointmentDataNode dataNode = (AppointmentDataNode) dataNodes.next();
            try {
                sendAppointment(dataNode, daysInAdvanced);
            } catch (Exception E) {
                dataNode.setErrorMessage("Error:"+E.getMessage());
            }


        }
    }

    /**
     * Method sends single reminder text message now or scheduled for the future based on how far the appointment reminder is from the current time
     *
     * @param appointmentDataNode appointmentDataNode which holds the row/patient information
     * @param daysInAdvanced      the amount of days the user wants to remind the patient before their appointment
     * @throws IndexOutOfBoundsException
     */
    public static void sendAppointment(AppointmentDataNode appointmentDataNode, int daysInAdvanced){
        if(appointmentDataNode.getSID()==null){//checks to see if the message has already been successfully created in the past
            ZonedDateTime sendDateAndTime = appointmentDataNode.getDateTime().minusDays(daysInAdvanced);
            ZonedDateTime currentTime = ZonedDateTime.now(appointmentDataNode.getDateTime().getZone());
            if (sendDateAndTime.getYear() <= currentTime.getYear() + 2) {//checks if appointment send date is over two years from current time
                if (appointmentDataNode.getDateTime().isAfter(currentTime)) {//checks if appointment dateTime is before current dateTime
                    if(sendDateAndTime.isBefore(currentTime)){//checks if appointment send dateTime is after current dateTime
                        sendAppointmentNow(appointmentDataNode);
                    }else{
                        sendAppointmentLater(appointmentDataNode, daysInAdvanced);
                    }
                }else {
                    appointmentDataNode.setErrorMessage("Error: Appointment has past");
                }
            }else {
                appointmentDataNode.setErrorMessage("Error: Appointment is over 2 years in the future");
            }
        }
    }

    /**
     * Method sends a message now to the node's phone number
     * @param node this contains appointment data which is used to send and format message
     */
    public static void sendAppointmentNow(AppointmentDataNode node) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("+1" + node.getPhoneNumber()),
                        "MGb591ce9eb4f87d9bd2b5815ccff5ce7a",
                        createMessage(node))
                .create();

        node.deleteErrorMessage();
        node.setSID(message.getSid());
    }

    /**
     * Method schedules a message days in advanced of the appointment to the Node's phone number
     * @param node this node contains appointment data which is used to send and format message
     * @param daysInAdvanced that the message will be sent in relation to the appointment date
     */
    public static void sendAppointmentLater(AppointmentDataNode node, int daysInAdvanced) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("+1" + node.getPhoneNumber()),
                        "MGb591ce9eb4f87d9bd2b5815ccff5ce7a",
                        createMessage(node))
                .setSendAt(
                        node.getDateTime().minusDays(daysInAdvanced))
                .setScheduleType(Message.ScheduleType.FIXED)
                .create();
        node.deleteErrorMessage();
        node.setSID(message.getSid());
    }

    /**
     * method replaces fillers in message with node data
     *
     * @param node
     * @return complete message
     */
    public static String createMessage(AppointmentDataNode node) {
        return message.replaceAll("#firstName#", node.getFirstName()).replaceAll("#lastName#", node.getLastName()).replaceAll("#appointmentDateTime#", node.getDateTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy - hh:mm a z")));

    }

}
