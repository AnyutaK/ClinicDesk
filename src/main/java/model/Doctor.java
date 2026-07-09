package model;

public final class Doctor {
    private final int doctorId;
    private final String doctorName;
    private final String department;

    public Doctor(int doctorId, String doctorName, String department) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.department = department;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDepartment() {
        return department;
    }
}
