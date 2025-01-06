package com.huffman.treenode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.huffman.compressed_data.HuffmanFileData;

//由文件夹组成树
public class FolderTreeNode implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 该文件夹中包含的文件夹，是其子节点
     */
    private List<FolderTreeNode> children;
    /**
     * 该文件夹下的文件对应的HuffmanFileData类的实例的集合
     */
    private List<HuffmanFileData> compressedfiles;
    /**
     * 该文件夹的文件夹名
     */
    private String folderName;


    public FolderTreeNode(String folderName) {
        this.compressedfiles = new ArrayList<>();
        this.children = new ArrayList<>();
        this.folderName = folderName;
    }

    public void addChild(FolderTreeNode child) {
        children.add(child);
    }

    public void addCompressedFiles(HuffmanFileData huffmanFileData) {
        compressedfiles.add(huffmanFileData);
    }

    public List<FolderTreeNode> getChildren() {
        return children;
    }

    public List<HuffmanFileData> getCompressedFiles() {
        return compressedfiles;
    }

    public String getFolderName() {
        return folderName;
    }

    public static FolderTreeNode readFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
            return (FolderTreeNode) inputStream.readObject();
        }
    }
}
