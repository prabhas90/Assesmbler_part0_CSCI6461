package com.csci6461.assembler;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses assembly lines into components: Label, Opcode, Operands, Comment.
 */
public class Parser {

    public static class ParsedLine {
        public String label;
        public String opcode;
        public String[] operands;
        public String comment;
        public String originalLine;

        public ParsedLine(String originalLine) {
            this.originalLine = originalLine;
            this.operands = new String[0];
        }
    }

    public ParsedLine parseLine(String line) {
        ParsedLine parsed = new ParsedLine(line);
        String cleanLine = line.trim();

        // 1. Extract Comment
        int commentIndex = cleanLine.indexOf(';'); // Assuming ';' starts comments, or maybe just check end
        // Some assemblers allow comments starting with ; anywhere.
        // NOTE: The description implies "comments included" in listing.
        // I'll assume comments are separated by some delimiter or just trailing.
        // Let's assume standard ";" for comments or checking if parsing finishes.
        // Actually, many simple assemblers just take the rest of the line.

        // Let's try to detect comment by looking for semicolon first
        if (commentIndex != -1) {
            parsed.comment = cleanLine.substring(commentIndex + 1).trim();
            cleanLine = cleanLine.substring(0, commentIndex).trim();
        }

        if (cleanLine.isEmpty()) {
            return parsed;
        }

        // 2. Split by whitespace
        String[] tokens = cleanLine.split("\\s+");
        if (tokens.length == 0)
            return parsed;

        int currentIndex = 0;
        String firstToken = tokens[currentIndex];

        // 3. Detect Label
        // Label usually ends with ':' or is in the first column if indented?
        // Course spec: "Pass 1: ... Split line into fields"
        // Usually: Label Opcode Operand
        // If line starts with a label, it might have a ':'

        boolean hasLabel = false;
        if (firstToken.endsWith(":")) {
            parsed.label = firstToken.substring(0, firstToken.length() - 1);
            hasLabel = true;
            currentIndex++;
        } else if (ISA.isOpcode(firstToken) || firstToken.equalsIgnoreCase("LOC") || firstToken.equalsIgnoreCase("Data")
                || firstToken.equalsIgnoreCase("End")) {
            // It's an opcode or directive, so no label (or label is absent)
            hasLabel = false;
        } else {
            // It's likely a label without a colon (if it's not a known opcode)
            // Check if next token is opcode
            if (tokens.length > 1) {
                // Assume it's a label
                parsed.label = firstToken;
                hasLabel = true;
                currentIndex++;
            } else {
                // Only one token? If it's not opcode, treat as label line? or error?
                // Some lines are just labels.
                parsed.label = firstToken;
                return parsed;
            }
        }

        if (currentIndex >= tokens.length)
            return parsed;

        // 4. Extract Opcode
        parsed.opcode = tokens[currentIndex];
        currentIndex++;

        if (currentIndex >= tokens.length) {
            parsed.operands = new String[0];
            return parsed;
        }

        // 5. Extract Operands
        String remainingResult = String.join(" ", java.util.Arrays.copyOfRange(tokens, currentIndex, tokens.length));
        if (!remainingResult.isEmpty()) {
            // Split by comma, trim each
            String[] rawOps = remainingResult.split(",");
            parsed.operands = new String[rawOps.length];
            for (int i = 0; i < rawOps.length; i++) {
                parsed.operands[i] = rawOps[i].trim();
            }
        } else {
            parsed.operands = new String[0];
        }

        return parsed;
    }

    public int parseValue(String value) throws NumberFormatException {
        value = value.trim();
        if (value.startsWith("0x") || value.startsWith("0X")) {
            return Integer.parseInt(value.substring(2), 16);
        } else if (value.length() > 1 && value.startsWith("0")) {
            try {
                return Integer.parseInt(value, 8);
            } catch (NumberFormatException e) {
                // Fallback to decimal if looks like octal but isn't
                return Integer.parseInt(value);
            }
        }
        return Integer.parseInt(value);
    }
}
