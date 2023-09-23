package cpu;

import java.util.ArrayList;
import java.util.HashMap;

import memory_management.RAM;

// https://www.google.com/search?client=firefox-b-d&q=Zero-Address+Assembly+Language
public class CPU {

	// hard codovali smo stek memoriju ali moze i dinamicki da se mijenja
	int stack_pointer = 0;
	int end_of_stack = 9;

	int instructionPointer = 0;

	boolean zero, carry;
	boolean hlt = false;

	private ArrayList<String> ramSimulation;

	// reversed values of instructionMappings with lower case letters
	@SuppressWarnings("serial")
	private HashMap<String, String> instructionMappingsReversed = new HashMap<>() {
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

	public void execute_code(ArrayList<String> binaryCode) {

		ramSimulation = binaryCode;
		instructionPointer = 0;

		String instructionName;
		while (hlt == false && instructionPointer < binaryCode.size()) {

			instructionName = instructionMappingsReversed.get(binaryCode.get(instructionPointer));

			execute_instruction(instructionName);

			instructionPointer++;

		}

		RAM.printMemory();

	}

	private void execute_instruction(String instructionName) {

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

			String binary_string = ramSimulation.get(instructionPointer); // ovo bi sve trebalo biti int jer je u ramu?

			// convert binary to int
			c = Integer.parseInt(binary_string, 2);

			push(c);
			break;

		case "pop":
			a = RAM.niz[--stack_pointer];
			break;

		default:
			System.out.println("Nepoznata instrukcija");
			break;

		}
	}

	private void push(int c) {
		RAM.niz[stack_pointer++] = c;
	}

	private int pop() {
		int a = RAM.niz[--stack_pointer];
		return a;
	}

}
