package com.huffman.method;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import com.huffman.bitstream.BitOutputStream;
import com.huffman.compressed_data.HuffmanFileData;
import com.huffman.helper.Constants;
import com.huffman.helper.FileHelper;
import com.huffman.treenode.FolderTreeNode;
import com.huffman.treenode.HuffmanTreeNode;

public class HuffmanEncode {
	/** 创建一个 Map 用于存储字符和对应的频率 */
	private static Map<Byte, Integer> frequencyMap;
	/** 哈夫曼树的根结点 */
	private static HuffmanTreeNode huffmanTree;
	/** 用于存储每个叶子节点的哈夫曼编码 */
	private static HashMap<Byte, String> huffmanCodes;
	/** 压缩出的HuffmanFileData，准备写入压缩文件 */
	private static HuffmanFileData compressedHFileData;
	/** 原文件全路径 */
	private static String inputFilePath;
	/** 压缩文件存放路径 */
	private static String compressedFilePath;
	/** 当前是否在执行文件夹压缩 */
	private static boolean isCompressFolder;
	/**文件夹树的根节点*/
	private static FolderTreeNode folderTree;
	/** 当前文件中的总字节数 */
	private static long totalBytes;
	/** 压缩文件时的全局 FileOutputStream,压缩结束时关闭 */
	private static FileOutputStream fos;
	/** 压缩文件时的全局 ObjectOutputStream,压缩结束时关闭 */
	private static ObjectOutputStream oos;
	/** 压缩文件时的全局 ObjectOutputStream,压缩结束时关闭 */
	private static BitOutputStream bitOutputStream;

	//TODO:处理无后缀文件名时有错
	//TODO:处理.huffman后缀的，不要用.huffman覆盖它

	// 私有构造函数，防止实例化
	private HuffmanEncode() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static void encode(String inputFilePath) {
		HuffmanEncode.inputFilePath = inputFilePath;
		//替换用户输入的待压缩的文件（夹）的文件后缀，作为存放压缩后文件（夹）的地址
		//替换的后缀构成为：原后缀+默认后缀，以防止原后缀与默认后缀相同，覆盖原文件的问题
		String originalExtension=FileHelper.getFileExtension(inputFilePath);
		HuffmanEncode.compressedFilePath = FileHelper.changeFileExtension(inputFilePath,
				originalExtension+Constants.DEFAULT_ENCODE_NAME);
		huffmanCodes = new HashMap<>();
		frequencyMap = new HashMap<>();

		// 用户不希望覆盖已有文件,直接返回
		if (!FileHelper.isCoverExistingFile(compressedFilePath)) {
			System.out.println("压缩过程已终止");
			return;
		}

		File file = new File(inputFilePath);
		isCompressFolder = file.isDirectory();
		if (!isCompressFolder) {
			//如果文件已经是压缩包，拒绝用户的压缩请求
			if(isEncodedFile(inputFilePath)){
				System.out.println("此文件是一个由本程序生成的压缩包,请勿使用本程序再次压缩！");
				return;
			}
			encodeFile(inputFilePath);
		} else {
			encodeFolder(file);
		}

		try {
			fos.close();
			oos.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			//若文件压缩过程出错，删除可能产生的不完整的压缩包
			FileHelper.deleteFile(compressedFilePath);
		}

	}

	private static void encodeFolder(File file) {
		try {
			long startTime = System.currentTimeMillis();
			fos = new FileOutputStream(compressedFilePath);
			oos = new ObjectOutputStream(fos);
			bitOutputStream = new BitOutputStream(fos);
			buildFolderTree(file, true);
			oos.writeObject(folderTree);
			writeFolderData(folderTree);
			bitOutputStream.close();
			encodeMsgPrinter(startTime, compressedFilePath);
		} catch (Exception e) {
			System.err.println("encodeFolder error:"+e.getMessage());
		}

	}

