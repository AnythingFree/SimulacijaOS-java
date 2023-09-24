package kernel;

import java.io.File;
import java.util.ArrayList;

import file_system.FileManager;
import file_system.FileSystem;
import hardware_modules.CPU;
import hardware_modules.HDD;
import hardware_modules.RAM;
import memory_management.RamManager;

// shell poziva kernelove funkcije
// shell: cd ls ps mkdir run mem exit rm

// Kernel is the core part of an operating system which manages
// system resources. It also acts like a bridge between
// application and hardware of the computer

// CPU Memory Disks IO
public class Kernel {

	private FileManager fileManager;
	private RamManager ramManager;

	public Kernel() {
		ProcessScheduler.startCleanUpThread();
	}

	public void createProcess(String path) {
		// load binary file from file
		ArrayList<String> binary = HDD.getBinary(path);
		int[] startEndAdress = RAM.loadBinary(binary);

		// create process
		ProcessScheduler.createProcess(startEndAdress[0], startEndAdress[1]);
	}

	// RAM ram = new RAM();
	// Disk disk = new Disc();
	// CPU cpu = new CPU();
	// FileTree fileTree = new FileTree();

	//======== TO DO MARIJA =========
	// SHELL KOMANDE cd ls ps mkdir run mem exit rm

}
