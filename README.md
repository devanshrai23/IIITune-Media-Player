# 🎵 IIITune - JavaFX Media Player

IIITune is a sleek and modern **JavaFX-based media player** built for music lovers.  
It provides a smooth, dark-themed experience with playlist, history tracking, and real-time metadata display.

---

## ✨ Features
- 🎧 **Modern Dark UI** with gradient themes and animations  
- 💿 **Playlist & History Management**  
- 🔁 **Smooth transitions** for track info and album art  
- 🧠 **Auto Metadata Extraction** (Title, Artist, Album Art)  
- 🎥 **MP3, WAV, AAC, and MP4 (audio-only)** support  
- ❤️ **Add Current Track to Playlist** (prevent duplicates)  
- 🗑 **Remove from Playlist / History** via right-click menus  
- 🕓 **Persistent History Storage** using file-based storage  
- 🎚 Volume, progress, and playback controls  
- 🚀 Optional **Splash Screen with Logo Animation**

---

## 🛠 Technologies Used
| Component | Description |
|------------|-------------|
| **Language** | Java (17+) |
| **Framework** | JavaFX 21 |
| **Build Tool** | Maven |
| **IDE** | IntelliJ IDEA / VS Code |
| **Design Tool** | Scene Builder |

---

## 🧩 Project Structure

IIITune-Media-Player/
│
├── pom.xml
├── history.txt                 # Stores playback history (ignored by Git)
│
└── src/
    └── main/
        ├── java/
        │   └── com/devansh/mediaplayer/
        │       ├── MainApp.java
        │       │
        │       ├── controllers/
        │       │   ├── MediaPlayerController.java
        │       │   └── SplashController.java
        │       │
        │       ├── models/
        │       │   └── Track.java
        │       │
        │       └── utils/
        │           ├── FileUtils.java
        │           └── HistoryUtils.java
        │
        └── resources/
            └── com/devansh/mediaplayer/
                ├── media_player.fxml
                ├── SplashScreen.fxml
                ├── style.css
                ├── default_art.jpg
                └── logo.png

---

## 🚀 How to Run
1. Clone the repository:
   ```bash
   git clone https://github.com/devanshrai23/IIITune-Media-Player.git
   cd IIITune-Media-Player
2. Run using Maven
   ```bash
   mvn javafx:run
