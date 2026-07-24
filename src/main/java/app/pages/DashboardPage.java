package app.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import dao.DashboardDAO;
import service.DashboardService;

public class DashboardPage extends VBox {
    private final DashboardService dashboardService;

    public DashboardPage() {

        dashboardService = new DashboardService(
                new DashboardDAO()
        );

        getStyleClass().add("dashboard-page");
        setPadding(new Insets(24));
        setSpacing(28);

        getChildren().addAll(
                createHeader(),
                createStatsRow(),
                createOverviewRow()
        );
    }

    private VBox createHeader() {
        VBox header = new VBox(8);
        header.getStyleClass().add("dashboard-header");

        Text greeting = new Text("Welcome back, Dr. Morgan");
        greeting.getStyleClass().add("dashboard-title");

        Label subtitle = new Label("ClinicDesk is ready. Review today’s schedule and patient intake at a glance.");
        subtitle.getStyleClass().add("dashboard-subtitle");

        header.getChildren().addAll(greeting, subtitle);
        return header;
    }

    private HBox createStatsRow() {
        HBox row = new HBox(18);
        row.setAlignment(Pos.CENTER_LEFT);

        row.getChildren().addAll(

        createStatCard(
            String.valueOf(
                dashboardService.getTodayAppointments()
            ),
            "Today's appointments",
            this::openAppointments
        ),


        createStatCard(
            String.valueOf(
                dashboardService.getWaitingPatients()
            ),
            "Patients waiting",
            this::openPatients
        ),


        createStatCard(
            String.valueOf(
                dashboardService.getDoctorsOnDuty()
            ),
            "Doctors on duty",
            () -> showInfo(
                "Doctors",
                "Doctors currently active"
            )
        ),


        createStatCard(
            dashboardService.getUtilization()+"%",
            "Slot utilization",
            () -> showInfo(
                "Utilization",
                "Today's clinic utilization"
            )
        )
    );

        row.setPrefWidth(Double.MAX_VALUE);
        return row;
    }

    private VBox createStatCard(String value, String labelText) {
        return createStatCard(value, labelText, null);
    }

    private VBox createStatCard(String value, String labelText, Runnable onClick) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stat-card");
        card.setPadding(new Insets(22));
        card.setPrefWidth(220);

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");

        Label label = new Label(labelText);
        label.getStyleClass().add("stat-label");
        label.setWrapText(true);

        card.getChildren().addAll(valueLabel, label);
        if (onClick != null) {
            card.setOnMouseClicked((MouseEvent e) -> onClick.run());
            card.getStyleClass().add("clickable-card");
        }
        return card;
    }

    private HBox createOverviewRow() {
        HBox row = new HBox(18);
        row.setAlignment(Pos.TOP_LEFT);

        row.getChildren().addAll(createUpcomingAppointmentsCard(), createQuickActionsCard());
        HBox.setHgrow(row.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(row.getChildren().get(1), Priority.SOMETIMES);
        return row;
    }

    private VBox createUpcomingAppointmentsCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("overview-card");
        card.setPadding(new Insets(24));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Upcoming appointments");
        title.getStyleClass().add("card-title");
        header.getChildren().add(title);

        VBox itemList = new VBox(14);
        itemList.getChildren().addAll(
            createAppointmentItem("09:30 AM", "Liam Patel", "Cardiology"),
            createAppointmentItem("10:15 AM", "Ava Chen", "Dermatology"),
            createAppointmentItem("11:00 AM", "Noah Rodriguez", "Telehealth")
        );

        card.getChildren().addAll(header, new Separator(), itemList);
        return card;
    }

    private HBox createAppointmentItem(String time, String patient, String context) {
        HBox item = new HBox(14);
        item.getStyleClass().add("appointment-item");
        item.setAlignment(Pos.CENTER_LEFT);

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("appointment-time");
        timeLabel.setMinWidth(92);

        VBox details = new VBox(4);
        Label patientLabel = new Label(patient);
        patientLabel.getStyleClass().add("appointment-patient");
        Label contextLabel = new Label(context);
        contextLabel.getStyleClass().add("appointment-context");

        details.getChildren().addAll(patientLabel, contextLabel);
        item.getChildren().addAll(timeLabel, details);
        return item;
    }

    private VBox createQuickActionsCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("overview-card");
        card.setPadding(new Insets(24));
        card.setPrefWidth(360);

        Label title = new Label("Quick actions");
        title.getStyleClass().add("card-title");

        VBox actions = new VBox(12);
        actions.getChildren().addAll(
            createActionChip("Book new appointment"),
            createActionChip("Review new patient intake"),
            createActionChip("Generate daily summary")
        );

        card.getChildren().addAll(title, new Separator(), actions);
        return card;
    }

    private Label createActionChip(String text) {
        Label chip = new Label(text);
        chip.getStyleClass().add("action-chip");
        chip.setOnMouseClicked(e -> handleActionChip(text));
        return chip;
    }

    private void handleActionChip(String text) {
        switch (text) {
            case "Book new appointment":
                openAppointments();
                break;
            case "Review new patient intake":
                openPatients();
                break;
            default:
                showInfo("Action", text);
        }
    }

    private void openPatients() {
        Scene s = getScene();
        if (s == null) return;
        for (Node n : s.getRoot().lookupAll(".nav-button")) {
            if (n instanceof ToggleButton) {
                ToggleButton t = (ToggleButton) n;
                if ("Patients".equals(t.getText())) { t.fire(); return; }
            }
        }
    }

    private void openAppointments() {
        Scene s = getScene();
        if (s == null) return;
        for (Node n : s.getRoot().lookupAll(".nav-button")) {
            if (n instanceof ToggleButton) {
                ToggleButton t = (ToggleButton) n;
                if ("Appointments".equals(t.getText())) { t.fire(); return; }
            }
        }
    }

    private void showInfo(String title, String message) {
        Label l = new Label(message);
        l.getStyleClass().add("page-subtitle");
        // very small info dialog
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION, message);
        a.setHeaderText(title);
        a.showAndWait();
    }
}
