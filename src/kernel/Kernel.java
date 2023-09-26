package kernel;

import java.io.File;
import java.util.ArrayList;

import file_system.FileManager;
import file_system.FileSystem;
import hardware_modules.CPU;
import hardware_modules.HDD;
import hardware_modules.RAM;
import memory_management.RamManager;
import process.ProcessScheduler;

// shell poziva kernelove funkcije
// shell: cd ls ps mkdir run mem exit rm

// Kernel is the core part of an operating system which manages
// system resources. It also acts like a bridge between
// application and hardware of the computer

// CPU Memory Disks IO
public class Kernel {

	private FileManager fileManager;
	private RamManager ramManager;
	private ProcessScheduler processScheduler;

	public Kernel(HDD hdd, RAM ram, CPU cpu) {
		this.fileManager = new FileManager(hdd);
		this.ramManager = new RamManager(ram);
		this.processScheduler = new ProcessScheduler(cpu, ram);
		ProcessScheduler.startCleanUpThread();
	}

	// SHELL KOMANDE cd ls ps mkdir run mem exit rm
	// ============= file manager ============================

	public boolean listFiles() {
		try {
			fileManager.listFiles();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean makeDirectory(String nameOfNewDir) {
		try {
			fileManager.makeDirectory(nameOfNewDir);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean changeDirectory(String nameORpath) {
		try {
			fileManager.changeCurrentDirectory(nameORpath);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean deleteFileORDir(String name) {
		try {
			fileManager.deleteFileORDir(name);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public String getCurrentDir() {
		return fileManager.getCurrentDir();
	}

	// ============= ram manager ============================

	// ============= process scheduler ============================

	// ovo treba da bude tred ili slicno ("run komanda vraca kontrolu korisniku")
	public void createProcess(String path) {
		// load binary file from file
		// ArrayList<String> binary = HDD.getBinary(path);
		// int[] startEndAdress = RAM.loadBinary(binary);

		// create process
		// ProcessScheduler.createProcess(startEndAdress[0], startEndAdress[1]);
	}

	public void shutDown() {
	}

}
