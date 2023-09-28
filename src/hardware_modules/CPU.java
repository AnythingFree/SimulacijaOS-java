package hardware_modules;

import java.util.ArrayList;
import java.util.HashMap;

import _util.Formater;
import memory_management.Partition;
import process.ProcessMY;
import process._ProcessControlBlock;
import process._ProcessState;

// https://www.google.com/search?client=firefox-b-d&q=Zero-Address+Assembly+Language
// BRZINA PROCESORA TREBA DA BUDE PARAMETAR
public class CPU extends Thread {

	private int startOfStack;
	private int endOfStack;
	private int outPointer;
	private int endOfCode;

	private int programCounter;
	private int stackPointer;

	private boolean hlt = false;

	private int speed;
	public Object signal = new Object();
	private ProcessMY process;
	private Partition partition;

	public CPU(int speed) {
		this.speed = speed;
		start();
	}

	// reversed values of instructionMappings with lower case letters
	@SuppressWarnings("serial")
	private static HashMap<String, String> ljudskiCitljivaKomanda = new HashMap<>() {
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

	@Override
	public void run() {
		while (true) {

			// wait till notified
			synchronized (this.signal) {
				try {
					this.signal.wait();

					this.hlt = false;
					startWorking();

					this.signal.notify();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

	}

	private void startWorking() {

		int numberOfinst = 0;
		while (this.process.getState() == _ProcessState.RUNNING && !hlt && (programCounter < (this.endOfCode + 1))) {

			String instruction = Formater.toBinaryString(readFromMemory(programCounter));
			try {

				// sleep for enough amount to simulate speed
				if (numberOfinst == this.speed) {
					numberOfinst = 0;
					Thread.sleep(1000);
				}
				numberOfinst++;

				executeInstruction(ljudskiCitljivaKomanda.get(instruction));
			} catch (Exception e) {
				System.out.println("\r --Greska u izvrsavanju instrukcije u CPU:\n --" + e.getMessage());
				break;
			}
			programCounter++;
		}

	}

	private void executeInstruction(String instructionName) throws Exception {

		int a, b, c = 0;
		switch (instructionName) {

			case "hlt":
				process.terminate();
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

			case "push": // "push 23"
				programCounter++;
				c = (int) readFromMemory(programCounter);
				push(c);
				break;

			// posto je zero adress asembly pop moze da uvijek pise u output
			// ne mora korisnik da pise pop 232, nego samo pop
			case "pop": // pop 232
				a = pop();
				this.partition.writeToOutput(outPointer++, (byte) a);
				break;

			default:
				System.out.println("Nepoznata instrukcija");
				break;

		}
	}

	private void push(int c) throws Exception {
		if (stackPointer > endOfStack) {
			throw new Exception("Stack overflow");
		}
		this.partition.writeToStack(stackPointer, (byte) c);
		stackPointer++;
	}

	private int pop() throws Exception {
		if (stackPointer < startOfStack) {
			throw new Exception("Stack underflow");
		}

		stackPointer--;
		int a = (int) readFromMemory(stackPointer);
		return a;
	}

	public synchronized void setRegisters(process.ProcessMY process) {
		this.hlt = false;
		this.process = process;
		_ProcessControlBlock pcb = process.getPcb();

		this.programCounter = pcb.getProgramCounter();
		this.stackPointer = pcb.getStackPointer();
		this.outPointer = pcb.getOutPointer();

		this.startOfStack = pcb.getStartOfStack();
		this.endOfStack = pcb.getEndOfStack();
		this.endOfCode = pcb.getEndOfCode();
		this.partition = pcb.getPartition();

	}

	private byte readFromMemory(int adress) {
		return this.partition.readFromRam(adress);
	}

	public int getProgramCounter() {
		return this.programCounter;
	}

	public int getStackPointer() {
		return this.stackPointer;
	}

	public int getOutPointer() {
		return this.outPointer;
	}

	// ovo obrisati
	public void executeCode(ArrayList<String> binaryCode) throws Exception {

		programCounter = 0;

		String instructionName;
		while (hlt == false && programCounter < binaryCode.size()) {

			instructionName = ljudskiCitljivaKomanda.get(binaryCode.get(programCounter));

			executeInstruction(instructionName);

			programCounter++;

		}

		// RAM.printMemory();

	}

	public void signal() {

	}

	public ProcessMY getProcess() {
		return this.process;
	}

}
