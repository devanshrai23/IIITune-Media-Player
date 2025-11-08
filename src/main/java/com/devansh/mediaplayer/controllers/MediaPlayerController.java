package com.devansh.mediaplayer.controllers;

import com.devansh.mediaplayer.models.Track;
import com.devansh.mediaplayer.utils.FileUtils;
import com.devansh.mediaplayer.utils.HistoryUtils;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.collections.*;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;

import java.io.File;
import java.util.List;

public class MediaPlayerController {
    
    @FXML private Label songLabel, timeLabel;
    @FXML private Slider timeSlider, volumeSlider;
    @FXML private ListView<Track> playlistView;
    @FXML private ImageView nowPlayingArt;
    @FXML private Label artistLabel;
    @FXML private ImageView backgroundArt;
    @FXML private StackPane nowPlayingContainer;
    @FXML private ListView<Track> historyView;

    private ObservableList<Track> playlist = FXCollections.observableArrayList();
    private MediaPlayer mediaPlayer;
    private Media media;
    private boolean isSeeking = false;
    private int currentIndex = -1;

    @FXML
    public void initialize() {
        // 🎵 Playlist setup
        playlistView.setItems(playlist);
        playlistView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Track selected = playlistView.getSelectionModel().getSelectedItem();
                if (selected != null) playTrack(selected);
            }
        });

        // 🎵 Playlist context menu
        ContextMenu playlistMenu = new ContextMenu();
        MenuItem playItem = new MenuItem("▶ Play");
        MenuItem removeItem = new MenuItem("🗑 Remove from Playlist");
        playlistMenu.getItems().addAll(playItem, removeItem);
        playlistView.setContextMenu(playlistMenu);

        playItem.setOnAction(e -> {
            Track selected = playlistView.getSelectionModel().getSelectedItem();
            if (selected != null) playTrack(selected);
        });

        removeItem.setOnAction(e -> {
            Track selected = playlistView.getSelectionModel().getSelectedItem();
            if (selected != null) playlist.remove(selected);
        });

        ObservableList<Track> historyList = FXCollections.observableArrayList(
            com.devansh.mediaplayer.utils.HistoryUtils.loadHistory()
        );
        historyView.setItems(historyList);

        ContextMenu historyMenu = new ContextMenu();
        MenuItem playHistoryItem = new MenuItem("▶ Play");
        MenuItem removeHistoryItem = new MenuItem("🗑 Remove from History");
        historyMenu.getItems().addAll(playHistoryItem, removeHistoryItem);
        historyView.setContextMenu(historyMenu);

        playHistoryItem.setOnAction(e -> {
            Track selected = historyView.getSelectionModel().getSelectedItem();
            if (selected != null) playTrack(selected);
        });

        removeHistoryItem.setOnAction(e -> {
            Track selected = historyView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // Remove the selected song from history file
                com.devansh.mediaplayer.utils.HistoryUtils.removeFromHistory(selected);

                // Reload history cleanly and refresh UI
                List<Track> updated = com.devansh.mediaplayer.utils.HistoryUtils.loadHistory();
                historyView.getItems().setAll(updated);

                // Optional: provide visual feedback
                System.out.println("Removed from history: " + selected.getTitle());
            } else {
                System.out.println("⚠ No track selected for removal");
            }
        });

        // 🎧 Double-click to play from History
        historyView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Track selected = historyView.getSelectionModel().getSelectedItem();
                if (selected != null) playTrack(selected);
            }
        });
    }

    @FXML
    public void openFile() {
        Stage stage = (Stage) songLabel.getScene().getWindow();
        File file = FileUtils.chooseMediaFile(stage);

        if (file != null && FileUtils.isValidMediaFile(file)) {
            Track track = new Track(file.getAbsolutePath());

            // ✅ Check if the song already exists in playlist
            int existingIndex = -1;
            for (int i = 0; i < playlist.size(); i++) {
                if (playlist.get(i).getFilePath().equals(track.getFilePath())) {
                    existingIndex = i;
                    break;
                }
            }

            if (existingIndex != -1) {
                // 🎵 Play it directly from playlist if it exists
                currentIndex = existingIndex;
                playlistView.getSelectionModel().select(existingIndex);
                playlistView.scrollTo(existingIndex);
                playTrack(playlist.get(existingIndex));
            } else {
                // 🎧 Play song temporarily (no playlist addition)
                playTrack(track);
                currentIndex = -1; // reset index to show it’s not from playlist
            }
        }
    }

    @FXML
    public void addToPlaylist() {
        Stage stage = (Stage) songLabel.getScene().getWindow();
        File file = FileUtils.chooseMediaFile(stage);

        if (file != null && FileUtils.isValidMediaFile(file)) {
            Track track = new Track(file.getAbsolutePath());

            boolean alreadyExists = playlist.stream()
                    .anyMatch(t -> t.getFilePath().equals(track.getFilePath()));

            if (!alreadyExists) {
                playlist.add(track);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("This song is already in your playlist!");
                alert.showAndWait();
            }
        }
    }

   @FXML
    public void addCurrentToPlaylist() {
        if (media == null) {
            showAlert(Alert.AlertType.WARNING, "No song is currently playing!");
            return;
        }

        // ✅ Resolve the path more efficiently
        String uri = media.getSource();
        String decodedPath;
        try {
            decodedPath = new File(new java.net.URI(uri)).getAbsolutePath();
        } catch (Exception e) {
            decodedPath = uri.replace("file:/", "").replace("%20", " ");
        }

        File file = new File(decodedPath);
        Track track = new Track(file.getAbsolutePath());

        // ✅ Check in a background thread
        new Thread(() -> {
            boolean alreadyExists = playlist.stream()
                    .anyMatch(t -> new File(t.getFilePath()).equals(file));

            // Switch back to UI thread
            javafx.application.Platform.runLater(() -> {
                if (alreadyExists) {
                    showAlert(Alert.AlertType.INFORMATION, "⚠️This song is already in your playlist!");
                } else {
                    playlist.add(track);
                    showAlert(Alert.AlertType.INFORMATION, "❤️Song added to playlist successfully!");
                }
            });
        }).start();
    }
    
    // Helper to show alerts
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    // Core track playing logic
    private void playTrack(Track track) {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }

        media = new Media(new File(track.getFilePath()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        setupMetadata(media, track);

        songLabel.setText(track.getTitle());
        artistLabel.setText("Unknown Artist"); // placeholder for now

        // Update background image (default or album art)
        if (backgroundArt != null) {
            Image bg = new Image(
                getClass().getResource("/com/devansh/mediaplayer/default_art.jpg").toExternalForm(),
                800, 350, false, true
            );
            backgroundArt.setImage(bg);

            FadeTransition fade = new FadeTransition(Duration.seconds(0.8), backgroundArt);
            fade.setFromValue(0.3);
            fade.setToValue(1);
            fade.play();
        }

        // Animate smooth title change
        if (songLabel != null && artistLabel != null) {
            // Fade out both labels together
            FadeTransition fadeOutTitle = new FadeTransition(Duration.seconds(0.25), songLabel);
            FadeTransition fadeOutArtist = new FadeTransition(Duration.seconds(0.25), artistLabel);
            fadeOutTitle.setFromValue(1.0);
            fadeOutTitle.setToValue(0.0);
            fadeOutArtist.setFromValue(1.0);
            fadeOutArtist.setToValue(0.0);

            // When fade-out completes, update labels and fade back in
            fadeOutTitle.setOnFinished(event -> {
                songLabel.setText(track.getTitle());
                artistLabel.setText("Unknown Artist"); // placeholder for now

                // Fade-in and scale-in animations
                FadeTransition fadeInTitle = new FadeTransition(Duration.seconds(0.6), songLabel);
                FadeTransition fadeInArtist = new FadeTransition(Duration.seconds(0.6), artistLabel);
                fadeInTitle.setFromValue(0.0);
                fadeInTitle.setToValue(1.0);
                fadeInArtist.setFromValue(0.0);
                fadeInArtist.setToValue(1.0);

                // Subtle pop effect
                ScaleTransition scaleTitle = new ScaleTransition(Duration.seconds(0.5), songLabel);
                ScaleTransition scaleArtist = new ScaleTransition(Duration.seconds(0.5), artistLabel);
                scaleTitle.setFromX(0.9);
                scaleTitle.setFromY(0.9);
                scaleTitle.setToX(1.0);
                scaleTitle.setToY(1.0);
                scaleArtist.setFromX(0.9);
                scaleArtist.setFromY(0.9);
                scaleArtist.setToX(1.0);
                scaleArtist.setToY(1.0);

                // Smooth easing for natural motion
                fadeInTitle.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
                fadeInArtist.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
                scaleTitle.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
                scaleArtist.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

                // Play both title + artist animations in sync
                fadeInTitle.play();
                fadeInArtist.play();
                scaleTitle.play();
                scaleArtist.play();
            });

            fadeOutTitle.play();
            fadeOutArtist.play();
        }

        setupMediaPlayer();

        currentIndex = playlist.indexOf(track);
        if (currentIndex >= 0) {
            playlistView.getSelectionModel().select(currentIndex);
            playlistView.scrollTo(currentIndex);
        }

        mediaPlayer.play();

        HistoryUtils.addToHistory(track);
        if (historyView != null) {
            ObservableList<Track> updatedHistory = FXCollections.observableArrayList(
                com.devansh.mediaplayer.utils.HistoryUtils.loadHistory()
            );
            historyView.setItems(updatedHistory);

            for (int i = 0; i < historyView.getItems().size(); i++) {
                if (historyView.getItems().get(i).getFilePath().equals(track.getFilePath())) {
                    historyView.getSelectionModel().select(i);
                    historyView.scrollTo(i);
                    break;
                }
            }
        }
    }

    // Smooth label update with fade effect
    private void updateLabelSmooth(Label label, String newText) {
        if (label == null) return;

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), label);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            label.setText(newText);

            FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.4), label);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
            fadeIn.play();
        });
        fadeOut.play();
    }

    // MediaPlayer setup
    private void setupMediaPlayer() {
        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (!isSeeking) {
                timeSlider.setValue(newTime.toSeconds());
                updateTimerLabel(newTime, media.getDuration());
            }
        });

        mediaPlayer.setOnReady(() -> {
            timeSlider.setMax(media.getDuration().toSeconds());
            updateTimerLabel(Duration.ZERO, media.getDuration());
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            if (currentIndex + 1 < playlist.size()) {
                currentIndex++;
                playTrack(playlist.get(currentIndex));
            } else {
                mediaPlayer.stop(); // gracefully stop if end of playlist
            }
        });

        volumeSlider.valueProperty().addListener((obs, old, val) ->
            mediaPlayer.setVolume(val.doubleValue() / 100)
        );

        timeSlider.setOnMousePressed(e -> isSeeking = true);
        timeSlider.setOnMouseReleased(e -> {
            isSeeking = false;
            mediaPlayer.seek(Duration.seconds(timeSlider.getValue()));
        });

        volumeSlider.setValue(50);
        mediaPlayer.setVolume(0.5);
    }

    // Metadata handling
    private void setupMetadata(Media media, Track track) {
        media.getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> change) -> {
            if (change.wasAdded()) {
                String key = change.getKey();
                Object value = change.getValueAdded();

                if ("title".equals(key)) {
                    updateLabelSmooth(songLabel, value.toString());
                } else if ("artist".equals(key)) {
                    updateLabelSmooth(artistLabel, value.toString());
                } else if ("image".equals(key) && value instanceof Image img) {
                    backgroundArt.setImage(img);

                    // Fade in the album art smoothly
                    FadeTransition fade = new FadeTransition(Duration.seconds(1), backgroundArt);
                    fade.setFromValue(0);
                    fade.setToValue(1);
                    fade.play();
                }
            }
        });

        // Fallbacks if metadata not found
        mediaPlayer.setOnReady(() -> {
            Object title = media.getMetadata().get("title");
            Object artist = media.getMetadata().get("artist");
            Object image = media.getMetadata().get("image");

            if (title == null) updateLabelSmooth(songLabel, track.getTitle());
            if (artist == null) updateLabelSmooth(artistLabel, "Unknown Artist");

            if (image == null) {
                try {
                    Image defaultArt = new Image(
                        getClass().getResource("/com/devansh/mediaplayer/default_art.jpg").toExternalForm()
                    );
                    backgroundArt.setImage(defaultArt);
                } catch (Exception e) {
                    System.err.println("Default image missing: " + e.getMessage());
                }
            }
        });
    }

    // Timer label update
    private void updateTimerLabel(Duration current, Duration total) {
        int curSec = (int) current.toSeconds();
        int totSec = (int) total.toSeconds();
        timeLabel.setText(formatTime(curSec) + " / " + formatTime(totSec));
    }

    // Format time in mm:ss
    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    @FXML
    public void playMedia() {
        if (mediaPlayer != null) mediaPlayer.play();
    }

    @FXML
    public void pauseMedia() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    @FXML
    public void stopMedia() {
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    @FXML
    public void prevTrack() {
        if (!playlist.isEmpty() && currentIndex > 0) {
            currentIndex--;
            playTrack(playlist.get(currentIndex));
        }
    }

    @FXML
    public void nextTrack() {
        if (!playlist.isEmpty() && currentIndex + 1 < playlist.size()) {
            currentIndex++;
            playTrack(playlist.get(currentIndex));
        }
    }
}
