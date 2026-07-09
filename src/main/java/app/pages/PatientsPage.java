package app.pages;

import dao.PatientDAO;
import model.Patient;
import service.PatientService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.concurrent.Task;
import javafx.collections.FXCollections;

import java.util.List;

public class PatientsPage extends VBox {

    private final ListView<Patient> patientList;
    private final PatientService patientService;
    private final TextField searchField;

    public PatientsPage() {
        getStyleClass().add("patients-page");
        setPadding(new Insets(24));
        setSpacing(24);

        this.patientService = new PatientService(new PatientDAO());
        this.patientList = createPatientList();
        this.searchField = new TextField();

        getChildren().addAll(createHeader(), createSearchBar(), patientList);
        refreshPatientCards("");
    }

    private VBox createHeader() {
        VBox header = new VBox(6);
        header.getStyleClass().add("page-card");
        header.setPadding(new Insets(24));

        Label title = new Label("Patients");
        title.getStyleClass().add("page-header");

        Label subtitle = new Label("Search patient records, review intake details, and manage appointments from a clean patient hub.");
        subtitle.getStyleClass().add("page-subtitle");
        subtitle.setWrapText(true);

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private HBox createSearchBar() {
        HBox searchRow = new HBox(16);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        searchField.setPromptText("Search patients, ID, condition...");
        searchField.getStyleClass().add("search-field");
        searchField.setOnAction(event -> refreshPatientCards(searchField.getText()));
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().addAll("secondary-button", "button-pill");
        searchButton.setOnAction(event -> refreshPatientCards(searchField.getText()));

        Button addPatient = new Button("+ New patient");
        addPatient.getStyleClass().addAll("primary-button", "button-pill");
        addPatient.setOnAction(e -> showCreatePatientDialog());

        searchRow.getChildren().addAll(searchField, searchButton, addPatient);
        return searchRow;
    }

    private ListView<Patient> createPatientList() {
        ListView<Patient> lv = new ListView<>();
        lv.setPrefHeight(600);
        lv.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(createPatientCard(patient));
                }
            }
        });
        lv.setPlaceholder(new Label("No patients found."));
        return lv;
    }

    private void refreshPatientCards(String keyword) {
        // Run DB search off the FX thread
        Task<List<Patient>> task = new Task<>() {
            @Override
            protected List<Patient> call() throws Exception {
                return patientService.searchPatients(keyword);
            }
        };

        task.setOnSucceeded(evt -> patientList.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(evt -> {
            Throwable t = task.getException();
            VBox errorState = new VBox(12);
            errorState.setPadding(new Insets(22));
            errorState.getStyleClass().add("page-card");
            Label title = new Label("Unable to load patients");
            title.getStyleClass().add("page-header");
            Label message = new Label((t != null && t.getMessage() != null) ? t.getMessage() : "There was a problem connecting to the database. Verify the PostgreSQL container and try again.");
            message.getStyleClass().add("page-subtitle");
            message.setWrapText(true);
            errorState.getChildren().addAll(title, message);
            patientList.setPlaceholder(errorState);
            patientList.setItems(FXCollections.observableArrayList());
        });

        new Thread(task, "patient-search").start();
    }

    private void showCreatePatientDialog() {
        Dialog<Patient> dlg = new Dialog<>();
        dlg.setTitle("New patient");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label sexLabel = new Label("Gender:");

        ComboBox<String> sexBox = new ComboBox<>();
        sexBox.getItems().addAll("Male","Female", "Other");
        sexBox.setValue("Male");

        Label dobLabel = new Label("Date of birth:");
        DatePicker dob = new DatePicker();

        Label insLabel = new Label("Insurance:");
        TextField insField = new TextField();

        VBox content = new VBox(8, nameLabel, nameField, sexLabel, sexBox, dobLabel, dob, insLabel, insField);
        content.setPadding(new Insets(12));

        dlg.getDialogPane().setContent(content);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                String name = nameField.getText();
                String sex = sexBox.getValue();
                java.sql.Date d = dob.getValue() == null ? null : java.sql.Date.valueOf(dob.getValue());
                String ins = insField.getText();
                if (name == null || name.trim().isEmpty() || d == null) return null;
                return patientService.createPatient(name.trim(),sex, d, ins);
            }
            return null;
        });

        dlg.showAndWait().ifPresent(p -> refreshPatientCards(searchField.getText()));
    }

    private void showPatientDetails(Patient patient) {

    Patient fullPatient =
            patientService.getPatient(patient.getPatientId());

    if (fullPatient == null) {
        return;
    }

    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    alert.setTitle("Patient Details");

    alert.setHeaderText(fullPatient.getName());

    alert.setContentText(
            "Patient ID : " + fullPatient.getPatientId() +
            "\nName : " + fullPatient.getName() +
            "\nDOB : " + fullPatient.getDob() +
            "\nGender : " + fullPatient.getSex() +
            "\nInsurance : " + fullPatient.getInsurance()
    );

    alert.showAndWait();
}
    private boolean confirmDelete(Patient patient) {
        Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Delete patient " + patient.getName() + "?", ButtonType.YES, ButtonType.NO);
        return c.showAndWait().filter(bt -> bt == ButtonType.YES).isPresent();
    }

    private VBox createPatientCard(Patient patient) {
        VBox card = new VBox(14);
        card.getStyleClass().add("patient-card");
        card.setPadding(new Insets(22));
        card.setPrefWidth(320);

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label patientName = new Label(patient.getName());
        patientName.getStyleClass().add("patient-name");
        Label patientInsurance = new Label(patient.getInsurance() == null ? "No insurance" : patient.getInsurance());
        patientInsurance.getStyleClass().add("patient-age");
        titleRow.getChildren().addAll(patientName, patientInsurance);

        Label patientMeta = new Label("Patient ID: " + patient.getPatientId());
        patientMeta.getStyleClass().add("patient-specialty");

        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        Button viewButton = new Button("View");
        viewButton.getStyleClass().addAll("secondary-button", "button-pill");
        viewButton.setOnAction(e -> showPatientDetails(patient));

        Button editButton = new Button("Edit");
        editButton.getStyleClass().addAll("secondary-button", "button-pill");
        editButton.setOnAction(e -> showEditPatientDialog(patient));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("tertiary-button", "button-pill");
        deleteButton.setOnAction(e -> {
            boolean confirmed = confirmDelete(patient);
            if (confirmed) {
                boolean ok = patientService.deletePatient(patient.getPatientId());
                if (ok) refreshPatientCards(searchField.getText());
            }
        });
        actionRow.getChildren().addAll(viewButton, editButton, deleteButton);

        card.getChildren().addAll(titleRow, patientMeta, new Separator(), actionRow);
        return card;
    }

