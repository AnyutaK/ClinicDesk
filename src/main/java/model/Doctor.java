package model;

public class Doctor {

    private int doctorId;
    private String name;
    private String department;
    private String specialization;
    private String phone;
    private String email;

    public Doctor(int doctorId,
                  String name,
                  String department,
                  String specialization,
                  String phone,
                  String email) {

        this.doctorId = doctorId;
        this.name = name;
        this.department = department;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
    @Override
    public String toString() {
    return doctorId + " - " + name;
}
}