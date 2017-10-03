package com.ivandrew.potatomixer.controller;

import com.ivandrew.potatomixer.media.PlayList;
import com.ivandrew.potatomixer.utils.Utils;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

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
    private AnchorPane bottomPanel;

    private Map<String, MediaPlayer> loopMap;
    private PlayList playList;
    private final static String EXCEPT_DIGITS = "[^0-9]";
    private final static String EMPTY = "";
    private final static String DURATION_LOOP = "#durationLoop";
    private final static String NAME_LOOP = "#nameLoop";
    private final static String DEFAULT_NAME = "Sample name";
    private final static String DAFAULT_DURATION = "00:00";
    private final static String LOOP_DELETE_BTN = "#delLoop";
    private final static String LOOP_PLAY_BTN = "#playLoop";
    private final static String LOOP_SLIDER = "#volumeLoop";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.loopMap = new HashMap();
        playList = new PlayList(bottomPanel);
       // scrollPane.prefWidthProperty().bind(leftPane.widthProperty().multiply(1));
    }

    @FXML
    private void handleTrackPicker(ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("MP3 Files only", "*.mp3"));
        File file = fileChooser.showOpenDialog(mainAP.getScene().getWindow());
        if (file != null) {
            try {
                playList.addToPlayList(new Media(file.toURI().toURL().toExternalForm()), playList, leftPane);
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
    private void handlePlayPause(ActionEvent event) {
        playList.playPause();
    }

    @FXML
    private void handlePlayNext(ActionEvent event) {
        playList.playNext();
    }

    @FXML
    private void handleStop(ActionEvent event) {
        String UUID = ((Control) event.getSource()).getId();
        playList.stop();
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

    private void addToLooperPad(String id, Media loop, String loopName) {
        loopMap.put(id, new MediaPlayer(loop));
        loopMap.get(id).setOnReady(() -> {
            ((Label) mainAP.lookup(DURATION_LOOP + id)).setText(Utils.toHumanReadbleTime(loop.getDuration().toMillis()));
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

    private String getId(ActionEvent event) {
        return ((Control) event.getSource()).getId().replaceAll(EXCEPT_DIGITS, EMPTY);
    }
}
