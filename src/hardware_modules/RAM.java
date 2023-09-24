package hardware_modules;

import java.util.ArrayList;

public class RAM {
	public final static int size = 1024; // The size of the RAM in bytes
	public static byte[] memory = new byte[size]; // An array to represent the memory bytes
	public static int occupiedSize = 0;

	// Initialize the memory to zeros
	public static void initializeMemory() {
		for (int i = 0; i < size; i++) {
			memory[i] = 0;
		}
	}

	// Read a byte from RAM at a given memory address
	public static byte read(int address) {
		if (isValidAddress(address)) {
			return memory[address];
		} else {
			throw new IllegalArgumentException("Invalid memory address");
		}
	}

	// Write a byte to RAM at a given memory address
	public static void write(int address, byte data) {
		if (isValidAddress(address)) {
			memory[address] = data;
		} else {
			throw new IllegalArgumentException("Invalid memory address");
		}
	}

	// Check if the memory address is within bounds
	private static boolean isValidAddress(int address) {
		return address >= 0 && address < size;
	}

	public static void printMemory() {
		StringBuilder sb = new StringBuilder();
		System.out.println("======== RAM ========");
		System.out.print("| ");
		for (int i = 0; i < size; i++) {
			if (i % 14 == 0 && i != 0) {
				sb.append("\n| ");
			}
			sb.append(memory[i] + " | ");
		}
		System.out.println(sb.toString());
	}

	public static ArrayList<String> readFromRam(int startAdress, int endAdress) {
		return null;
	}

	public static int[] loadBinary(ArrayList<String> binary) {
		return null;
	}

}
