package shell;

import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class GUI extends Application {
	private static String textToShow;
	private static TextArea top = new TextArea();
	private static TextField bottom = new TextField();
	private static Button light;
	private static Button dark;
	private PipedInputStream input = new PipedInputStream();
	private PipedOutputStream output = new PipedOutputStream();
	private StringBuilder outSB = new StringBuilder();
	private OutputStream outputStream;
	private int length1 = 0;

	@Override
	public void start(Stage primaryStage) throws Exception {
	}

	public static void main(String[] args) {
		System.out.println("Dobrodošli u CLI! Unesite naredbu ili 'exit' za izlaz.");

		Scanner scanner = new Scanner(System.in);
		String command;

		do {
			System.out.print("> ");
			command = scanner.nextLine();
			processCommand(command);
		} while (!command.equalsIgnoreCase("exit"));

		scanner.close();
	}

	public static void processCommand(String command) {
		switch (command) {
		case "cd":
			System.out.println("Izvršena naredba: prelazi u ciljni direktorijum");
			// Implementirajte logiku za prelazak u ciljni direktorijum
			break;
		case "dir":
		case "ls":
			System.out.println("Izvršena naredba: lista trenutni direktorijum");
			// Implementirajte logiku za ispis trenutnog direktorijuma
			break;
		case "ps":
			System.out.println("Izvršena naredba: lista procese");
			// Implementirajte logiku za ispis procesa i osnovnih informacija o njima
			break;
		case "mkdir":
			System.out.println("Izvršena naredba: pravi direktorijum");
			// Implementirajte logiku za stvaranje direktorija
			break;
		case "run":
			System.out.println("Izvršena naredba: pokreće proces");
			// Implementirajte logiku za pokretanje procesa
			break;
		case "mem":
			System.out.println("Izvršena naredba: zauzeće RAM memorije");
			// Implementirajte logiku za ispis zauzeća RAM memorije
			break;
		case "exit":
			System.out.println("Izvršena naredba: gasi OS");
			// Implementirajte logiku za izlazak iz CLI
			break;
		default:
			System.out.println("Nepoznata naredba: " + command);
			break;
		}
	}

}
