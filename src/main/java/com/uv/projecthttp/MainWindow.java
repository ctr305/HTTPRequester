package com.uv.projecthttp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {


    @FXML
    private TextArea bodyField;

    @FXML
    private TextArea headersField;

    @FXML
    private ComboBox<String> methodSelector;

    @FXML
    private RadioButton prettyRadiobutton;

    @FXML
    private RadioButton rawRadiobutton;

    @FXML
    private Label typeField;

    @FXML
    private Label responseField;

    @FXML
    private TextField urlField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateMethodSelector();
        rawRadiobutton.setSelected(true);
        rawRadiobutton.setOnAction(actionEvent -> {
            if(rawRadiobutton.isSelected());
            prettyRadiobutton.setSelected(false);
        });
        prettyRadiobutton.setOnAction(actionEvent -> {
            if(prettyRadiobutton.isSelected())
                rawRadiobutton.setSelected(false);
        });
    }

    private void populateMethodSelector() {
        methodSelector.getItems().addAll("GET", "POST", "PUT", "DELETE");
        methodSelector.setValue("GET");
    }

    @FXML
    private void makeRequest() throws IOException, InterruptedException {
        String url = urlField.getText();
        String method = methodSelector.getValue();
        String body = "";
        boolean pretty = prettyRadiobutton.isSelected();
        boolean raw = rawRadiobutton.isSelected();
        HttpResponse response;

        if(!url.isEmpty()) {
            if (method.equals("GET")) {
                try {
                    response = ClientHTTP.sendGETRequest(url);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                response = ClientHTTP.sendPOSTRequest(url, body);
            }

            Integer responseCode = response.statusCode();

            if (pretty) {
                System.out.println("Pretty");
            } else if (raw) {
                responseField.setText("Respuesta HTTP: " + responseCode.toString());
                headersField.setText(response.headers().toString());
                bodyField.setText(response.body().toString());
                typeField.setText("Tipo de Contenido: " + response.headers().firstValue("Content-Type").get());
            }
        } else {
            System.err.println("URL field is empty");
        }
    }

    @FXML
    private void saveRequest() {
        System.out.println("Save");
    }
}
