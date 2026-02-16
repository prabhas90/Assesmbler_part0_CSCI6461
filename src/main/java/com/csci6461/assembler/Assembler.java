package com.csci6461.assembler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Assembler {

    private SymbolTable symbolTable;
    private Parser parser;
    private int locationCounter;
    private List<String> sourceLines;
    private List<String> listingOutput;
    private List<String> loadOutput;

    public Assembler() {
        this.symbolTable = new SymbolTable();
        this.parser = new Parser();
        this.sourceLines = new ArrayList<>();
        this.listingOutput = new ArrayList<>();
        this.loadOutput = new ArrayList<>();
        this.locationCounter = 0;
    }

    public void assemble(String inputFileStr) {
        System.out.println("Assembling " + inputFileStr + "...");

        File inputFile = new File(inputFileStr);
        if (!inputFile.exists()) {
            System.err.println("Error: File not found: " + inputFileStr);
            return;
        }

        // Read all lines
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                sourceLines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            // Pass 1
            System.out.println("Starting Pass 1...");
            passOne();

            // Pass 2
            System.out.println("Starting Pass 2...");
            passTwo();

            // Generate Output Files
            String baseName = inputFileStr;
            if (baseName.contains(".")) {
                baseName = baseName.substring(0, baseName.lastIndexOf('.'));
            }
            writeListingFile(baseName + ".lst");
            writeLoadFile(baseName + ".load");

            System.out.println("Assembly Complete!");
            System.out.println("Generated: " + baseName + ".lst, " + baseName + ".load");

        } catch (Exception e) {
            System.err.println("Assembly Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void passOne() {
        locationCounter = 0; // Default start? Usually 0 unless LOC directive

        for (String line : sourceLines) {
            Parser.ParsedLine parsed = parser.parseLine(line);

            // Handle Label
            if (parsed.label != null && !parsed.label.isEmpty()) {
                symbolTable.addLabel(parsed.label, locationCounter);
            }

            // Handle Directives affecting LOC directly
            if (parsed.opcode != null) {
                if (parsed.opcode.equalsIgnoreCase("LOC")) {
                    if (parsed.operands.length > 0) {
                        locationCounter = parser.parseValue(parsed.operands[0]);
                    }
                    continue; // LOC does not occupy memory itself, usually? Or it just changes counter.
                }

                // Increment Counter
                // Determine instruction size. Assuming 1 word per instruction/data for now.
                // Except Labels on their own line?

                // If line is just a label, do not increment (?)
                // Usually labels point to the NEXT instruction.
                // If the line has an Opcode or Data, it takes space.

                if (parsed.opcode != null && !parsed.opcode.isEmpty()) {
                    // Everything with an opcode/directive takes 1 word?
                    // End directive usually doesn't take space in memory?
                    if (!parsed.opcode.equalsIgnoreCase("End") && !parsed.opcode.equalsIgnoreCase("LOC")) {
                        locationCounter++;
                    }
                }
            } else if (parsed.label != null) {
                // Label only line -> Points to next address. LOC not incremented?
                // Yes, label is address of what follows.
            }
        }
    }

    private void passTwo() {
        locationCounter = 0; // Reset

        // Header for Listing
        listingOutput.add(String.format("%-8s %-8s %-8s %-6s %-15s %s", "Address", "Octal", "Label", "Opcode",
                "Operands", "Comments"));

        for (String line : sourceLines) {
            Parser.ParsedLine parsed = parser.parseLine(line);
            String octalCode = "";
            String addressStr = "";

            // Handle LOC
            if (parsed.opcode != null && parsed.opcode.equalsIgnoreCase("LOC")) {
                if (parsed.operands.length > 0) {
                    locationCounter = parser.parseValue(parsed.operands[0]);
                }
                addToListing(null, null, parsed); // Just list source
                continue;
            }

            // Handle Instruction/Data
            if (parsed.opcode != null) {
                if (parsed.opcode.equalsIgnoreCase("Data")) {
                    int value = 0;
                    if (parsed.operands.length > 0) {
                        String operand = parsed.operands[0];
                        // FIX: Check if the operand is a label in the symbol table first
                        if (symbolTable.contains(operand)) {
                            value = symbolTable.getAddress(operand);
                        } else {
                            try {
                                value = parser.parseValue(operand);
                            } catch (Exception e) {
                                System.err.println("Error: Could not resolve Data value: " + operand);
                            }
                        }
                    }
                    // Data fits in 1 word (16 bits)
                    octalCode = String.format("%06o", value & 0xFFFF);
                    addressStr = String.format("%06o", locationCounter);
                    loadOutput.add(String.format("%06o %06o", locationCounter, value & 0xFFFF));
                    locationCounter++;
                } else if (parsed.opcode.equalsIgnoreCase("End")) {
                    addToListing(null, null, parsed);
                    continue;
                } else {
                    // Encode Instruction
                    int encoded = encodeInstruction(parsed);
                    octalCode = String.format("%06o", encoded);
                    addressStr = String.format("%06o", locationCounter);
                    loadOutput.add(String.format("%06o %06o", locationCounter, encoded));
                    locationCounter++;
                }
                addToListing(addressStr, octalCode, parsed);
            } else {
                // Label only
                if (parsed.label != null) {
                    addressStr = String.format("%06o", symbolTable.getAddress(parsed.label));
                }
                addToListing(addressStr, null, parsed);
            }
        }
    }

    private int encodeInstruction(Parser.ParsedLine parsed) {
        if (!ISA.isOpcode(parsed.opcode)) {
            System.err.println("Error: Invalid opcode " + parsed.opcode);
            return 0;
        }

        int opcodeVal = ISA.getOpcode(parsed.opcode);
        int r = 0, ix = 0, i = 0, addr = 0;
        String[] ops = parsed.operands;

        // HLT is 000000 octal [cite: 148, 159]
        if (opcodeVal == 0)
            return 0;

        try {
            // Handle immediate instructions (AIR/SIR) [cite: 238, 239]
            if (parsed.opcode.equalsIgnoreCase("AIR") || parsed.opcode.equalsIgnoreCase("SIR")) {
                r = parser.parseValue(ops[0]);
                addr = parser.parseValue(ops[1]); // Immediate value is stored in address field [cite: 236]
            }
            // Handle RFS: R0 <- Immed [cite: 225]
            else if (parsed.opcode.equalsIgnoreCase("RFS")) {
                addr = parser.parseValue(ops[0]);
            }
            // Handle Index Load/Store: LDX/STX (Use IX field instead of R) [cite: 209, 210]
            else if (parsed.opcode.equalsIgnoreCase("LDX") || parsed.opcode.equalsIgnoreCase("STX")) {
                ix = parser.parseValue(ops[0]);
                addr = symbolTable.contains(ops[1]) ? symbolTable.getAddress(ops[1]) : parser.parseValue(ops[1]);
                i = (parsed.originalLine.endsWith(",1")) ? 1 : 0; // Check for indirect bit [cite: 207, 208]
            }
            // Standard instructions: LDR r, x, address[, I] [cite: 209, 211]
            else {
                if (ops.length >= 1)
                    r = parser.parseValue(ops[0]);
                if (ops.length >= 2)
                    ix = parser.parseValue(ops[1]);
                if (ops.length >= 3) {
                    addr = symbolTable.contains(ops[2]) ? symbolTable.getAddress(ops[2]) : parser.parseValue(ops[2]);
                }
                if (ops.length >= 4 || parsed.originalLine.endsWith(",1"))
                    i = 1;
            }
        } catch (Exception e) {
            System.err.println("Error encoding " + parsed.originalLine + ": " + e.getMessage());
        }

        // --- CRITICAL FIX: 16-BIT PACKING LOGIC ---
        // [Opcode: 6 bits] [R: 2 bits] [IX: 2 bits] [I: 1 bit] [Address: 5 bits]
        int encoded = 0;
        encoded |= (opcodeVal & 0x3F) << 10; // Bits 15-10
        encoded |= (r & 0x03) << 8; // Bits 9-8
        encoded |= (ix & 0x03) << 6; // Bits 7-6
        encoded |= (i & 0x01) << 5; // Bit 5
        encoded |= (addr & 0x1F); // Bits 4-0 (5-bit address)

        return encoded;
    }

    private void addToListing(String address, String octal, Parser.ParsedLine parsed) {
        String addr = (address == null) ? "      " : address;
        String code = (octal == null) ? "      " : octal;
        String label = (parsed.label == null) ? "" : parsed.label + ":";
        String op = (parsed.opcode == null) ? "" : parsed.opcode;
        String operands = String.join(",", parsed.operands);
        String comment = (parsed.comment == null) ? "" : ";" + parsed.comment;

        listingOutput.add(String.format("%s   %s   %-8s %-6s %-15s %s", addr, code, label, op, operands, comment));
    }

    private void writeListingFile(String filename) throws IOException {
        try (FileWriter fw = new FileWriter(filename)) {
            for (String line : listingOutput) {
                fw.write(line + "\n");
            }
        }
    }

    private void writeLoadFile(String filename) throws IOException {
        try (FileWriter fw = new FileWriter(filename)) {
            for (String line : loadOutput) {
                fw.write(line + "\n");
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java com.csci6461.assembler.Assembler <input_file.asm>");
            return;
        }
        new Assembler().assemble(args[0]);
    }
}
