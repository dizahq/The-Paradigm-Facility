# The Paradigm Facility

At the Paradigm Facility, bad code creates "spaghetti code monsters" that corrupt codebases. Quarantined in packages, you must defeat them daily with paradigm quizzes to pass onboarding and fix the bugs.

## Quick start

This project is built with plain Swing/AWT. It does not use JavaFX.

### Requirements

- Java 17+ is enough
- Java 21 is recommended
- Make sure `java` is in your PATH

### Run the project

#### Windows

Double-click `run.bat`, or run from PowerShell:

```powershell
./run.bat
```

#### Mac/Linux

```bash
chmod +x run.sh
./run.sh
```

#### VS Code

1. Open the repo root.
2. Press `Ctrl+Shift+D`.
3. Select `Launch Game`.
4. Press `F5`.

#### IntelliJ IDEA

1. Open the repo root.
2. Make sure the project SDK is Java 17+.
3. Create an Application run configuration with:
   - Main class: `TheParadigmFacility`
4. Run it.

### Notes

- The project uses Swing and AWT for the UI and the animated background.
- No JavaFX modules are required.
- The JavaFX SDK folder can remain in the repo, but it is no longer used.
- If you want a smooth animated background, use an image/GIF-based loop instead of MP4 playback in Swing.

## Troubleshooting

### `Unsupported major.minor version 69.0`

This is a Java version mismatch. Use Java 17 or 21, not Java 25.

```bash
java -version
```

### `ClassNotFoundException`

Run the project from the repo root and compile before launching, or use the provided launcher scripts.

### The background is black

This means the background asset path is not being read correctly. The Swing implementation falls back to black if the media file is not a supported image type.

Use a PNG/JPG/GIF asset instead of MP4 for the background loop.
