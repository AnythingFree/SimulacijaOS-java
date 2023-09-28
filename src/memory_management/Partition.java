package memory_management;

import _util.Formater;
import hardware_modules.RAM;

// code, stack, output
public class Partition implements Comparable<Partition> {

    private static final int sizeOfStack = 10;
    private static final int sizeOfOutput = 10;

    private int startAddress; // start of code
    private int endOfCode;
    private int endAddress;

    private int startOfStack;
    private int endOfStack;
    private int outputAdress;
    private RAM ram;

    private boolean free = true;

    public Partition(int startAddress, int endAddress, RAM ram) {
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.endOfCode = endAddress - sizeOfStack - sizeOfOutput;

        this.startOfStack = endOfCode + 1;
        this.endOfStack = endOfCode + sizeOfStack;
        this.outputAdress = endOfStack + 1;

        this.ram = ram;
        this.free = false;

    }

    public byte readFromRam(int address) {
        return ram.read(address);
    }

    public void writeToStack(int pointer, byte data) {
        ram.write(pointer, data);
    }

    public void writeToOutput(int outPointer, byte a) {
        ram.write(outPointer, a);
    }

    public int getStartAddress() {
        return startAddress;
    }

    public int getEndAddress() {
        return endAddress;
    }

    // ============

    public int getStartOfCode() {
        return this.startAddress;
    }

    public int getEndOfCode() {
        return this.endOfCode;
    }

    public int getStartOfStack() {
        return this.startOfStack;
    }

    public int getEndOfStack() {
        return this.endOfStack;
    }

    public int getOutputAdress() {
        return this.outputAdress;
    }

    // =====================

    public static int getSizeOfStack() {
        return sizeOfStack;
    }

    public static int getSizeOfOutput() {
        return sizeOfOutput;
    }

    public void setFree() {
        // printaj na konzolu rezultat
        System.out.println("\rRezultat: ");
        for (int i = outputAdress; i <= endAddress; i++) {
            int s = Formater.toInt(ram.read(i));
            System.out.print(s + " ");
        }
        System.out.print("\n>>>");

        // oslobodi prostor
        for (int i = startAddress; i <= endAddress; i++) {
            byte d = 0;
            ram.write(i, d);
            RamManager.MAT[i] = false;
        }

        this.free = true;
    }

    public boolean isFree() {
        return this.free;
    }

    @Override
    public int compareTo(Partition o) {
        if (startAddress < o.startAddress) {
            return -1;
        } else if (startAddress > o.startAddress) {
            return 1;
        } else {
            return 0;
        }
    }

    public void moveToStartAdress(int newAdress) {
        int size = endAddress - startAddress + 1;
        byte nullByte = 0;
        for (int i = 0; i < size; i++) {

            byte data = ram.read(startAddress + i);
            ram.write(newAdress + i, data);
            RamManager.MAT[newAdress + i] = true;

            RamManager.MAT[startAddress + i] = false;
            ram.write(startAddress + i, nullByte);
        }
        this.startAddress = newAdress;
        this.endAddress = newAdress + size - 1;
        this.endOfCode = endAddress - sizeOfStack - sizeOfOutput;
        
        this.startOfStack = endOfCode + 1;
        this.endOfStack = endOfCode + sizeOfStack;
        this.outputAdress = endOfStack + 1;
    }

}