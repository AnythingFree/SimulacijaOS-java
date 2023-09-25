package hardware_modules;

import java.util.ArrayList;
import java.util.HashMap;

import kernel._ProcessControlBlock;

// https://www.google.com/search?client=firefox-b-d&q=Zero-Address+Assembly+Language
// BRZINA PROCESORA TREBA DA BUDE PARAMETAR
public class CPU {

	// hard codovali smo stek memoriju ali moze i dinamicki da se mijenja
	static int stack_pointer = 0;
	int end_of_stack = 9;

	static int instructionPointer = 0;

	boolean zero, carry;
	static boolean hlt = false;

	private static ArrayList<String> ramSimulation;

	// reversed values of instructionMappings with lower case letters
	@SuppressWarnings("serial")
	private static HashMap<String, String> instructionMappingsReversed = new HashMap<>() {
		{
			put("0000", "hlt");
			put("0001", "add");
			put("0010", "sub");
			put("0011", "mul");
			put("0100", "div");
			put("0101", "inc");
			put("0110", "dec");
			put("0111", "push");
			put("1000", "pop");

		}
	};

	public static void executeCode(ArrayList<String> binaryCode) {

		ramSimulation = binaryCode;
		instructionPointer = 0;

		String instructionName;
		while (hlt == false && instructionPointer < binaryCode.size()) {

			instructionName = instructionMappingsReversed.get(binaryCode.get(instructionPointer));

			executeInstruction(instructionName);

			instructionPointer++;

		}

		RAM.printMemory();

	}

	private static void executeInstruction(String instructionName) {

		int a, b, c = 0;
		switch (instructionName) {

			case "hlt":
				hlt = true;
				break;

			case "add":
				a = pop();
				b = pop();
				c = a + b;
				push(c);
				break;

			case "sub":
				a = pop();
				b = pop();
				c = a - b;
				push(c);
				break;

			case "mul":
				a = pop();
				b = pop();
				c = a * b;
				push(c);
				break;

			case "div":
				a = pop();
				b = pop();
				c = a / b;
				push(c);
				break;

			case "inc":
				a = pop();
				a++;
				push(a);
				break;

			case "dec":
				a = pop();
				a--;
				push(a);
				break;

			case "push":
				instructionPointer++;

				String binary_string = ramSimulation.get(instructionPointer); // ovo bi sve trebalo biti int jer je u
																				// ramu?

				// convert binary to int
				c = Integer.parseInt(binary_string, 2);

				push(c);
				break;

			case "pop":
				a = RAM.memory[--stack_pointer];
				break;

			default:
				System.out.println("Nepoznata instrukcija");
				break;

		}
	}

	private static void push(int c) {
		RAM.memory[stack_pointer++] = (byte) c;
	}

	private static int pop() {
		int a = RAM.memory[--stack_pointer];
		return a;
	}

	public static void setRegisters(_ProcessControlBlock pcb) {
	}

}
