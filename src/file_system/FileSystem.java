package file_system;

import java.util.ArrayList;

import file_system.FileSystemTree.NodeType;
import file_system.FileSystemTree.TreeNode;
import hardware_modules.HDD;

public class FileSystem {

	private FileSystemTree fileSystemTree; // Objekat za reprezentaciju drvolike strukture fajl sistema
	private TreeNode currentDirectory;

	private ArrayList<Block> blocks = new ArrayList<Block>();
	private int freeBlocksIndex = 0;
	private ArrayList<Block> freeBlocks;

	private HDD hdd = new HDD();

	public FileSystem() {
		this.fileSystemTree = new FileSystemTree();
		this.currentDirectory = fileSystemTree.getRoot();

		for (int i = 0; i < hdd.getSize(); i = i + 2) {
			blocks.add(new Block(i));
		}

		this.freeBlocks = getFreeBlocks();
	}

	// ================== READ/WRITE FUNC ========================

	// split it into blocks sizes and write to blocks
	public void writeData(ArrayList<Block> blocksAllocated, byte[][] data) {
		for (int i = 1; i < data.length; i++) {
			blocksAllocated.get(i).write(data[i], this.hdd);
		}
	}

	public byte[] readData(int blockID) {
		if (blockID >= 0 && blockID < this.blocks.size()) {
			byte[] data = this.blocks.get(blockID).read(hdd);
			// obrisi 0 ili -1 ili idi do znaka za kraj TO DO
			return data;
		} else {
			System.err.println("Invalid block number. readData");
			return null;
		}
	}

	// ===========================================================
	// ================== FILE SYSTEM ТRЕЕ FUNC ========================F

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
		// int dataPointer = node.getMetadata().getDataPointer(); TO DO
		int dataPointer = 0;

		// oslobodi iz hdd-a podatke
		Block indexBlock = this.blocks.get(dataPointer);
		byte[] data = indexBlock.read(hdd);

		for (int i = 0; i < data.length; i++) {
			if (data[i] == -1) {
				break;
			}
			Block b = this.blocks.get(data[i]);
			b.setOccupied(false);
			this.freeBlocks.add(b);
		}
		indexBlock.setOccupied(false);
		this.freeBlocks.add(indexBlock);
	}

	public boolean createFile(String fileName, String data) {
		String path = this.currentDirectory.getPath() + "/" + fileName;

		byte[][] dataInBlockSizes = getDataInBlockSizes(data.getBytes());
		ArrayList<Block> blocksAllocated = allocateBloks(fileName, dataInBlockSizes.length);
		int indexBlockID = makeIndexBlock(blocksAllocated);
		writeData(blocksAllocated, dataInBlockSizes);

		// add to tree
		return this.fileSystemTree.createFile(path, indexBlockID);
	};

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

	public boolean createDirectory(String directoryName) {
		return this.fileSystemTree.createDirectory(this.currentDirectory.getPath() + "/" + directoryName);
	}

	// ====================
	// ucitava sa hdd sve free blokove
	ArrayList<Block> getFreeBlocks() {
		ArrayList<Block> freeBlocks = new ArrayList<Block>();
		byte[] data = this.blocks.get(this.freeBlocksIndex).read(hdd);

		// jedan bajt je jedan blokID tj adresa. -1 je kraj niza
		for (int i = 0; i < this.blocks.size(); i++) {
			if (data[i] == -1) {
				return freeBlocks;
			}
			freeBlocks.add(this.blocks.get(data[i]));
		}
		return freeBlocks;
	}

	// pise u hdd sve free blokove tj njihove ids
	void writeFreeBlocks(ArrayList<Block> freeBlocks) {
		ArrayList<Integer> data = new ArrayList<>();
		for (Block b : freeBlocks) {
			data.add(b.getId());
		}
		byte[] dataToWrite = new byte[data.size()];
		// array to byte
		for (int i = 0; i < data.size(); i++) {
			dataToWrite[i] = data.get(i).byteValue(); // bice isto msms
		}
		this.blocks.get(this.freeBlocksIndex).write(dataToWrite, hdd);
	}
	// ===================== pomocne ================

	private int makeIndexBlock(ArrayList<Block> blocks) {
		Block indexBlock = blocks.get(0);
		byte[] dataToWrite = new byte[blocks.size() - 1];
		// array to byte
		for (int i = 0; i < blocks.size() - 1; i++) {
			dataToWrite[i] = (byte) blocks.get(i + 1).getId(); // bice isto msms
		}
		indexBlock.write(dataToWrite, hdd);

		return indexBlock.getId();
	}

	private byte[][] getDataInBlockSizes(byte[] data) {

		byte[][] dataInBlockSizes;
		if (data.length > Block.getBlockSize()) {
			int numOfChunks = data.length / Block.getBlockSize();
			dataInBlockSizes = new byte[numOfChunks + 1][];

			for (int i = 0; i < numOfChunks + 1; i++) {
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

	private ArrayList<Block> allocateBloks(String fileName, int length) {

		ArrayList<Block> blocksAllocated = new ArrayList<Block>();
		for (int i = 0; i < length; i++) {
			if (this.freeBlocks.size() == 0) {
				System.err.println("No free blocks. allocateBloks");
				return null;
			}
			blocksAllocated.add(this.freeBlocks.get(i));
			this.freeBlocks.remove(i);
		}

		writeFreeBlocks(freeBlocks);
		return blocksAllocated;
	}
}
