package com.ivandrew.potatomixer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;

public class LooperSceneFXMLController implements Initializable {

    @FXML
    private Button loopBtn;
    @FXML
    private AnchorPane loopAP;
    @FXML
    private Button fileBtn;
    @FXML
    private Slider loopVolumeSlider;
    @FXML
    private Label loopLabel;

    private final static String UNKNOWN = "Unknown";
    private Media audioClip = null;
    private MediaPlayer mp = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loopBtn.setDisable(true);
        loopVolumeSlider.setDisable(true);
        loopLabel.setText(UNKNOWN);
    }

    @FXML
    private void handleFilePicker(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("MP3 Files only", "*.mp3"));
        File file = fileChooser.showOpenDialog(loopAP.getScene().getWindow());
        if (file != null) {
            try {
                this.audioClip = new Media(file.toURI().toURL().toExternalForm());
                mp = new MediaPlayer(audioClip);
                loopLabel.setText(file.getName());
            } catch (Exception ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("");
                alert.setHeaderText(null);
                alert.setContentText("Проге пизда, звони Фиксикам! (шутка)");
                alert.showAndWait();
            }
            initializeMP();
        }
    }

    @FXML
    private void handleLoopBtn(ActionEvent event) {
        if (Status.PLAYING.equals(mp.getStatus())) {
            loopBtn.setStyle("-fx-background-color: #AEB6BF;");
            mp.stop();
        } else {
            loopBtn.setStyle("-fx-background-color: #F1C40F;");
            mp.play();
        }
    }

    private void initializeMP() {
        if (mp != null) {

            // enable control
            loopBtn.setDisable(false);
            loopBtn.setStyle("-fx-background-color: #AEB6BF;");
            loopVolumeSlider.setDisable(false);

            //Volume watcher
            loopVolumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable,
                        Number oldValue, Number newValue) {
                    mp.setVolume(loopVolumeSlider.getValue() / 100);
                }
            });

            mp.setOnEndOfMedia(() -> {
                loopBtn.setStyle("-fx-background-color: #AEB6BF;");
                mp.stop();
            });
            
            final KeyCombination keyComb1 = new KeyCodeCombination(KeyCode.R,
                    KeyCombination.CONTROL_DOWN);
            loopAP.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
                if (keyComb1.match(event)) {
                    loopBtn.setStyle("-fx-background-color: #F1C40F;");
                    mp.play();
                }
            });
        }
    }
}
