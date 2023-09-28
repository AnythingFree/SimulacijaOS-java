package file_system;

import java.util.Date;

public class _Metadata {
    private String path;
    private int size;
    private Date dateCreated;

    private FPermission permission;
    private int indexBlockPointer;

    public _Metadata(String path, int size, Date dateCreated, FPermission permission, int indexBlockPointer) {
        this.path = path;
        this.size = size;
        this.dateCreated = dateCreated;
        this.permission = permission;
        this.indexBlockPointer = indexBlockPointer;
    }

    public enum FPermission {
        READ, WRITE, EXECUTE,
        READ_WRITE, READ_EXECUTE,
        WRITE_EXECUTE, READ_WRITE_EXECUTE

    }

    public int getIndexBlockPointer() {
        return indexBlockPointer;
    }

    public FPermission getPermision() {
        return permission;
    }

    public String getPath() {
        return path;
    }

    public int getSize() {
        return size;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

}
