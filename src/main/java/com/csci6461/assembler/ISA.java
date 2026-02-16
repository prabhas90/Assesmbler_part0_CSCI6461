package com.csci6461.assembler;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the Instruction Set Architecture (ISA) for the CSCI 6461 Assembler.
 * Contains Opcode values and instruction format definitions.
 */
public class ISA {

    public static final Map<String, Integer> OPCODES = new HashMap<>();

    static {
        // Miscellaneous / Transfer
        OPCODES.put("HLT", 000); // Halt
        OPCODES.put("LDR", 001); // Load Register from Memory
        OPCODES.put("STR", 002); // Store Register to Memory
        OPCODES.put("LDA", 003); // Load Register with Address
        OPCODES.put("LDX", 041); // Load Index Register from Memory
        OPCODES.put("STX", 042); // Store Index Register to Memory

        // Arithmetic
        OPCODES.put("AMR", 004); // Add Memory to Register
        OPCODES.put("SMR", 005); // Subtract Memory from Register
        OPCODES.put("AIR", 006); // Add Immediate to Register
        OPCODES.put("SIR", 007); // Subtract Immediate from Register

        // Transfer Control
        OPCODES.put("YZ", 010);  // Jump if Zero (Assuming JZ mapping)
        OPCODES.put("JZ", 010);  // Alias
        OPCODES.put("JNE", 011); // Jump if Not Equal
        OPCODES.put("JCC", 012); // Jump if Condition Code
        OPCODES.put("JMA", 013); // Unconditional Jump to Address
        OPCODES.put("JSR", 014); // Jump to Subroutine
        OPCODES.put("RFS", 015); // Return From Subroutine
        OPCODES.put("SOB", 016); // Subtract One and Branch
        OPCODES.put("JGE", 017); // Jump Greater Than or Equal

        // Arithmetic/Logical (Register-to-Register)
        OPCODES.put("MLT", 020); // Multiply Register by Register
        OPCODES.put("DVD", 021); // Divide Register by Register
        OPCODES.put("TRR", 022); // Test Equality of Register and Register
        OPCODES.put("AND", 023); // Logical And of Register and Register
        OPCODES.put("ORR", 024); // Logical Or of Register and Register
        OPCODES.put("NOT", 025); // Logical Not of Register
        OPCODES.put("SRC", 031); // Shift Register by Count
        OPCODES.put("RRC", 032); // Rotate Register by Count

        // VO
        OPCODES.put("IN", 061);  // Input Character to Register from Device
        OPCODES.put("OUT", 062); // Output Character from Register to Device
        OPCODES.put("CHK", 063); // Check Device Status to Register
    }

    public static int getOpcode(String mnemonic) {
        return OPCODES.getOrDefault(mnemonic.toUpperCase(), -1);
    }
    
    public static boolean isOpcode(String mnemonic) {
        return OPCODES.containsKey(mnemonic.toUpperCase());
    }
}
