package bios_start_simulacije;

import kernel.Kernel;
import shell.Shell;

public class Bootloader {

	public static void main(String[] args) {
		Kernel k = new Kernel();
		//k.start();
		
		Shell shell = new Shell(k);
		// nekako treba dati useru da unosi komande a shell da onda koristi kernel za dalje?
		
		// GUI start;
	}

}
