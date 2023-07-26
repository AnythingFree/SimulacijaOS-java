package kernel;

import java.time.Instant;

public class Process implements Comparable<Process> {
	private Instant arrivalTime;
	
	
	
	//msm da treba process da extend Thread
	
	
	public Instant getArrivalTime() {
		return arrivalTime;
	}
	
	
	//If the current object came before the object being compared (other), compareTo returns a negative integer.
	//If the current object came after the object being compared (other), compareTo returns a positive integer.
	@Override
	public int compareTo(Process p) {
		return this.arrivalTime.compareTo(p.getArrivalTime());
	}
}
