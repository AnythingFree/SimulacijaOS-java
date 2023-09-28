package kernel;

import java.util.ArrayList;
import java.util.PriorityQueue;

import hardware_modules.CPU;
import memory_management.Partition;
import process.ProcessMY;
import process._ProcessState;

public class ProcessScheduler {
	volatile static PriorityQueue<ProcessMY> processQueue = new PriorityQueue<>();
	private volatile ArrayList<ProcessMY> processList = new ArrayList<>();
	private volatile ArrayList<ProcessMY> doneProcesses = new ArrayList<>();
	private ArrayList<Integer> idlist = new ArrayList<>();

	public Object defragSignal = new Object();

	private CPU cpu;

	public ProcessScheduler(CPU cpu) {
		this.cpu = cpu;
		startRunThread();
		startCleanUpThread();
	}

	private void addProcess(ProcessMY process) {
		synchronized (processQueue) {
			System.out.println("Adding process with id " + process.getId() + " to the queue");
			processQueue.add(process);
			processQueue.notify();
		}
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
		process.block();

	}

	public void unblockProcess(String processID) throws Exception {
		ProcessMY process = getProcesByID(processID);
		process.unblock();
		synchronized (processQueue) {
			processQueue.add(process);
			processQueue.notify();
		}

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
			int brojac = 0;
			ProcessMY currentProcess;
			while (true) {

				synchronized (processQueue) {
					if (processQueue.isEmpty())
						try {
							processQueue.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					currentProcess = processQueue.poll();
				}

				// notifies the cpu and waits for it to finish the process (or to be blocked)
				currentProcess.start(cpu);

				brojac++;

				System.out.println("brojac do defragmentacije: " + brojac);
				if (brojac > 2) {
					brojac = 0;
					synchronized (this.defragSignal) {
						this.defragSignal.notify();
						try {
							this.defragSignal.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
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

							// remove
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

	public String printProcesses() {
		// go through processQueue and print all processes
		String rez = "";
		int broj = 0;
		synchronized (processList) {
			rez += ("========Active process list:==========\n");
			for (ProcessMY p : processList) {
				rez += ("(" + broj + ") " + p + "\n");
				broj++;
			}
		}
		synchronized (doneProcesses) {
			rez += ("=========Done process list:===========\n");
			for (ProcessMY p : doneProcesses) {
				rez += ("(" + broj + ") " + p + "\n");
				broj++;
			}
		}
		rez += ("======================================");
		return rez;
	}

}
