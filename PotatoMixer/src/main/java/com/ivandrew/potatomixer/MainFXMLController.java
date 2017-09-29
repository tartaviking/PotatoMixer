package com.ivandrew.potatomixer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Андрей
 */
public class MainFXMLController implements Initializable {

    @FXML
    private AnchorPane mainAP;
    @FXML
    private VBox leftPane;
    @FXML
    private Slider progressSlider;
    @FXML
    private Label timeLabel;

    private Map<String, MediaPlayer> loopMap;
    private List<PlayListElement> playlist;
    private final static String EXCEPT_DIGITS = "[^0-9]";
    private final static String EMPTY = "";
    private final static String DURATION_LOOP = "#durationLoop";
    private final static String NAME_LOOP = "#nameLoop";
    private final static String DEFAULT_NAME = "Sample name";
    private final static String DAFAULT_DURATION = "00:00";
    private final static String LOOP_DELETE_BTN = "#delLoop";
    private final static String LOOP_PLAY_BTN = "#playLoop";
    private final static String LOOP_SLIDER = "#volumeLoop";
    private final static String UNKNOWN = "UNKNOWN";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.loopMap = new HashMap();
        this.playlist = new ArrayList();
    }

    @FXML
    private void handleTrackPicker(ActionEvent event) {
        PlayListElement track;
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("MP3 Files only", "*.mp3"));
        File file = fileChooser.showOpenDialog(mainAP.getScene().getWindow());
        if (file != null) {
            try {
                track = new PlayListElement(new Media(file.toURI().toURL().toExternalForm()));
                this.playlist.add(track);
                preparePanel(track);
            } catch (MalformedURLException ex) {
                alertWindow();
                System.err.println(ex);
            }
        }
    }

    @FXML
    private void handleLoopPicker(ActionEvent event) {
        Media loop;
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("MP3 Files only", "*.mp3"));
        File file = fileChooser.showOpenDialog(mainAP.getScene().getWindow());
        if (file != null) {
            try {
                loop = new Media(file.toURI().toURL().toExternalForm());
                addToLooperPad(getId(event), loop, file.getName());
            } catch (MalformedURLException ex) {
                alertWindow();
                System.err.println(ex);
            }

        }
    }

    @FXML
    private void handleLoopPickerDel(ActionEvent event) {
        deleteFromLooperPad(getId(event));
    }

    @FXML
    private void handleLoopPlay(ActionEvent event) {
        ((Button) mainAP.lookup(LOOP_PLAY_BTN + getId(event))).setStyle("-fx-background-color: #F1C40F;");
        loopMap.get(getId(event)).play();
        loopMap.get(getId(event)).setOnEndOfMedia(() -> {
            ((Button) mainAP.lookup(LOOP_PLAY_BTN + getId(event))).setStyle("-fx-background-color: #91ffff;");
            loopMap.get(getId(event)).stop();
        });
    }

    private void preparePanel(PlayListElement track) {
        track.getMp().setOnReady(() -> {
            HBox playListPanel = new HBox();
            playListPanel.setId(track.getId().toString());

            //metadata to panel
            String artist = track.getMp().getMedia().getMetadata().get("artist") != null ? track.getMp().getMedia().getMetadata().get("artist").toString() : UNKNOWN;
            String title = track.getMp().getMedia().getMetadata().get("title") != null ? track.getMp().getMedia().getMetadata().get("title").toString() : UNKNOWN;
            Label name = new Label(artist + " - " + title + " / ");
            name.setFont(new Font(14));
            playListPanel.getChildren().add(name);

            //Duration
            Label duration = new Label(toHumanReadbleTime(track.getMp().getMedia().getDuration().toMillis()));
            name.setFont(new Font(14));
            playListPanel.getChildren().add(duration);

            CheckBox isLoop = new CheckBox("Loop");
            isLoop.onActionProperty().addListener((observable, oldValue, newValue) -> {
                track.setLoop(isLoop.isSelected());
            });

            //deletebutton
            Button deleteBtn = new Button("x");
            deleteBtn.setOnAction((event) -> {
                deleteFromPlayList(track.getId());
            });

            //play button
            Button playBtn = new Button("pp");
            playBtn.setOnAction((event) -> {
                if (MediaPlayer.Status.PLAYING.equals(track.getMp().getStatus())) {
                    track.getMp().pause();
                } else {
                    bindToBottomBar(track);
                    track.getMp().play();
                }
            });

            //volume control
            Slider slider = new Slider(0, 100, 100);
            slider.setOrientation(Orientation.HORIZONTAL);
            slider.setShowTickLabels(true);
            slider.setShowTickMarks(true);
            slider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable,
                        Number oldValue, Number newValue) {
                    track.getMp().setVolume(slider.getValue() / 100);
                }
            });

            track.getMp().setOnEndOfMedia(() -> {
                if (track.isLoop()) {
                    track.getMp().play();
                    track.setIsFocused(true);
                } else {
                    track.getMp().stop();
                    playNext();
                }
            });

            playListPanel.getChildren().add(deleteBtn);
            playListPanel.getChildren().add(playBtn);
            playListPanel.getChildren().add(slider);
            playListPanel.getChildren().add(isLoop);
            playListPanel.setStyle(leftPane.getChildren().size() % 2 == 0 ? "-fx-background-color:#f7f2f2" : "");

            //
            leftPane.getChildren().add(playListPanel);
        });
    }

    private void bindToBottomBar(PlayListElement track) {
        //fill timecode
        Double time = track.getMp().getMedia().durationProperty().get().toMillis();
        timeLabel.setText(toHumanReadbleTime(time));
        progressSlider.setMin(0);
        progressSlider.setMax(time);

        //Progres slider work
        track.getMp().currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            timeLabel.setText(toHumanReadbleTime(newValue.toMillis()) + "/" + toHumanReadbleTime(time));
            progressSlider.setValue(newValue.toMillis());
        });

        //Progres slider click
        progressSlider.setOnMousePressed((MouseEvent mouseEvent) -> {
            track.getMp().seek(Duration.millis(progressSlider.getValue()));
        });
    }

    private void addToLooperPad(String id, Media loop, String loopName) {
        loopMap.put(id, new MediaPlayer(loop));
        loopMap.get(id).setOnReady(() -> {
            ((Label) mainAP.lookup(DURATION_LOOP + id)).setText(toHumanReadbleTime(loop.getDuration().toMillis()));
            ((Label) mainAP.lookup(NAME_LOOP + id)).setText(loopName);
            ((Button) mainAP.lookup(LOOP_DELETE_BTN + id)).setDisable(false);
            ((Button) mainAP.lookup(LOOP_PLAY_BTN + id)).setStyle("-fx-background-color: #91ffff;");
            ((Button) mainAP.lookup(LOOP_PLAY_BTN + id)).setDisable(false);
            ((Slider) mainAP.lookup(LOOP_SLIDER + id)).setDisable(false);
            ((Slider) mainAP.lookup(LOOP_SLIDER + id)).valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable,
                        Number oldValue, Number newValue) {
                    loopMap.get(id).setVolume(((Slider) mainAP.lookup(LOOP_SLIDER + id)).getValue() / 100);
                }
            });
        });
    }

    private void deleteFromLooperPad(String id) {
        loopMap.get(id).stop();
        loopMap.remove(id);
        ((Button) mainAP.lookup(LOOP_PLAY_BTN + id)).setStyle("-fx-background-color: #A9A9A9;");
        ((Label) mainAP.lookup(DURATION_LOOP + id)).setText(DAFAULT_DURATION);
        ((Label) mainAP.lookup(NAME_LOOP + id)).setText(DEFAULT_NAME);
        ((Button) mainAP.lookup(LOOP_DELETE_BTN + id)).setDisable(true);
        ((Button) mainAP.lookup(LOOP_PLAY_BTN + id)).setDisable(true);
        ((Slider) mainAP.lookup(LOOP_SLIDER + id)).setDisable(true);
    }

    private void alertWindow() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(EMPTY);
        alert.setHeaderText(null);
        alert.setContentText("Проге пизда, звони Фиксикам! (шутка)");
        alert.showAndWait();
    }

    private String toHumanReadbleTime(Double millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis.longValue()),
                TimeUnit.MILLISECONDS.toSeconds(millis.longValue())
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis.longValue()))
        );
    }

    private String getId(ActionEvent event) {
        return ((Control) event.getSource()).getId().replaceAll(EXCEPT_DIGITS, EMPTY);
    }

    private void deleteFromPlayList(UUID id) {
        this.leftPane.getChildren().remove(leftPane.lookup("#" + String.valueOf(id)));
        for (PlayListElement element : playlist) {
            if (element.getId().equals(id)) {
                element.getMp().stop();
                playlist.remove(element);
                break;
            }
        }
    }

    private void playNext() {
        for (PlayListElement element : playlist) {
            if (element.isFocused()) {
                element.getMp().stop();
                element.setIsFocused(false);
                playlist.get(playlist.indexOf(element)).setIsFocused(true);
                playlist.get(playlist.indexOf(element)).getMp().play();
            }
            break;
        }
    }
}
