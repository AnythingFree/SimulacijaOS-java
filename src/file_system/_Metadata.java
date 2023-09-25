package file_system;

import java.util.Date;

public class _Metadata {
    private String path;
    private int size;
    private Date dateCreated;
    private FPermission permission;
    private int indexBlockPointer;

    enum FPermission {
        READ, WRITE, EXECUTE
    }

}
