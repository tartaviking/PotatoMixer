/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivandrew.potatomixer;

import java.util.UUID;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author Andrey_Ivanov
 */
public class PlayListElement {

    private MediaPlayer mp;
    private boolean isFocused;
    private boolean loop;
    private UUID id;
    
    public PlayListElement(Media track) {
        this.mp = new MediaPlayer(track);
        this.isFocused = true;
        this.id = UUID.randomUUID();
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }   

    public UUID getId() {
        return id;
    }
    
    public MediaPlayer getMp() {
        return mp;
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setIsFocused(boolean isFocused) {
        this.isFocused = isFocused;
    }
    
    
    
}
