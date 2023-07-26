package assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import cpu.CPU;

public class Asembler {
	private Asembler_interpreter a1 = new Asembler_interpreter();
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
		// execute code on cpu
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

}
