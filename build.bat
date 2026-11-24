@echo off
setlocal enabledelayedexpansion
rem ============================================================
rem  The Paradigm Facility - build a runnable JAR and a Windows EXE
rem
rem  Put this file in the project root (next to README.md / run.bat)
rem  and double-click it, or run it from a terminal:
rem
rem     build.bat              -> JAR + portable EXE folder (no install)
rem     build.bat installer    -> also makes a Setup .exe installer
rem                               (needs WiX Toolset installed)
rem
rem  Requires: a JDK 17 or newer on PATH (javac, jar, jpackage).
rem ============================================================

cd /d "%~dp0"

set "APP_NAME=The Paradigm Facility"
set "JAR_NAME=TheParadigmFacility.jar"
set "MAIN_CLASS=TheParadigmFacility"
set "PROJECT=TheParadigmFacility"
set "SRC=%PROJECT%\src"
set "BUILD=build"
set "CLASSES=%BUILD%\classes"
set "INPUT=%BUILD%\input"
set "DIST=dist"

rem ---- check the tools ------------------------------------------------
where javac >nul 2>nul || (echo [ERROR] javac not found. Install a JDK 17+ and add it to PATH. & goto :fail)
where jar >nul 2>nul || (echo [ERROR] jar not found. Install a JDK 17+ and add it to PATH. & goto :fail)
where jpackage >nul 2>nul || (echo [ERROR] jpackage not found. You need a full JDK 17+, not just a JRE. & goto :fail)

if not exist "%SRC%" (
  echo [ERROR] Cannot find "%SRC%". Run this script from the project root.
  goto :fail
)

rem ---- clean ----------------------------------------------------------
if exist "%BUILD%" rmdir /s /q "%BUILD%"
if exist "%DIST%" rmdir /s /q "%DIST%"
mkdir "%CLASSES%"
mkdir "%INPUT%"

rem ---- 1. compile -----------------------------------------------------
echo.
echo [1/3] Compiling (Java 17 target)...
if exist "%BUILD%\sources.txt" del "%BUILD%\sources.txt"
for /r "%SRC%" %%f in (*.java) do (
  set "p=%%f"
  echo "!p:\=/!">>"%BUILD%\sources.txt"
)
javac --release 17 -encoding UTF-8 -d "%CLASSES%" @"%BUILD%\sources.txt"
if errorlevel 1 (echo [ERROR] Compile failed. & goto :fail)

rem ---- 2. runnable JAR (assets/ and data/ are packed inside) ----------
echo.
echo [2/3] Building %JAR_NAME%...
jar --create --file "%INPUT%\%JAR_NAME%" --main-class %MAIN_CLASS% ^
  -C "%CLASSES%" . ^
  -C "%PROJECT%" assets ^
  -C "%PROJECT%" data
if errorlevel 1 (echo [ERROR] jar failed. & goto :fail)

rem ---- 3. Windows EXE (bundles its own Java, no install needed) -------
echo.
echo [3/3] Building the Windows EXE with jpackage...
jpackage --type app-image ^
  --name "%APP_NAME%" ^
  --input "%INPUT%" ^
  --main-jar "%JAR_NAME%" ^
  --main-class %MAIN_CLASS% ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --dest "%DIST%"
if errorlevel 1 (echo [ERROR] jpackage failed. & goto :fail)

if /i "%~1"=="installer" (
  echo.
  echo [extra] Building the Setup installer...
  jpackage --type exe ^
    --name "%APP_NAME%" ^
    --input "%INPUT%" ^
    --main-jar "%JAR_NAME%" ^
    --main-class %MAIN_CLASS% ^
    --java-options "-Dfile.encoding=UTF-8" ^
    --win-menu --win-shortcut --win-dir-chooser ^
    --dest "%DIST%"
  if errorlevel 1 (echo [WARN] Installer failed - is WiX Toolset installed? The portable EXE above is still fine.)
)

copy /y "%INPUT%\%JAR_NAME%" "%DIST%\%JAR_NAME%" >nul

echo.
echo ============================================================
echo  Done.
echo    JAR : %DIST%\%JAR_NAME%      (run: java -jar %JAR_NAME%)
echo    EXE : %DIST%\%APP_NAME%\%APP_NAME%.exe
echo  To share the EXE, zip the whole "%DIST%\%APP_NAME%" folder.
echo ============================================================
endlocal
exit /b 0

:fail
echo.
echo Build failed.
endlocal
exit /b 1
