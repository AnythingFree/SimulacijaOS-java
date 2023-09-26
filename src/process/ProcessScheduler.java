package process;

import java.util.ArrayList;
import java.util.PriorityQueue;

import hardware_modules.CPU;
import hardware_modules.RAM;

public class ProcessScheduler {
	static PriorityQueue<Process> processQueue = new PriorityQueue<>();
	static ArrayList<Process> processList = new ArrayList<>();
	static ArrayList<Integer> idlist = new ArrayList<>();

	public ProcessScheduler(CPU cpu, RAM ram) {
    }

    public static void runProcesses() {
		while (!processQueue.isEmpty()) {
			Process currentProcess = processQueue.poll();
			currentProcess.start();
		}
	}

	public static void addProcess(Process process) {
		processQueue.add(process);
		processList.add(process);
	}

	public static void createProcess(int start, int end) {
		// generate number not in idlist
		int id = 0;
		while (idlist.contains(id)) {
			id++;
		}
		addProcess(new Process(id, start, end));
	}

	// this cleans up the processList from terminated processes
	public static void startCleanUpThread() {
		Thread cleanupThread = new Thread(() -> {

			ArrayList<Process> processesToRemove = new ArrayList<>();
			ArrayList<Integer> idlistToRemove = new ArrayList<>();

			for (Process process : processList) {
				if (process.getState() == _ProcessState.TERMINATED) {
					processesToRemove.add(process);
					idlistToRemove.add(process.getId());
				}
			}

			// free the resurces associated with this process
			// if you can not remove the process from rpocesses to be removed
			// TO DO

			// remove the process from the processList
			processList.removeAll(processesToRemove);
			idlist.removeAll(idlistToRemove);
		});
		cleanupThread.start();
	}

	public static void printProcesses() {
		// go through processQueue and print all processes
		for (Process p : processQueue) {
			System.out.println(p);
		}
	}
}
