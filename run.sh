#!/bin/bash
# Mac/Linux launcher script for The Paradigm Facility
# Requires: Java 21 LTS

cd "$(dirname "$0")"

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "ERROR: Java not found in PATH"
    echo "Please install Java 21 LTS and add it to PATH"
    exit 1
fi

# Run the game with JavaFX module path
java \
  --module-path "$PWD/TheParadigmFacility/lib/javafx-sdk-27/lib" \
  --add-modules javafx.controls,javafx.media,javafx.swing \
  --enable-native-access=javafx.graphics,javafx.media,javafx.swing \
  -cp "$PWD/TheParadigmFacility/src" \
  TheParadigmFacility
