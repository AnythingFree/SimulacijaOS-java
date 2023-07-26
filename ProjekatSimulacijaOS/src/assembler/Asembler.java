package assembler;

import cpu.CPU;

public class Asembler {
	private Asembler_interpreter a1 = new Asembler_interpreter();
	private CPU a2 = new CPU();
	
	public void create_machine_code(String path_to_file, String new_file_name) {
		a1.asemble(path_to_file, new_file_name);
		// write that code to txt file
		
	}

	public void execute_code(String path_to_file) {
		// read machine code
		// execute code on cpu
	}
	
}
