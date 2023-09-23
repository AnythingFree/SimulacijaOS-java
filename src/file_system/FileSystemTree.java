package file_system;

import java.io.File;

import javafx.scene.control.TreeItem;

public class FileSystemTree {
	
	//konstruktor, pozivamo iz FileSystem
	public FileSystemTree(String currentDirectory) {
		// TODO Auto-generated constructor stub
	}

	public static void createTree(TreeItem<File> root) {
		// kreirati logiku za pravljenje stabla
		File rootFolder = root.getValue();

        if (rootFolder.isDirectory()) {
            File[] files = rootFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    TreeItem<File> treeItem = new TreeItem<>(file);
                    root.getChildren().add(treeItem);
                    createTree(treeItem); // Rekurzivno pozivanje za svaki poddirektorijum
                }
            }
        }
	}

}
