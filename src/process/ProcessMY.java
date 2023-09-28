package process;

import java.util.Date;

import hardware_modules.CPU;
import memory_management.Partition;

public class ProcessMY implements Comparable<ProcessMY> {
	private volatile _ProcessControlBlock pcb;

	public ProcessMY(int id, Partition partition) {
		this.pcb = new _ProcessControlBlock(id, partition, new Date(), _ProcessState.READY);
	}

	public void start(CPU cpu) {
		if (getState() == _ProcessState.READY) {
			setState(_ProcessState.RUNNING);
			cpu.setRegisters(this);

			synchronized (cpu.signal) {
				cpu.signal.notify();

				try {
					cpu.signal.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else if (getState() == _ProcessState.BLOCKED) {
			return;
		} else {
			System.out.println("Process with id " + getId() + " is terminated or done or running");
		}
	}

	public void block() throws Exception {
		if (getState() == _ProcessState.RUNNING || getState() == _ProcessState.READY) {
			setState(_ProcessState.BLOCKED);
		} else
			throw new Exception("Process with id " + getId() + " is not in running or ready state");

	}

	public void unblock() throws Exception {
		if (getState() == _ProcessState.BLOCKED) {
			this.setState(_ProcessState.READY);
		} else
			throw new Exception("Process with id " + getId() + " is not blocked");

	}

	public void terminate() {
		this.setState(_ProcessState.TERMINATED);

	}

	synchronized void setState(_ProcessState state) {
		this.pcb.setState(state);
	}

	public synchronized _ProcessState getState() {
		return this.pcb.getState();
	}

	public int getId() {
		return this.pcb.getID();
	}

	private Date getArrivalTime() {
		return this.pcb.getArrivalTime();
	}

	@Override
	public int compareTo(ProcessMY p) {
		return this.getArrivalTime().compareTo(p.getArrivalTime());
	}

	public void releaseResources() {
		this.pcb.getPartition().setFree();
	}

	public _ProcessControlBlock getPcb() {
		return this.pcb;
	}

	@Override
	public String toString() {
		return this.pcb.toString();
	}

	public void saveState(int programCounter, int stackPointer, int outPointer) {
		this.pcb.setProgramCounter(programCounter);
		this.pcb.setStackPointer(stackPointer);
		this.pcb.setOutPointer(outPointer);
	}

}
