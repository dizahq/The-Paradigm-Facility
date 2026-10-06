@echo off
REM Windows launcher script for The Paradigm Facility
REM Requires: Java 21 LTS

cd /d "%~dp0"

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java not found in PATH
    echo Please install Java 21 LTS and add it to PATH
    pause
    exit /b 1
)

REM Run the game with JavaFX module path
java ^
  --module-path "TheParadigmFacility\lib\javafx-sdk-27\lib" ^
  --add-modules javafx.controls,javafx.media,javafx.swing ^
  --enable-native-access=javafx.graphics,javafx.media,javafx.swing ^
  -cp "TheParadigmFacility\src" ^
  TheParadigmFacility

pause
