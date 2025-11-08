package com.devansh.mediaplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/devansh/mediaplayer/SplashScreen.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("🎵 IIITune - Loading...");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
