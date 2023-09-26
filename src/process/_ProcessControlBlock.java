package process;

import java.util.Date;

public class _ProcessControlBlock {
	private int id;
	private int startAdress;
	private int endAdress;
	private Date arrivalTime;
	private _ProcessState state;

	// CPU registers are saved here
	// TO DO

	_ProcessControlBlock(int id, int startAdress, int endAdress, Date date, _ProcessState ready) {
		this.id = id;
		this.startAdress = startAdress;
		this.endAdress = endAdress;
		this.arrivalTime = date;
		this.state = ready;
	}

	_ProcessState getProcessState() {
		return state;
	}

	void setState(_ProcessState state) {
		this.state = state;
	}

	_ProcessState getState() {
		return null;
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

	int getEndAdress() {
		return endAdress;
	}
}
