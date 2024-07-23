package com.huffman.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import com.huffman.compressed_data.HuffmanFileData;
import com.huffman.treenode.FolderTreeNode;


public class FolderHelper {

    // 私有构造函数，防止实例化
    private FolderHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    // 创建根文件夹
    public static File createRootFolder(String outputFolderPath) {
        File rootFolder = new File(outputFolderPath);
        if (!rootFolder.mkdirs()) {
            System.out.println("创建根文件夹失败");
            return null;
        }
        return rootFolder;
    }

    public static long calculateFolderSizeRecursive(File folder) {
        long size = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是子文件夹，递归计算其大小
                    size += calculateFolderSizeRecursive(file);
                } else {
                    // 如果是文件，累加其大小
                    size += file.length();
                }
            }
        }
        return size;
    }

    public static void printFolderTree(FolderTreeNode node, int depth) {
        if (node == null) {
            return;
        }
        System.out.println(getDepthIndent(depth) + node.getFolder().getName() + "（文件夹）");
        //打印该文件夹之下的所有子文件
        for (HuffmanFileData huffmanFileData : node.getCompressedFiles()) {
            System.out.println(getDepthIndent(depth + 1) + FileHelper.getFileNameWithExtension(huffmanFileData.getFileName()));
        }
        //递归调用该方法，打印子文件夹信息
        for (FolderTreeNode child : node.getChildren()) {
            printFolderTree(child, depth + 1);
        }
    }

    private static String getDepthIndent(int depth) {
        // 两个空格表示一层深度
        return "--".repeat(Math.max(0, depth));
    }

    //传入一个文件夹路径（字符串），如果对应的文件夹存在，删除它；否则直接返回
    public static void deleteDirectory(String directoryPath) {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            return;
        }
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("删除文件夹时出错："+e.getMessage());
        }

    }


}
