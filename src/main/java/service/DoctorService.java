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
    public Doctor getDoctor(int id){
    return doctorDAO.getDoctorById(id);
    }
    public boolean deleteDoctor(int id) {
    return doctorDAO.deleteDoctor(id);
    }

    public Doctor createDoctor(String name,
                           String department,
                           String specialization,
                           String phone,
                           String email) {

    return doctorDAO.createDoctor(
            name,
            department,
            specialization,
            phone,
            email
    );
}

public boolean updateDoctor(int id,
                            String name,
                            String department,
                            String specialization,
                            String phone,
                            String email) {

    return doctorDAO.updateDoctor(
            id,
            name,
            department,
            specialization,
            phone,
            email
    );
}
}
