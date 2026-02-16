@echo off
REM Batch script to create JAR file for CSCI 6461 Assembler

echo Compiling Java source files...
javac -d target\classes src\main\java\com\csci6461\assembler\*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    exit /b %errorlevel%
)

echo Creating JAR file...
cd target\classes
jar cfm ..\..\assembler.jar ..\..\MANIFEST.MF .
cd ..\..

if exist assembler.jar (
    echo JAR created successfully: assembler.jar
    echo.
    echo Usage: java -jar assembler.jar ^<input_file.asm^>
) else (
    echo JAR creation failed!
    exit /b 1
)
