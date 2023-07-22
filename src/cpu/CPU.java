package cpu;

import simulacije_jer_ih_jos_nemamo.Simulacija_RAMmemorije;

public class CPU{
	
	// hard codovali smo stek memoriju ali moze i dinamicki da se mijenja
	int stack_pointer = 0;
	int end_of_stack = 9;
	
	int instruction_pointer = 0;
	
	
	
	
	private void push(int a) {
		Simulacija_RAMmemorije.niz[stack_pointer++] = a;
	}

	private int pop() {
		int a = Simulacija_RAMmemorije.niz[--stack_pointer];
		return a;
	}


}
