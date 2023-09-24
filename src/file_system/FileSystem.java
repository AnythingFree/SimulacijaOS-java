package file_system;

import java.io.File;

import hardware_modules.HDD;
import javafx.scene.control.TreeItem;

public class FileSystem {
	private FileSystemTree fileSystemTree; // Objekat za reprezentaciju drvolike strukture fajl sistema

	private static HDD hdd = new HDD();

	public FileSystem(File file) {
		this.fileSystemTree = new FileSystemTree(file);
	}

}
