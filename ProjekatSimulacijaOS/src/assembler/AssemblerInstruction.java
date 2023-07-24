package assembler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssemblerInstruction {
    private static final String INPUT_FILE_PATH = "program.asm";
    private static final String OUTPUT_FILE_PATH = "output.txt";

   

   

    private static HashMap<String, String> loadInstructionMappings() {
        // Implementirajte ovu metodu kako biste učitali mapiranja instrukcija iz nekog izvora (txt fajl, hardcoded, itd.)
        // Ovdje biste trebali mapirati naredbe na binarne kodove.
        HashMap<String, String> instructionMappings = new HashMap<>();
        instructionMappings.put("ADD", "0001");
        instructionMappings.put("SUB", "0010");
        instructionMappings.put("MOV", "0101");
        instructionMappings.put("PUSH", "1000");
        instructionMappings.put("POP", "1001");
        instructionMappings.put("JMP", "1100");
        instructionMappings.put("JNZ", "1101");
        instructionMappings.put("HLT", "1110");
        return instructionMappings;
    }

    private static ArrayList<String> readInputFile() throws IOException {
        ArrayList<String> instructions = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE_PATH));
        String line;
        while ((line = reader.readLine()) != null) {
            instructions.add(line.trim());
        }
        reader.close();
        return instructions;
    }

  

    private static ArrayList<InstructionPair> assembleInstructions(ArrayList<String> functionInstructions, HashMap<String, String> instructionMappings) {
        ArrayList<InstructionPair> binaryCode = new ArrayList<>();
        for (String instruction : functionInstructions) {
            String[] parts = instruction.split("\\s+", 2);
            String opcode = instructionMappings.get(parts[0]);
            if (opcode != null) {
                String argument = (parts.length > 1) ? parts[1] : null;
                binaryCode.add(new InstructionPair(opcode, argument));
            } else {
                System.err.println("Nepoznata instrukcija: " + parts[0]);
            }
        }
        return binaryCode;
    }

    private static void writeBinaryCodeToFile(ArrayList<InstructionPair> binaryCode) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH));
        for (InstructionPair pair : binaryCode) {
            writer.write(pair.getOpcode());
            if (pair.getArgument() != null) {
                writer.write(" " + pair.getArgument());
            }
            writer.newLine();
        }
        writer.close();
    }

   

    private static class InstructionPair {
        private String opcode;
        private String argument;

        public InstructionPair(String opcode, String argument) {
            this.opcode = opcode;
            this.argument = argument;
        }

        public String getOpcode() {
            return opcode;
        }

        public String getArgument() {
            return argument;
        }
    }
    private static ArrayList<String> extractFunctionInstructions(ArrayList<String> instructions) {
        ArrayList<String> functionInstructions = new ArrayList<>();
        for (String line : instructions) {
            String[] parts = line.split("\\s+", 2);
            String instruction = parts[0].trim();
            if (!instruction.isEmpty()) {
                functionInstructions.add(line.trim());
            }
        }
        return functionInstructions;
    }

    // ... (ostatak koda je nepromijenjen)

    public static void main(String[] args) {
        try {
            ArrayList<String> instructions = readInputFile();
            ArrayList<String> functionInstructions = extractFunctionInstructions(instructions);
            HashMap<String, String> instructionMappings = loadInstructionMappings();

            ArrayList<InstructionPair> binaryCode = assembleInstructions(functionInstructions, instructionMappings);
            writeBinaryCodeToFile(binaryCode);
            System.out.println("Asembliranje uspješno završeno. Binarni kod je sačuvan u " + OUTPUT_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Greška prilikom obrade datoteka: " + e.getMessage());
        }
    }
}
