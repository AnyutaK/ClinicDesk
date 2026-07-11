package controller;

import app.pages.DashboardPage;
import app.pages.DoctorsPage;
import app.pages.PatientsPage;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.HashMap;
import java.util.Map;

public class NavigationController {

    private final Map<String, ToggleButton> buttons = new HashMap<>();
    private final Map<String, Node> pages = new HashMap<>();
    private StackPane contentTarget;
    private String activePage = "dashboard";

    public NavigationController() {
        pages.put("dashboard", new DashboardPage());
        pages.put("patients", new PatientsPage());
        pages.put("doctors", new DoctorsPage());
        pages.put("appointments", new app.pages.AppointmentsPage());
        pages.put("reports", createPlaceholderPage("Reports"));
        pages.put("settings", createPlaceholderPage("Settings"));
    }

    public void registerNavigationButton(String key, ToggleButton button) {
        buttons.put(key, button);
    }

    public void setContentTarget(StackPane target) {
        this.contentTarget = target;
    }

    public void navigateTo(String key) {
        if (!pages.containsKey(key) || contentTarget == null) {
            return;
        }

        buttons.values().forEach(btn -> btn.setSelected(false));
        ToggleButton selectedButton = buttons.get(key);
        if (selectedButton != null) {
            selectedButton.setSelected(true);
        }

        contentTarget.getChildren().setAll(pages.get(key));
        activePage = key;
    }

    public Node getCurrentPage() {
        return pages.get(activePage);
    }

    private Node createPlaceholderPage(String title) {
        VBox wrapper = new VBox();
        wrapper.getStyleClass().add("page-card");
        wrapper.setPrefSize(1000, 780);

        Text header = new Text(title);
        header.getStyleClass().add("page-header");

        wrapper.getChildren().add(header);
        return wrapper;
    }
}
