package com.huffman.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.huffman.userIO.OutputManager;

public class FileHelper {

    // 私有构造函数，防止实例化
    private FileHelper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String changeFileExtension(String filePath, String newExtension) {

        // 找到文件名的位置
        int lastSeparatorIndex = filePath.lastIndexOf("\\");
        int lastDotIndex = filePath.lastIndexOf(".");
        String path = filePath.substring(0, lastSeparatorIndex + 1);

        //如果有点（.），这里理解为文件，比如myImg.png->myImg.newExtension
        if (lastDotIndex != -1) {
            // 提取文件路径和文件名

            String fileName = filePath.substring(lastSeparatorIndex + 1, lastDotIndex);
            // 拼接新的文件路径
            return path + fileName + "." + newExtension;
        }
        //如果没有点，这里理解为文件夹，或者没有后缀的文件，比如myImages->myImages(newExtension)
        else {
            return filePath + "(" + newExtension + ")";
        }
    }

    /**
     * 获取文件后缀，若无后缀返回空字符串
     */
    public static String getFileExtension(String filePath) {
        String fileExtension;
        int lastDotIndex = filePath.lastIndexOf(".");
        // 获取后缀
        if (lastDotIndex != -1) {
            fileExtension = filePath.substring(lastDotIndex + 1);
        } else {
            fileExtension = "";
        }
        return fileExtension;
    }

    // 文件是否为空
    public static boolean isFileEmpty(String filePath) {
        File file = new File(filePath);
        return file.length() == 0;
    }

    // 创建空文件
    public static void createEmptyFile(String outputFilePath) {
        try {
            Path path = Path.of(outputFilePath);
            if (Files.exists(path)) {
                deleteFile(outputFilePath);
            }
            Files.createFile(path);
        } catch (IOException e) {
            OutputManager.showErrorMsg("创建空文件时发生错误：" + e.getMessage());
        }
    }

    /**
     * 计算压缩率,将待打印的信息以字符串形式返回
     */
    public static String calcCompressibility(String beforeFileString, String afterFilesString) {
        File beforeFile = new File(beforeFileString);
        Path beforePath = Path.of(beforeFileString);
        Path afterPath = Path.of(afterFilesString);
        long beforeFileSize;
        long afterFileSize;
        try {
            if (beforeFile.isDirectory()) {
                beforeFileSize = FolderHelper.calculateFolderSizeRecursive(beforeFile);
            } else {
                beforeFileSize = Files.size(beforePath);
            }
            afterFileSize = Files.size(afterPath);
            if (beforeFileSize == 0) {
                return "压缩率: NAN% (待压缩文件为空文件)";
            } else {
                double printCompressibility = (1 - (double) afterFileSize / (double) beforeFileSize);
                // 取压缩率后两位
                String formattedRate = String.format("%.2f", printCompressibility * 100);

                return "压缩率: " + formattedRate + "%";
            }
        } catch (IOException e) {
            OutputManager.showErrorMsg("计算压缩率时发生错误：" + e.getMessage());
        }
        return "计算压缩率时发生错误";
    }


    public static String getFileNameWithExtension(String originalFilePath) {
        int lastSlashIndex = originalFilePath.lastIndexOf("\\");
        return originalFilePath.substring(lastSlashIndex + 1);
    }

    /**
     * 删除文件,若文件不存在，直接返回
     */
    public static void deleteFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return;
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            OutputManager.showErrorMsg("删除文件时发生错误：" + e.getMessage());
        }
    }

    /**
     * 检测文件是否已经存在，并由用户决定是否覆盖之
     */
    public static boolean isCoverExistingFile(String filePath) {
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            return OutputManager.overwriteConfirm(filePath);
        } else {
            return true;
        }
    }

    /**
     * 获取父文件夹路径，用于解压缩
     */
    public static String getParentDirectory(String compressedFilePath) {
        File file = new File(compressedFilePath);
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            return parentFile.getAbsolutePath();
        } else {
            // 如果文件没有父目录，返回null
            return null;
        }
    }

    /**
     * 如果输入的文件路径为：空、过长、包含非法字符、无对应文件，该方法返回false
     */
    public static boolean isFilePathValid(String filePath) {
        if (filePath.isEmpty()) {
            OutputManager.showErrorMsg("X_X  输入不能为空！");
            return false;
        }
        if (filePath.length() > Constants.MAX_PATH_STRING_LENGTH) {
            OutputManager.showErrorMsg("X_X 文件路径过长！");
            return false;
        }
        if (Constants.ILLEGAL_CHAR_PATTERN_WINDOWS.matcher(filePath).find()) {
            OutputManager.showErrorMsg("X_X  文件路径包含非法字符！");
            return false;
        }
        Path path = Paths.get(filePath);
        boolean isFileExists = Files.exists(path);
        // 文件是否存在？
        if (!isFileExists) {
            OutputManager.showErrorMsg("X_X  找不到对应的文件！");
            return false;
        }
        return true;
    }

}
