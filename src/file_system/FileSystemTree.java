package file_system;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import file_system._Metadata.FPermission;

public class FileSystemTree {
    private TreeNode root;

    public FileSystemTree() {
        this.root = new TreeNode("/", NodeType.DIRECTORY);
    }

    public TreeNode getRoot() {
        return root;
    }

    public static class TreeNode {
        private String name;
        private NodeType type;
        private List<TreeNode> children;
        private TreeNode parent = null;

        private _Metadata metadata;

        public TreeNode(String name, NodeType type) {
            this.name = name;
            this.type = type;
            this.children = new ArrayList<>();
            // this.metadata = new _Metadata( bla bla bla); TO DO MARIJA
        }

        public String getName() {
            return name;
        }

        public NodeType getType() {
            return type;
        }

        public List<TreeNode> getChildren() {
            return children;
        }

        public void addChild(TreeNode childNode) {
            children.add(childNode);
            childNode.parent = this;
        }

        @Override
        public String toString() {
            return name;
        }

        public TreeNode getParent() {
            return this.parent;
        }

        public String getPath() {
            if (this.parent == null) {
                return "";
            } else {
                return this.parent.getPath() + "/" + this.name;
            }
        }

        public _Metadata getMetadata() {
            return this.metadata;
        }
    }

    public enum NodeType {
        DIRECTORY,
        FILE
    }

    public static void main(String[] args) {
        FileSystemTree fileTree = new FileSystemTree();

        // Example: Creating a directory and adding files to it
        TreeNode directoryNode = new TreeNode("documents", NodeType.DIRECTORY);
        TreeNode fileNode1 = new TreeNode("file1.txt", NodeType.FILE);
        TreeNode fileNode2 = new TreeNode("file2.txt", NodeType.FILE);
        TreeNode dir2 = new TreeNode("documents2", NodeType.DIRECTORY);
        TreeNode file3 = new TreeNode("file3.txt", NodeType.FILE);

        // Adding files to the directory
        directoryNode.addChild(fileNode1);
        directoryNode.addChild(fileNode2);
        dir2.addChild(file3);

        // Adding the directory to the root
        fileTree.getRoot().addChild(directoryNode);
        fileTree.getRoot().addChild(dir2);

        // fileTree.getRoot().getChildren().remove(dir2);

        // Print the file system tree
        printFileTree(fileTree.getRoot(), "");
    }

    // Helper method to print the file system tree (depth-first traversal)
    private static void printFileTree(TreeNode node, String indent) {
        System.out.println(indent + node.getName());
        for (TreeNode child : node.getChildren()) {
            printFileTree(child, indent + "  ");
        }
    }

    // ====================
    // TO DO Marija

    // vraca TreeNode tog imena ako postoji, ako ne null
    public TreeNode getChildByName(TreeNode currentDirectory, String directoryName) {
        for (TreeNode child : currentDirectory.getChildren()) {
            if (child.getName().equals(directoryName)) {
                return child;
            }
        }
        return null;
    }

    // vraca sve imena fajlova i dir-ova iz trenutnog dir-a
    public String[] getNameChildrenNodes(TreeNode directory) {

        String[] names = new String[directory.getChildren().size()];
        for (int i = 0; i < directory.getChildren().size(); i++) {
            names[i] = directory.getChildren().get(i).getName();
        }
        return names;
    }

    public void deleteNode(TreeNode nodeToDelete) {
        TreeNode parent = nodeToDelete.getParent();
        parent.getChildren().remove(nodeToDelete);
    }

    // ovo mozda nece trebati ne znam
    public void createDirectory(String path) throws Exception {
        String[] names = path.split("/");
        TreeNode currentNode = this.root;
        TreeNode node = null;
        for (String name : names) {
            if (name.equals("")) {
                continue;
            }
            node = getChildByName(currentNode, name);
            if (names[names.length - 1].equals(name)) {
                if (node == null) {
                    TreeNode newNode = new TreeNode(name, NodeType.DIRECTORY);
                    currentNode.addChild(newNode);
                } else {
                    throw new Exception("Directory already exists");
                }
                return;
            } else if (node == null) {
                throw new Exception("Direcotry in path does not exist");
            } else {
                currentNode = node;
            }

        }

    }

    public void createFile(TreeNode currentDirectory, String name, int indexBlockID, int size, FPermission permision) {
        TreeNode newNode = new TreeNode(name, NodeType.FILE);
        String path = currentDirectory.getPath() + "/" + name;
        newNode.metadata = new _Metadata(path, size, new Date(), permision, indexBlockID);
        currentDirectory.addChild(newNode);
    }

    public TreeNode getByPath(String path) throws Exception {
        String[] names = path.split("/");
        TreeNode currentNode = this.root;
        TreeNode node = null;
        for (String name : names) {
            if (name.equals("")) {
                continue;
            }
            node = getChildByName(currentNode, name);
            if (names[names.length - 1].equals(name)) {
                if (node == null) {
                    throw new Exception("Directory does not exists");
                } else {
                    return node;
                }
            } else if (node == null) {
                throw new Exception("Direcotry in path does not exist");
            } else {
                currentNode = node;
            }
        }
        throw new Exception("Direcotry does not exist");
    }

    public void createDirectoryInDir(String directoryName, TreeNode currentDirectory) throws Exception {
        if (getChildByName(currentDirectory, directoryName) != null) {
            throw new Exception("Directory already exists");
        }
        TreeNode newNode = new TreeNode(directoryName, NodeType.DIRECTORY);
        currentDirectory.addChild(newNode);
    }

}
