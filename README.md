# CSCI 6461 Assembler Project

**Author**: [Your Name]
**Date**: February 15, 2026
**Course**: CSCI 6461 Computer Architecture

## Overview
This project implements a two-pass assembler for the CSCI 6461 architecture. It takes assembly source files (`.asm`) and produces listing (`.lst`) and load (`.load`) files.

## Features
- **Full Two-Pass Assembly**: Resolves forward and backward references.
- **Support for all ISA Instructions**: LDR, STR, AMR, SMR, JZ, JNE, etc.
- **Directives**: Supports `LOC`, `Data`, `End`.
- **18-bit Machine Code generation**: Compatible with 6-digit octal output format.
- **Detailed Error Reporting**: Identifies syntax errors, duplicate labels, and address overflows.

## Directory Structure
```
csci6461_assembler_project/
├── src/main/java/  # Source Code
├── tests/          # Test files (*.asm)
├── docs/           # Documentation
└── USER_GUIDE.md   # Usage Instructions
```

## Quick Start
1.  Navigate to source: `cd src/main/java`
2.  Compile: `javac com/csci6461/assembler/*.java`
3.  Run: `java com.csci6461.assembler.Assembler ../../../course_sample.asm`

See `USER_GUIDE.md` for detailed instructions.
