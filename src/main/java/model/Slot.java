package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Slot {

    private final int slotId;
    private final int doctorId;
    private final LocalDate appointmentDate;
    private final LocalTime appointmentTime;

    public Slot(
            int slotId,
            int doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime) {

        this.slotId = slotId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
    }

    public int getSlotId() {
        return slotId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    @Override
    public String toString() {
        return appointmentDate + "  " + appointmentTime;
    }
}