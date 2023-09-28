package process;

import java.util.Date;

import hardware_modules.CPU;
import memory_management.Partition;

public class _ProcessControlBlock {
	private int id;
	private int startAdress;
	private Date arrivalTime;
	private volatile _ProcessState state;
	private Partition partition;

	private int stackpointer;
	private int programCounter;
	private int outPointer;

	_ProcessControlBlock(int id, Partition partition, Date date, _ProcessState ready) {
		this.id = id;
		this.startAdress = partition.getStartAddress();

		this.arrivalTime = date;
		this.state = ready;
		this.partition = partition;

		this.outPointer = partition.getOutputAdress();
		this.stackpointer = partition.getStartOfStack();
		this.programCounter = partition.getStartOfCode();

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
		return startAdress;
	}

	public int getProgramCounter() {
		return this.programCounter;
	}

	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}

	public int getStackPointer() {
		return stackpointer;
	}

	public void setStackPointer(int p) {
		stackpointer = p;
	}

	public int getOutPointer() {
		return this.outPointer;
	}

	public void setOutPointer(int outPointer2) {
		this.outPointer = outPointer2;
	}

	public void saveRegisters(CPU cpu) {
		this.programCounter = cpu.getProgramCounter();
		this.stackpointer = cpu.getStackPointer();
		this.outPointer = cpu.getOutPointer();
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
		return "_ProcessControlBlock [id=" + id + ", startAdress=" + startAdress + ", arrivalTime=" + arrivalTime
				+ ", state=" + state + ", partition=" + partition + ", stackpointer=" + stackpointer
				+ ", programCounter=" + programCounter + ", outPointer=" + outPointer + "]";
	}

}
