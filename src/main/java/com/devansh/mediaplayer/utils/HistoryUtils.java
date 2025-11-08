package com.devansh.mediaplayer.utils;

import com.devansh.mediaplayer.models.Track;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class HistoryUtils {

    private static final String HISTORY_FILE = "history.txt";

    // Save song to history
    public static void addToHistory(Track track) {
        try {
            List<Track> existing = loadHistory();

            // Avoid duplicates
            boolean exists = existing.stream()
                    .anyMatch(t -> t.getFilePath().equals(track.getFilePath()));
            if (!exists) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
                    writer.write(track.getFilePath());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load all songs from history
    public static List<Track> loadHistory() {
        List<Track> tracks = new ArrayList<>();
        File file = new File(HISTORY_FILE);

        if (!file.exists()) return tracks;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                File f = new File(line);
                if (f.exists()) tracks.add(new Track(f.getAbsolutePath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tracks;
    }

    public static void removeFromHistory(Track trackToRemove) {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) return;

        try {
            List<String> allLines = Files.readAllLines(file.toPath());
            List<String> updated = new ArrayList<>();

            String targetPath = new File(trackToRemove.getFilePath()).getAbsolutePath().trim().toLowerCase();

            for (String line : allLines) {
                String linePath = new File(line.trim()).getAbsolutePath().toLowerCase();
                // Keep all except the one that matches (normalized comparison)
                if (!linePath.equals(targetPath)) {
                    updated.add(line);
                }
            }

            // Always write the updated list safely (even if empty)
            Files.write(file.toPath(), updated, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("✅ Removed only: " + trackToRemove.getTitle());
        } catch (IOException e) {
            System.err.println("⚠ Error removing from history: " + e.getMessage());
        }
    }
}
