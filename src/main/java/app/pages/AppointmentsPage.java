package app.pages;

import dao.AppointmentDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.Appointment;
import model.Doctor;
import model.Patient;
import model.Slot;
import service.AppointmentService;
import java.util.List;
import dao.PatientDAO;
import dao.DoctorDAO;
import dao.SlotDAO;
import service.PatientService;
import service.DoctorService;
import service.SlotService;


public class AppointmentsPage extends VBox {

    private final FlowPane appointmentGrid;
    private final AppointmentService appointmentService;
    private final TextField searchField;
    private final PatientService patientService =
        new PatientService(new PatientDAO());

    private final DoctorService doctorService =
        new DoctorService(new DoctorDAO());
  
    private final SlotService slotService =
        new SlotService(new SlotDAO());

    public AppointmentsPage() {

        getStyleClass().add("appointments-page");
        setPadding(new Insets(24));
        setSpacing(24);
        
        appointmentService = new AppointmentService(new AppointmentDAO());

        appointmentGrid = createAppointmentGrid();
        searchField = new TextField();

        ScrollPane scrollPane = new ScrollPane(appointmentGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(
                createHeader(),
                createSearchBar(),
                scrollPane
        );

        

        System.out.println("Refreshing...");

        refreshAppointmentCards("");

        System.out.println("Done");
        
    }

    private VBox createHeader() {

        VBox header = new VBox(6);
        header.getStyleClass().add("page-card");
        header.setPadding(new Insets(24));

        Label title = new Label("Appointments");
        title.getStyleClass().add("page-header");

        Label subtitle = new Label(
                "Manage appointments, schedules and consultation status."
        );

        subtitle.getStyleClass().add("page-subtitle");
        subtitle.setWrapText(true);

        header.getChildren().addAll(title, subtitle);

        return header;
    }

    private HBox createSearchBar() {

        HBox row = new HBox(16);

        row.setAlignment(Pos.CENTER_LEFT);

        searchField.setPromptText(
                "Search patient, doctor or status..."
        );

        searchField.getStyleClass().add("search-field");

        searchField.setOnAction(e ->
                refreshAppointmentCards(searchField.getText()));

        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchButton = new Button("Search");
        searchButton.getStyleClass().addAll(
                "secondary-button",
                "button-pill"
        );

        searchButton.setOnAction(e ->
                refreshAppointmentCards(searchField.getText()));

        Button addButton = new Button("+ New Appointment");

        addButton.getStyleClass().addAll(
                "primary-button",
                "button-pill"
        );

        addButton.setOnAction(e ->
                showCreateAppointmentDialog());

        row.getChildren().addAll(
                searchField,
                searchButton,
                addButton
        );

        return row;
    }

    private FlowPane createAppointmentGrid() {

        FlowPane grid = new FlowPane();

        grid.setHgap(18);
        grid.setVgap(18);

        grid.setPadding(new Insets(4));

        grid.setPrefWrapLength(1050);

        return grid;
    }

    private void refreshAppointmentCards(String keyword) {

        appointmentGrid.getChildren().clear();

        List<Appointment> appointments;

        try {

            appointments =
                    appointmentService.searchAppointments(keyword);

        } catch (RuntimeException e) {

            VBox error = new VBox(12);

            error.getStyleClass().add("page-card");

            error.setPadding(new Insets(20));

            error.getChildren().addAll(

                    new Label("Unable to load appointments"),

                    new Label(e.getMessage())

            );

            appointmentGrid.getChildren().add(error);

            return;
        }

        if (appointments.isEmpty()) {

            VBox empty = new VBox(12);

            empty.getStyleClass().add("page-card");

            empty.setPadding(new Insets(20));

            empty.getChildren().add(

                    new Label("No appointments found.")

            );

            appointmentGrid.getChildren().add(empty);

            return;
        }

        for (Appointment appointment : appointments) {

            appointmentGrid.getChildren().add(

                    createAppointmentCard(appointment)

            );

        }}
    private VBox createAppointmentCard(Appointment appointment) {

    VBox card = new VBox(16);

    card.getStyleClass().add("patient-card");

    card.setPadding(new Insets(22));

    card.setPrefWidth(320);

    Label patient = new Label(appointment.getPatientName());
    patient.getStyleClass().add("patient-name");

    Label doctor = new Label("Dr. " + appointment.getDoctorName());
    doctor.getStyleClass().add("patient-specialty");

    Label time = new Label(
            appointment.getAppointmentDateTime().toString()
    );
    time.getStyleClass().add("patient-meta");

    Label status = new Label(
            "Status : " + appointment.getStatus()
    );
    status.getStyleClass().add("patient-meta");

    VBox info = new VBox(6);

    info.getChildren().addAll(
            doctor,
            time,
            status
    );

    HBox buttons = new HBox(10);

    Button view = new Button("View");
    view.getStyleClass().addAll(
            "secondary-button",
            "button-pill"
    );

    view.setOnAction(e ->
            showAppointmentDetails(appointment));

    Button edit = new Button("Edit");
    edit.getStyleClass().addAll(
            "secondary-button",
            "button-pill"
    );

    edit.setOnAction(e ->
            showEditAppointmentDialog(appointment));

    Button delete = new Button("Delete");
    delete.getStyleClass().addAll(
            "tertiary-button",
            "button-pill"
    );

    delete.setOnAction(e -> {

        if (confirmDelete(appointment)) {

            if (appointmentService.deleteAppointment(
                    appointment.getAppointmentId())) {

                refreshAppointmentCards(searchField.getText());

            }

        }

    });

    buttons.getChildren().addAll(
            view,
            edit,
            delete
    );

    card.getChildren().addAll(

            patient,

            info,

            new Separator(),

            buttons

    );

    return card;
}

private void showCreateAppointmentDialog() {

    Dialog<ButtonType> dialog = new Dialog<>();

    dialog.setTitle("Book Appointment");

    ButtonType saveButton =
            new ButtonType(
                    "Book",
                    ButtonBar.ButtonData.OK_DONE);

    dialog.getDialogPane().getButtonTypes().addAll(
            saveButton,
            ButtonType.CANCEL
    );

    ComboBox<Patient> patientBox = new ComboBox<>();

    patientBox.getItems().addAll(
            patientService.getAllPatients()
    );

    ComboBox<Doctor> doctorBox = new ComboBox<>();

    doctorBox.getItems().addAll(
            doctorService.searchDoctors("")
    );

    ComboBox<Slot> slotBox = new ComboBox<>();

    doctorBox.setOnAction(e -> {

        slotBox.getItems().clear();

        Doctor doctor = doctorBox.getValue();

        if (doctor != null) {

            slotBox.getItems().addAll(

                    slotService.getAvailableSlotsByDoctor(doctor.getDoctorId())
            );
        }
    });

    ComboBox<String> statusBox = new ComboBox<>();

    statusBox.getItems().addAll(

            "available",
            "attended",
            "did not attend",
            "cancelled"

    );

    statusBox.setValue("available");

    GridPane grid = new GridPane();

    grid.setHgap(10);

    grid.setVgap(10);

    grid.setPadding(new Insets(20));

    grid.add(new Label("Patient"),0,0);
    grid.add(patientBox,1,0);

    grid.add(new Label("Doctor"),0,1);
    grid.add(doctorBox,1,1);

    grid.add(new Label("Slot"),0,2);
    grid.add(slotBox,1,2);

    grid.add(new Label("Status"),0,3);
    grid.add(statusBox,1,3);

    dialog.getDialogPane().setContent(grid);

    dialog.showAndWait().ifPresent(result -> {

        if(result != saveButton)
            return;

        if(patientBox.getValue()==null){

            showError("Select a patient.");

            return;
        }

        if(doctorBox.getValue()==null){

            showError("Select a doctor.");

            return;
        }

        if(slotBox.getValue()==null){

            showError("Select a slot.");

            return;
        }

        Appointment created =
        appointmentService.createAppointment(
                patientBox.getValue().getPatientId(),
                slotBox.getValue().getSlotId(),
                java.sql.Date.valueOf(java.time.LocalDate.now()),
                statusBox.getValue()
        );

        if (created != null) {
        refreshAppointmentCards(searchField.getText());
        } else {
        showError("Unable to create appointment.");
        }});

}

private void showEditAppointmentDialog(Appointment appointment) {

    Appointment full =
            appointmentService.getAppointment(
                    appointment.getAppointmentId());

    if(full == null)
        return;

    Dialog<ButtonType> dialog = new Dialog<>();

    dialog.setTitle("Edit Appointment");

    ButtonType saveButton =
            new ButtonType(
                    "Save",
                    ButtonBar.ButtonData.OK_DONE);

    dialog.getDialogPane().getButtonTypes().addAll(
            saveButton,
            ButtonType.CANCEL);

    ComboBox<Patient> patientBox = new ComboBox<>();
    patientBox.getItems().addAll(
            patientService.getAllPatients());

    ComboBox<Doctor> doctorBox = new ComboBox<>();
    doctorBox.getItems().addAll(
            doctorService.searchDoctors(""));

    ComboBox<Slot> slotBox = new ComboBox<>();

    ComboBox<String> statusBox = new ComboBox<>();

    statusBox.getItems().addAll(
            "available",
            "attended",
            "did not attend",
            "cancelled"
    );

    // Select current patient

    for(Patient p : patientBox.getItems()){

        if(p.getPatientId()==full.getPatientId()){

            patientBox.setValue(p);

            break;
        }
    }

    // Select current doctor

    Doctor selectedDoctor = null;

    for(Doctor d : doctorBox.getItems()){

        if(d.getDoctorId()==full.getDoctorId()){

            selectedDoctor = d;

            doctorBox.setValue(d);

            break;
        }
    }

    // Load slots for current doctor

    if(selectedDoctor!=null){

    List<Slot> slots =
            slotService.getAvailableSlotsByDoctor(
                    selectedDoctor.getDoctorId());

    slotBox.getItems().addAll(slots);

    // Add currently booked slot so it can be selected
    Slot currentSlot =
            slotService.getSlot(full.getSlotId());

    if(currentSlot != null &&
       !slotBox.getItems().contains(currentSlot)) {

        slotBox.getItems().add(currentSlot);
    }
}
    // Select current slot if present

    for(Slot s : slotBox.getItems()){

        if(s.getSlotId()==full.getSlotId()){

            slotBox.setValue(s);

            break;

        }

    }

    statusBox.setValue(full.getStatus());

    doctorBox.setOnAction(e->{

        slotBox.getItems().clear();

        Doctor d=doctorBox.getValue();

        if(d!=null){

            slotBox.getItems().addAll(
        slotService.getAvailableSlotsByDoctor(
                d.getDoctorId())
        );

        slotBox.getItems().add(
        slotService.getSlot(full.getSlotId())
        );
        }

    });

    GridPane grid=new GridPane();

    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(20));

    grid.add(new Label("Patient"),0,0);
    grid.add(patientBox,1,0);

    grid.add(new Label("Doctor"),0,1);
    grid.add(doctorBox,1,1);

    grid.add(new Label("Slot"),0,2);
    grid.add(slotBox,1,2);

    grid.add(new Label("Status"),0,3);
    grid.add(statusBox,1,3);

    dialog.getDialogPane().setContent(grid);

    dialog.showAndWait().ifPresent(result->{

        if(result!=saveButton)
            return;

        if(patientBox.getValue()==null){

            showError("Select a patient.");
            return;
        }

        if(slotBox.getValue()==null){

            showError("Select a slot.");
            return;
        }

        boolean ok=

                appointmentService.updateAppointment(
                full.getAppointmentId(),
                patientBox.getValue().getPatientId(),
                slotBox.getValue().getSlotId(),
                java.sql.Date.valueOf(slotBox.getValue().getAppointmentDate()),
                statusBox.getValue()
                );

        if(ok){

            refreshAppointmentCards(
                    searchField.getText());

        }

        else{

            showError("Unable to update appointment.");

        }

    });

}

private void showAppointmentDetails(Appointment appointment) {

    Appointment full =
            appointmentService.getAppointment(
                    appointment.getAppointmentId());

    if (full == null)
        return;

    Alert alert =
            new Alert(Alert.AlertType.INFORMATION);

    alert.setTitle("Appointment Details");

    alert.setHeaderText(
            "Appointment #" + full.getAppointmentId());

    alert.setContentText(

            "Patient : "
                    + full.getPatientName()

            + "\nDoctor : Dr. "
                    + full.getDoctorName()

            + "\nSlot ID : "
                    + full.getSlotId()

            + "\nDate & Time : "
                    + full.getAppointmentDateTime()

            + "\nStatus : "
                    + full.getStatus()

    );

    alert.showAndWait();

}

private boolean confirmDelete(Appointment appointment) {

    Alert alert = new Alert(

            Alert.AlertType.CONFIRMATION,

            "Delete Appointment #"
                    + appointment.getAppointmentId()
                    + " ?",

            ButtonType.YES,

            ButtonType.NO

    );

    return alert.showAndWait()

            .filter(button -> button == ButtonType.YES)

            .isPresent();

}
private void showError(String message) {

    Alert alert = new Alert(Alert.AlertType.ERROR);

    alert.setTitle("Validation Error");

    alert.setHeaderText(null);

    alert.setContentText(message);

    alert.showAndWait();
}
}
