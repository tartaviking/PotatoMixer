package com.ivandrew.potatomixer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class LooperSceneFXMLController implements Initializable {

    @FXML
    private Button playpauseBTN;
    @FXML
    private Pane leftPane;
    @FXML
    private Button stopBTN;
    @FXML
    private Button fileBtn;
    @FXML
    private Label timeLabel;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider progressSlider;
    @FXML
    private CheckBox isLoop;
    @FXML
    private CheckBox isFadeOut;
    @FXML
    private Label artistLabel;
    @FXML
    private Label titleLabel;

    private final static String UNKNOWN = "Unknown";
    private Media audioClip = null;
    private MediaPlayer mp = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        playpauseBTN.setDisable(true);
        stopBTN.setDisable(true);
        isLoop.setDisable(true);
        isFadeOut.setDisable(true);
        volumeSlider.setDisable(true);
        artistLabel.setText(UNKNOWN);
        titleLabel.setText(UNKNOWN);
        timeLabel.setText("00:00/00:00");
    }

    @FXML
    private void handleFilePicker(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("MP3 Files only", "*.mp3"));
        File file = fileChooser.showOpenDialog(leftPane.getScene().getWindow());
        if (file != null) {
            try {
                this.audioClip = new Media(file.toURI().toURL().toExternalForm());
                mp = new MediaPlayer(audioClip);
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
    private void handlePlayPauseBTN(ActionEvent event) {
        if (Status.PLAYING.equals(mp.getStatus())) {
            progressSlider.setValue(0);
            mp.pause();
        } else {
            mp.play();
        }
    }

    @FXML
    private void handleStopBTN(ActionEvent event) {
        mp.stop();
    }

    private void initializeMP() {
        if (mp != null) {

            // enable control
            playpauseBTN.setDisable(false);
            stopBTN.setDisable(false);
            isLoop.setDisable(false);
            isFadeOut.setDisable(false);
            volumeSlider.setDisable(false);

            //Volume watcher
            volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable,
                        Number oldValue, Number newValue) {
                    mp.setVolume(volumeSlider.getValue() / 100);
                }
            });

            mp.setOnReady(() -> {

                //fill timecode
                Double time = mp.getMedia().durationProperty().get().toMillis();
                timeLabel.setText(humanReadableMillis(time));
                progressSlider.setMin(0);
                progressSlider.setMax(time);

                //Progres slider work
                mp.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
                    timeLabel.setText(humanReadableMillis(newValue.toMillis()) + "/" + humanReadableMillis(time));
                    progressSlider.setValue(newValue.toMillis());
                });

                //Progres slider click
                progressSlider.setOnMousePressed((MouseEvent mouseEvent) -> {
                    mp.seek(Duration.millis(progressSlider.getValue()));
                });

                //metadata
                String artist = (String) mp.getMedia().getMetadata().get("artist");
                String title = (String) mp.getMedia().getMetadata().get("title");
                artistLabel.setText("".equals(artist) ? UNKNOWN : artist);
                titleLabel.setText("".equals(title) ? UNKNOWN : title);
            });

            mp.setOnEndOfMedia(() -> {
                if (isLoop.isSelected()) {
                    mp.stop();
                    mp.play();
                } else {
                    mp.stop();
                }
            });
        }
    }

    private String humanReadableMillis(Double millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis.longValue()),
                TimeUnit.MILLISECONDS.toSeconds(millis.longValue())
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis.longValue()))
        );
    }

}
