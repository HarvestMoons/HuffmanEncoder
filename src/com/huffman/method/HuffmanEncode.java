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
import com.huffman.helper.FolderHelper;
import com.huffman.userIO.OutputManager;
import com.huffman.treenode.FolderTreeNode;
import com.huffman.treenode.HuffmanTreeNode;


public class HuffmanEncode {
    /**
     * 创建一个 Map 用于存储字符和对应的频率
     */
    private static Map<Byte, Integer> frequencyMap;
    /**
     * 哈夫曼树的根结点
     */
    private static HuffmanTreeNode huffmanTree;
    /**
     * 用于存储每个叶子节点的哈夫曼编码
     */
    private static HashMap<Byte, String> huffmanCodes;
    /**
     * 压缩出的HuffmanFileData，准备写入压缩文件
     */
    private static HuffmanFileData compressedHFileData;
    /**
     * 原文件全路径
     */
    private static String inputFilePath;
    /**
     * 压缩文件存放路径
     */
    private static String compressedFilePath;
    /**
     * 当前是否在执行文件夹压缩
     */
    private static boolean isCompressFolder;
    /**
     * 文件夹树的根节点
     */
    private static FolderTreeNode folderTree;
    /**
     * 当前文件中的总字节数
     */
    private static long totalBytes;
    /**
     * 压缩文件时的全局 FileOutputStream,压缩结束时关闭
     */
    private static FileOutputStream fos;
    /**
     * 压缩文件时的全局 ObjectOutputStream,压缩结束时关闭
     */
    private static ObjectOutputStream oos;
    /**
     * 压缩文件时的全局 ObjectOutputStream,压缩结束时关闭
     */
    private static BitOutputStream bitOutputStream;
    /**
     * 压缩文件夹时，文件夹的大小
     */
    private static long folderSize;

    private static long handledFileSizeBFT;
    private static long handledFileSizeWFD;

