package kernel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import file_system.FileSystem;
import hardware_modules.HDD;
import hardware_modules.RAM;
import memory_management.Partition;
import memory_management.RamManager;

public class MemoryManager {

    private FileSystem fs;
    private RamManager ramManager;

    // TODO: funkcija na kraju koja vraca full path.
    // ovde se sve relativne putanje pretvaraju u apsolutne
    public MemoryManager(HDD hdd, RAM ram) {
        this.fs = new FileSystem(hdd);
        this.ramManager = new RamManager(ram);
    }

    public String listFiles() {
        return fs.listFiles();
    }

    public void makeDirectory(String nameOfNewDir) throws Exception {
        this.fs.createDirectoryInCurrentDir(nameOfNewDir);
    }

    public void changeCurrentDirectory(String nameORpath) throws Exception {
        this.fs.changeCurrentDirectory(nameORpath);
    }

    public void deleteFileORDir(String name) throws Exception {
        this.fs.delete(name);
    }

    public String getCurrentDir() {
        String path = this.fs.getCurrentDirPath();
        if (path.equals(""))
            return "/";
        return path;
    }

    public String getFileFromDisk(String nameOfFile) throws Exception {
        byte[] byteArray = this.fs.getFileFromDisk(nameOfFile);
        String text = new String(byteArray, StandardCharsets.UTF_8);
        return text;
    }

    public Partition loadToRam(ArrayList<String> binary) throws Exception {
        return this.ramManager.loadToRam(binary);
    }

    public String printRAM() {
        return this.ramManager.printRAM();
    }

    public void printRAMAdmin() {
        this.ramManager.printRAMAdmin();
    }

    public void defragmentation() {
        this.ramManager.defragmentation();
    }

}
