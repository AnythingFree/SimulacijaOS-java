package memory_management;

import java.util.ArrayList;

import hardware_modules.RAM;

import java.util.Collections;

public class RamManager {
    private RAM ram;
    static boolean[] MAT; // true - zauzeto, false - slobodno
    private ArrayList<Partition> partitions = new ArrayList<>();

    public RamManager(RAM ram) {
        this.ram = ram;
        MAT = new boolean[ram.getRAMSize()];
    }

    public Partition loadToRam(ArrayList<String> instructionsBinary) throws Exception {
        synchronized (ram) {
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
    }
    //Metoda koja pronalazi adresu u RAM-u na kojoj se može smestiti 
	//particija određene veličine. Vraća -1 ako nema dovoljno slobodnog prostora

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
   // Proverava da li su naredne `size` adresa u RAM-u slobodne 
   // počevši od adrese `i`.
    private boolean checkIfFree(int i, int size) {
        for (int j = i; j < i + size; j++) {
            if (MAT[j] == true) {
                return false;
            }
        }
        return true;
    }

    public String printRAM() {
        synchronized (ram) {
            return ram.printMemory();
        }
    }

    public void printRAMAdmin() {
        synchronized (ram) {
            System.out.println(ram.printMemory());
        }
    }
//premještanje particija kako bi se stvorio kontinuiran prostor u RAM-u
    public void defragmentation() {
        synchronized (ram) {
            ArrayList<Partition> freePartitions = new ArrayList<>();
            for (Partition p : partitions) {
                if (p.isFree())
                    freePartitions.add(p);
            }
            System.out.println("Free partitions: " + freePartitions.size());
            partitions.removeAll(freePartitions);
            Collections.sort(partitions);

            System.out.println("Partitions: " + partitions.size());
            Partition previousOne = partitions.get(0);
            if (previousOne.getStartAddress() != 0) {
                previousOne.moveToStartAdress(0);
            }
            System.out.println("partition moved to 0");
            Partition p;
            for (int i = 1; i < partitions.size(); i++) {
                p = partitions.get(i);
                if (p.getStartAddress() != previousOne.getEndAddress() + 1) {
                    p.moveToStartAdress(previousOne.getEndAddress() + 1);
                }
                previousOne = p;
            }
        }

    }

}
