package assembler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import interfaces.Asembler_interpreter_i;

//ova klasa mora da prima fajl. Pogledati Test.java klasu odakle ide poziv... i
// tu u Test klasi pises kako ti se zove fajl tj program.asm kao i naziv output txt datoteke
public class Asembler_interpreter implements Asembler_interpreter_i {

	private static final String INPUT_FILE_PATH = "program.asm";
	private static final String OUTPUT_FILE_PATH = "output.txt";

	// hard codovane funkcije asemblera u binarni kod
	// MORAS OVDE DA NAPISES ONO IZ "IDEJA.TXT" FAJLA SAMO OSNOVNE TJ PRVIH 6
	// KAD URADIS DA SE POZIVA OVA KLASA IZ DRUGE KLASE (Asembler.java) BIRSES OVO
	// STATIC, NIGDJE TI NE TREBA STATIC OVDE U KLASI
	private static HashMap<String, String> instructionMappings = new HashMap<>() {
		{
			put("ADD", "0001");
			put("SUB", "0010");
			put("PUSH", "1000");
			put("POP", "1001");
			put("HLT", "1110");
		}
	};

	public Asembler_interpreter() {
		super();
		// TODO Auto-generated constructor stub
	}

	// pomocna klasa - za ovo mozes koristi niz u javi: String[] s = new String[2];
	// kad zamjenis sa nizom obrisi ovu klasu
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

	// ucitati .asm fajl
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

	// pretvara insptrukcije u binarno
	private static ArrayList<InstructionPair> assembleInstructions(ArrayList<String> functionInstructions) {
		ArrayList<InstructionPair> binaryCode = new ArrayList<>();
		for (String instruction : functionInstructions) {
			String[] parts = instruction.split(" ", 2); // LEXER funkcija NEK CISTI KOD OD KOMENTARA !!!!!!!!!!!!!!!
			String opcode = instructionMappings.get(parts[0]);
			if (opcode != null) {
				String argument = (parts.length > 1) ? parts[1] : null; // ako insturkcija ima vise od 1 dijela u
																		// argument ide drugi dio u suprotnom null
				binaryCode.add(new InstructionPair(opcode, argument)); // dodaje instrukciju i broj (ako ga ima)
			} else {
				System.err.println("Nepoznata instrukcija: " + parts[0]);
			}
		}
		return binaryCode;
	}

	// pise binarni kod u fajl
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

	public static void main(String[] args) {
		try {
			ArrayList<String> instructions = readInputFile(); // cita .asm fajl

			ArrayList<InstructionPair> binaryCode = assembleInstructions(instructions);

			writeBinaryCodeToFile(binaryCode);

			System.out.println("Asembliranje uspješno završeno. Binarni kod je sačuvan u " + OUTPUT_FILE_PATH);

		} catch (IOException e) {
			System.err.println("Greška prilikom obrade datoteka: " + e.getMessage());
		}
	}

	@Override
	public void read_asm_file(String path_to_file) {
		// = readInputFile()
		// ovo je isto sto i readInputFile ali bolji naziv :)
	}

	@Override
	public ArrayList<String> lexer() {
		// filtracija komentara i gluposti
		return null;
	}

	@Override
	public void asemble(String path_to_file, String name_of_new_file) {
		// read_asm_file()
		// lexer()
		// asembleInstructions()

		// writeBinaryCodeToFilen() - mislim da je bolje da se ovo premjesti u Asembler
		// klasu

	}
}
