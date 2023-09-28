package bios_start_simulacije;

import java.io.IOException;
import shell.Shell;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class GUI extends Application {
	private static String textToShow;
	private static TextArea top = new TextArea();
	private static TextField bottom = new TextField();
	private PipedInputStream input = new PipedInputStream();
	private PipedOutputStream output = new PipedOutputStream();
	private StringBuilder outSB = new StringBuilder();
	private OutputStream outputStream;
	private int length1 = 0;

	private static TextArea outputArea;

	private static Shell shell;

	public static void clear() {
		top.setText("");
		bottom.clear();
	}

	private void addTextToTop() {
		if (outSB.length() > 0) {
			top.appendText(outSB.toString());
			outSB = new StringBuilder();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("CLI");


		input.connect(output);
		textToShow = "";

		// 	Input area
		TextField inputField = new TextField();
        inputField.setPrefWidth(600);

		// Output area
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefWidth(600);
        outputArea.setPrefHeight(300);

		// Submit button
        Button submitButton = new Button("shutDown");
        submitButton.getStyleClass().add("submit-button");
        submitButton.setOnAction(e -> {
			try {

				/*byte array[] = inputField.getText().getBytes();
				output.write(array);
				length1 = array.length;

				readCommand(input, length1);
				System.out.println(inputField.getText());
				String s = shell.processCommand(inputField.getText());

				//textToShow = textToShow + ">>>" + inputField.getText() + "\n" + ">>>" + s + "\n";
				outputArea.appendText(textToShow + ">>>" + inputField.getText() + "\n" + ">>>" + s + "\n");
		

				inputField.clear();
				textToShow = "";*/


				byte array[] = inputField.getText().getBytes();
				output.write(array);
				length1 = array.length;
		
				// Ako je ovo važno za vaš kod, sada možete čitati komandu iz inputField-a
				String komanda = inputField.getText();
				
				// Procesuirajte komandu koristeći shell objekt
				String s = shell.processCommand(komanda);
		
				outputArea.appendText(">>>" + inputField.getText() + "\n" + ">>>" + s + "\n");
		
				System.out.println("Izvrseno");
				inputField.clear();

			} catch (IOException e2) {
				e2.printStackTrace();
			}
		});

        // Clear button
        Button clearButton = new Button("clearScreen");
        clearButton.getStyleClass().add("clear-button");
        clearButton.setOnAction(e -> {
			outputArea.clear();
		});

		// Exit button
		Button exitButton = new Button("exit");
		exitButton.getStyleClass().add("exit-button");
		exitButton.setOnAction(e -> {
			System.exit(0);
		});

		// HBox for buttons
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.getChildren().addAll(submitButton, clearButton, exitButton);

		// VBox for input area, output area and buttons
		VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.setStyle("-fx-background-color: #ADD8E6;"); // Set background color
        vbox.getChildren().addAll(inputField, outputArea, buttonBox);

        Scene scene = new Scene(vbox, 620, 400);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm()); // Load CSS

        primaryStage.setScene(scene);
        primaryStage.show();

        submitButton.setDefaultButton(true);
		inputField.requestFocus();



		
		/*bottom = new TextField();
		bottom.setPrefSize(900, 70);

		bottom.setOnAction(e1 -> {
			try {

				byte array[] = bottom.getText().getBytes();
				output.write(array);
				length1 = array.length;

				readCommand(input, length1);
				System.out.println(bottom.getText());
				String s = shell.processCommand(bottom.getText());

				textToShow = textToShow + ">" + bottom.getText() + "\n" + ">" + s + "\n";
				top.appendText(textToShow);
				

				bottom.clear();
				textToShow = "";

			} catch (IOException e2) {
				e2.printStackTrace();
			}
		});*/

		outputStream = new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub
				outSB.append((char) b);
				if (((char) b) == '\n')
					addTextToTop();

			}

		};

		shell.setOut(outputStream);

		/*VBox root = new VBox(15);
		root.setPadding(new Insets(10, 30, 30, 30));
		root.getChildren().addAll(top, bottom);
		VBox.setVgrow(top, Priority.ALWAYS);
		Scene scena = new Scene(root, 1200, 650);*/

		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		/*primaryStage.setScene(scene);
		primaryStage.show();
		bottom.requestFocus();*/
	}

	// funkcija za citanje komande
	public static void readCommand(PipedInputStream input, int length) {
		String c = "";
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

	public static void main(String[] args) {
		GUI.shell = Bootloader.boot();
		System.out.println("Shell initialized.");
		launch(args);
	}

}