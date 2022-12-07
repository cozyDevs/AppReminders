package edu.appointmentreminder.appointment_reminder;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.Scanner;

public class AppointmentData {

    /**
     * Method converts csv or Excel file to linkedList
     * xlxs is read using Apache poi
     *
     * @param filePath
     * @return linkedList of the data that was on each row of file
     * @throws WrongFileTypeException
     */

    public static LinkedList<AppointmentDataNode> convertFileToLinkedList(String filePath) throws WrongFileTypeException {
        LinkedList<AppointmentDataNode> appointmentDataFromCSV = new LinkedList<>();
        //throw an error if the csv is empty
        if (filePath.endsWith(".xlxs")) { //convert Excel spreadsheet to linked list(Probably not going to do .xlxs so don't worry about it)
            //https://www.youtube.com/watch?v=ipjl49Hgsg8&ab_channel=SDET-QAAutomation

        } else if (filePath.endsWith(".csv")) {//convert csv to linked list
            FileReader fileReader = null;
            try{
                Scanner sc = new Scanner(new File(filePath));
                sc.useDelimiter(",");
                sc.nextLine();
                int lineNumber = 1;
                while (sc.hasNextLine()){
                   String line  = sc.nextLine();
                   String values[] = line.split(",");
                   if(values.length==5){ // 5 values, 4 commas in file - change if we have more values
                       try{
                           ZonedDateTime dateTime = createValidZoneDateTime(values[3],values[4]);
                           appointmentDataFromCSV.add(new AppointmentDataNode(lineNumber, dateTime, values[0], values[1], Long.parseLong(values[2])));
                       }catch (Exception e){
                           appointmentDataFromCSV.add(new AppointmentDataNode(lineNumber, ZonedDateTime.of(2000,11,11,11,11,11,11,ZoneId.systemDefault()), values[0], values[1], Long.parseLong(values[2])));
                           appointmentDataFromCSV.getLast().setErrorMessage("Error: Couldn't read dateAndTime");
                           appointmentDataFromCSV.getLast().setErrorMessage(e.getMessage());
                       }
                   }
                   else {
                       appointmentDataFromCSV.add(new AppointmentDataNode(lineNumber, ZonedDateTime.of(2000,11,11,11,11,11,11,ZoneId.systemDefault()), "N/a", "N/a", 0));
                       appointmentDataFromCSV.getLast().setErrorMessage("Error: Not enough commas were present");
                   }
                    lineNumber++;
                }

            }catch (Exception e){
                throw new WrongFileTypeException(e.getMessage());
            }
        } else {
            throw new WrongFileTypeException("File type is not .xlxs or csv");
        }
        return appointmentDataFromCSV;
    }

    /**
     * Method correctly formats a given date and time to a ZoneDateTime
     * @param date format needs to be MM/dd/YYYY
     * @param time format needs to be hh:mm:ss pm or hh:mm:ss am
     * @return
     * @throws Exception
     */
    private static ZonedDateTime createValidZoneDateTime(String date, String time) throws Exception{
        //separate month, day, and year
        String[] dateArray = date.split("/");
        //separate hour, minutes, seconds, and am/pm
        String[] timeArray  = time.split(" ");
        String timeField = timeArray[1].toLowerCase();
        timeArray = timeArray[0].split(":");
        //convert 12 hours to 24 hours
        int hour = Integer.parseInt(timeArray[0]);
        if(timeField.toLowerCase().equals("pm")){
            hour += 12;
        }
        LocalDate localDate = LocalDate.of(Integer.parseInt(dateArray[2]),Integer.parseInt(dateArray[0]),Integer.parseInt(dateArray[1]));
        LocalTime localTime = LocalTime.of(hour,Integer.parseInt(timeArray[1]),Integer.parseInt(timeArray[2]));
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDate,localTime,ZoneId.systemDefault());
        return zonedDateTime;
    }

    /**
     * getSuccessAndErrorList creates an array of size 2 that contain the success nodes and the failure nodes
     * @param appointmentDataNodes
     * @return ArrayIndex[0] gives nodes that are successful and  ArrayIndex[1] gives nodes that are failures
     */
    public static LinkedList<AppointmentDataNode>[] getSuccessAndErrorList(LinkedList<AppointmentDataNode> appointmentDataNodes) {
        LinkedList<AppointmentDataNode>[] successAndErrorList = new LinkedList[]{new LinkedList<AppointmentDataNode>(), new LinkedList<AppointmentDataNode>()};
        for(AppointmentDataNode appointmentDataNode: appointmentDataNodes){
            if(!appointmentDataNode.isError()){
                successAndErrorList[0].add(appointmentDataNode);
            }else {
                successAndErrorList[1].add(appointmentDataNode);
            }
        }
        return successAndErrorList;
    }

    /**
     * printList prints a node on separate lines
     * @param appointmentDataNodes
     */
    public static void printList(LinkedList<AppointmentDataNode> appointmentDataNodes){
        for(AppointmentDataNode appointmentDataNode: appointmentDataNodes){
            System.out.println(appointmentDataNode);
        }
    }
}

