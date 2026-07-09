package service;

import dao.DoctorDAO;
import model.Doctor;

import java.util.List;

public class DoctorService {

    private final DoctorDAO doctorDAO;

    public DoctorService(DoctorDAO doctorDAO) {
        this.doctorDAO = doctorDAO;
    }

    public List<Doctor> searchDoctors(String keyword) {
        return doctorDAO.searchDoctors(keyword);
    }
}
