package utils;

public final class DBConfig {
    public static final String JDBC_URL = System.getenv().getOrDefault(
        "CLINICDESK_JDBC_URL",
        "jdbc:postgresql://localhost:5432/clinicdesk"
    );

    public static final String USER = System.getenv().getOrDefault(
        "CLINICDESK_USER",
        "clinicdesk"
    );

    public static final String PASSWORD = System.getenv().getOrDefault(
        "CLINICDESK_PASSWORD",
        "clinicdesk123"
    );

    private DBConfig() {
    }
}
