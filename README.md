# The Paradigm Facility

At the Paradigm Facility, bad code creates "spaghetti code monsters" that corrupt codebases. Quarantined in packages, you must defeat them daily with paradigm quizzes to pass onboarding and fix the bugs.

## 🚀 Quick Start

This is a **pure Swing/AWT** project. **No JavaFX required.**

### Requirements

- **Java 11+** (Java 17+ recommended)
- `java` command available in PATH

### Run the Game

#### Windows

```bash
./run.bat
```

#### Mac/Linux

```bash
chmod +x run.sh
./run.sh
```

#### VS Code

1. Open the project root folder
2. Press `Ctrl+Shift+D` (Run and Debug)
3. Select **"Launch Game"**
4. Press `F5`

#### IntelliJ IDEA

1. Open the project root folder
2. Set project JDK to Java 11+
3. Create a new Application run configuration:
   - **Main class**: `TheParadigmFacility`
4. Click Run

#### Manual Compilation & Run

```bash
# Compile
mkdir -p out
javac -d out $(find TheParadigmFacility/src -name "*.java")

# Run
java -cp out TheParadigmFacility
```

## 📁 Project Structure

```
The-Paradigm-Facility/
├── TheParadigmFacility/
│   ├── src/                          # Java source code
│   │   ├── TheParadigmFacility.java  # Main entry point
│   │   └── ui/                       # UI panels and components
│   └── assets/                       # Game assets (images, background)
├── .vscode/                          # VS Code configuration
├── run.bat                           # Windows launcher
├── run.sh                            # Mac/Linux launcher
└── README.md                         # This file
```

## ⚠️ Important Notes

- This project uses **Swing/AWT for the UI and background animation**
- **No external dependencies** - uses only Java standard library
- The background loop expects image files (PNG/JPG/GIF), not MP4 videos
- Place background assets in: `TheParadigmFacility/assets/background/`

## 🛠️ Development

### Compiling

```bash
javac -d out $(find TheParadigmFacility/src -name "*.java")
```

### Running

```bash
java -cp out TheParadigmFacility
```

### Clean Build

```bash
rm -rf out
mkdir out
javac -d out $(find TheParadigmFacility/src -name "*.java")
java -cp out TheParadigmFacility
```

## 🤝 Team Workflow

1. Clone the repository
2. Run `./run.bat` (Windows) or `./run.sh` (Mac/Linux)
3. Develop and test locally
4. Commit and push changes

## ❓ Troubleshooting

### "java: command not found"

Install Java:
- [OpenJDK 17](https://jdk.java.net/17/)
- [Eclipse Temurin](https://adoptium.net/)

### "Cannot find symbol" errors

Make sure you're compiling all Java files:

```bash
javac -d out $(find TheParadigmFacility/src -name "*.java")
```

### The background is black

This is expected if the background asset is not a recognized image format. Replace the background file with a PNG/JPG/GIF:

```
TheParadigmFacility/assets/background/background.mp4 → background.png
```

---

**Made with pure Java Swing/AWT. No external frameworks required.**
