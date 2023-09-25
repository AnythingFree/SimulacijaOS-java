package hardware_modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// sector (512 bytes) -> track (40 sectors) -> cylinder (1 track) -> plate (5 cylinders)
// but track = cylinder (for simplicity) so: sector (512 bytes) -> track (40 sectors) -> plate (5 tracks)
// block = 2 sectors (1024 bytes) -> 1 block = 1KB
// (block is abstraction of memory in OS)

public class HDD {

    private final int totalbytesInSector = 512;
    private final static int totalSectorsOnTrack = 40; // 40 sectors = 20 blocks
    private final static int totalTracks = 5; // lets asume tracks = cylinders so we have only 1 plate
    private ArrayList<byte[][]> listOfTracks = new ArrayList<byte[][]>();

    private ArrayList<DiskRequest> listOfRequests = new ArrayList<DiskRequest>();
    private boolean read;
    private ArrayList<byte[]> dataToReturn = new ArrayList<byte[]>();
    private int[] headPosition = new int[] { 0, 0 };

    // pomocna klasa
    private class DiskRequest implements Comparable<DiskRequest> {
        private int trackNumber;
        private int sectorNumber;
        private byte[] data;

        DiskRequest(int trackNumber, int sectorNumber, byte[] data) {
            this.trackNumber = trackNumber;
            this.sectorNumber = sectorNumber;
            this.data = data;
        }

        public int getTrackNumber() {
            return trackNumber;
        }

        public int getSectorNumber() {
            return sectorNumber;
        }

        public byte[] getData() {
            return data;
        }

        @Override
        public int compareTo(DiskRequest o) {
            if (this.trackNumber == o.trackNumber) {
                return this.sectorNumber - o.sectorNumber;
            } else {
                return this.trackNumber - o.trackNumber;
            }
        }
    }

    // glavni konstruktor
    public HDD() {

        for (int g = 0; g < totalTracks; g++) {
            byte[][] track = new byte[totalSectorsOnTrack][totalbytesInSector];
            for (int i = 0; i < totalSectorsOnTrack; i++) {
                for (int j = 0; j < totalbytesInSector; j++) {
                    track[i][j] = 0;
                }
            }
            listOfTracks.add(track);
        }

        // da imamo razlicit broj cilindara ovde bi to realizovali

    }

    // ============ PRIVATE ================
    private void writeDataToSector(int trackNumber, int sectorNumber, byte[] data) {
        if (sectorNumber >= 0 && sectorNumber < totalSectorsOnTrack) {
            if (data.length < 512) {
                listOfTracks.get(trackNumber)[sectorNumber] = Arrays.copyOf(data, data.length);
                System.out.println("Data written to sector " + sectorNumber);
            } else {
                System.err.println("Data must be 512 bytes in length. writeData");
            }
        } else {
            System.err.println("Invalid sector number. writeData");
        }
    }

    private byte[] readDataFromSector(int trackNumber, int sectorNumber) {
        if (sectorNumber >= 0 && sectorNumber < totalSectorsOnTrack) {
            return Arrays.copyOf(listOfTracks.get(trackNumber)[sectorNumber], 512);
        } else {
            System.err.println("Invalid sector number. readDataFromSector");
            return null;
        }
    }

    private void CSCAN() {
        // from this headPosition go right and serve requests
        // when you reach end of tracksList go to the beginning of the tracksList and
        // serve

        Collections.sort(this.listOfRequests);

        // Split requests into two lists: those ahead of the initial position and those
        // behind it
        List<DiskRequest> ahead = new ArrayList<>();
        List<DiskRequest> behind = new ArrayList<>();
        DiskRequest initialPosition = new DiskRequest(this.headPosition[0], this.headPosition[1], null);
        for (DiskRequest request : this.listOfRequests) {
            if (request.compareTo(initialPosition) >= 0) {
                ahead.add(request);
            } else {
                behind.add(request);
            }
        }

        // serve ahead and then behind list
        for (DiskRequest request : ahead) {
            serve(request);
        }
        for (DiskRequest request : behind) {
            serve(request);
        }

    }

    private void serve(DiskRequest request) {
        setHeadPosition(request);
        if (this.read) { // ako se ova oznaka (read) prenese u dataRequest klasu onda mozes slati mix
                         // raed and write requests
            this.dataToReturn.add(readDataFromSector(request.getTrackNumber(), request.getSectorNumber()));
        } else {
            writeDataToSector(request.getTrackNumber(), request.getSectorNumber(), request.getData());
        }
    }

    private void setHeadPosition(DiskRequest request) {
        this.headPosition[0] = request.getTrackNumber();
        this.headPosition[1] = request.getSectorNumber();
    }

    // ===================== PUBLIC ====================================

    public void addRequest(int trackNumber, int sectorNumber, byte[] data) {
        this.listOfRequests.add(new DiskRequest(trackNumber, sectorNumber, data));
    }

    public ArrayList<byte[]> read() {
        this.read = true;
        this.dataToReturn.clear();
        CSCAN();
        this.listOfRequests.clear();
        return this.dataToReturn;
    }

    public void write() {
        this.read = false;
        CSCAN();
        this.listOfRequests.clear();
    }

    // ============ GETTERS ================
    public static int getTotalNumberOfSectors() {
        return totalSectorsOnTrack * totalTracks;
    }

    public static int getTotalNumberOfTracks() {
        return totalTracks;
    }

    public static int getTotalNumberOfSectorsOnTrack() {
        return totalSectorsOnTrack;
    }

    public static int getSectorSize() {
        return 512;
    }

}
