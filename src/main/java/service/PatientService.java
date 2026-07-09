package service;

import dao.PatientDAO;
import model.Patient;

import java.util.List;

public class PatientService {

    private final PatientDAO patientDAO;

    public PatientService(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    public List<Patient> searchPatients(String keyword) {
        return patientDAO.searchPatients(keyword);
    }

    public Patient getPatient(int id) {
        return patientDAO.getPatientById(id);
    }

    public Patient createPatient(String name, java.sql.Date dob, String insurance) {
        return patientDAO.createPatient(name, dob, insurance);
    }

    public boolean deletePatient(int id) {
        return patientDAO.deletePatient(id);
    }
}
