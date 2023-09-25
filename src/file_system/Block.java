package file_system;

import hardware_modules.HDD;

public class Block {
    final int id;
    final int[] pointers;
    boolean occupied;

    public Block(int i) {
        this.id = i;
        this.pointers = new int[] { i, i + 1 };
        this.occupied = false;
    }

    public int getId() {
        return id;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public static int getBlockSize() {
        return 2 * HDD.getSectorSize();
    }

    public void write(byte[] bs, HDD hdd) {
        int sectorNumber = this.pointers[0];
        if (bs.length > HDD.getSectorSize()) {

            // chunk it
            byte[] firstSector = new byte[HDD.getSectorSize()];
            System.arraycopy(bs, 0, firstSector, 0, HDD.getSectorSize());
            hdd.writeData(sectorNumber, firstSector);

            sectorNumber = this.pointers[1];
            byte[] secondSector = new byte[HDD.getSectorSize()];
            System.arraycopy(bs, HDD.getSectorSize(), secondSector, 0, bs.length - HDD.getSectorSize());
            hdd.writeData(sectorNumber, secondSector);

        } else {
            hdd.writeData(sectorNumber, bs);
        }

        this.occupied = true;

    }

    public byte[] read(HDD hdd) {
        // read from hdd
        byte[] firstSector = hdd.readData(this.pointers[0]);
        byte[] secondSector = hdd.readData(this.pointers[1]);

        byte[] result = new byte[firstSector.length + secondSector.length];

        System.arraycopy(firstSector, 0, result, 0, firstSector.length);
        System.arraycopy(secondSector, 0, result, firstSector.length, secondSector.length);
        return result;
    }

}