private void showEditPatientDialog(Patient patient) {

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Edit Patient");

    TextField nameField = new TextField(patient.getName());

    ComboBox<String> sexBox = new ComboBox<>();
    sexBox.getItems().addAll("Male", "Female", "Other");
    sexBox.setValue(patient.getSex());

    DatePicker dobPicker = new DatePicker();

    if (patient.getDob() != null) {
        dobPicker.setValue(patient.getDob().toLocalDate());
    }

    TextField insuranceField =
            new TextField(patient.getInsurance());

    VBox content = new VBox(
            10,
            new Label("Name"),
            nameField,
            new Label("Gender"),
            sexBox,
            new Label("Date of Birth"),
            dobPicker,
            new Label("Insurance"),
            insuranceField
    );

    content.setPadding(new Insets(15));

    dialog.getDialogPane().setContent(content);

    dialog.getDialogPane().getButtonTypes().addAll(
            ButtonType.OK,
            ButtonType.CANCEL
    );

    dialog.showAndWait().ifPresent(result -> {

        if (result == ButtonType.OK) {

            boolean success =
                    patientService.updatePatient(

                            patient.getPatientId(),

                            nameField.getText(),

                            sexBox.getValue(),

                            java.sql.Date.valueOf(
                                    dobPicker.getValue()),

                            insuranceField.getText()

                    );

            if (success) {
                refreshPatientCards(searchField.getText());
            }
        }
    });
}}