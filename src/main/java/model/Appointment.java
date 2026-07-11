package model;

import java.sql.Timestamp;

public class Appointment {

    private int appointmentId;
    private int patientId;
    private String patientName;
    private int doctorId;
    private String doctorName;
    private int slotId;
    private Timestamp appointmentDateTime;
    private String status;

    public Appointment(
            int appointmentId,
            int patientId,
            String patientName,
            int doctorId,
            String doctorName,
            int slotId,
            Timestamp appointmentDateTime,
            String status) {

        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.slotId = slotId;
        this.appointmentDateTime = appointmentDateTime;
        this.status = status;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public int getSlotId() {
        return slotId;
    }

    public Timestamp getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public String getStatus() {
        return status;
    }
}