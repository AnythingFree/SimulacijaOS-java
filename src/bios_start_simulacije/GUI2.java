package bios_start_simulacije;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import shell.Shell;

public class GUI2 extends Application {
    private static Shell shell;
    public static Label curDirLabel = new Label("/");
    private static Label[] outputLabels = new Label[10];

    public static void main(String[] args) {
        GUI2.shell = Bootloader.boot();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("OS Simulator");
        primaryStage.setOnCloseRequest(event -> System.exit(0));

        // Input Area
        TextField inputField = new TextField();
        Button submitButton = new Button("Submit");
        inputField.setOnAction(event -> submitButton.fire());

        Button shutDownButton = new Button("shutDown");
        Button clsButton = new Button("Cls");
        HBox inputBox = new HBox(10);
        inputBox.getChildren().addAll(curDirLabel, inputField, submitButton, clsButton, shutDownButton);

        // Text Area
        TextArea textArea = new TextArea();
        textArea.setEditable(false); // Make the text area non-editable
        textArea.setWrapText(true);
        textArea.setText("Welcome to the OS Simulator!\n");

        // Output Boxes
        HBox outputBox = new HBox(10);
        outputBox.getChildren().add(new Label("RAM: "));
        for (int i = 0; i < 10; i++) {
            outputLabels[i] = new Label("|A " + (i + 1) + ": ");
            outputBox.getChildren().add(outputLabels[i]);
        }
        // ========================================================================================
        // Handle the submit button click event
        submitButton.setOnAction(event -> {
            String input = inputField.getText().toLowerCase();
            inputField.clear();
            textArea.appendText(curDirLabel.getText() + ">>>" + input + "\n");

            if (Bootloader.checkIfPermited(input)) {
                textArea.appendText(shell.processCommand(input));
                textArea.appendText(shell.getOutput() + "\n");
            } else {
                textArea.appendText("Command not recognized.\n");
            }

        });
        // ==========================================================================================

        // Handle the cls button click event
        clsButton.setOnAction(event -> textArea.clear());

        // Handle the shutDown button click event
        shutDownButton.setOnAction(event -> System.exit(0));
        // ==========================================================================================
        // Create the layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setTop(inputBox);
        root.setCenter(textArea);
        root.setBottom(outputBox);

        Scene scene = new Scene(root, 800, 400);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void setRAMOUT(ArrayList<String> ramout) {
        Platform.runLater(() -> {
            for (int i = 0; i < 10; i++) {
                outputLabels[i].setText(ramout.get(i));
            }
        });

    }
}
