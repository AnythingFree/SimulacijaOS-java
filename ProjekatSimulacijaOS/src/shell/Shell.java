package shell;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PrintStream;

import file_system.FileSystem;
import kernel.Kernel;
import kernel.ProcessScheduler;
import memory_management.RAM;
public class Shell {
	Kernel kernel;
	private static String c;

	public Shell(Kernel k) {
		this.kernel = k;
	}
	
	public static void getCommand() {
	String [] s=c.split("");
	String com=s[0];
	
	}
	public static void processCommand() {
		String [] s=c.split("");
		String command=s[0];
        switch (command) {
            case "cd":
                System.out.println("Izvršena naredba: prelazi u ciljni direktorijum");
                // prelazak u ciljni direktorijum
                FileSystem.changeDirectory();
                break;
            case "dir":
            	System.out.println("Izvršena naredba: ispisuje sadržaj trenutnog direktorijuma");
                // prelazak u ciljni direktorijum
            	FileSystem.listFiles();
            	break;
            case "ls":
                System.out.println("Izvršena naredba: ispisuje sadržaj trenutnog direktorija");
                // ispis trenutnog direktorijuma
                FileSystem.listFiles();
                break;
            case "ps":
                System.out.println("Izvršena naredba: ispisuje popis aktivnih procesa na računaru");
                // ispis procesa i osnovnih informacija o njima
                ProcessScheduler.printProcesses();
                break;
            case "mkdir":
                System.out.println("Izvršena naredba: pravi novi direktorijum u trenutnom radnom direktorijumu");
                // stvaranje direktorija

                if (s.length == 2) {
    				String name_of_new_directory = s[1];
    				FileSystem.makeDirectory(name_of_new_directory);
    			}
    			else
    				System.out.println("Unesite korektne parametre!");		
                break;
            case "run":
                System.out.println("Izvršena naredba: pokreće određeni proces ili aplikaciju");
                // Pokretanje procesa
                ProcessScheduler.runProcess();
                break;
            case "mem":
                System.out.println("Izvršena naredba: prikazuje informacije o zauzeću RAM memorije na računaru");
                // ispis zauzeća RAM memorije
                RAM.printMemory();
                break;
            case "exit":
                System.out.println("Izvršena naredba: zatvara CLI, zaustavljajući izvršavanje programa.");
                // izlazak iz CLI
                System.exit(1);
                break;
            case "rm":
            	System.out.println("Izvršena naredba:  uklanja datoteku ili direktorijum");
            	//uklanjanje datoteke ili direktorijuma
            	if (s.length == 2) {
    				String name_of_file = s[1];
    				FileSystem.deleteFile(name_of_file);
    			}
    			else
    				System.out.println("Unesite korektne parametre!");
            	break;
            case "help":
        			if (s.length == 1) {
        				String help = "";
        				help += "CD" + "\t\t\t\t" + "Izvršena naredba: prelazi u ciljni direktorijum.\n";
        				help += "DIR (LS)" + "\t\t\t\t" + "Izvršena naredba: ispisuje sadržaj trenutnog direktorijuma.\n";
        				help += "MKDIR" + "\t\t\t" + "Izvršena naredba: pravi novi direktorijum u trenutnom radnom direktorijumu.\n";
        				help += "MEM" + "\t\t\t" + "Izvršena naredba: prikazuje informacije o zauzeću RAM memorije na računaru.\n";
        				help += "RUN" + "\t\t\t" + "Izvršena naredba: pokreće određeni proces ili aplikaciju.\n";
        				help += "EXIT" + "\t\t\t\t" + "Izvršena naredba: zatvara CLI, zaustavljajući izvršavanje programa.\n";
        				help += "PS" + "\t\t\t\t" + "Izvršena naredba: ispisuje popis aktivnih procesa na računaru.\n";
        				help += "RM" + "\t\t\t\t" + "Izvršena naredba:  uklanja datoteku ili direktorijum.\n";
        				
        				System.out.println(help);
        			}
        			else
        				System.out.println("Unesite korektne parametre!");		
        		
            default:
                System.out.println("Nepoznata naredba: " + command);
                break;
        }
    }
	//funkcija za citanje komande
	public static void readCommand(PipedInputStream input, int length) {
		c = "";
		char ch;
		for (int i = 0; i < length; i++) {
			try {
				ch = (char) input.read();
				c += ch;
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Grečka prilikom čitanja komande!");
			}
		}
	}

	public static void setOut(OutputStream output) {
		System.setOut(new PrintStream(output, true));
	}

}
