package com.devansh.mediaplayer.models;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

// Represents a single media track with basic metadata.
public class Track {
    private String title;
    private String filePath;
    private double duration; // in seconds

    public Track(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);
        this.title = file.getName();

        try {
            Media media = new Media(file.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnReady(() -> duration = media.getDuration().toSeconds());
        } catch (Exception e) {
            System.err.println("Error loading track metadata: " + e.getMessage());
        }
    }

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }

    public double getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Track other = (Track) obj;
        return filePath != null && filePath.equals(other.filePath);
    }

    @Override
    public int hashCode() {
        return filePath != null ? filePath.hashCode() : 0;
    }
}
