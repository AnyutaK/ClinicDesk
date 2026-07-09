package app.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.AppointmentService;
import model.Slot;

import java.time.LocalDate;
import java.util.List;

public class AppointmentsPage extends VBox {

    private final AppointmentService appointmentService = new AppointmentService();
    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final ComboBox<Slot> slotBox = new ComboBox<>();
    private final ListView<String> bookingsList = new ListView<>();

    public AppointmentsPage() {
        getStyleClass().add("appointments-page");
        setPadding(new Insets(24));
        setSpacing(12);

        Label title = new Label("Appointments");
        title.getStyleClass().add("page-header");

        HBox controls = new HBox(8);
        controls.setAlignment(Pos.CENTER_LEFT);
        datePicker.setOnAction(e -> loadSlots());

        slotBox.setCellFactory(c -> new ListCell<>() {
            @Override
            protected void updateItem(Slot s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getTime().toString());
            }
        });
        slotBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Slot s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? null : s.getTime().toString());
            }
        });

        Button bookBtn = new Button("Book slot");
        bookBtn.getStyleClass().addAll("primary-button", "button-pill");
        bookBtn.setOnAction(e -> bookSelectedSlot());

        controls.getChildren().addAll(datePicker, slotBox, bookBtn);

        bookingsList.setPlaceholder(new Label("No appointments"));

        getChildren().addAll(title, controls, new Separator(), new Label("Today's bookings"), bookingsList);

        // initial load
        loadSlots();
        refreshBookings();
    }

    private void loadSlots() {
        LocalDate date = datePicker.getValue();
        List<Slot> slots = appointmentService.getAvailableSlots(date);
        slotBox.getItems().setAll(slots);
        if (!slots.isEmpty()) slotBox.getSelectionModel().select(0);
    }

    private void refreshBookings() {
        bookingsList.getItems().setAll(appointmentService.getBookingsForDay(datePicker.getValue()));
    }

    private void bookSelectedSlot() {
        Slot slot = slotBox.getValue();
        if (slot == null) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Select a slot first", ButtonType.OK);
            a.showAndWait();
            return;
        }

        boolean ok = appointmentService.bookAppointment(datePicker.getValue(), slot.getTime(), "clinicdesk-demo-patient");
        if (ok) {
            refreshBookings();
            loadSlots();
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR, "Unable to book slot", ButtonType.OK);
            a.showAndWait();
        }
    }
}
