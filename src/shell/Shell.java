package shell;

import java.io.File;
import java.io.IOException;

import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PrintStream;

import file_system.FileSystem;
import hardware_modules.RAM;
import kernel.Kernel;
import kernel.ProcessScheduler;
public class Shell {
	Kernel kernel;
	private static String c;
	public static FileSystem tree;
	public static RAM memory;

	public Shell(Kernel k) {
		this.kernel = k;
	}
	
	 
	public static String processCommand(String komanda) {
		c=komanda;
		String [] s=c.split(" ");
		String command=s[0];
		String out="";
        switch (command) {
            case "cd":
            	//out="da";
            	out="Izvrsena naredba: prelazi u ciljni direktorijum";
                // prelazak u ciljni direktorijum
                //FileSystem.changeDirectory();
                break;
            case "dir":
            	 //System.out.println("Izvrsena naredba: ispisuje sadržaj trenutnog direktorijuma");
                out="Izvrsena naredba: ispisuje sadržaj trenutnog direktorijuma";
            	 // ispis trenutnog direktorijuma
            	FileSystem.listFiles();
            	break;
            case "ls":
                //System.out.println("Izvrsena naredba: ispisuje sadržaj trenutnog direktorijuma");
                // ispis trenutnog direktorijuma
            	out="Izvrsena naredba: ispisuje sadržaj trenutnog direktorijuma.\n";
                FileSystem.listFiles();
                break;
            case "ps":
                //System.out.println("Izvrsena naredba: ispisuje popis aktivnih procesa na računaru");
                out="Izvrsena naredba: ispisuje popis aktivnih procesa na računaru.\n";
            	// ispis procesa i osnovnih informacija o njima
                ProcessScheduler.printProcesses();
                break;
            case "mkdir":
                //System.out.println("Izvrsena naredba: pravi novi direktorijum u trenutnom radnom direktorijumu");
                out="Izvrsena naredba: pravi novi direktorijum u trenutnom radnom direktorijumu.\n";
                // stvaranje direktorija

                if (s.length == 2) {
    				String name_of_new_directory = s[1];
    				FileSystem.makeDirectory(name_of_new_directory);
    				System.out.println("Napravljen direktorijum: "+name_of_new_directory);
    			}
    			else
    				//System.out.println("Unesite korektne parametre!");
    				out="Unesite korektne parametre!\n";
                break;
            case "run":
                //System.out.println("Izvrsena naredba: pokreće određeni proces ili aplikaciju");
                out="Izvrsena naredba: pokreće određeni proces ili aplikaciju.\n";
                // Pokretanje procesa
                ProcessScheduler.runProcesses();
                break;
            case "mem":
                //System.out.println("Izvrsena naredba: prikazuje informacije o zauzeću RAM memorije na računaru");
                out="Izvrsena naredba: prikazuje informacije o zauzeću RAM memorije na računaru.\n";
            	// ispis zauzeća RAM memorije
                RAM.printMemory();
                break;
            case "exit":
                //System.out.println("Izvrsena naredba: zatvara CLI, zaustavljajući izvršavanje programa.");
                out="Izvrsena naredba: zatvara CLI, zaustavljajući izvršavanje programa.\n";
            	// izlazak iz CLI
                System.exit(1);
                break;
            case "rm":
            	//System.out.println("Izvrsena naredba:  uklanja datoteku ili direktorijum");
            	out="Izvrsena naredba:  uklanja datoteku ili direktorijum.\n";
            	//uklanjanje datoteke ili direktorijuma
            	if (s.length == 2) {
    				String name_of_file = s[1];
    				FileSystem.deleteFile(name_of_file);
    				System.out.println("Izbrisana datoteka ili direktorijum: "+name_of_file);
    			}
    			else
    				//System.out.println("Unesite korektne parametre!");
    				out="Unesite korekte parametre!\n";
            	break;
            case "help":
        				String help = "";
        				help +="Lista naredbi:\n"+ "CD" + "\t\t\t\t" + "Naredba: prelazi u ciljni direktorijum.\n";
        				help += "DIR (LS)" + "\t\t\t" + "Naredba: ispisuje sadržaj trenutnog direktorijuma.\n";
        				help += "MKDIR" + "\t\t\t" + "Naredba: pravi novi direktorijum u trenutnom radnom direktorijumu.\n";
        				help += "MEM" + "\t\t\t" + "Naredba: prikazuje informacije o zauzeću RAM memorije na računaru.\n";
        				help += "RUN" + "\t\t\t" + "Naredba: pokreće određeni proces ili aplikaciju.\n";
        				help += "EXIT" + "\t\t\t\t" + "Naredba: zatvara CLI, zaustavljajući izvršavanje programa.\n";
        				help += "PS" + "\t\t\t\t" + "Naredba: ispisuje popis aktivnih procesa na računaru.\n";
        				help += "RM" + "\t\t\t\t" + "Naredba:  uklanja datoteku ili direktorijum.\n";
        				
        				//System.out.println(help);
        				out=help;
        			break;	
            default:
                //System.out.println("Nepoznata naredba: " + command);
            	out="Nepoznata naredba: " + command;
                break;
        }
        return out;
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
	
	public static void booting() {
		new ProcessScheduler();
		memory = new RAM();
		tree = new FileSystem(new File("PROGRAMS"));
	}

}
