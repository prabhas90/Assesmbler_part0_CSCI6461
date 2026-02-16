# CSCI 6461 Assembler - Design Notes

## Architecture Overview

The assembler uses a **two-pass architecture** to translate CSCI 6461 assembly language into 18-bit machine code.

## Core Components

### 1. Assembler (Main Orchestrator)
- **Responsibility**: Coordinates the assembly process
- **Key Methods**:
  - `passOne()`: Builds symbol table and calculates addresses
  - `passTwo()`: Generates machine code and output files
  - `assemble()`: Main entry point that orchestrates both passes

### 2. Parser (Lexical Analyzer)
- **Responsibility**: Breaks down assembly source lines into components
- **Output**: `ParsedLine` objects containing:
  - Label (if present)
  - Opcode
  - Operands array
  - Comments
- **Features**:
  - Handles multiple number formats (decimal, octal, hex)
  - Strips comments and whitespace
  - Splits operands by commas or spaces

### 3. SymbolTable (Label Management)
- **Responsibility**: Maps labels to memory addresses
- **Operations**:
  - `addLabel(name, address)`: Define a new label
  - `getAddress(name)`: Resolve label to address
  - `contains(name)`: Check if label exists
- **Error Handling**: Detects duplicate labels

### 4. ISA (Instruction Set Architecture)
- **Responsibility**: Defines all valid opcodes and their numeric values
- **Data**: Static mapping of mnemonic → opcode number
- **Examples**:
  - LDR → 1, STR → 2, LDA → 3
  - JZ → 8, JNE → 9, JMA → 11
  - HLT → 0

## Two-Pass Algorithm

### Pass 1: Symbol Resolution
1. Initialize location counter (LOC) to 0
2. For each source line:
   - Parse the line
   - If label exists, add to symbol table with current LOC
   - Update LOC based on instruction type:
     - Regular instructions: LOC++
     - LOC directive: Set LOC to specified value
     - End directive: No change

### Pass 2: Code Generation
1. Reset location counter to 0
2. For each source line:
   - Parse the line
   - Look up any label references in symbol table
   - Encode instruction into 18-bit machine code
   - Format as 6-digit octal
   - Write to listing and load files
   - Increment LOC

## Instruction Encoding

### 18-Bit Format
```
| Opcode | R  | IX | I | Address |
| 17-12  | 11-10 | 9-8 | 7 | 6-0   |
| 6 bits | 2 bits| 2 bits|1 bit|7 bits|
```

### Encoding Process
1. Extract opcode from ISA mapping
2. Parse operands (R, IX, Address, I)
3. Resolve any label references to addresses
4. Pack bits according to format:
   ```
   encoded = (opcode << 12) | (r << 10) | (ix << 8) | (i << 7) | address
   ```
5. Convert to 6-digit octal

### Example: `LDR 1,0,10`
- Opcode: 1 (001 binary)
- R: 1 (01 binary)
- IX: 0 (00 binary)
- I: 0 (0 binary)
- Address: 10 decimal = 12 octal (0001010 binary)
- Packed: 001 01 00 0 0001010 = 012012 octal

## Output Files

### Listing File (.lst)
- Human-readable format
- Shows original source with generated addresses and codes
- Useful for debugging

### Load File (.load)
- Machine-readable format
- Address-value pairs in octal
- Direct input to simulator/loader

## Design Decisions

### Why Two Passes?
- **Forward References**: Labels can be used before they're defined
- **Example**: A jump to a label that appears later in the code
- Pass 1 collects all label definitions, Pass 2 uses them

### Why 18-bit Instructions?
- 6-digit octal representation (3 bits per digit × 6 = 18 bits)
- Balances instruction complexity with memory efficiency
- Standard for CSCI 6461 architecture

### Address Limitation (7 bits)
- Direct addressing limited to 128 words (0-127)
- For higher addresses, use:
  - Index registers (IX field)
  - Indirect addressing (I bit)
  - Combination of both

## Error Handling

The assembler detects and reports:
- Invalid opcodes
- Duplicate labels
- Address overflow (> 127 in direct mode)
- File I/O errors
