package assembler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Asembler_translator {

	@SuppressWarnings("serial")
	private static HashMap<String, String> instructionMappings = new HashMap<>() {
		{
			put("HLT", "0000");
			put("ADD", "0001");
			put("SUB", "0010");
			put("MUL", "0011");
			put("DIV", "0100");
			put("INC", "0101");
			put("DEC", "0110");
			put("PUSH", "0111");
			put("POP", "1000");

		}
	};

	// ucitava .asm fajl
	private ArrayList<String> readASMFile(String INPUT_FILE_PATH) throws IOException {
		ArrayList<String> instructions = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE_PATH));
		String line;
		while ((line = reader.readLine()) != null) {
			instructions.add(line.trim());
		}
		reader.close();
		return instructions;
	}

	// funkcija kojom pravimo 4 cifre u izlaznoj datoteci
	private static String intToBinary(String ins) {
		int number = Integer.parseInt(ins);
		String binaryString = Integer.toBinaryString(number);
		return String.format("%04d", Integer.parseInt(binaryString));
	}

	// pretvara insptrukcije u binarno
	private static ArrayList<String> assembleInstructions(ArrayList<String> instructions) throws Exception {
		ArrayList<String> binaryCode = new ArrayList<String>();

		for (String instruction : instructions) {
			String instructionBinary;
			try {
				instructionBinary = intToBinary(instruction);

			} catch (NumberFormatException e) {
				instructionBinary = instructionMappings.get(instruction);
			} catch (Exception e) {
				throw new Exception("Nepoznata instrukcija za asembler");
			}

			binaryCode.add(instructionBinary);
		}

		return binaryCode;
	}

	private static String removeComments(String instruction) {
		// Remove everything after the first occurrence of ';'
		int commentIndex = instruction.indexOf(';');
		if (commentIndex != -1) {
			return instruction.substring(0, commentIndex).trim();
		}
		return instruction;
	}

	private static ArrayList<String> lexer(ArrayList<String> instructions) {
		ArrayList<String> cleanedInstructions = new ArrayList<>();

		// remove empty lines
		instructions.remove("");

		for (String instruction : instructions) {

			if (!instruction.startsWith(";")) { // izbacuje linije koje pocinju sa ;

				// remove comments
				String cleanedInstruction = removeComments(instruction);

				// tokenize it
				String[] tokens = cleanedInstruction.toUpperCase().split("\\s+"); // mozda mijenjati ovaj znak
				for (String token : tokens) {
					if (!token.isEmpty())
						cleanedInstructions.add(token);
				}
			}

		}

		return cleanedInstructions;
	}

	protected ArrayList<String> asemble(String path_to_file) throws Exception {

		try {
			ArrayList<String> instructions;

			instructions = readASMFile(path_to_file);
			System.out.println("Datoteka procitana: " + path_to_file);

			instructions = lexer(instructions); // DOPUSTA DA NOVI RED ZAPOCINJE SA BROJEM DA LI TO DOZVOLITI?
			// instructions = parser(instructions); check sintax?

			ArrayList<String> binaryCode = assembleInstructions(instructions);
			return binaryCode;

		} catch (IOException e) {
			System.err.println("Greška prilikom obrade datoteka: " + e.getMessage());
		}

		return null;
	}

	public static ArrayList<String> assemble2(ArrayList<String> instructions) throws Exception {

		instructions = lexer(instructions); 
		// instructions = parser(instructions); check sintax?

		ArrayList<String> binaryCode = assembleInstructions(instructions);

		return binaryCode;

	}

}
