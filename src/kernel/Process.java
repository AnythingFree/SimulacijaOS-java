package kernel;

import java.util.Date;

import hardware_modules.CPU;
import hardware_modules.RAM;

import java.util.ArrayList;

public class Process implements Comparable<Process> {
	private _ProcessControlBlock pcb;

	public Process(int id, int startAdress, int endAdress) {
		this.pcb = new _ProcessControlBlock(id, startAdress, endAdress, new Date(), _ProcessState.READY);
	}

	public void start() {
		if (this.pcb.getState() == _ProcessState.READY) {
			setState(_ProcessState.RUNNING);

			ArrayList<String> binary = RAM.readFromRam(this.pcb.getStartAdress(), this.pcb.getEndAdress());
			CPU.setRegisters(pcb);
			CPU.executeCode(binary);

			terminate();
		}
	}

	public void block() {
		if (this.pcb.getState() == _ProcessState.RUNNING) {
			this.setState(_ProcessState.BLOCKED);
			// save cpu registers to pcb
			// TO DO
			if (ProcessScheduler.processQueue.contains(this))
				ProcessScheduler.processQueue.remove(this);
		}
	}

	public void unblock() {
		if (this.pcb.getState() == _ProcessState.BLOCKED) {
			this.setState(_ProcessState.READY);
			ProcessScheduler.processQueue.add(this);
		}

	}

	public void terminate() {
		this.setState(_ProcessState.TERMINATED);
		if (ProcessScheduler.processQueue.contains(this)) {
			ProcessScheduler.processQueue.remove(this);
		}

	}

	private void setState(_ProcessState state) {
		this.pcb.setState(state);
	}

	public int getId() {
		return this.pcb.getID();
	}

	private Date getArrivalTime() {
		return this.pcb.getArrivalTime();
	}

	@Override
	public int compareTo(Process p) {
		return this.pcb.getArrivalTime().compareTo(p.pcb.getArrivalTime());
	}

	public _ProcessState getState() {
		return this.pcb.getState();
	}

}
