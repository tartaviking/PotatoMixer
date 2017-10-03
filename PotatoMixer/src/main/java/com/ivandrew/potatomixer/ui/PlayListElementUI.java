/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivandrew.potatomixer.ui;

import com.ivandrew.potatomixer.media.PlayList;
import com.ivandrew.potatomixer.media.PlayListElement;
import com.ivandrew.potatomixer.utils.Utils;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public final class PlayListElementUI {

    private final ToolBar trackPanel;
    private final static String UNKNOWN = "UNKNOWN";
    private final AnchorPane bottomPanel;
    private final static String backColor = "-fx-background-color:";
    private final static String SELECTED_COLOR = "-fx-background-color:#CCFFFF";
    private final static String PROGRESS_SLIDER = "#progressSlider";
    private final static String TIME_LABEL = "#timeLabel";

    public PlayListElementUI(PlayListElement track, PlayList list, VBox parent) {
        this.trackPanel = new ToolBar();
        this.bottomPanel = list.getBottomPanel();
        trackPanel.setId(track.getId().toString());
        trackPanel.paddingProperty().set(new Insets(5, 0, 0, 5));
        trackPanel.borderProperty().set(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
                BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
                CornerRadii.EMPTY, new BorderWidths(1), Insets.EMPTY)));
        //metadata to panel
        String artist = track.getMp().getMedia().getMetadata().get("artist") != null ? track.getMp().getMedia().getMetadata().get("artist").toString() : UNKNOWN;
        String title = track.getMp().getMedia().getMetadata().get("title") != null ? track.getMp().getMedia().getMetadata().get("title").toString() : UNKNOWN;
        Label name = new Label(artist + " - " + title + " / ");
        name.setFont(new Font(14));
        name.prefWidthProperty().bind(trackPanel.widthProperty().multiply(0.3));
        trackPanel.setOnMouseClicked((MouseEvent event) -> {
            list.setFocused(track);
        });

        //Duration
        Label duration = new Label(Utils.toHumanReadbleTime(track.getMp().getMedia().getDuration().toMillis()));
        name.setFont(new Font(14));

        CheckBox isLoop = new CheckBox("Loop");

        isLoop.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            track.setLoop(new_val);
        });

        CheckBox isFadeOut = new CheckBox("FadeOut");

        isFadeOut.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            track.setFadeOut(new_val);
        });

        //deletebutton
        Button deleteBtn = new Button("x");
        deleteBtn.setOnAction((event) -> {
            parent.getChildren().remove(parent.lookup("#" + track.getId()));
            list.deleteFromPlayList(track.getId());
        });
        deleteBtn.setStyle("-fx-background-color:#FF6347");

        //play button
        Button playBtn = new Button("pp");
        playBtn.setOnAction((event) -> {
            list.playPause(track.getId(), false);
        });

        //volume control
        Slider slider = new Slider(0, 100, 100);
        slider.setOrientation(Orientation.HORIZONTAL);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.valueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            track.getMp().setVolume(slider.getValue() / 100);
        });

        trackPanel.getItems().add(name);
        trackPanel.getItems().add(duration);

        trackPanel.getItems().add(playBtn);
        trackPanel.getItems().add(isLoop);
        trackPanel.getItems().add(isFadeOut);
        trackPanel.getItems().add(slider);

        trackPanel.getItems().add(deleteBtn);
        trackPanel.setStyle(backColor);
        parent.getChildren().add(trackPanel);
    }

    public void bindToBottomBar(PlayListElement track) {
        //Bind to bottom time code label and progress bar (slider)
        Double time = track.getMp().getMedia().durationProperty().get().toMillis();
        Slider progressSlider = (Slider) bottomPanel.lookup(PROGRESS_SLIDER);
        Label timeLabel = (Label) bottomPanel.lookup(TIME_LABEL);
        timeLabel.setText(Utils.toHumanReadbleTime(time));
        progressSlider.setMin(0);
        progressSlider.setMax(time);

        //Progres slider work
        track.getMp().currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            timeLabel.setText(Utils.toHumanReadbleTime(newValue.toMillis()) + "/" + Utils.toHumanReadbleTime(time));
            progressSlider.setValue(newValue.toMillis());
        });

        //Progres slider click
        progressSlider.setOnMousePressed((MouseEvent mouseEvent) -> {
            track.getMp().seek(Duration.millis(progressSlider.getValue()));
        });
    }

    public void selectElement() {
        trackPanel.setStyle(SELECTED_COLOR);
    }

    public void unSelectElement() {
        trackPanel.setStyle(backColor);
    }

    public ToolBar getTrackPanel() {
        return trackPanel;
    }
}
