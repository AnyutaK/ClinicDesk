package service;

import dao.PatientDAO;
import model.Patient;

import java.sql.Date;
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
    
    public Patient createPatient(String name, String sex , Date dob, String insurance) {
        return patientDAO.createPatient(name, sex, dob, insurance);
    }

    public boolean deletePatient(int id) {
        return patientDAO.deletePatient(id);
    }

    public boolean updatePatient(int id,
                             String name,
                             String sex,
                             java.sql.Date dob,
                             String insurance) {

    return patientDAO.updatePatient(
            id,
            name,
            sex,
            dob,
            insurance
    );

}
public List<Patient> getAllPatients() {
    return patientDAO.getAllPatients();
}
}

