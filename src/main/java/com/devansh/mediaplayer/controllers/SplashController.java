package com.devansh.mediaplayer.controllers;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;

public class SplashController {

    @FXML private ImageView logoImage;
    @FXML private Label appTitle;
    @FXML private ProgressBar loadingBar;

    @FXML
    public void initialize() {
        // Set logo image
        logoImage.setImage(new Image(getClass().getResource("/com/devansh/mediaplayer/logo.png").toExternalForm()));

        // Fade in the logo and title
        FadeTransition fadeLogo = new FadeTransition(Duration.seconds(1.5), logoImage);
        fadeLogo.setFromValue(0);
        fadeLogo.setToValue(1);

        FadeTransition fadeTitle = new FadeTransition(Duration.seconds(2), appTitle);
        fadeTitle.setFromValue(0);
        fadeTitle.setToValue(1);

        // Animate progress bar
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(loadingBar.progressProperty(), 0)),
            new KeyFrame(Duration.seconds(3), new KeyValue(loadingBar.progressProperty(), 1))
        );

        // After animation completes, load main player
        timeline.setOnFinished(e -> loadMainApp());
        fadeLogo.play();
        fadeTitle.play();
        timeline.play();
    }

    private void loadMainApp() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/devansh/mediaplayer/media_player.fxml"));
            Stage stage = (Stage) logoImage.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("🎵 IIITune - Simple Media Player");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
