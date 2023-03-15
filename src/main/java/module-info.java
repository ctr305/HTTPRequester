module com.uv.projecthttp {
    requires javafx.controls;
    requires javafx.web;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.net.http;
    requires javafx.swing;
    requires org.apache.commons.io;

    opens com.uv.projecthttp to javafx.fxml;
    exports com.uv.projecthttp;
}