package shell;

import java.io.File;
import java.io.IOException;

import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PrintStream;

import file_system.FileSystem;
import hardware_modules.RAM;
import kernel.Kernel;
import process.ProcessScheduler;

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
				if (s.length == 2 && kernel.changeDirectory(s[1]))
					out = "Izvrsena naredba: prelazi u ciljni direktorijum (naziv ili path)\n";
				else
					out = "Nepostojeci direktorijum\n";
				break;
			// ========================================================================
			case "rm":
				if (s.length == 2 && kernel.deleteFileORDir(s[1]))
					out = "Izvrsena naredba:  uklanja datoteku ili direktorijum (naziv ili path).\n";
				else
					out = "Ime nije valjano. Datoteka ili direktorijum nije uklonjen.\n";
				break;
			// =============== OVO JE MOJE =================================
			case "run":
				out = "Izvrsena naredba: pokreće određeni proces ili aplikaciju.\n";
				// Pokretanje procesa
				ProcessScheduler.runProcesses();
				break;
			case "ps":
				out = "Izvrsena naredba: ispisuje popis aktivnih procesa na računaru.\n";
				// ProcessScheduler.printProcesses();
				break;
			case "mem":
				out = "Izvrsena naredba: prikazuje informacije o zauzeću RAM memorije na računaru.\n";
				// ispis zauzeća RAM memorije
				RAM.printMemory();
				break;
			// ========================================================================
			case "exit":
				kernel.shutDown();
				out = "Izvrsena naredba: zatvara CLI, zaustavljajući izvršavanje programa.\n";
				// izlazak iz CLI
				System.exit(1);
				break;

			case "help":
				String help = "";
				help += "Lista naredbi:\n" +
						"CD" + "\t\t\t\t" + "Naredba: prelazi u ciljni direktorijum.\n";
				help += "LS" + "\t\t\t" + "Naredba: ispisuje sadržaj trenutnog direktorijuma.\n";
				help += "MKDIR" + "\t\t\t" + "Naredba: pravi novi direktorijum u trenutnom radnom direktorijumu.\n";
				help += "MEM" + "\t\t\t" + "Naredba: prikazuje informacije o zauzeću RAM memorije na računaru.\n";
				help += "RUN" + "\t\t\t" + "Naredba: pokreće određeni proces ili aplikaciju.\n";
				help += "EXIT" + "\t\t\t\t" + "Naredba: zatvara CLI, zaustavljajući izvršavanje programa.\n";
				help += "PS" + "\t\t\t\t" + "Naredba: ispisuje popis aktivnih procesa na računaru.\n";
				help += "RM" + "\t\t\t\t" + "Naredba:  uklanja datoteku ili direktorijum.\n";

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

	public static void setOut(OutputStream output) {
		System.setOut(new PrintStream(output, true));
	}

	public String getOutput() {
		String ret = Shell.output;
		Shell.output = "";
		return ret;
	}

	public String getCurrentDir() {
		return kernel.getCurrentDir();
	}

}
