package com.ivandrew.potatomixer.media;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.util.Duration;

/**
 *
 * @author Андрей
 */
public class PlayList {

    private UUID focused;

    private List<PlayListElement> elements;
    private AnchorPane bottomPanel;

    public PlayList(AnchorPane bottomPanel) {
        this.elements = new ArrayList<>();
        this.bottomPanel = bottomPanel;
    }

    public void addToPlayList(Media track, PlayList playList, VBox parent) {
        elements.add(new PlayListElement(track, this, parent));
    }

    public void deleteFromPlayList(UUID id) {
        PlayListElement element = getElementById(id);
        element.stop();
        if (id.equals(getFocusedElement().getId())) {
            setFocused(elements.get(0));
        }
        elements.remove(element);
    }

    public void playNext() {
        if (!elements.isEmpty()) {
            PlayListElement element = getFocusedElement();
            if (elements.indexOf(element) < elements.size() - 1) {
                if (!element.isFadeOut()) {
                    stop();
                    playPause(elements.get(elements.indexOf(element) + 1).getId(), false);
                } else {
                    playPause(elements.get(elements.indexOf(element) + 1).getId(), true);
                }
            } else {
                stop();
            }
        }
    }

    public void playPause() {
        PlayListElement element = getFocusedElement();
        if (element != null) {
            setFocused(element);
            playPause(element.getId(), false);
        } else {
            if (!elements.isEmpty()) {
                playPause(elements.get(0).getId(), false);
            }
        }
    }

    public void playPause(UUID id, boolean isFadeOut) {
        PlayListElement element = getElementById(id);
        setFocused(element);
        if (!isFadeOut) {
            elements.forEach((e) -> {
                if (!e.getId().equals(id)) {
                    e.stop();
                }
            });
            element.playPause();
        } else {
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(3),
                            new KeyValue(element.getMp().volumeProperty(), 1.0)));
            element.getMp().setVolume(0);
            element.playPause();
            timeline.play();
        }
    }

    public void stop() {
        PlayListElement element = getFocusedElement();
        if (null != element) {
            setFocused(element);
            element.stop();
        }
    }

    public PlayListElement getFocusedElement() {
        for (PlayListElement element : elements) {
            if (element.getId().equals(focused)) {
                return element;
            }
        }
        return null;
    }

    public PlayListElement getElementById(UUID id) {
        for (PlayListElement element : elements) {
            if (element.getId().equals(id)) {
                return element;
            }
        }
        return null;
    }

    public void setFocused(PlayListElement element) {
        if (!elements.isEmpty()) {
            this.focused = element.getId();
            elements.forEach((el) -> {
                el.getElementUI().unSelectElement();
            });
            element.getElementUI().selectElement();
        }
    }

    public AnchorPane getBottomPanel() {
        return bottomPanel;
    }
}
