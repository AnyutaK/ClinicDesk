package app;

import controller.NavigationController;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainLayout extends BorderPane {

    private final NavigationController navigationController;

    public MainLayout() {
        this.navigationController = new NavigationController();
        setPrefSize(1440, 900);
        getStyleClass().add("main-root");
        setLeft(buildSidebar());
        setTop(buildHeader());
        setCenter(buildContentWrapper());
    }

    private HBox buildHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("app-header");
        header.setPadding(new Insets(12, 20, 12, 20));
        Label title = new Label("ClinicDesk");
        title.getStyleClass().add("header-title");
        Button account = new Button("Admin");
        account.getStyleClass().addAll("secondary-button", "button-pill");
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, account);
        return header;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(24);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(28, 16, 28, 16));

        Label title = new Label("ClinicDesk");
        title.getStyleClass().add("sidebar-title");

        VBox navGroup = new VBox(12);
        navGroup.getChildren().addAll(
            createNavItem("Dashboard", "dashboard"),
            createNavItem("Patients", "patients"),
            createNavItem("Doctors", "doctors"),
            createNavItem("Appointments", "appointments")
        );

        sidebar.getChildren().addAll(title, new Separator(), navGroup);
        return sidebar;
    }

    private ToggleButton createNavItem(String label, String pageKey) {
        ToggleButton button = new ToggleButton(label);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> navigationController.navigateTo(pageKey));
        navigationController.registerNavigationButton(pageKey, button);
        if (pageKey.equals("dashboard")) {
            button.setSelected(true);
        }
        return button;
    }

    private StackPane buildContentWrapper() {
        StackPane contentWrapper = new StackPane();
        contentWrapper.getStyleClass().add("content-wrapper");
        contentWrapper.setPadding(new Insets(28));

        ScrollPane sc = new ScrollPane(navigationController.getCurrentPage());
        sc.setFitToWidth(true);
        sc.setFitToHeight(true);
        sc.getStyleClass().add("content-scroll");

        contentWrapper.getChildren().add(sc);

        navigationController.setContentTarget(contentWrapper);
        return contentWrapper;
    }
}
