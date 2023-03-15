package com.uv.projecthttp;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class MainWindow implements Initializable {

    @FXML
    private TextArea bodyField;

    @FXML
    private ImageView imageBody;

    @FXML
    private WebView webViewBody;

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

    private String contentType = "";
    private Image image = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateMethodSelector();
        webViewBody.setVisible(false);
        imageBody.setVisible(true);

        rawRadiobutton.setSelected(true);
        rawRadiobutton.setOnAction(actionEvent -> {
            if(rawRadiobutton.isSelected()) {
                prettyRadiobutton.setSelected(false);
                webViewBody.setVisible(false);
                imageBody.setVisible(false);
                bodyField.setVisible(true);
            }
        });
        prettyRadiobutton.setOnAction(actionEvent -> {
            if(prettyRadiobutton.isSelected()) {
                rawRadiobutton.setSelected(false);
                if(contentType.contains("image")) {
                    imageBody.setImage(image);
                    imageBody.setVisible(true);
                    bodyField.setVisible(false);
                }
                if(contentType.contains("text/html")) {
                    webViewBody.setVisible(true);
                    bodyField.setVisible(false);
                } else {
                    webViewBody.setVisible(true);
                    imageBody.setVisible(false);
                    bodyField.setVisible(false);
                }
            }
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
        String requestBody = "";
        HttpResponse<byte[]> response;

        if(!url.isEmpty()) {
            if (method.equals("GET")) {
                try {
                    response = ClientHTTP.sendGETRequest(url);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                response = ClientHTTP.sendPOSTRequest(url, requestBody);
            }

            int responseCode = response.statusCode();
            byte[] body = response.body();
            contentType = response.headers().firstValue("Content-Type").orElse("NULL");

            responseField.setText("Respuesta HTTP: " + responseCode);
            headersField.setText(response.headers().toString());
            bodyField.setText(new String(body, StandardCharsets.UTF_8));
            typeField.setText("Tipo de Contenido: " + contentType);
            if(contentType != null && !contentType.isEmpty()) {
                if(contentType.contains("image")) {
                    image = new Image(url);
                    imageBody.setImage(image);
                    imageBody.setVisible(true);
                } else if (contentType.contains("text/html")) {
                    webViewBody.getEngine().loadContent(new String(body, StandardCharsets.UTF_8));
                } else {
                    webViewBody.setVisible(false);
                    imageBody.setVisible(false);
                    bodyField.setVisible(true);
                }
            }

        } else {
            System.err.println("URL field is empty");
        }
    }

    @FXML
    private void saveRequest() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar");
        if(contentType != null && !contentType.isEmpty()) {
            if(contentType.contains("image")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
            } else if (contentType.contains("text/html")) {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML Files", "*.html"));
            } else {
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            }
        }

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            if(contentType != null && !contentType.isEmpty()) {
                if(contentType.contains("image")) {
                    String extension = contentType.substring(contentType.indexOf("/") + 1);
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                    if(!FilenameUtils.getExtension(file.getName()).equals(extension))
                        file = new File(file.getAbsolutePath() + "." + extension);
                    ImageIO.write(bufferedImage, extension, file);
                    System.out.println("Image saved: " + file.getName() + " with extension: " + extension);
                } else if (contentType.contains("text/html")) {
                    BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(file));
                    if(!FilenameUtils.getExtension(file.getName()).equals("html"))
                        file = new File(file.getAbsolutePath() + ".html");
                    writer.write(bodyField.getText());
                    System.out.println("HTML saved: " + file.getName() + " with extension: html");
                } else {
                    BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(file));
                    if(!FilenameUtils.getExtension(file.getName()).equals("txt"))
                        file = new File(file.getAbsolutePath() + ".txt");
                    writer.write(bodyField.getText());
                    System.out.println("Text saved: " + file.getName() + " with extension: txt");
                }
            }
        }
    }
}
