package assembler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import _interfaces.Asembler_interpreter_i;

//ova klasa mora da prima fajl. Pogledati Test.java klasu odakle ide poziv... i
// tu u Test klasi pises kako ti se zove fajl tj program.asm kao i naziv output txt datoteke
public class Asembler_interpreter implements Asembler_interpreter_i {

	private static String INPUT_FILE_PATH = "";//"program.asm";
	private static String OUTPUT_FILE_PATH = "";//"output.txt";

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
	}
	public Asembler_interpreter(String path_to_file,String name_of_new_file) {
		this.INPUT_FILE_PATH=path_to_file;
		this.OUTPUT_FILE_PATH=name_of_new_file;
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

	// ucitava .asm fajl
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
			String[] parts = instruction.split(" ", 3); // LEXER funkcija NEK CISTI KOD OD KOMENTARA
														// U sklopu ove funkcije cistimo kod od komentara 
			String opcode = instructionMappings.get(parts[0]); // i ostavljamo samo naredbe i brojeve
			String binarniArgument="";
			if (opcode != null) {
				String argument = (parts.length > 1) ? parts[1] : null; // ako insturkcija ima vise od 1 dijela u
				if(argument != null) {									// argument ide drugi dio u suprotnom null
				int argumentBroj=Integer.parseInt(argument);			//uzimamo argument, pretvorimo ga u int
				binarniArgument=intToBinary(argumentBroj,4); //da bismo primjenili metodu toBinatyString
				} 															//i broj koji je uz naredbu pretvaramo u binarni 
				binaryCode.add(new InstructionPair(opcode, binarniArgument)); // dodaje instrukciju i broj (ako ga ima)
			} else {
				System.err.println("Nepoznata instrukcija: " + parts[0]);
			}
		}
		return binaryCode;
	}
	//funkcija kojom pravimo 4 cifre u izlaznoj datoteci
	private static String intToBinary(int number, int desiredDigits) {
		String binaryString = Integer.toBinaryString(number);
        int leadingZeros = Math.max(desiredDigits - binaryString.length(), 0);
        return String.format("%04d", Integer.parseInt(binaryString)); 
        //koristimo format "%04d" unutar String.format() kako bismo osigurali da
        //binarni zapis ima četiri cifre i da se nule dodaju ispred ako je potrebno
	}

	// upisuje binarni kod u fajl .txt
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
	public void asemble(String path_to_file, String name_of_new_file) {
		// read_asm_file()
		// lexer()
		// asembleInstructions()

		// writeBinaryCodeToFilen() - mislim da je bolje da se ovo premjesti u Asembler
		// klasu
		Asembler_interpreter a=new Asembler_interpreter(path_to_file,name_of_new_file);
		
		//System.out.println();
		try {
			ArrayList<String> instructions = readInputFile(); // cita .asm fajl

			ArrayList<InstructionPair> binaryCode = assembleInstructions(instructions);

			writeBinaryCodeToFile(binaryCode);

			System.out.println("Asembliranje uspješno završeno. Binarni kod je sačuvan u " + OUTPUT_FILE_PATH);

		} catch (IOException e) {
			System.err.println("Greška prilikom obrade datoteka: " + e.getMessage());
		}
	} 

	
	// ovo su funkcije koje smo implementirali, jer ova klasa naslijedjuje klasu Asembler_interpreter_i
	@Override
	public void read_asm_file(String path_to_file) {
		// Funkcija koja je ista kao readInputFile, samo drugaciji naziv
		
	}

	@Override
	public ArrayList<String> lexer() {
		// Funkcija koja cisti kod od komentara, ali ovdje smo to uradili u sklopu
		// funkcije assembleInstructions
		return null;
	}

	
	
}
