package file_system;

import file_system.FileSystemTree.NodeType;
import file_system.FileSystemTree.TreeNode;
import hardware_modules.HDD;

public class FileSystem {

	private FileSystemTree fileSystemTree; // Objekat za reprezentaciju drvolike strukture fajl sistema
	private TreeNode currentDirectory;

	private static HDD hdd = new HDD();

	public FileSystem() {
		this.fileSystemTree = new FileSystemTree();
		this.currentDirectory = fileSystemTree.getRoot();
	}

	public void delete(String name) {
		TreeNode node = this.fileSystemTree.deleteNode(this.currentDirectory, name);
		if (node.getType() == NodeType.FILE)
			deleteFile(node);
	}

	// =======================FIles=================================================
	public void listFiles() { // lista sve iz trenutnog noda
		String[] nodes = this.fileSystemTree.getNameChildrenNodes(currentDirectory);
		for (String node : nodes) {
			System.out.println(node);
		}
	}

	public void deleteFile(TreeNode node) {
		// treba da oslobodi iz hdd-a podatke
	}

	// =======================Directories=================================================
	public void changeCurrentDirectory(String directoryName) {

		// vraca currentDir na dir iznad currentDir
		if (directoryName.equals("..")) {
			this.currentDirectory = this.currentDirectory.getParent();
			return;

			// ostaje u currentDir
		} else if (directoryName.equals(".")) {
			return;

			// ako je unet root (tj /)
		} else if (directoryName.equals("/")) {
			this.currentDirectory = this.fileSystemTree.getRoot();
			return;

			// ako je uneta putanja
		} else if (directoryName.contains("/")) {
			this.currentDirectory = this.fileSystemTree.getRoot();
			String[] path = directoryName.split("/");
			for (String dir : path) {
				changeCurrentDirectory(dir);
			}
			return;

		}
		this.currentDirectory = this.fileSystemTree.getChildByName(currentDirectory, directoryName);
	}

	public boolean makeDirectory(String directoryName) {
		return this.fileSystemTree.makeDirectory(this.currentDirectory.getPath() + "/" + directoryName);
	}
}
