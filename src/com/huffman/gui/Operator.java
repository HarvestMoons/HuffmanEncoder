package com.huffman.gui;

import com.huffman.helper.Constants;
import com.huffman.helper.FolderHelper;
import com.huffman.method.HuffmanDecode;
import com.huffman.method.HuffmanEncode;

import java.io.File;

public class Operator {
    private static int currentOperation = 0;
    private static String currentFilePath = null;

    public static void setCurrentOperation(int currentOperation) {
        Operator.currentOperation = currentOperation;
    }

    public static void setCurrentFilePath(String currentFilePath) {
        Operator.currentFilePath = currentFilePath;
    }

    public static void executeOperation() {
        if (currentFilePath == null) {
            return;
        }
        switch (currentOperation) {
            case Constants.ENCODE_OP:
                // 哈夫曼压缩
                HuffmanEncode.encode(currentFilePath);
                break;
            case Constants.DECODE_OP:
                // 哈夫曼解压缩
                HuffmanDecode.decode(currentFilePath);
                break;
            case Constants.CHECK_OP:
                // 预览压缩包结构
                File folder = new File(currentFilePath);
                FolderHelper.printFolderTree(HuffmanDecode.getFolderTree(folder), 0);
                break;
            default:
                //todo:用log
                System.out.println("Invalid operation");
        }
    }

    public static void clear() {
        currentOperation = 0;
        currentFilePath = null;
    }

}
