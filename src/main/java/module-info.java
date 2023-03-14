module com.uv.projecthttp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.net.http;

    opens com.uv.projecthttp to javafx.fxml;
    exports com.uv.projecthttp;
}