	// 建立文件夹树结构
	private static FolderTreeNode buildFolderTree(File folder, boolean isRoot) {
		FolderTreeNode node = new FolderTreeNode(folder);
		if (isRoot) {
			folderTree = node;
		}
		File[] files = folder.listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					// 是文件夹，建立新节点
					FolderTreeNode childNode = buildFolderTree(file, false);
					node.addChild(childNode);
				} else {
					// 不是文件夹，将对其进行压缩，文件信息加入父文件夹节点的CompressedFiles中
					String originalFileFullName = file.getAbsolutePath();
					// 只创建compressedHFileData写入
					encodeFile(originalFileFullName);
					node.addCompressedFiles(compressedHFileData);
					frequencyMap = new HashMap<>();
					huffmanCodes = new HashMap<>();
				}
			}
		}
		return node;
	}

	//
	private static void writeFolderData(FolderTreeNode node) {
		try {
			for (HuffmanFileData fileData : node.getCompressedFiles()) {
				String originalFileFullName = fileData.getFileName();
				String binaryFilePath = originalFileFullName+Constants.BINARY_FILE_EXTENSION;
				huffmanCodes = fileData.getHuffmanCodes();
				writeOriginalDataToFile(binaryFilePath);
				// 删除二进制文件（过河拆桥）
				FileHelper.deleteFile(binaryFilePath);
			}

			// 递归遍历子文件夹
			for (FolderTreeNode child : node.getChildren()) {
				writeFolderData(child);
			}
		} catch (Exception e) {
			System.err.println("压缩文件夹时出错： " + e.getMessage());
		}
	}

	private static void encodeFile(String originalFileFullName) {
		try {// 计时器
			long startTime = System.currentTimeMillis();

			// 输入文件对应的二进制文件存放地址(...\\myFile.bin)
			String binaryFilePath = originalFileFullName+Constants.BINARY_FILE_EXTENSION;

			// 若不是在解压文件夹，新建全局的OutputStream
			if (!isCompressFolder) {
				fos = new FileOutputStream(compressedFilePath);
				oos = new ObjectOutputStream(fos);
				bitOutputStream = new BitOutputStream(fos);
			}
			// 把输入文件转化为二进制文件
			convert2BinaryFile(originalFileFullName, binaryFilePath);
			// 空文件压缩判断与处理
			if (isEncodingEmptyFile(binaryFilePath, originalFileFullName, oos)) {
				if (!isCompressFolder) {
					fos.close();
				}
				return;
			}
			// 统计字符频率
			huffmanFrequencyCounter(binaryFilePath);
			// 构建哈夫曼树
			createHuffmanTree();
			// 构建哈夫曼编码
			buildHuffmanCodes(huffmanTree, "");
			compressedHFileData = new HuffmanFileData(originalFileFullName, huffmanTree, totalBytes, huffmanCodes);
			// 若在解压文件夹，构建完huffmanHFileData就可以返回了
			if (isCompressFolder) {
				return;
			}
			// 若在解压文件，先把该对象写入文件头
			oos.writeObject(compressedHFileData);
			// 使用哈夫曼编码压缩文件，把压缩数据写入压缩文件地址
			writeOriginalDataToFile(binaryFilePath);
			// 删除二进制文件（过河拆桥）
			FileHelper.deleteFile(binaryFilePath);
			// 打印相关信息
			encodeMsgPrinter(startTime, compressedFilePath);
			fos.close();

		} catch (IOException e) {
			System.err.println("encodeFile error: " + e.getMessage());
		}

	}

	// 统计字符频率
	private static void huffmanFrequencyCounter(String filePath) throws IOException {
		try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
			int byteRead;
			byte[] bytes = new byte[2048];
			// 逐字节读取文件内容
			totalBytes = 0;
			while ((byteRead = fileInputStream.read(bytes)) != -1) {
				// 统计字符频率
				totalBytes += byteRead;
				for (int i = 0; i < byteRead; i++) {
					frequencyMap.put(bytes[i], frequencyMap.getOrDefault(bytes[i], 0) + 1);
				}
			}
		}
	}

	// 创建一个比较器
	private static final Comparator<HuffmanTreeNode> CP = new Comparator<HuffmanTreeNode>() {
		@Override
		public int compare(HuffmanTreeNode o1, HuffmanTreeNode o2) {
			// 按照frequency升序
			return o1.getFrequency() - o2.getFrequency();
		}
	};

	// 构建哈夫曼树
	private static void createHuffmanTree() {
		// 使用优先队列，并提供比较器
		PriorityQueue<HuffmanTreeNode> queue = new PriorityQueue<>(frequencyMap.size(), CP);
		// 将 Map 中的元素放入优先队列
		for (Map.Entry<Byte, Integer> entry : frequencyMap.entrySet()) {
			HuffmanTreeNode node = new HuffmanTreeNode(entry.getKey(), entry.getValue(), true);
			queue.offer(node);
		}
		while (queue.size() > 1) {
			// 取出最小的两个元素，作为左右节点(Frequency: 左<=右)
			HuffmanTreeNode left = queue.poll();
			HuffmanTreeNode right = queue.poll();

			// 合成为新节点，节点的左右子节点为上面取出的左右节点
			assert right != null;
			HuffmanTreeNode parent = new HuffmanTreeNode((byte) 0, left.getFrequency() + right.getFrequency(), false);
			parent.setLeft(left);
			parent.setRight(right);

			// 把这个新节点加入优先队列，直至队列中仅剩下一个元素(根节点)
			queue.offer(parent);
		}
		huffmanTree = queue.peek();
	}

	// 构建哈夫曼编码
	private static void buildHuffmanCodes(HuffmanTreeNode root, String code) {
		if (root == null) {
			return;
		}

		// 如果是叶子节点，将其哈夫曼编码存入HashMap
		if (root.isLeaf()) {
			if (code.isEmpty()) {
				code = "1";
			}
			huffmanCodes.put(root.getData(), code);
		}

		// 递归处理左子树
		buildHuffmanCodes(root.getLeft(), code + "0");
		// 递归处理右子树
		buildHuffmanCodes(root.getRight(), code + "1");
	}

	// 第二次遍历输入文件的每个字节,转化为huffmanCode组成的01串,再由bitOutputStream转化为字节写入文件
	private static void writeOriginalDataToFile(String binaryFilePath) {

		try (FileInputStream inputFileStream = new FileInputStream(binaryFilePath)) {
			// 根据需要调整缓冲区大小
			byte[] buffer = new byte[Constants.BUFFER_SIZE];
			int bytesRead;
			StringBuilder compressedData = new StringBuilder();
			while ((bytesRead = inputFileStream.read(buffer)) != -1) {
				// 获取哈夫曼编码
				for (int i = 0; i < bytesRead; i++) {
					String huffmanCode = huffmanCodes.get(buffer[i]);
					compressedData.append(huffmanCode);
				}

				// 足够大的时候，就可以编辑为bytes并写入bufferInputStream了
				if (compressedData.length() > Constants.MAX_STRING_SIZE && compressedData.length() % 8 == 0) {
					bitOutputStream.writeBits(compressedData.toString());
					compressedData.setLength(0);
				}
			}
			bitOutputStream.writeBits(compressedData.toString());
			if (!isCompressFolder) {
				bitOutputStream.close();
			} else {
				bitOutputStream.flush();
			}
			// 向待压缩的compressedHFileData的compressedData中写入压缩的内容（byte）
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	private static boolean isEncodingEmptyFile(String binaryFilePath, String orginalFileFullName,
											   ObjectOutputStream oos) throws IOException {
		Path path = Paths.get(binaryFilePath);
		if (Files.size(path) != 0) {
			return false;
		} else {
			long startTime = System.currentTimeMillis();
			compressedHFileData = new HuffmanFileData(orginalFileFullName, null, 0, null);
			if (!isCompressFolder) {
				oos.writeObject(compressedHFileData);
				encodeMsgPrinter(startTime, compressedFilePath);
			}
			return true;
		}

	}

	// 打印压缩信息
	private static void encodeMsgPrinter(long startTime, String compressedFilePath) {
		long endTime = System.currentTimeMillis();
		long executionTime = endTime - startTime;
		System.out.println("----------------------------------------");
		System.out.println("文件已压缩加密               ");
		// 打印压缩率、压缩用时等信息
		FileHelper.printCompressibility(inputFilePath, compressedFilePath);
		System.out.println("压缩时间: " + (double) executionTime / 1000 + " 秒");
		System.out.println("----------------------------------------");
	}

	private static boolean isEncodedFile(String unjudgedFilePath)  {
		try (FileInputStream fis = new FileInputStream(unjudgedFilePath);
			 ObjectInputStream inputStream = new ObjectInputStream(fis)) {
			Object object = inputStream.readObject();
			return object instanceof HuffmanFileData || object instanceof FolderTreeNode;
		} catch (IOException | ClassNotFoundException e) {
			return false;
		}
	}

	/**读取inputFilePath的文件，将二进制内容写入outputFilePath*/
	public static void convert2BinaryFile(String inputFilePath,String outputFilePath) {
		// 判断文件路径是否正确
		Path path = Paths.get(inputFilePath);
		boolean isFileExists = Files.exists(path);
		if(!isFileExists) {
			System.err.println("转换为二进制文件时，遇到不存在的文件路径："+inputFilePath);
			return;
		}
		try {
			File inputFile = new File(inputFilePath);
			File outputFile = new File(outputFilePath);
			try (FileInputStream inputStream = new FileInputStream(inputFile);
				 FileOutputStream outputStream = new FileOutputStream(outputFile)) {
				byte[] buffer = new byte[2048];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}
		} catch (IOException e) {
			System.err.println("读取文件时发生错误：" + e.getMessage());
		}
	}
}