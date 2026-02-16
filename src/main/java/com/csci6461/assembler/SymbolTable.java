package com.csci6461.assembler;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the symbol table for the assembler.
 * Stores label-address mappings.
 */
public class SymbolTable {
    private final Map<String, Integer> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }

    public void addLabel(String label, int address) throws IllegalArgumentException {
        if (table.containsKey(label)) {
            throw new IllegalArgumentException("Error: Duplicate label '" + label + "'");
        }
        table.put(label, address);
    }

    public int getAddress(String label) {
        return table.getOrDefault(label, -1);
    }

    public boolean contains(String label) {
        return table.containsKey(label);
    }

    public Map<String, Integer> getTable() {
        return table;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Symbol Table:\n");
        for (Map.Entry<String, Integer> entry : table.entrySet()) {
            sb.append(String.format("%-10s : %06o\n", entry.getKey(), entry.getValue()));
        }
        return sb.toString();
    }
}
