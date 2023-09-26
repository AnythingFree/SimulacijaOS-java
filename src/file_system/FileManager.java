package file_system;

import hardware_modules.HDD;
import shell.Shell;

public class FileManager {

    private FileSystem fs;

    // TODO: funkcija na kraju koja vraca full path.
    // ovde se sve relativne putanje pretvaraju u apsolutne
    public FileManager(HDD hdd) {
        this.fs = new FileSystem(hdd);
    }

    public void listFiles() {
        Shell.output = fs.listFiles() + "\n";
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
        return this.fs.getCurrentDirPath();
    }

    private String getFullPath() {
        return null;
    }

}
