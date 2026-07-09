package model;

public final class Patient {
    private final int patientId;
    private final String name;
    private final String insurance;

    public Patient(int patientId, String name, String insurance) {
        this.patientId = patientId;
        this.name = name;
        this.insurance = insurance;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public String getInsurance() {
        return insurance;
    }
}
