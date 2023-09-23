package shell;
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
	private static TextArea top=new TextArea();
	private static TextField bottom=new TextField();
	private PipedInputStream input=new PipedInputStream();
	private PipedOutputStream output=new PipedOutputStream();
	private StringBuilder outSB=new StringBuilder();
	private OutputStream outputStream;
	private int length1=0;
	
	public static void clear() {
		top.setText("");
		bottom.clear();
	}
	private void addTextToTop() {
		if(outSB.length()>0) {
			top.appendText(outSB.toString());
			outSB=new StringBuilder();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		input.connect(output);
		textToShow="";
		
		
		top=new TextArea();
		top.setPrefSize(900,400);
		top.setEditable(false);
		top.setText("Dobrodošli u CLI! Unesite naredbu ili 'exit' za izlaz.\nUnesite 'help' ukoliko zelite listu naredbi.\n");
		
		bottom=new TextField();
		bottom.setPrefSize(900,70);
		
		
		bottom.setOnAction(e1 -> {
			try {
				
				byte array[]=bottom.getText().getBytes();
				output.write(array);
				length1=array.length;
				
				Shell.readCommand(input, length1);
				//System.out.println(bottom.getText());
				String s=Shell.processCommand(bottom.getText());
				
				
				textToShow=textToShow + ">" +bottom.getText()+"\n"+">"+s+"\n";
				top.appendText(textToShow);
				//Shell.getCommand();
				
				bottom.clear();
				textToShow="";
				
				
			}catch(IOException e2) {
				e2.printStackTrace();
			}
		});
		
		outputStream=new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub
				outSB.append((char)b);
				if(((char) b) == '\n')
					addTextToTop();
				
			}
			
		};
		
		Shell.setOut(outputStream);
		
		VBox root =new VBox(15);
		root.setPadding(new Insets(10,30,30,30));
		root.getChildren().addAll(top,bottom);
		VBox.setVgrow(top, Priority.ALWAYS);
		Scene scena=new Scene(root,1200,650);
		
		scena.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		
		primaryStage.setScene(scena);
		primaryStage.show();
		bottom.requestFocus();
	}
	public static void main(String []args) {
		Shell.booting();
		launch(args);
	}

}