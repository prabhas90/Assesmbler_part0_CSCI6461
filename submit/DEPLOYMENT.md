# CSCI 6461 Assembler - Deployment Guide

## Quick Start with JAR

The assembler has been packaged as an executable JAR file for easy distribution and use.

### Running the Assembler

```bash
java -jar assembler.jar <input_file.asm>
```

### Example

```bash
java -jar assembler.jar test_simple.asm
```

This will generate:
- `test_simple.lst` - Listing file with addresses and machine code
- `test_simple.load` - Load file for the simulator

## Building the JAR (If Needed)

If you need to rebuild the JAR from source:

### Using PowerShell (Windows)

```powershell
.\build-jar.ps1
```

The script will:
1. Compile all Java source files to `target/classes`
2. Create a manifest with the main class
3. Package everything into `assembler.jar`

### Manual Build (If jar command is available)

```bash
# Compile source files
javac -d target/classes src/main/java/com/csci6461/assembler/*.java

# Create JAR
jar cfm assembler.jar MANIFEST.MF -C target/classes .
```

## Output Files

All output files (`.lst` and `.load`) are created in the same directory as the input `.asm` file.

## System Requirements

- Java Runtime Environment (JRE) 11 or higher
- No additional dependencies required
