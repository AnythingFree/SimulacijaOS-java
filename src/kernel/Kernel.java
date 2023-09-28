package kernel;

import java.util.ArrayList;
import java.util.Arrays;

import assembler.Asembler_translator;
import hardware_modules.CPU;
import hardware_modules.HDD;
import hardware_modules.RAM;
import memory_management.MemoryManager;
import memory_management.Partition;
import process.ProcessScheduler;

// shell poziva kernelove funkcije
// shell: cd ls ps mkdir run mem exit rm block unblock

// Kernel is the core part of an operating system which manages
// system resources. It also acts like a bridge between
// application and hardware of the computer

// CPU Memory Disks IO
public class Kernel {

	private MemoryManager memoryManager;
	private ProcessScheduler processScheduler;

	public Kernel(HDD hdd, RAM ram, CPU cpu) {
		this.memoryManager = new MemoryManager(hdd, ram);
		this.processScheduler = new ProcessScheduler(cpu);
		defragmentationThread();
	}

	// SHELL KOMANDE cd ls ps mkdir run mem exit rm
	// ============= file manager ============================

	public boolean listFiles() {
		try {
			memoryManager.listFiles();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean makeDirectory(String nameOfNewDir) {
		try {
			memoryManager.makeDirectory(nameOfNewDir);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean changeDirectory(String nameORpath) {
		try {
			memoryManager.changeCurrentDirectory(nameORpath);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean deleteFileORDir(String name) {
		if (name.equals("/") || name.equals("root"))
			return false;
		try {
			memoryManager.deleteFileORDir(name);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public String getCurrentDir() {
		return memoryManager.getCurrentDir();
	}

	// ============= ram manager ============================
	public void printRAM() {
		memoryManager.printRAM();
	}

	// ============= process scheduler ============================

	public boolean blockProcess(String string) {
		try {
			processScheduler.blockProcess(string);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace()[0]);
			return false;
		}
		return true;
	}

	public boolean unblockProcess(String string) {
		try {
			processScheduler.unblockProcess(string);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace()[0]);
			return false;
		}
		return true;
	}

	public void printProcesses() {
		processScheduler.printProcesses();
	}

	// =================== process =============================
	public boolean runProcesses(String nameOfFile) {
		try {
			// (dobavi iz diska podatke, pretvori u masinsku inst, sacuvaj u ram)

			// dobavi iz diska
			String text = memoryManager.getFileFromDisk(nameOfFile);

			// pretvori u masinsku inst
			ArrayList<String> instructionsString = new ArrayList<>(Arrays.asList(text.split("\n")));
			instructionsString.removeIf(String::isEmpty);
			ArrayList<String> instructionsBinary = Asembler_translator.assemble2(instructionsString);

			// sacuvaj u ram
			Partition partition = memoryManager.loadToRam(instructionsBinary);

			// (napravi proces i pusti ga da se izvrsava)
			processScheduler.createProcess(partition);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace()[0]);
			return false;
		}

		return true;
	}

	public void defragmentationThread() {
		Thread defragThread = new Thread(() -> {

			while (true) {

				synchronized (processScheduler.defragSignal) {
					try {
						processScheduler.defragSignal.wait();

						System.out.println("======= Defragmentacija (start) =======");
						memoryManager.printRAM();
						memoryManager.defragmentation();
						memoryManager.printRAM();
						System.out.println("======= Defragmentacija (kraj) =======");

						processScheduler.defragSignal.notify();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

		});
		defragThread.start();
	}
}
