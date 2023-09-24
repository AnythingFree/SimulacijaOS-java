package file_system;

import java.io.File;

import javafx.scene.control.TreeItem;

public class FileSystemTree {

    private File root;
    private File currentDirectory; // Trenutni radni direktorijum

    public FileSystemTree(File root) {
        // createTree(root);
        this.root = root;
        this.currentDirectory = root;

    }

    // =======================FIles=================================================
    public void listFiles() {
        // kreirati logiku za prelazak u ciljni direktorijum
        // potrebna funkcija zbog cd naredbe u klasi Shell
        // ako zelis preimenuj funkciju, samo mi napisi da i ja tamo izmjenim

        // Provera da li je trenutni direktorijum postavljen
        if (currentDirectory == null) {
            System.out.println("Trenutni direktorijum nije postavljen.");
            return;
        }

        // Provera da li je trenutni direktorijum direktorijum
        if (!currentDirectory.isDirectory()) {
            System.out.println("Trenutni direktorijum nije validan direktorijum.");
            return;
        }

        // Prikazivanje sadržaja trenutnog direktorijuma
        File[] files = currentDirectory.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("Trenutni direktorijum je prazan.");
        } else {
            System.out.println("Sadržaj trenutnog direktorijuma:");
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("[DIR] " + file.getName());
                } else {
                    System.out.println(file.getName());
                }
            }
        }
    }

    public void deleteFile(String directory) {
        // kreirati logiku za brisanje datoteke ili direktorijuma
        // kao parametar funkcije proslijedjuje se naziv datoteke ili direktorijuma
        // koji zelimo izbrisati
        deleteDirectory(root, directory);
    }

    // =======================Directories=================================================

    public void changeDirectory(String directoryName) {
        // kreirati logiku za promjenu direktorijuma
        File newDirectory = new File(currentDirectory, directoryName);

        if (newDirectory.exists() && newDirectory.isDirectory()) {
            currentDirectory = newDirectory;
            System.out.println("Direktorijum promenjen na: " + currentDirectory.getAbsolutePath());
        } else {
            System.out.println("Direktorijum '" + directoryName + "' ne postoji ili nije direktorijum.");
        }
    }

    public void makeDirectory(String directoryName) {
        // kreirati logiku za pravljenje novog direktorijuma
        // kao parametar funkcije proslijedjuje se naziv novog direktorijuma
        File newDirectory = new File(currentDirectory + File.separator + directoryName);

        if (!newDirectory.exists()) {
            newDirectory.mkdir();
            System.out.println("Napravljen direktorijum: " + directoryName);
        } else {
            System.out.println("Direktorijum već postoji!");
        }
    }

    private void deleteDirectory(File node, String directory) {
        
    }

}
