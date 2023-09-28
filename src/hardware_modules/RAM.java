package hardware_modules;

public class RAM {
	private int size = 1024;
	private byte[] memory = new byte[size];

	public RAM() {
		for (int i = 0; i < size; i++) {
			memory[i] = 0;
		}
	}

	// Read a byte from RAM at a given memory address
	public byte read(int address) {
		if (isValidAddress(address)) {
			return memory[address];
		} else {
			throw new IllegalArgumentException("Invalid memory address");
		}
	}

	// Write a byte to RAM at a given memory address
	public void write(int address, byte data) {
		if (isValidAddress(address)) {
			memory[address] = data;
		} else {
			throw new IllegalArgumentException("Invalid memory address");
		}
	}

	// Check if the memory address is within bounds
	private boolean isValidAddress(int address) {
		return address >= 0 && address < size;
	}

	public void printMemory() {
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

	public int getRAMSize() {
		return this.size;
	}

	public void setFree(int startAddress, int endAddress) {
		for (int i = startAddress; i < endAddress; i++) {
			memory[i] = 0;
		}
	}

}
