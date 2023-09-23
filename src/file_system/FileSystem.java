package file_system;

import java.io.File;

import javafx.scene.control.TreeItem;

public class FileSystem {
	private static TreeItem<File> root;
	 private static File currentDirectory; // Trenutni radni direktorijum
	 private FileSystemTree fileSystemTree; // Objekat za reprezentaciju drvolike strukture fajl sistema
	 private static File rootDirectory;
	 
	  

	public FileSystem(File file) {
		// konstruktor sa ulaznim parametrom File, sto predstavlja direktorijum
		//nazvati direktorijum po zelji, u ovom slucaju sam napisala PROGRAMS
		 this.rootDirectory = file;
	     this.currentDirectory = rootDirectory;
	     this.root = new TreeItem<>(rootDirectory);
	     FileSystemTree.createTree(root);
	}

	public static void listFiles() {
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

	public static void changeDirectory(String directoryName) {
		// kreirati logiku za promjenu direktorijuma
		File newDirectory = new File(currentDirectory, directoryName);

	    if (newDirectory.exists() && newDirectory.isDirectory()) {
	    	currentDirectory = newDirectory;
	        System.out.println("Direktorijum promenjen na: " + currentDirectory.getAbsolutePath());
	    } else {
	        System.out.println("Direktorijum '" + directoryName + "' ne postoji ili nije direktorijum.");
	    }
	}

	public static void makeDirectory(String directoryName) {
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

	public static void deleteFile(String directory) {
		// kreirati logiku za brisanje datoteke ili direktorijuma
		// kao parametar funkcije proslijedjuje se naziv datoteke ili direktorijuma
		// koji zelimo izbrisati
		deleteDirectory(root, directory);
	}

    private static void deleteDirectory(TreeItem<File> node, String directory) {
        if (node.getValue().getName().equals(directory) && node.getValue().isDirectory()) {
            node.getValue().delete();
            System.out.println("Direktorijum je obrisan!");
            return;
        }

        for (TreeItem<File> child : node.getChildren()) {
            deleteDirectory(child, directory);
        }
    }
	
    
}
