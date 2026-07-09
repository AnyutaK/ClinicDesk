package service;

import dao.AppointmentDAO;
import model.Slot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class AppointmentService {

    private final AppointmentDAO dao = new AppointmentDAO();

    public List<Slot> getAvailableSlots(LocalDate date) {
        try {
            return dao.getAvailableSlots(date);
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }

    public List<String> getBookingsForDay(LocalDate date) {
        try {
            return dao.getBookingsForDay(date);
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }

    /**
     * Attempt to book using existing patient id and slot id. For the demo UI we accept
     * a fixed demo patient id (1) if caller doesn't have one. Returns true on success.
     */
    public boolean bookAppointment(LocalDate date, LocalTime time, String patientRef) {
        List<Slot> slots = getAvailableSlots(date);
        Slot match = null;
        for (Slot s : slots) {
            if (s.getTime().equals(time)) {
                match = s;
                break;
            }
        }
        if (match == null) return false;

        // For now use patient id 1 as demo patient (assumes sample data exists). A real
        // implementation would resolve `patientRef` to a patient_id or create one.
        int demoPatientId = 1;
        String result = dao.callBookAppointment(demoPatientId, match.getSlotId());
        return result != null && result.toLowerCase().contains("success");
    }
}
