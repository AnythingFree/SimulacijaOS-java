package process;

import java.util.ArrayList;
import java.util.PriorityQueue;

import hardware_modules.CPU;
import memory_management.Partition;

public class ProcessScheduler {
	volatile static PriorityQueue<ProcessMY> processQueue = new PriorityQueue<>();
	private volatile ArrayList<ProcessMY> processList = new ArrayList<>();
	private volatile ArrayList<ProcessMY> doneProcesses = new ArrayList<>();
	private ArrayList<Integer> idlist = new ArrayList<>();

	private CPU cpu;

	public ProcessScheduler(CPU cpu) {
		this.cpu = cpu;
		startRunThread();
		startCleanUpThread();
	}

	private void addProcess(ProcessMY process) {
		processQueue.add(process);
		synchronized (processList) {
			processList.add(process);
		}
	}

	public void createProcess(Partition partition) {
		// generate number not in idlist
		int id = 0;
		while (idlist.contains(id)) {
			id++;
		}
		idlist.add(id);
		addProcess(new ProcessMY(id, partition));
	}

	public void blockProcess(String processID) throws Exception {
		ProcessMY process = getProcesByID(processID);
		if (process.getState() == _ProcessState.RUNNING) {
			process.block();
		} else
			throw new Exception("Process with id " + processID + " is not running");

	}

	public void unblockProcess(String processID) throws Exception {
		ProcessMY process = getProcesByID(processID);
		if (process.getState() == _ProcessState.BLOCKED) {
			process.unblock();
			processQueue.add(process);
		} else
			throw new Exception("Process with id " + processID + " is not blocked");

	}

	private ProcessMY getProcesByID(String processID) throws Exception {
		synchronized (processList) {
			for (ProcessMY process : processList) {
				if (process.getId() == Integer.parseInt(processID)) {
					return process;
				}
			}
		}
		throw new Exception("Process with id " + processID + " does not exist");
	}

	// this cleans up the processList from terminated processes
	private void startRunThread() {
		Thread runThread = new Thread(() -> {
			while (true) {
				if (cpu.isFree() && !processQueue.isEmpty()) { // cpu is free trenutno ne treba ali neka stoji
					ProcessMY currentProcess = processQueue.poll();
					currentProcess.start(cpu);
				}
			}
		});
		runThread.start();
	}

	// this cleans up the processList from terminated processes
	private void startCleanUpThread() {
		Thread cleanupThread = new Thread(() -> {

			ArrayList<ProcessMY> processesToRemove = new ArrayList<>();
			ArrayList<Integer> idlistToRemove = new ArrayList<>();

			while (true) {
				// pause for 2 seconds
				try {
					Thread.sleep(5000); // 2 seconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (processList) {
					for (ProcessMY process : processList) {
						if (process.getState() == _ProcessState.TERMINATED) {

							// release resourses
							process.releaseResources();

							// set DONE
							process.setState(_ProcessState.DONE);

							// remove from queque
							processesToRemove.add(process);
							idlistToRemove.add(process.getId());
						}
					}

					// remove the process from the processList
					processList.removeAll(processesToRemove);
				}

				synchronized (idlist) {
					idlist.removeAll(idlistToRemove);

				}

				// add the processes to the doneProcesses list
				synchronized (doneProcesses) {
					doneProcesses.addAll(processesToRemove);
				}

				// clear the lists
				processesToRemove.clear();
				idlistToRemove.clear();
			}

		});
		cleanupThread.start();
	}

	public void printProcesses() {
		// go through processQueue and print all processes
		synchronized (processList) {
			System.out.println("Active process list:");
			for (ProcessMY p : processList) {
				System.out.println(p);
			}
		}
		synchronized (doneProcesses) {
			System.out.println("Done process list:");
			for (ProcessMY p : doneProcesses) {
				System.out.println(p);
			}
		}

	}

}
