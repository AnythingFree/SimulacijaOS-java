package process;

import java.util.Date;

import hardware_modules.CPU;
import memory_management.Partition;

public class _ProcessControlBlock {
	private int id;

	private Date arrivalTime;
	private volatile _ProcessState state;
	private Partition partition;

	private int stackpointer_relative;
	private int programCounter_relative;
	private int outPointer_relative;

	_ProcessControlBlock(int id, Partition partition, Date date, _ProcessState ready) {
		this.id = id;

		this.arrivalTime = date;
		this.state = ready;
		this.partition = partition;

		this.outPointer_relative = 0;
		this.stackpointer_relative = 0;
		this.programCounter_relative = 0;

	}

	synchronized void setState(_ProcessState state) {
		this.state = state;
	}

	public _ProcessState getState() {
		return this.state;
	}

	int getID() {
		return id;
	}

	Date getArrivalTime() {
		return arrivalTime;
	}

	int getStartAdress() {
		return this.partition.getStartAddress();
	}

	public int getProgramCounter() {
		return this.partition.getStartOfCode() + this.programCounter_relative;
	}

	public void setProgramCounter(int programCounter) {
		this.programCounter_relative = programCounter - this.partition.getStartOfCode();
	}

	public int getStackPointer() {
		return this.partition.getStartOfStack() + this.stackpointer_relative;
	}

	public void setStackPointer(int stackP) {
		this.stackpointer_relative = stackP - this.partition.getStartOfStack();
	}

	public int getOutPointer() {
		return this.partition.getOutputAdress() + this.outPointer_relative;
	}

	public void setOutPointer(int outPointer) {
		this.outPointer_relative = outPointer - this.partition.getOutputAdress();
	}

	// ========== partition values ============

	public int getStartOfCode() {
		return partition.getStartOfCode();
	}

	public int getEndOfCode() {
		return partition.getEndOfCode();
	}

	public int getStartOfStack() {
		return partition.getStartOfStack();
	}

	public int getEndOfStack() {
		return partition.getEndOfStack();
	}

	public int getOutputAdress() {
		return partition.getOutputAdress();
	}

	public Partition getPartition() {
		return partition;
	}

	@Override
	public String toString() {
		return "_ProcessControlBlock [id=" + id + ", startAdress=" + getStartAdress() + ", \narrivalTime=" + arrivalTime
				+ ", state=" + state + ", \npartition=" + partition + ", stackpointer=" + getStackPointer()
				+ ", \nprogramCounter=" + getProgramCounter() + ", outPointer=" + getOutPointer() + "]\n";
	}

}
