package com.huffman.compressed_data;

import java.io.Serializable;
import java.util.HashMap;

import com.huffman.treenode.HuffmanTreeNode;

public class HuffmanFileData implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 哈夫曼编码树
     */
    private HuffmanTreeNode huffmanTree;
    /**
     * 原文件全路径
     */
    private String fileName;
    /**
     * 原文件字节数
     */
    private long totalBytes;
    /**
     * 用于存储每个叶子节点的哈夫曼编码
     */
    private HashMap<Byte, String> huffmanCodes;

    public HuffmanFileData(String fileName, HuffmanTreeNode huffmanTree, long totalBytes, HashMap<Byte, String> huffmanCodes) {
        this.fileName = fileName;
        this.huffmanTree = huffmanTree;
        this.totalBytes = totalBytes;
        this.huffmanCodes = huffmanCodes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public HuffmanTreeNode getHuffmanTree() {
        return huffmanTree;
    }

    public HashMap<Byte, String> getHuffmanCodes() {
        return huffmanCodes;
    }


}