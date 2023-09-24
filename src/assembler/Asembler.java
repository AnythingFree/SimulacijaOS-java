package assembler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import hardware_modules.CPU;

public class Asembler {
	private Asembler_translator a1 = new Asembler_translator();
	private CPU a2 = new CPU();

	protected void create_machine_code(String path_to_file, String OUTPUT_FILE_PATH) {

		// pretvara u masinski kod binarno
		ArrayList<String> binaryCode = a1.asemble(path_to_file);

		// pise u fajl
		try {
			writeBinaryCodeToFile(binaryCode, OUTPUT_FILE_PATH);
			System.out.println("Asembliranje uspješno završeno. Binarni kod je sačuvan u " + OUTPUT_FILE_PATH);
		} catch (IOException e) {
			System.err.println("Greška prilikom obrade datoteka: " + e.getMessage());
		}

	}

	protected void execute_code(String path_to_file) {
		// read machine code
		ArrayList<String> binaryCode = null;
		try {
			binaryCode = readBinaryCodeFromFile(path_to_file);
		} catch (FileNotFoundException e) {
			System.err.println("Datoteka " + path_to_file + " nije pronađena.");
		} catch (IOException e) {
			System.err.println("Greška prilikom obrade datoteka: " + e.getMessage());
		}

		// execute code on cpu
		a2.executeCode(binaryCode);
	}

	// ====================================

	// upisuje binarni kod u fajl .txt
	private void writeBinaryCodeToFile(ArrayList<String> binaryCode, String OUTPUT_FILE_PATH) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE_PATH));
		for (String piece : binaryCode) {
			writer.write(piece + " ");
		}
		writer.close();
	}

	// cita binarni kod iz fajla
	private ArrayList<String> readBinaryCodeFromFile(String path_to_file) throws IOException {
		ArrayList<String> binaryCode = new ArrayList<String>();
		
		BufferedReader reader = new BufferedReader(new FileReader(path_to_file));
		for (String line : reader.readLine().split(" ")) {
			binaryCode.add(line);
		}
		return binaryCode;

	}

}
