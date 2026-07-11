package model;

import java.sql.Date;

public class Patient {

    private int patientId;
    private String name;
    private java.sql.Date dob;
    private String sex;
    private String insurance;

    public Patient(
            int patientId,
            String name,
            String sex,
            java.sql.Date dob,
            String insurance) {

        this.patientId = patientId;
        this.name = name;
        this.dob = dob;
        this.sex = sex;
        this.insurance = insurance;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public java.sql.Date getDob() {
        return dob;
    }

    public String getSex() {
        return sex;
    }

    public String getInsurance() {
        return insurance;
    }
    @Override
    public String toString() {
        return name;
    }
}