package com.huffman.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import com.huffman.Main;

public class FileHelper {

	// 私有构造函数，防止实例化
	private FileHelper() {
		throw new UnsupportedOperationException("Utility class");
	}
	
	//若要压缩.huffman文件？
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

	/** 获取文件后缀，若无后缀返回空字符串*/
	public static String getFileExtension(String filePath) {
		String fileExtension;
		int lastDotIndex = filePath.lastIndexOf(".");
		// 获取后缀
		if (lastDotIndex != -1) {
            fileExtension = filePath.substring(lastDotIndex + 1);
        } else {
            fileExtension ="";
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
			System.err.println("创建空文件时发生错误：" + e.getMessage());
		}
	}

	/** 计算、打印压缩率 */
	public static void printCompressibility(String beforeFileString, String afterFilesString) {
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
                System.out.println("压缩率: NAN% (待压缩文件为空文件)");
            } else {
				double printCompressibility = (1 - (double) afterFileSize / (double) beforeFileSize);
				// 取压缩率后两位
				String formattedRate = String.format("%.2f", printCompressibility * 100);

				System.out.println("压缩率: " + formattedRate + "%");
			}
		} catch (IOException e) {
			System.err.println("计算压缩率时发生错误：" + e.getMessage());
		}
	}
	
	public static  String getFileNameWithExtension(String originalFilePath) {
		int lastSlashIndex = originalFilePath.lastIndexOf("\\");
		return originalFilePath.substring(lastSlashIndex+1);
	}

	/** 删除文件,若文件不存在，直接返回 */
	public static void deleteFile(String filePath) {
		Path path = Paths.get(filePath);
		File file=new File(filePath);
		if(!file.exists()){
			return;
		}
		try {
			Files.delete(path);
		} catch (IOException e) {
			System.err.println("删除文件时发生错误：" + e.getMessage());
		}
	}

	/** 检测文件是否已经存在，并由用户决定是否覆盖之*/
	public static boolean isCoverExistingFile(String filePath) {
		Path path = Paths.get(filePath);
		if (Files.exists(path)) {
			Scanner scanner = Main.SCANNER;
			System.out.println("文件(可能是过时的):" + filePath + "已存在！");
			System.out.println("是否覆盖此文件?(Y/N)");
			// 将输入转为小写(大小写不限)
			String userInput = scanner.next().toUpperCase();
			char option = userInput.charAt(0);
			while (userInput.length() > 1 || (option != 'Y' && option != 'N')) {
				System.out.println("请输入Y或N!(大小写不限)");
				userInput = scanner.next().toLowerCase();
				option = userInput.charAt(0);
			}
			scanner.nextLine();
            return option == 'Y';
		} else {
			return true;
		}

	}

	//获取父文件夹路径，用于解压缩
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

}
