package hardware_modules;

public class RAM {
	private int size = 256;
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
			throw new IllegalArgumentException("Invalid memory address" + address);
		}
	}

	// Write a byte to RAM at a given memory address
	public void write(int address, byte data) {
		if (isValidAddress(address)) {
			memory[address] = data;
		} else {
			throw new IllegalArgumentException("Invalid memory address" + address);
		}
	}

	// Check if the memory address is within bounds
	private boolean isValidAddress(int address) {
		return address >= 0 && address < size;
	}

	public String printMemory() {
		StringBuilder sb = new StringBuilder();
		sb.append("======== RAM ========\n");
		sb.append("| ");
		for (int i = 0; i < size; i++) {
			if (i % 10 == 0 && i != 0) {
				sb.append("\n| ");
			}
			sb.append(memory[i] + " | ");
		}

		return sb.toString();
	}

	public int getRAMSize() {
		return this.size;
	}

}
