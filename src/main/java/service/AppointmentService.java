package service;

import dao.AppointmentDAO;
import model.Appointment;

import java.sql.Date;
import java.util.List;

public class AppointmentService {

    private final AppointmentDAO appointmentDAO;

    public AppointmentService(AppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    public List<Appointment> searchAppointments(String keyword) {
        return appointmentDAO.searchAppointments(keyword);
    }

    public Appointment getAppointment(int id) {
        return appointmentDAO.getAppointmentById(id);
    }

    public Appointment createAppointment(
            int patientId,
            int slotId,
            Date schedulingDate,
            String status) {

        return appointmentDAO.createAppointment(
                patientId,
                slotId,
                schedulingDate,
                status
        );
    }

    public boolean updateAppointment(
            int appointmentId,
            int patientId,
            int slotId,
            Date schedulingDate,
            String status) {

        return appointmentDAO.updateAppointment(
                appointmentId,
                patientId,
                slotId,
                schedulingDate,
                status
        );
    }

    public boolean deleteAppointment(int appointmentId) {
        return appointmentDAO.deleteAppointment(appointmentId);
    }
}