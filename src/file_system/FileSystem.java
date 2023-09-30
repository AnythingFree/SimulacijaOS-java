package file_system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import file_system.FileSystemTree.NodeType;
import file_system.FileSystemTree.TreeNode;
import file_system._Metadata.FPermission;
import hardware_modules.HDD;

public class FileSystem {

	private FileSystemTree fileSystemTree; // Objekat za reprezentaciju drvolike strukture fajl sistema
	private TreeNode currentDirectory;

	private ArrayList<Block> blocks = new ArrayList<Block>();
	private Block freeBlocksIndex;
	private ArrayList<Block> freeBlocks;

	private HDD hdd;
	private String rootPath = "root";

	// Konstruktor za inicijalizaciju fajl sistema
	public FileSystem(HDD hdd) {
		this.hdd = hdd;
		this.fileSystemTree = new FileSystemTree();
		this.currentDirectory = fileSystemTree.getRoot();

		int id = 0;
		for (int j = 0; j < HDD.getTotalNumberOfTracks(); j++) {
			for (int i = 0; i < HDD.getTotalNumberOfSectorsOnTrack(); i = i + 2) {
				blocks.add(new Block(id, j, i));
				id++;
			}
		}

		this.freeBlocksIndex = blocks.get(7);
		// blocks.remove(7);

		makeIndexBlockInHDD(this.freeBlocksIndex, blocks);

		this.freeBlocks = new ArrayList<>(blocks);
		// updateFreeBlocksInHDD();

		try {
			load();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// Konstruktor za kreiranje fajl sistema iz datoteke
	public FileSystem(File file) {
		this.fileSystemTree = new FileSystemTree();
		this.currentDirectory = fileSystemTree.getRoot();
	}

	private void load() throws Exception {
		// go through all folder in the directory root
		File rootDir = new File(this.rootPath);
		File[] filesAndDirs = rootDir.listFiles();

		// Iterate through each file/folder
		for (File fileOrDir : filesAndDirs) {
			load2(fileOrDir);
		}
	}

	private void load2(File file) throws Exception {
		// Check if the given path exists and is a directory
		if (file.isDirectory()) {

			// cretae dir
			createDirectoryInCurrentDir(file.getName());
			changeCurrentDirectory(file.getName());

			// Get a list of all files and directories in the directory
			File[] filesAndDirs = file.listFiles();

			// Iterate through each file/folder
			for (File fileOrDir : filesAndDirs) {
				load2(fileOrDir);
			}
			// go back to parent dir
			changeCurrentDirectory("..");

		} else {
			// add it to dir
			loadFile(file);
		}
	}

	private void loadFile(File file) {
		String data = "";
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				data += line + "\n";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		FPermission per = FPermission.READ_WRITE_EXECUTE;
		try {
			createFile(file.getName(), data, per);
		} catch (Exception e) {
			System.out.println(file.getName() + "Fajl nije kreiran:" + e.getMessage());
		}
		System.err.println("File " + file.getName() + " loaded.\n");
	}

	// ================== READ/WRITE FUNC ========================

	// ===================== PRIVATE =============================

	// split it into blocks sizes and write to blocks
	private void sendRequests(ArrayList<Block> blocks, byte[][] data) {

		if (data == null) {
			for (int i = 0; i < blocks.size(); i++) {
				blocks.get(i).sendRequest(null, this.hdd);
			}
		} else {
			for (int i = 0; i < blocks.size(); i++) {
				blocks.get(i).sendRequest(data[i], this.hdd);
			}
		}

	}

	private void writeData(ArrayList<Block> blocksAllocated, byte[][] data) {
		sendRequests(blocksAllocated, data);
		this.hdd.write();
	}

	private byte[] readData(ArrayList<Block> blocks) {
		sendRequests(blocks, null);

		// turn ArrayList<byte[]> into byte[]
		ArrayList<byte[]> data = this.hdd.read();
		byte[] dataByte = new byte[data.size() * HDD.getSectorSize()];
		for (int i = 0; i < data.size(); i++) {
			System.arraycopy(data.get(i), 0, dataByte, i * HDD.getSectorSize(), HDD.getSectorSize());
		}

		// copy exactly occupsize of bytes
		int size = 0;
		for (Block b : blocks) {
			size += b.getOccupSize();
		}
		byte[] result = new byte[size];
		System.arraycopy(dataByte, 0, result, 0, size);

		return result;
	}

	// ===========================================================
	// ================== FILE SYSTEM ТRЕЕ FUNC ========================F

	public void delete(String name) throws Exception {

		// trazi node i provjerava da li je u rootu
		TreeNode node = this.fileSystemTree.getChildByName(this.currentDirectory, name);
		if (node == null) {
			throw new Exception("File or directory with that name does not exist.");
		}

		// brise sve fajlove ako je dir ili brise fajl ako je to fajl
		delete2(node); // trazi i brise samo fajlove iz diska ne iz fileTree

		// brise fajl iz stvarnog sistema
		File file = new File(this.rootPath + node.getPath());
		if (file.exists()) {
			deleteS2(file);
			file.delete();
		} else {
			throw new Exception("File or directory with that name does not exist2.");
		}

		// brise fajl iz naseg sistema tj iz fileTree
		this.fileSystemTree.deleteNode(node); // java garbage collector ce obrisati i childove
	}

	private void delete2(TreeNode node) throws Exception {
		System.out.println("Deleting " + node.getName());
		if (node.getType() == NodeType.DIRECTORY) {
			for (TreeNode child : node.getChildren()) {
				System.out.println("Child " + child.getName());
				delete2(child);
			}
		} else {
			deleteFile(node);
		}
		System.out.println("Deleted2 " + node.getName());

	}

	public void deleteS2(File file) {
		File[] filesAndDirs = file.listFiles();
		for (File fileOrDir : filesAndDirs) {
			if (fileOrDir.isDirectory()) {
				deleteS2(fileOrDir);
				fileOrDir.delete();
			} else {
				fileOrDir.delete();
			}
		}
	}

	// =======================FIles=================================================
	public String listFiles() { // lista sve iz trenutnog noda
		String names = "";
		String[] nodes = this.fileSystemTree.getNameChildrenNodes(currentDirectory);
		for (String node : nodes) {
			names += node + " ";
		}
		return names;
	}

	public void deleteFile(TreeNode node) throws Exception {

		FPermission permission = node.getMetadata().getPermision();
		if (permission == FPermission.READ || permission == FPermission.EXECUTE
				|| permission == FPermission.READ_EXECUTE) {
			throw new Exception("Stoped. only file with write permission can be deleted: " + node.getName());
		}

		int indexBlockPointerID = node.getMetadata().getIndexBlockPointer();

		// oslobodi iz hdd-a podatke
		Block indexBlock = this.blocks.get(indexBlockPointerID); // ID i mjesto u arraylisti su isti u nasem slucaju
		ArrayList<Block> b = new ArrayList<>();
		b.add(indexBlock);

		byte[] data = readData(b);

		for (int i = 0; i < data.length; i++) {
			Block bl = this.blocks.get(data[i]);
			bl.setOccupied(false);
			this.freeBlocks.add(bl);
		}

		indexBlock.setOccupied(false);
		this.freeBlocks.add(indexBlock);
		writeFreeBlocksToDisk();
	}

	public void createFile(String fileName, String data, FPermission per) throws Exception {
		byte[][] dataInBlockSizes = getDataInBlockSizes(data.getBytes());
		ArrayList<Block> blocksAllocated = allocateBloks(dataInBlockSizes.length);
		Block indexBlock = blocksAllocated.get(0);
		blocksAllocated.remove(0);
		makeIndexBlockInHDD(indexBlock, blocksAllocated);

		writeData(blocksAllocated, dataInBlockSizes);

		// add to tree
		int indexBlockID = indexBlock.getId();
		int size = 0;
		for (Block b : blocksAllocated) {
			size += b.getOccupSize();
		}
		this.fileSystemTree.createFile(this.currentDirectory, fileName, indexBlockID, size, per);
	};

	// =======================Directories=================================================
	public void changeCurrentDirectory(String nameORpath) throws Exception {

		// vraca currentDir na dir iznad currentDir
		if (nameORpath.equals("..")) {
			this.currentDirectory = this.currentDirectory.getParent();
			return;

			// ostaje u currentDir
		} else if (nameORpath.equals(".")) {
			return;

			// ako je unet root (tj /)
		} else if (nameORpath.equals("/")) {
			this.currentDirectory = this.fileSystemTree.getRoot();
			return;

			// ako je uneta putanja
		} else if (nameORpath.contains("/")) {
			this.currentDirectory = this.fileSystemTree.getByPath(nameORpath);
			return;

		}
		TreeNode n = this.fileSystemTree.getChildByName(this.currentDirectory, nameORpath);
		if (n != null && n.getType() == NodeType.DIRECTORY)
			this.currentDirectory = n;
		else
			throw new Exception("Directory with that name does not exist.");
	}

	public void createDirectoryInCurrentDir(String directoryName) throws Exception {
		File newDir = new File(this.rootPath + "/" + this.currentDirectory.getPath() + "/" + directoryName);
		try {
			newDir.mkdir();
			this.fileSystemTree.createDirectoryInDir(directoryName, this.currentDirectory);
		} catch (Exception e) {
			throw new Exception("Directory with that name already exists.");
		}
	}

	// ====================
	// ===================== pomocne ================

	// pise u hdd sve free blokove tj njihove ids
	void writeFreeBlocksToDisk() {
		ArrayList<Integer> data = new ArrayList<>();
		for (Block b : freeBlocks) {
			data.add(b.getId());
		}

		byte[] dataToWrite = new byte[data.size()];
		// array to byte
		for (int i = 0; i < data.size(); i++) {
			dataToWrite[i] = data.get(i).byteValue(); // bice isto msms
		}

		ArrayList<Block> bl = new ArrayList<Block>();
		bl.add(this.freeBlocksIndex);

		byte[][] dataInBlockSizes = getDataInBlockSizes(dataToWrite);
		// sendRequests(bl, dataInBlockSizes);
		writeData(bl, dataInBlockSizes);
	}

	private void makeIndexBlockInHDD(Block indexBlock, ArrayList<Block> blocks) {
		byte[][] dataToWrite = new byte[1][blocks.size()];
		// array to byte
		for (int i = 0; i < blocks.size(); i++) {
			dataToWrite[0][i] = (byte) blocks.get(i).getId(); // bice isto msms
		}
		ArrayList<Block> b = new ArrayList<Block>();
		b.add(indexBlock);
		sendRequests(b, dataToWrite);
		// writeData(b, dataToWrite);
	}

	private byte[][] getDataInBlockSizes(byte[] data) {

		byte[][] dataInBlockSizes;
		if (data.length > Block.getBlockSize()) {
			int numOfChunks = data.length / Block.getBlockSize();
			if (data.length % Block.getBlockSize() != 0) {
				numOfChunks++;
			}
			dataInBlockSizes = new byte[numOfChunks][];

			for (int i = 0; i < numOfChunks; i++) {
				int start = i * Block.getBlockSize();
				int length = Math.min(data.length - start, Block.getBlockSize());
				byte[] chunk = new byte[length];
				System.arraycopy(data, start, chunk, 0, length); // copy from, start, to, start, length
				dataInBlockSizes[i] = chunk;
			}

		} else {
			dataInBlockSizes = new byte[1][1];
			dataInBlockSizes[0] = data;
		}

		return dataInBlockSizes;
	}

	private ArrayList<Block> allocateBloks(int length) throws Exception {

		ArrayList<Block> blocksAllocated = new ArrayList<Block>();
		for (int i = 0; i < length; i++) {
			if (this.freeBlocks.size() == 0) {
				throw new Exception("No free blocks. allocateBloks");
			}
			blocksAllocated.add(this.freeBlocks.get(0));
			this.freeBlocks.remove(0);
		}
		// za indeks dodaj jos jedan blok
		blocksAllocated.add(this.freeBlocks.get(0));
		this.freeBlocks.remove(0);

		writeFreeBlocksToDisk();
		return blocksAllocated;
	}

	public String getCurrentDirPath() {
		return this.currentDirectory.getPath();
	}

	public byte[] getFileFromDisk(String nameOfFile) throws Exception {
		TreeNode node = this.fileSystemTree.getChildByName(this.currentDirectory, nameOfFile);
		if (node == null) {
			throw new Exception("File with that name does not exist.");
		}
		_Metadata meta = node.getMetadata();
		int indexBlockPointerID = meta.getIndexBlockPointer();
		Block block = this.blocks.get(indexBlockPointerID);
		ArrayList<Block> blockList = new ArrayList<Block>();
		blockList.add(block);

		byte[] indexBlockData = readData(blockList);

		blockList.clear();
		for (byte b : indexBlockData) {
			blockList.add(this.blocks.get((int) b));
		}

		return readData(blockList);
	}

}
