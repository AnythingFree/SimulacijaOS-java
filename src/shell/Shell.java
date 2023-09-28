package shell;

import java.util.ArrayList;

import bios_start_simulacije.GUI2;
import kernel.Kernel;

public class Shell {
	private Kernel kernel;
	public static String output = "";

	public Shell(Kernel k) {
		this.kernel = k;
	}

	public String processCommand(String komanda) {
		String c = komanda;
		String[] s = c.split(" ");
		String command = s[0];
		String out = "";
		switch (command) {
			case "ls":
				if (kernel.listFiles())
					out = "Izvrsena naredba: ispisuje sadržaj trenutnog direktorijuma.\n";
				else
					out = "Desila se greska.\n";
				break;
			case "mkdir":
				if (s.length == 2 && kernel.makeDirectory(s[1]))
					out = "Izvrsena naredba: pravi novi direktorijum u trenutnom radnom direktorijumu.\n";
				else
					out = "Ime nije valjano. Direktorijum nije kreiran.\n";
				break;
			case "cd":
				if (s.length == 2 && kernel.changeDirectory(s[1])) {
					out = "Izvrsena naredba: prelazi u ciljni direktorijum (naziv ili path)\n";
					GUI2.curDirLabel.setText(kernel.getCurrentDir());
				} else
					out = "Nepostojeci direktorijum\n";
				break;
			// ========================================================================
			case "rm":
				if (s.length == 2 && kernel.deleteFileORDir(s[1]))
					out = "Izvrsena naredba:  uklanja datoteku ili direktorijum iz trenutnog dir.\n";
				else
					out = "Ime nije valjano. Datoteka ili direktorijum nije uklonjen.\n";
				break;
			// =============== OVO JE MOJE =================================
			case "run":
				if (s.length == 2 && kernel.runProcesses(s[1]))
					out = "Izvrsena naredba: pokreće određeni proces ili aplikaciju.\n";
				else
					out = "Ime ili broj parametara nije valjano.\n";
				break;
			case "ps":
				out = "Izvrsena naredba: ispisuje popis aktivnih procesa na računaru.\n";
				kernel.printProcesses();
				break;
			case "mem":
				out = "Izvrsena naredba: prikazuje informacije o zauzeću RAM memorije na računaru.\n";
				kernel.printRAM();
				break;
			case "defrag":
				out = "Izvrsena naredba: defragmentacija rama.\n";
				kernel.defragment();
				break;
			case "block":
				if (s.length == 2 && kernel.blockProcess(s[1]))
					out = "Izvrsena naredba:  blokira proces.\n";
				else
					out = "Ime ili broj param nije valjano.\n";
				break;
			case "unblock":
				if (s.length == 2 && kernel.unblockProcess(s[1]))
					out = "Izvrsena naredba:  odblokira proces.\n";
				else
					out = "Ime ili broj param nije valjano.\n";
				break;
			// ========================================================================
			case "exit":
				// kernel.shutDown();
				out = "Izvrsena naredba: zatvara CLI, zaustavljajući izvršavanje programa.\n";
				// izlazak iz CLI
				System.exit(1);
				break;

			case "help":
				String help = "";
				help += "Lista naredbi:\n" +
						"LS" + "\t\t\t\t" + "Naredba: ispisuje sadržaj trenutnog direktorijuma.\n"
						+ "MKDIR" + "\t\t\t" + "Naredba: pravi novi direktorijum u trenutnom radnom direktorijumu.\n"
						+ "CD" + "\t\t\t\t" + "Naredba: prelazi u ciljni direktorijum.\n"
						+ "RM" + "\t\t\t\t" + "Naredba:  uklanja datoteku ili direktorijum.\n"
						+ "RUN" + "\t\t\t\t" + "Naredba: pokreće određeni proces ili aplikaciju.\n"
						+ "PS" + "\t\t\t\t" + "Naredba: ispisuje popis aktivnih procesa na računaru.\n"
						+ "MEM" + "\t\t\t" + "Naredba: prikazuje informacije o zauzeću RAM memorije na računaru.\n"
						+ "EXIT" + "\t\t\t\t" + "Naredba: zatvara CLI, zaustavljajući izvršavanje programa.\n";

				// System.out.println(help);
				out = help;
				break;
			default:
				// System.out.println("Nepoznata naredba: " + command);
				out = "Nepoznata naredba: " + command;
				break;
		}
		return out;
	}

	public String getOutput() {
		String ret = Shell.output;
		Shell.output = "";
		return ret;
	}

	public String getCurrentDir() {
		return kernel.getCurrentDir();
	}

	public static void setGUIRAM(ArrayList<String> ramout) {
		GUI2.setRAMOUT(ramout);
	}

}
