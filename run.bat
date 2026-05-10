@echo off
setlocal

set PROJECT_DIR=%~dp0SportsManager
set JAR=%PROJECT_DIR%\target\SportsManager-fat.jar
set MAVEN_CMD=%PROJECT_DIR%\maven\apache-maven-3.9.6\bin\mvn.cmd

echo.
echo  ============================================
echo       AngryFM - Sports Manager
echo  ============================================
echo.

if exist "%MAVEN_CMD%" (
    echo  Building project...
    call "%MAVEN_CMD%" -f "%PROJECT_DIR%\pom.xml" clean package -DskipTests -q
) else (
    echo  Bundled Maven not found, trying system Maven...
    call mvn -f "%PROJECT_DIR%\pom.xml" clean package -DskipTests -q
)

if errorlevel 1 (
    echo.
    echo  BUILD FAILED.
    echo  Make sure JDK 21 or later is installed and set in PATH.
    echo.
    pause
    exit /b 1
)

echo  Build successful. Starting AngryFM...
echo.

java -jar "%JAR%"

if errorlevel 1 (
    echo.
    echo  Application exited with an error.
    pause
)
