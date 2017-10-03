/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivandrew.potatomixer.media;

import com.ivandrew.potatomixer.ui.PlayListElementUI;
import java.util.UUID;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author Andrey_Ivanov
 */
public class PlayListElement {

    static int findFocused(UUID focused) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private final MediaPlayer mp;
    private boolean loop;
    private boolean fadeOut;
    private final UUID id;
    private PlayListElementUI elementUI;

    public PlayListElement(Media track, PlayList list, VBox parent) {
        this.mp = new MediaPlayer(track);
        this.id = UUID.randomUUID();
        this.mp.setOnReady(() -> {
            this.elementUI = new PlayListElementUI(this, list, parent);
            mp.setOnEndOfMedia(() -> {
                if (loop) {
                    mp.seek(Duration.ZERO);
                } else {
                    if (!fadeOut) {
                        list.playNext();
                    }
                }
            });
        });
        this.mp.setOnMarker((event) -> {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(2),
                            new KeyValue(this.mp.volumeProperty(), 0)));
            timeline.play();
            list.playNext();
        });
    }

    public void playPause() {
        if (MediaPlayer.Status.PLAYING.equals(mp.getStatus())) {
            mp.pause();
        } else {
            elementUI.bindToBottomBar(this);
            mp.play();
        }
    }

    public void stop() {
        mp.stop();
    }

    public boolean isLoop() {
        return loop;
    }

    public boolean isFadeOut() {
        return fadeOut;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setFadeOut(boolean fadeOut) {
        if (fadeOut) {
            this.mp.getMedia().getMarkers().put("fadeout", this.mp.getMedia().getDuration().subtract(Duration.seconds(4)));
        } else {
            this.mp.getMedia().getMarkers().remove("fadeout");
        }
        this.fadeOut = fadeOut;
    }

    public UUID getId() {
        return id;
    }

    public MediaPlayer getMp() {
        return mp;
    }

    public PlayListElementUI getElementUI() {
        return elementUI;
    }
}
