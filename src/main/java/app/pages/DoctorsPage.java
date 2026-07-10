package app.pages;

import dao.DoctorDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Doctor;
import service.DoctorService;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ButtonBar;
import java.util.List;

public class DoctorsPage extends VBox {

    private final FlowPane doctorGrid;
    private final DoctorService doctorService;
    private final TextField searchField;

    public DoctorsPage() {
        getStyleClass().add("patients-page");
        setPadding(new Insets(24));
        setSpacing(24);

        this.doctorService = new DoctorService(new DoctorDAO());
        this.doctorGrid = createDoctorGrid();
        this.searchField = new TextField();
        ScrollPane scrollPane = new ScrollPane(doctorGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(
                createHeader(),
                createSearchBar(),
                scrollPane
        );
        refreshDoctorCards("");
    }

    private VBox createHeader() {
        VBox header = new VBox(6);
        header.getStyleClass().add("page-card");
        header.setPadding(new Insets(24));

        Label title = new Label("Doctors");
        title.getStyleClass().add("page-header");

        Label subtitle = new Label("Browse doctors, filter by department, and review schedules from a modern provider dashboard.");
        subtitle.getStyleClass().add("page-subtitle");
        subtitle.setWrapText(true);

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private HBox createSearchBar() {
        HBox searchRow = new HBox(16);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        searchField.setPromptText("Search doctors, department, specialty...");
        searchField.getStyleClass().add("search-field");
        searchField.setOnAction(event -> refreshDoctorCards(searchField.getText()));
        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().addAll("secondary-button", "button-pill");
        searchButton.setOnAction(event -> refreshDoctorCards(searchField.getText()));

        Button addDoctor = new Button("+ New Doctor");
        addDoctor.getStyleClass().addAll("primary-button", "button-pill");
        addDoctor.setOnAction(e -> showCreateDoctorDialog());
        
        searchRow.getChildren().addAll(searchField, searchButton, addDoctor);
        return searchRow;
    }

    private FlowPane createDoctorGrid() {
        FlowPane grid = new FlowPane();
        grid.setHgap(18);
        grid.setVgap(18);
        grid.setPadding(new Insets(4, 0, 0, 0));
        grid.setPrefWrapLength(1040);
        return grid;
    }

    private void refreshDoctorCards(String keyword) {
        doctorGrid.getChildren().clear();

        List<Doctor> doctors;
        try {
            doctors = doctorService.searchDoctors(keyword);
        } catch (RuntimeException error) {
            VBox errorState = new VBox(12);
            errorState.setPadding(new Insets(22));
            errorState.getStyleClass().add("page-card");
            Label title = new Label("Unable to load doctors");
            title.getStyleClass().add("page-header");
            Label message = new Label("There was a problem connecting to the database. Check your Docker/PostgreSQL setup and try again.");
            message.getStyleClass().add("page-subtitle");
            message.setWrapText(true);
            errorState.getChildren().addAll(title, message);
            doctorGrid.getChildren().add(errorState);
            return;
        }

        if (doctors.isEmpty()) {
            VBox emptyState = new VBox(12);
            emptyState.setPadding(new Insets(22));
            emptyState.getStyleClass().add("page-card");
            Label label = new Label("No doctors found. Try another search term.");
            label.getStyleClass().add("page-subtitle");
            emptyState.getChildren().add(label);
            doctorGrid.getChildren().add(emptyState);
            return;
        }

        for (Doctor doctor : doctors) {
            doctorGrid.getChildren().add(createDoctorCard(doctor));
        }
    }

    private VBox createDoctorCard(Doctor doctor) {
        VBox card = new VBox(16);
        card.getStyleClass().add("patient-card");
        card.setPadding(new Insets(22));
        card.setPrefWidth(320);

        Label doctorName = new Label(doctor.getDoctorName());
        doctorName.getStyleClass().add("patient-name");

        Label doctorDepartment = new Label(doctor.getDepartment());
        doctorDepartment.getStyleClass().add("patient-specialty");

        HBox metaRow = new HBox(10);
        metaRow.setAlignment(Pos.CENTER_LEFT);
        Label idLabel = new Label("ID: " + doctor.getDoctorId());
        idLabel.getStyleClass().add("patient-meta");
        Label scheduleLabel = new Label("Open slots: 12");
        scheduleLabel.getStyleClass().add("patient-meta");
        metaRow.getChildren().addAll(idLabel, scheduleLabel);

        HBox actionRow = new HBox(12);
        actionRow.setAlignment(Pos.CENTER_LEFT);
        Button viewButton = new Button("View");
        viewButton.getStyleClass().addAll("secondary-button", "button-pill");
        viewButton.setOnAction(e -> showDoctorDetails(doctor));
        Button editButton = new Button("Edit");
        editButton.getStyleClass().addAll("secondary-button","button-pill");
        editButton.setOnAction(e -> showEditDoctorDialog(doctor));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("tertiary-button","button-pill");
        deleteButton.setOnAction(e -> {

            if(confirmDelete(doctor)){

                if(doctorService.deleteDoctor(doctor.getDoctorId())){

                    refreshDoctorCards(searchField.getText());

                }
            }
        });

        actionRow.getChildren().addAll(viewButton,editButton,deleteButton);
        card.getChildren().addAll(doctorName, doctorDepartment, new Separator(), metaRow, actionRow);
        return card;
    }
    private void showDoctorDetails(Doctor doctor){

    Doctor fullDoctor =
            doctorService.getDoctor(doctor.getDoctorId());

    if(fullDoctor == null)
        return;

    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    alert.setTitle("Doctor Details");

    alert.setHeaderText(fullDoctor.getDoctorName());

    alert.setContentText(

            "Doctor ID : " + fullDoctor.getDoctorId() +

            "\nName : " + fullDoctor.getDoctorName() +

            "\nDepartment : " + fullDoctor.getDepartment() +

            "\nSpecialization : " + fullDoctor.getSpecialization() +

            "\nPhone : " + fullDoctor.getPhone() +

            "\nEmail : " + fullDoctor.getEmail()

    );

    alert.showAndWait();
}
private boolean confirmDelete(Doctor doctor) {

    Alert alert = new Alert(
            Alert.AlertType.CONFIRMATION,
            "Delete Dr. " + doctor.getDoctorName() + "?",
            ButtonType.YES,
            ButtonType.NO
    );

    return alert.showAndWait()
            .filter(button -> button == ButtonType.YES)
            .isPresent();
}
private void showCreateDoctorDialog() {

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Create Doctor");

    ButtonType saveButton =
            new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

    dialog.getDialogPane().getButtonTypes().addAll(
            saveButton,
            ButtonType.CANCEL
    );

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20));

    TextField nameField = new TextField();

    TextField departmentField = new TextField();

    TextField specializationField = new TextField();

    TextField phoneField = new TextField();

    TextField emailField = new TextField();

    grid.add(new Label("Name"),0,0);
    grid.add(nameField,1,0);

    grid.add(new Label("Department"),0,1);
    grid.add(departmentField,1,1);

    grid.add(new Label("Specialization"),0,2);
    grid.add(specializationField,1,2);

    grid.add(new Label("Phone"),0,3);
    grid.add(phoneField,1,3);

    grid.add(new Label("Email"),0,4);
    grid.add(emailField,1,4);

    dialog.getDialogPane().setContent(grid);

    dialog.showAndWait().ifPresent(button -> {

        if(button == saveButton){

            doctorService.createDoctor(

                    nameField.getText(),
                    departmentField.getText(),
                    specializationField.getText(),
                    phoneField.getText(),
                    emailField.getText()

            );

            refreshDoctorCards(searchField.getText());
        }

    });

}
private void showEditDoctorDialog(Doctor doctor) {

    Doctor fullDoctor =
            doctorService.getDoctor(doctor.getDoctorId());

    if(fullDoctor == null)
        return;

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Edit Doctor");

    ButtonType saveButton =
            new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

    dialog.getDialogPane().getButtonTypes().addAll(
            saveButton,
            ButtonType.CANCEL
    );

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20));

    TextField nameField =
            new TextField(fullDoctor.getDoctorName());

    TextField departmentField =
            new TextField(fullDoctor.getDepartment());

    TextField specializationField =
            new TextField(fullDoctor.getSpecialization());

    TextField phoneField =
            new TextField(fullDoctor.getPhone());

    TextField emailField =
            new TextField(fullDoctor.getEmail());

    grid.add(new Label("Name"),0,0);
    grid.add(nameField,1,0);

    grid.add(new Label("Department"),0,1);
    grid.add(departmentField,1,1);

    grid.add(new Label("Specialization"),0,2);
    grid.add(specializationField,1,2);

    grid.add(new Label("Phone"),0,3);
    grid.add(phoneField,1,3);

    grid.add(new Label("Email"),0,4);
    grid.add(emailField,1,4);

    dialog.getDialogPane().setContent(grid);

    dialog.showAndWait().ifPresent(button -> {

        if(button == saveButton){

            doctorService.updateDoctor(

                    fullDoctor.getDoctorId(),
                    nameField.getText(),
                    departmentField.getText(),
                    specializationField.getText(),
                    phoneField.getText(),
                    emailField.getText()

            );

            refreshDoctorCards(searchField.getText());
        }

    });

}
}
