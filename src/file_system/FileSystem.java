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
		blocks.remove(7);

		makeIndexBlockInHDD(this.freeBlocksIndex, blocks);

		this.freeBlocks = new ArrayList<>(blocks);
		// updateFreeBlocksInHDD();

		loadASMFiles();
	}

	public FileSystem(File file) {
		this.fileSystemTree = new FileSystemTree();
		this.currentDirectory = fileSystemTree.getRoot();
	}

	private void loadASMFiles() {
		String path = "src//assembler//FILES"; // Replace with the correct path

		File folder = new File(path);
		File[] files = folder.listFiles();

		if (files != null) {
			for (File file : files) {

				String data = "";
				if (file.isFile() && file.getName().endsWith(".asm")) {
					try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
						String line;
						while ((line = reader.readLine()) != null) {
							// Process each line of the ASM file here
							data += line + "\n";
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				FPermission per = FPermission.READ_WRITE_EXECUTE;
				createFile(file.getName(), data, per);
				System.err.println("File " + file.getName() + " loaded.\n");
			}
		} else {
			System.err.println("The specified path is not a directory.");
		}
	}

	// ================== READ/WRITE FUNC ========================

	// ===================== PRIVATE =============================

	// split it into blocks sizes and write to blocks
	private void sendRequests(ArrayList<Block> blocks, byte[][] data) {
		for (int i = 0; i < blocks.size(); i++) {
			blocks.get(i).sendRequest(data[i], this.hdd);
		}

	}

	private void writeData(ArrayList<Block> blocksAllocated, byte[][] data) {
		sendRequests(blocksAllocated, data);
		this.hdd.write();
	}

	private byte[] readData(ArrayList<Block> blocks) {
		sendRequests(blocks, new byte[1][]);

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
		TreeNode node = this.fileSystemTree.getChildByName(this.currentDirectory, name);
		if (node == null) {
			throw new Exception("File or directory with that name does not exist.");
		}
		delete2(node, this.currentDirectory);
	}

	private void delete2(TreeNode node, TreeNode curent) throws Exception {
		if (node.getType() == NodeType.DIRECTORY) {
			for (TreeNode child : node.getChildren()) {
				delete2(child, node);
			}
		} else {
			deleteFile(node);
		}
		this.fileSystemTree.deleteNode(curent, node);

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
			throw new Exception("Stoped. Read only file can not be deleted: " + node.getName());
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

	public void createFile(String fileName, String data, FPermission per) {
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
		this.fileSystemTree.createDirectoryInDir(directoryName, this.currentDirectory);
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
		System.out.println("indexBlock: " + indexBlock.getId());
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
			dataInBlockSizes = new byte[1][];
			dataInBlockSizes[0] = data;
		}

		return dataInBlockSizes;
	}

	private ArrayList<Block> allocateBloks(int length) {

		ArrayList<Block> blocksAllocated = new ArrayList<Block>();
		for (int i = 0; i < length; i++) {
			if (this.freeBlocks.size() == 0) {
				System.err.println("No free blocks. allocateBloks");
				return null;
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

}
