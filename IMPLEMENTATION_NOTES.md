# CSCI 6461 Assembler - Implementation Notes

## Architecture
The assembler is implemented in Java and consists of four main components:
1.  **Assembler**: Orchestrates the two passes (Symbol resolution and Code generation).
2.  **Parser**: Handles lexical analysis of source lines.
3.  **SymbolTable**: Manages label-to-address mappings.
4.  **ISA**: Defines the Instruction Set Architecture (Opcodes, Formats).

## Two-Pass Algorithm

### Pass 1: Symbol Definition
- Iterate through each line of the source file.
- Keep track of a `Location Counter` (LOC).
- If a **Label** is found, add `(Label, LOC)` to the `SymbolTable`.
- Update `LOC` based on instruction type and `LOC` directives.
- Record lines for Pass 2.

### Pass 2: Code Generation
- Reset `LOC`.
- Re-process each line.
- For instructions using labels, look up the address in `SymbolTable`.
- **Encode** the instruction into 18-bit machine code.
- Write to `.lst` and `.load` files.

## Instruction Format
The assembler uses an **18-bit instruction format** to accommodate the output requirements (6-digit octal) and allow for reasonable direct addressing.

| Field   | Bits  | Description |
| :---    | :---  | :--- |
| Opcode  | 17-12 | 6-bit Operation Code |
| R       | 11-10 | 2-bit General Purpose Register |
| IX      | 9-8   | 2-bit Index Register |
| I       | 7     | 1-bit Indirect Flag |
| Address | 6-0   | 7-bit Address |

**Note on Addressing**:
Direct addressing is limited to 128 words (0-127). To access memory locations above 127 (e.g., `LOC 1024`), you must use Index Registers (`IX`) or Indirect Addressing (`I`). The Assembler will follow the standard behavior of truncating the address to 7 bits if it exceeds the limit, and printing a warning.

## Error Handling
- **Duplicate Labels**: formatting error in Pass 1.
- **Invalid Opcodes**: Detected during encoding.
- **Address Overflow**: Warns if direct address > 127.

## Assumptions
- Comments begin with `;` or are implicit at end of line.
- `Data` directive assumes decimal input unless prefixed with `0` (Octal) or `0x` (Hex).
- Case-insensitivity for Opcodes and Directives.
