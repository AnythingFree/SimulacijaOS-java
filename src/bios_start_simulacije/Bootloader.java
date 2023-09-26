package bios_start_simulacije;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import hardware_modules.CPU;
import hardware_modules.HDD;
import hardware_modules.RAM;
import kernel.Kernel;
import shell.Shell;

public class Bootloader {

	public static void main(String[] args) {
		Shell shell = boot();

		// input from user
		Scanner sc = new Scanner(System.in);
		System.out.print(">>>");
		String input = sc.nextLine();
		String shOut;
		while (input != "x") {
			if (checkIfPermited(input)) {
				System.out.print(shell.processCommand(input));
				shOut = shell.getOutput();
				if (shOut != "")
					System.out.print(shOut);
			} else {
				System.err.println("Command not recognized.");
			}
			input = sc.nextLine();
		}
	}

	private static boolean checkIfPermited(String input) {
		List<String> listOfCommands = Arrays.asList("cd", "ls", "ps", "mkdir", "run", "mem", "exit", "rm");
		String[] s = input.split(" ");
		if (s.length == 1 || s.length == 2) {
			if (listOfCommands.contains(s[0]))
				return true;
		}
		return false;
	}

	public static Shell boot() {
		HDD hdd = new HDD();
		RAM ram = new RAM();
		CPU cpu = new CPU();

		Kernel kernel = new Kernel(hdd, ram, cpu);

		Shell shell = new Shell(kernel);

		return shell;
	}

}
