package com.devansh.mediaplayer.utils;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

// Utility class for file selection and validation.
public class FileUtils {

    /**
     * Opens a FileChooser dialog for selecting media files.
     * Supports common video/audio formats.
     *
     * @param stage the current application stage
     * @return selected File or null if none chosen
     */
    public static File chooseMediaFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Media File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Media Files", "*.mp3", "*.wav", "*.m4a", "*.mkv"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        return fileChooser.showOpenDialog(stage);
    }

    // Validates whether a given file is a supported media format.
    public static boolean isValidMediaFile(File file) {
        if (file == null) return false;

        String name = file.getName().toLowerCase();
        return name.endsWith(".mp3") || name.endsWith(".wav")
                || name.endsWith(".m4a") || name.endsWith(".mkv");
    }
}
