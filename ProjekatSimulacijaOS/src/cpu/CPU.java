package cpu;

import java.util.ArrayList;

import interfaces.CPU_i;
import simulacije_jer_ih_jos_nemamo.Simulacija_RAMmemorije;

public class CPU implements CPU_i{
	
	// hard codovali smo stek memoriju ali moze i dinamicki da se mijenja
	int stack_pointer = 0;
	int end_of_stack = 9;
	
	int instruction_pointer = 0;
	
	boolean zero, carry;
	
	
	private void push(int a) {
		Simulacija_RAMmemorije.niz[stack_pointer++] = a;
	}

	private int pop() {
		int a = Simulacija_RAMmemorije.niz[--stack_pointer];
		return a;
	}

	@Override
	public void execute_code(ArrayList<Integer> kodovi_binarni) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void execute_instruction(int naredba, int a, int b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hlt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void add(int a, int b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sub(int a, int b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mul(int a, int b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void div(int a, int b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dec(int a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inc(int a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increment_instr_point() {
		// cita trenutnu naredbu, zna koliko mjesta zauzima zajendo sa argumentima, povecava za toliko mijesta
		// u ovom slucaju, samo pop nema argumente svi ostali imaju 1 argument
	}


}
