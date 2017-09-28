package com.ivandrew.potatomixer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

public class PlayerSceneFXMLController implements Initializable {

    @FXML
    Button playpauseBTN;
    @FXML
    Button stopBTN;
    @FXML
    Label testLabel;
    @FXML
    Slider volumeSlider;
    @FXML
    Slider progressSlider;
    
    private String song = "D:\\dev\\PotatoMixer\\PotatoMixer\\src\\main\\resources\\sounds\\rickRoll.mp3";
    private Media audioClip = null;
    private MediaPlayer mp = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            this.audioClip = new Media(new File(song).toURI().toURL().toExternalForm());
            mp = new MediaPlayer(audioClip);
        } catch (MalformedURLException ex) {
            Logger.getLogger(PlayerSceneFXMLController.class.getName()).log(Level.SEVERE, null, ex);
        }

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                mp.setVolume(volumeSlider.getValue()/100);
            }
        });
        
        mp.setOnReady(new Runnable() {
            public void run() {
                ObservableMap<String, Object> metadata = audioClip.getMetadata();
                for (String key : metadata.keySet()) {
                    testLabel.setText("\n" + key + " = " + metadata.get(key));
                }
            }
        });
        
        /*progressSlider.maxProperty().bind(Bindings.createDoubleBinding(
                () -> mp.getTotalDuration().toSeconds(),
                mp.totalDurationProperty()));*/
    }

    @FXML
    private void handlePlayPauseBTN(ActionEvent event) {
        if (Status.PLAYING.equals(mp.getStatus())) {
            testLabel.setText("Pause");
            mp.pause();
        } else {
            testLabel.setText("Play");
            mp.play();
        }
    }

    @FXML
    private void handleStopBTN(ActionEvent event) {
        mp.stop();
    }

}
