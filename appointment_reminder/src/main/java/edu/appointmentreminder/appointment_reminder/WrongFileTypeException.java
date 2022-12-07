package edu.appointmentreminder.appointment_reminder;

public class WrongFileTypeException extends Exception {
    public WrongFileTypeException(String errorMessage) {
        super(errorMessage);
    }
}