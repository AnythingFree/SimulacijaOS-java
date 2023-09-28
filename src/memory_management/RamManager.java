package memory_management;

import java.util.ArrayList;

import hardware_modules.RAM;

public class RamManager {
    private RAM ram;
    static boolean[] MAT; // true - zauzeto, false - slobodno
    private ArrayList<Partition> partitions = new ArrayList<>();

    public RamManager(RAM ram) {
        this.ram = ram;
        MAT = new boolean[ram.getRAMSize()];
    }

    public Partition loadToRam(ArrayList<String> instructionsBinary) throws Exception {
        int sizeOfPartition = instructionsBinary.size() + Partition.getSizeOfStack() + Partition.getSizeOfOutput();

        int startAddress = getAdress(sizeOfPartition);
        if (startAddress == -1) {
            throw new Exception("Nema dovoljno RAM memorije");
        }
        int endAddress = startAddress + sizeOfPartition - 1;

        // write data to ram
        for (int i = startAddress; i < startAddress + instructionsBinary.size(); i++) {
            this.ram.write(i, (byte) Integer.parseInt(instructionsBinary.get(i - startAddress), 2));
            MAT[i] = true;
        }
        // set mat
        for (int i = startAddress + instructionsBinary.size(); i <= endAddress; i++) {
            MAT[i] = true;
        }

        // create partition
        Partition partition = new Partition(startAddress, endAddress, ram);
        this.partitions.add(partition);

        return partition;
    }

    private int getAdress(int size) {
        for (int i = 0; i < MAT.length; i++) {
            if (MAT[i] == false) {
                if (checkIfFree(i, size)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean checkIfFree(int i, int size) {
        for (int j = i; j < i + size; j++) {
            if (MAT[j] == true) {
                return false;
            }
        }
        return true;
    }

    public void printRAM() {
        ram.printMemory();
    }

}
