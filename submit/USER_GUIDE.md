# CSCI 6461 Assembler - User Guide

## Overview
This Java-based assembler converts CSCI 6461 assembly language into machine code. It implements a two-pass architecture to resolve forward references and produces both a listing file (`.lst`) and a load file (`.load`).

## Requirements
- Java Development Kit (JDK) 8 or higher.
- Command line terminal.

## building the Project
The project is structured with standard Java conventions. To compile the source code:

```bash
cd src/main/java
javac com/csci6461/assembler/*.java
```

## Running the Assembler
To assemble a program, provide the path to the `.asm` file as a command line argument:

```bash
java com.csci6461.assembler.Assembler <path_to_asm_file>
```

### Example
```bash
java com.csci6461.assembler.Assembler ../../../course_sample.asm
```

## Output Files
The assembler generates two files in the same directory as the input file:
1.  **Listing File (`.lst`)**: Contains the address, generated octal machine code, and the original source line.
2.  **Load File (`.load`)**: Contains address-value pairs for the simulator.

## Input Format
The assembler accepts standard CSCI 6461 assembly format:
```asm
[Label:] Opcode R, IX, Addr [, I] [; Comment]
```
- **Label**: Optional, ends with or without color (logic detects labels).
- **Opcode**: Standard opcodes (LDR, STR, etc.).
- **Directives**: `LOC` (set address), `Data` (define value), `End`.
- **Operands**: Comma-separated or space-separated.

## Troubleshooting
- **"Address truncated" warning**: If you try to access an address > 127 directly without indexing, the assembler warns that the address field (7 bits) will be truncated. Use Index Registers or Indirect addressing for higher memory.

