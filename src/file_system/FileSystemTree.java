package file_system;

import java.io.File;

import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

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
            // this.metadata = new _Metadata( bla bla bla);
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
                return this.name;
            } else {
                return this.parent.getPath() + "/" + this.name;
            }
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
        return null;
    }

    // vraca sve imena fajlova i dir-ova iz trenutnog dir-a
    public String[] getNameChildrenNodes(TreeNode directory) {
        return null;
    }

    // vraca TreeNode tog imena ako postoji, ako ne null
    // treba mi da bi oslobodila hdd od podataka
    // a ti obrisi ovde iz drveta
    public TreeNode deleteNode(TreeNode currentNode, String nameOfNode) {
        return null;
    }

    // full path se dobije: /dir1/dir2/dir3 gdje je dir3 novi dir
    // ako uspije vraca true, ako ne false
    public boolean makeDirectory(String path) {
        return false;
    }

}
