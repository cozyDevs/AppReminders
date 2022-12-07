package edu.appointmentreminder.appointment_reminder;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

//test appointment class
public class AppointmentTester {

    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        int userInput = 0;
        while (userInput != 4) {
            try {
                System.out.print("1 - Test Node class\n" +
                        "2 - Test Csv Class\n" +
                        "3 - Test Appointment Class\n" +
                        "4 - Exit\n" +
                        "Option: ");
                userInput = input.nextInt();
                input.nextLine();
                System.out.println();
                if (userInput == 1) {
                    TestNodes();
                } else if (userInput == 2) {
                    TestCSV();
                } else if (userInput == 3) {
                    TestAppointmentReminder();
                } else if (userInput != 4) {
                    System.out.println("Enter either 1, 2, 3, or 4");
                }
            } catch (Exception E) {
                System.out.println("Error Occurred, retry\n"+E.getMessage());
            }
            System.out.println("\n");
        }

    }

    public static void TestNodes() {
        System.out.println("Enter First Name: ");
        String firstName = input.nextLine();
        System.out.println("Enter Last Name: ");
        String lastName = input.nextLine();
        try {
            ZonedDateTime dateTime = ZonedDateTime.now().plusMinutes(30);
            System.out.println("Enter Phone Number: ");
            long phoneNumber = input.nextLong();
            System.out.println("Time of appointment set to 30 minutes from current time");
            AppointmentDataNode appointmentDataNode = new AppointmentDataNode(1, dateTime, firstName, lastName, phoneNumber);
            System.out.println("appointment_reminder Node: \n" + appointmentDataNode);
        } catch (Exception E) {
            System.out.println(E.getMessage());
        }
    }

    public static void TestCSV() {
        System.out.println("Change the csv in src/main/resources/AppointmentReminderTestErrors.csv");
        try {
            URL url = AppointmentTester.class.getClassLoader().getResource("edu/appointmentreminder/appointment_reminder/AppointmentReminderTestErrors.csv");

            LinkedList<AppointmentDataNode> appointmentDataNodes = AppointmentData.convertFileToLinkedList(url.getFile());
            System.out.println("\nAll:");
            AppointmentData.printList(appointmentDataNodes);

            LinkedList<AppointmentDataNode>[] successAndFailureList = AppointmentData.getSuccessAndErrorList(appointmentDataNodes);
            System.out.println("\nSuccess:");
            AppointmentData.printList(successAndFailureList[0]);
            System.out.println("\nFailures:");
            AppointmentData.printList(successAndFailureList[1]);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }

    }

    public static void TestAppointmentReminder() throws InterruptedException {
        System.out.println("Enter phone number: ");
        long phoneNumber = input.nextLong();
        System.out.println("I set appointment for 3 days ahead of current time\n");

        System.out.println("I set 1 message to be sent 5 days in advanced (this means it will be sent right now)");
        AppointmentDataNode appointmentDataNode = new AppointmentDataNode(1, ZonedDateTime.now().plusDays(3), "testFirst", "testLast", phoneNumber);
        SendAppointments.sendAppointment(appointmentDataNode,5);
        TimeUnit.SECONDS.sleep(2);
        System.out.println(appointmentDataNode);

        System.out.println("\nI set 1 message to be sent 2 days in advanced (this means it will be scheduled for later) \n" +
                    "--To check the scheduled message go to console.twilio.com >Monitor>Logs>Messaging>");
        AppointmentDataNode appointmentDataNode1 = new AppointmentDataNode(1, ZonedDateTime.now().plusDays(3), "testFirst", "testLast", phoneNumber);
        SendAppointments.sendAppointment(appointmentDataNode1,2);
        TimeUnit.SECONDS.sleep(2);
        System.out.println(appointmentDataNode1);

        System.out.println("\nI set appointment for yesterday in relation to the current time)");
        AppointmentDataNode appointmentDataNode2 = new AppointmentDataNode(1, ZonedDateTime.now().minusDays(1), "testFirst", "testLast", phoneNumber);
        SendAppointments.sendAppointment(appointmentDataNode2,1);
        TimeUnit.SECONDS.sleep(2);
        System.out.println(appointmentDataNode2);
        }

}
