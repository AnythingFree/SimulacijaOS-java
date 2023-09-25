package hardware_modules;

import java.util.ArrayList;
import java.util.Arrays;

public class HDD {
    // 2 sectors is 1 block = 1024 bytes is 1 block

    private int totalSectors = 40; // 40 sectors = 20 blocks
    byte[][] sectors = new byte[totalSectors][512];

    public HDD() {
        for (int i = 0; i < totalSectors; i++) {
            for (int j = 0; j < 512; j++) {
                sectors[i][j] = 0;
            }
        }
    }

    public void writeData(int sectorNumber, byte[] data) {
        if (sectorNumber >= 0 && sectorNumber < totalSectors) {
            if (data.length < 512) {
                sectors[sectorNumber] = Arrays.copyOf(data, 512);
                System.out.println("Data written to sector " + sectorNumber);
            } else {
                System.err.println("Data must be 512 bytes in length. writeData");
            }
        } else {
            System.err.println("Invalid sector number. writeData");
        }
    }

    public byte[] readData(int sectorNumber) {
        if (sectorNumber >= 0 && sectorNumber < totalSectors) {
            return Arrays.copyOf(sectors[sectorNumber], 512);
        } else {
            System.err.println("Invalid sector number. readData");
            return null;
        }
    }

    public static ArrayList<String> getBinary(String path) {
        return null;
    }

    public int getSize() {
        return totalSectors;
    }

    public static int getSectorSize() {
        return 512;
    }

}