    // 私有构造函数，防止实例化
    private HuffmanEncode() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void encode(String inputFilePath) {
        HuffmanEncode.inputFilePath = inputFilePath;
        //替换用户输入的待压缩的文件（夹）的文件后缀，作为存放压缩后文件（夹）的地址
        //替换的后缀构成为：原后缀+默认后缀，以防止原后缀与默认后缀相同，覆盖原文件的问题
        String originalExtension = FileHelper.getFileExtension(inputFilePath);
        HuffmanEncode.compressedFilePath = FileHelper.changeFileExtension(inputFilePath,
                originalExtension + Constants.DEFAULT_ENCODE_NAME);
        huffmanCodes = new HashMap<>();
        frequencyMap = new HashMap<>();

        // 用户不希望覆盖已有文件,直接返回
        if (!FileHelper.isCoverExistingFile(compressedFilePath)) {
            OutputManager.outputInConsoleModeOnly("压缩过程已终止");
            return;
        }

        File file = new File(inputFilePath);
        isCompressFolder = file.isDirectory();
        if (!isCompressFolder) {
            //如果文件已经是压缩包，拒绝用户的压缩请求
            if (isEncodedFile(inputFilePath)) {
                OutputManager.showErrorMsg("此文件是一个由本程序生成的压缩包,请勿使用本程序再次压缩！");
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
            folderSize = FolderHelper.getFolderSize(file.toPath());
            fos = new FileOutputStream(compressedFilePath);
            oos = new ObjectOutputStream(fos);
            bitOutputStream = new BitOutputStream(fos);
            OutputManager.outputInConsoleModeOnly("开始建立文件夹树结构:");
            buildFolderTree(file, true);
            OutputManager.outputInConsoleModeOnly("");
            oos.writeObject(folderTree);
            OutputManager.outputInConsoleModeOnly("开始写入压缩内容:");
            writeFolderData(folderTree);
            OutputManager.outputInConsoleModeOnly("");
            bitOutputStream.close();
            encodeMsgPrinter(startTime, compressedFilePath);
        } catch (IOException e) {
            OutputManager.showErrorMsg("encodeFolder error:" + e.getMessage());
        }finally {
            folderSize=0;
            handledFileSizeBFT=0;
            handledFileSizeWFD=0;
        }

    }

    // 建立文件夹树结构
    private static FolderTreeNode buildFolderTree(File folder, boolean isRoot) {
        FolderTreeNode node = new FolderTreeNode(folder.getName());
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
                    handledFileSizeBFT +=file.length();

                    OutputManager.showProgress(handledFileSizeBFT,folderSize);
                }
            }
        }
        return node;
    }

    //
    private static void writeFolderData(FolderTreeNode node) {
        for (HuffmanFileData fileData : node.getCompressedFiles()) {
            String originalFileFullName = fileData.getFileName();
            huffmanCodes = fileData.getHuffmanCodes();
            writeOriginalDataToFile(originalFileFullName);
            handledFileSizeWFD+=new File(fileData.getFileName()).length();
            OutputManager.showProgress(handledFileSizeWFD,folderSize);
        }

        // 递归遍历子文件夹
        for (FolderTreeNode child : node.getChildren()) {
            writeFolderData(child);
        }
    }

    private static void encodeFile(String originalFileFullName) {
        try {
            long startTime = System.currentTimeMillis();

            // 若不是在解压文件夹，新建全局的OutputStream
            if (!isCompressFolder) {
                fos = new FileOutputStream(compressedFilePath);
                oos = new ObjectOutputStream(fos);
                bitOutputStream = new BitOutputStream(fos);
            }
            // 空文件压缩判断与处理
            if (isEncodingEmptyFile(originalFileFullName, oos)) {
                if (!isCompressFolder) {
                    fos.close();
                }
                return;
            }
            // 统计字符频率
            huffmanFrequencyCounter(originalFileFullName);
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
            writeOriginalDataToFile(originalFileFullName);
            // 打印相关信息
            encodeMsgPrinter(startTime, compressedFilePath);
            fos.close();

        } catch (IOException e) {
            OutputManager.showErrorMsg("encodeFile error: " + e.getMessage());
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

    // 使用 Lambda 表达式创建比较器
    private static final Comparator<HuffmanTreeNode> CP =
            Comparator.comparingInt(HuffmanTreeNode::getFrequency);

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
            HuffmanTreeNode parent = new HuffmanTreeNode((byte) 0,
                    left.getFrequency() + right.getFrequency(), false);
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
    private static void writeOriginalDataToFile(String filePath) {

        try (FileInputStream inputFileStream = new FileInputStream(filePath)) {
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
            OutputManager.showErrorMsg(e.getMessage());
        }
    }

    private static boolean isEncodingEmptyFile(String originalFileFullName,
                                               ObjectOutputStream oos) throws IOException {
        Path path = Paths.get(originalFileFullName);
        if (Files.size(path) != 0) {
            return false;
        } else {
            long startTime = System.currentTimeMillis();
            compressedHFileData = new HuffmanFileData(originalFileFullName, null, 0, null);
            if (!isCompressFolder) {
                oos.writeObject(compressedHFileData);
                encodeMsgPrinter(startTime, compressedFilePath);
            }
            return true;
        }

    }

    /**
     * 打印压缩率、压缩用时等信息
     */
    private static void encodeMsgPrinter(long startTime, String compressedFilePath) {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        String encodeMsg = """
                文件已压缩加密
                %s
                压缩时间: %.2f 秒
                """.formatted(FileHelper.calcCompressibility(inputFilePath, compressedFilePath),
                (double) executionTime / 1000);
        OutputManager.showSuccessMsg(encodeMsg);
    }

    private static boolean isEncodedFile(String undeterminedFilePath) {
        try (FileInputStream fis = new FileInputStream(undeterminedFilePath);
             ObjectInputStream inputStream = new ObjectInputStream(fis)) {
            Object object = inputStream.readObject();
            return object instanceof HuffmanFileData || object instanceof FolderTreeNode;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }
}