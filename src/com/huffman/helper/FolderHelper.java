package com.huffman.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import com.huffman.compressed_data.HuffmanFileData;
import com.huffman.userIO.OutputManager;
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
        System.out.println(getDepthIndent(depth) + node.getFolderName() + "（文件夹）");
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

    /**
     * 传入一个文件夹路径（字符串），如果对应的文件夹存在，删除它；否则直接返回。返回值表示是否成功删除(文件不存在也算成功删除)。
     */
    public static boolean deleteDirectory(String directoryPath) {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            return true;
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
            return true;
        } catch (AccessDeniedException e) {
            OutputManager.showErrorMsg("删除文件夹时出错：本程序没有足够权限删除原文件夹(" + directoryPath + ")，请尝试手动删除。");
            return false;
        } catch (IOException e) {
            OutputManager.showErrorMsg("删除文件夹时出错：" + e.getMessage());
            return false;
        }
    }

    /**
     * 计算并返回文件夹大小
     */
    public static long getFolderSize(Path folderPath) throws IOException {
        return Files.walk(folderPath)
                .filter(Files::isRegularFile)
                .mapToLong(p -> p.toFile().length())
                .sum();
    }
}
