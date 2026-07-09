package app.pages;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import service.AppointmentService;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AnalyticsPage extends VBox {

    private final AppointmentService service = new AppointmentService();

    public AnalyticsPage() {
        getStyleClass().add("analytics-page");
        setPadding(new Insets(24));
        setSpacing(12);

        Label title = new Label("Analytics");
        title.getStyleClass().add("page-header");

        WebView web = new WebView();
        web.setPrefHeight(640);

        // Collect data from service
        Map<java.time.LocalDate, Integer> byDay = service.getAppointmentsCountByDay(14);
        Map<String, Integer> byDept = service.getAppointmentsByDepartment();

        // Build JS-friendly arrays
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        StringBuilder dayLabels = new StringBuilder();
        StringBuilder dayData = new StringBuilder();
        dayLabels.append("["); dayData.append("[");
        boolean first = true;
        for (Map.Entry<java.time.LocalDate, Integer> e : byDay.entrySet()) {
            if (!first) { dayLabels.append(","); dayData.append(","); }
            dayLabels.append('"').append(e.getKey().format(fmt)).append('"');
            dayData.append(e.getValue());
            first = false;
        }
        dayLabels.append("]"); dayData.append("]");

        StringBuilder deptLabels = new StringBuilder();
        StringBuilder deptData = new StringBuilder();
        deptLabels.append("["); deptData.append("[");
        first = true;
        for (Map.Entry<String, Integer> e : byDept.entrySet()) {
            if (!first) { deptLabels.append(","); deptData.append(","); }
            deptLabels.append('"').append(e.getKey().replace("\"", "\\\"")).append('"');
            deptData.append(e.getValue());
            first = false;
        }
        deptLabels.append("]"); deptData.append("]");

        String html = "<!doctype html>\n" +
                "<html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">" +
                "<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>" +
                "<style>body{background:#121212;color:#e6e6e6;font-family:system-ui, -apple-system, 'Segoe UI', Roboto, 'Helvetica Neue', Arial;}" +
                ".card{background:#1e1e1e;padding:12px;border-radius:8px;margin:12px 0}</style></head><body>" +
                "<h2>Appointments — Last 14 days</h2><div class=\"card\"><canvas id=\"byDay\"></canvas></div>" +
                "<h2>Appointments — By Department</h2><div class=\"card\"><canvas id=\"byDept\"></canvas></div>" +
                "<script>const dayLabels=" + dayLabels.toString() + "; const dayData=" + dayData.toString() + ";" +
                "const deptLabels=" + deptLabels.toString() + "; const deptData=" + deptData.toString() + ";" +
                "new Chart(document.getElementById('byDay'), {type:'line', data:{labels:dayLabels, datasets:[{label:'Appointments',data:dayData,fill:true,borderColor:'#4fd1c5',backgroundColor:'rgba(79,209,197,0.15)'}]}, options:{scales:{x:{ticks:{color:'#cbd5e1'}}, y:{ticks:{color:'#cbd5e1'}}}, plugins:{legend:{labels:{color:'#cbd5e1'}}}}});" +
                "new Chart(document.getElementById('byDept'), {type:'bar', data:{labels:deptLabels, datasets:[{label:'Appointments',data:deptData,backgroundColor:'#60a5fa'}]}, options:{scales:{x:{ticks:{color:'#cbd5e1'}}, y:{ticks:{color:'#cbd5e1'}}}, plugins:{legend:{labels:{color:'#cbd5e1'}}}}});" +
                "</script></body></html>";

        web.getEngine().loadContent(html);

        getChildren().addAll(title, web);
    }
}
