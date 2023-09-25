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

		int id = 0;
		for (int j = 0; j < HDD.getTotalNumberOfTracks(); j++) {
			for (int i = 0; i < HDD.getTotalNumberOfSectorsOnTrack(); i = i + 2) {
				blocks.add(new Block(id, j, i));
				id++;
			}
		}

		Block indexBlock = blocks.get(0);
		blocks.remove(0);
		makeIndexBlockInHDD(indexBlock, blocks);

		this.freeBlocks = blocks;
		// updateFreeBlocksInHDD();
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
		sendRequests(blocks, new byte[0][]);

		// turn ArrayList<byte[]> into byte[]
		ArrayList<byte[]> data = this.hdd.read();
		byte[] dataByte = new byte[data.size() * HDD.getSectorSize()];
		for (int i = 0; i < data.size(); i++) {
			System.arraycopy(data.get(i), 0, dataByte, i * HDD.getSectorSize(), HDD.getSectorSize());
		}

		return dataByte;
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
		int indexBlockPointer = 0;

		// oslobodi iz hdd-a podatke
		Block indexBlock = this.blocks.get(indexBlockPointer);
		ArrayList<Block> b = new ArrayList<>();
		b.add(indexBlock);
		byte[] data = readData(b);

		for (int i = 0; i < data.length; i++) {
			if (data[i] == -1) {
				break;
			}
			Block bl = this.blocks.get(data[i]);
			bl.setOccupied(false);
			this.freeBlocks.add(bl);
		}
		indexBlock.setOccupied(false);
		this.freeBlocks.add(indexBlock);
	}

	public boolean createFile(String fileName, String data) {
		String path = this.currentDirectory.getPath() + "/" + fileName;

		byte[][] dataInBlockSizes = getDataInBlockSizes(data.getBytes());
		ArrayList<Block> blocksAllocated = allocateBloks(fileName, dataInBlockSizes.length);
		Block indexBlock = blocksAllocated.get(0);
		blocksAllocated.remove(0);
		makeIndexBlockInHDD(indexBlock, blocksAllocated);

		writeData(blocksAllocated, dataInBlockSizes);

		// add to tree
		int indexBlockID = indexBlock.getId();
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
	// ===================== pomocne ================

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

		// this.blocks.get(this.freeBlocksIndex) to arrayList
		ArrayList<Block> bl = new ArrayList<Block>();
		bl.add(this.blocks.get(this.freeBlocksIndex));

		byte[][] dataInBlockSizes = getDataInBlockSizes(dataToWrite);
		writeData(bl, dataInBlockSizes);
	}

	private void makeIndexBlockInHDD(Block indexBlock, ArrayList<Block> blocks) {
		byte[][] dataToWrite = new byte[0][blocks.size()];
		// array to byte
		for (int i = 0; i < blocks.size(); i++) {
			dataToWrite[0][i] = (byte) blocks.get(i).getId(); // bice isto msms
		}
		ArrayList<Block> b = new ArrayList<Block>();
		b.add(indexBlock);
		writeData(b, dataToWrite);
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
