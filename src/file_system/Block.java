package file_system;

import java.util.ArrayList;

import hardware_modules.HDD;

public class Block {
    final int id;
    final ArrayList<int[]> pointers;
    boolean occupied;

    public Block(int id, int trackN, int sectorN) {
        this.id = id;
        this.pointers = new ArrayList<int[]>();
        this.pointers.add(new int[] { trackN, sectorN });
        this.pointers.add(new int[] { trackN, sectorN + 1 });
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

    // ovo moze sa manje koda
    public void sendRequest(byte[] bs, HDD hdd) {
        this.occupied = true;

        int trackNumber = this.pointers.get(0)[0];
        int sectorNumber = this.pointers.get(0)[1];
        if (bs != null && bs.length > HDD.getSectorSize()) {

            // chunk it
            byte[] firstSector = new byte[HDD.getSectorSize()];
            System.arraycopy(bs, 0, firstSector, 0, HDD.getSectorSize());
            hdd.addRequest(trackNumber, sectorNumber, firstSector);

            trackNumber = this.pointers.get(1)[0];
            sectorNumber = this.pointers.get(1)[1];
            byte[] secondSector = new byte[HDD.getSectorSize()];
            System.arraycopy(bs, HDD.getSectorSize(), secondSector, 0, bs.length - HDD.getSectorSize());
            hdd.addRequest(trackNumber, sectorNumber, secondSector);

        } else {
            hdd.addRequest(trackNumber, sectorNumber, bs);
        }

    }

}
