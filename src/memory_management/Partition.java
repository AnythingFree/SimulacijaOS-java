package memory_management;

import java.util.ArrayList;

import _util.Formater;
import hardware_modules.RAM;
import shell.Shell;

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
        synchronized (ram) {
            return ram.read(address);
        }
    }

    public void writeToStack(int pointer, byte data) {
        synchronized (ram) {
            ram.write(pointer, data);
        }
    }

    public void writeToOutput(int outPointer, byte a) {
        synchronized (ram) {
            ram.write(outPointer, a);
        }
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
        synchronized (ram) {
            // printaj na konzolu rezultat
            ArrayList<String> ramout = new ArrayList<>();
            System.out.println("Rezultat: ");
            for (int i = outputAdress; i <= endAddress; i++) {
                int s = Formater.toInt(ram.read(i));
                System.out.print(s + " ");
                ramout.add("|A " + i + ": " + s);
            }

            Shell.setGUIRAM(ramout);

            // oslobodi prostor
            for (int i = startAddress; i <= endAddress; i++) {
                byte d = 0;
                ram.write(i, d);
                RamManager.MAT[i] = false;
            }
        }

        this.free = true;
    }

    public void moveToStartAdress(int newAdress) {
        synchronized (ram) {
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

    @Override
    public String toString() {
        return "\n    Start Address: " + startAddress +

                "\n    Start of Code: " + startAddress +
                "\n    End of Code: " + endOfCode +
                "\n    Start of Stack: " + startOfStack +
                "\n    End of Stack: " + endOfStack +
                "\n    Output Address: " + outputAdress +

                "\n    End Address: " + endAddress +
                "\n    Free: " + free;
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

}
