package interfaces;

import java.util.ArrayList;

// izvrsava masinski kod
public interface CPU_i {
	//=========varijable================================
	// mapa sta radi koja naredba ili direktno pisati u switch case (ovo mijenja stanje ram memorije)
	//
	// pokazivac na stek (memorijsku adresu gdje pocinje stek)
	// int instruction_pointer sadrzi adresu naredne instrukcije
	//
	// varijable flagovi zero, carry
	//=============================================================================== 
	// treba da ucita masinski kod u RAM memoriju i obavlja sta treba i ispisuje u RAM memoriju
	//
	public void execute_code(ArrayList<Integer> kodovi_binarni);
	// 
	//
	//==================NAREDBE===one pricaju sa RAMom==================================================
	//
	abstract void execute_instruction(int naredba, int a, int b);
	// switch (naredba)
	// 		case add...
	//		case sub...
	
	// na neki nacin ce da utice na memoriju
	abstract void hlt(); // zavrsava program
	abstract void add(int a, int b);
	abstract void sub(int a, int b);
	abstract void mul(int a, int b);
	abstract void div(int a, int b);
	abstract void dec(int a);
	abstract void inc(int a);
	//=================================================================
	abstract void increment_instr_point();	// treba da zna koliko koja naredba zauzima memorije tako da samo poveca za 2 adrese ili jednu adresu
}
