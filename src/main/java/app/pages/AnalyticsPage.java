package app.pages;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class AnalyticsPage extends VBox {

    public AnalyticsPage() {
        getStyleClass().add("analytics-page");
        setPadding(new Insets(24));
        setSpacing(12);

        Label title = new Label("Analytics");
        title.getStyleClass().add("page-header");

        WebView web = new WebView();
        web.getEngine().loadContent("<h3>Analytics placeholder</h3><p>Charts will be rendered here.</p>");
        web.setPrefHeight(600);

        getChildren().addAll(title, web);
    }
}
