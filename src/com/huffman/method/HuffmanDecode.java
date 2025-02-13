package com.huffman.method;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;

import com.huffman.bitstream.BitInputStream;
import com.huffman.compressed_data.HuffmanFileData;
import com.huffman.helper.Constants;
import com.huffman.helper.FileHelper;
import com.huffman.helper.FolderHelper;
import com.huffman.myexception.UnknownObjectTypeException;
import com.huffman.userIO.OutputManager;
import com.huffman.treenode.FolderTreeNode;
import com.huffman.treenode.HuffmanTreeNode;

public class HuffmanDecode {
    /**
     * 压缩文件存放地址
     */
    private static String compressedFilePath;
    /**
     * 压缩文件父文件夹地址
     */
    private static String parentFilePath;
    /**
     * 读取的HuffmanFileData，准备解压缩文件
     */
    private static HuffmanFileData decodeHFileData;
    /**
     * 读取的FolderTreeNode，准备解压缩文件夹
     */
    private static FolderTreeNode folderTreeRootNode;
    /**
     * 压缩文件中存储的哈夫曼编码树
     */
    private static HuffmanTreeNode huffmanTree;
    /**
     * 解压缩后的文件地址
     */
    private static String decodeFilePath;
    /**
     * 等待用户输入所花费的总时长
     */
    private static long waitingForInputTime;
    /**
     * 全局的BitInputStream类，decode结束时关闭
     */
    private static BitInputStream bitInputStream;
    /**
     * 全局解压缩指示：是解压普通文件还是文件夹
     */
    private static boolean isDecodingFolder;

    private static long totalSize;
    private static long handledSize;

    public static void decode(String compressedFilePath) {
        HuffmanDecode.compressedFilePath = compressedFilePath;
        HuffmanDecode.parentFilePath = FileHelper.getParentDirectory(compressedFilePath);
        waitingForInputTime = 0;
        try (FileInputStream fis = new FileInputStream(compressedFilePath);
             ObjectInputStream inputStream = new ObjectInputStream(fis)) {
            bitInputStream = new BitInputStream(fis);
            OutputManager.outputInConsoleModeOnly("开始读取压缩文件...");
            Object object = inputStream.readObject();
            // 解压普通文件
            // 注意，下面这种模式匹配（Pattern Matching）功能是java16引入的
            if (object instanceof HuffmanFileData huffmanFileData) {
                isDecodingFolder = false;
                decodeHFileData = huffmanFileData;
                decodeFile();
            }
            // 解压文件夹
            // 注意，下面这种模式匹配（Pattern Matching）功能是java16引入的
            else if (object instanceof FolderTreeNode fTreeNode) {
                isDecodingFolder = true;
                folderTreeRootNode = fTreeNode;
                decodeFolder();
            } else {
                throw new UnknownObjectTypeException("读取对象时出错: 文件中含有未知的类型");
            }
        } catch (FileNotFoundException e) {
            OutputManager.showErrorMsg("文件未找到" + e.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            if (e instanceof StreamCorruptedException) {
                OutputManager.showErrorMsg("解压缩过程已终止\n" + "文件: " + compressedFilePath + "不可用此程序解压!");
            } else {
                OutputManager.showErrorMsg("读取文件对象时发生错误：" + e.getMessage());
            }
        } catch (UnknownObjectTypeException e) {
            OutputManager.showErrorMsg(e.getMessage());
        } finally {
            bitInputStream.close();
        }

    }

    private static void decodeFolder() {
        long startTime = System.currentTimeMillis();
        calcTotalSize(folderTreeRootNode);
        File compressedFile = new File(compressedFilePath);
        String decodeRootFolderPath = compressedFile.getParentFile().getAbsolutePath() + "\\" + folderTreeRootNode.getFolderName();
        try {
            if (isCoverFile(decodeRootFolderPath)) {
                //如果删除原文件夹的过程失败，直接退出
                if (!FolderHelper.deleteDirectory(decodeRootFolderPath)) {
                    return;
                }
                OutputManager.outputInConsoleModeOnly("开始解压缩：");
                createFolderStructure(folderTreeRootNode, compressedFile.getParentFile());
                OutputManager.outputInConsoleModeOnly("");
                decodeMsgPrinter(startTime);
            }
        } catch (IOException e) {
            //todo:调整e位置
            OutputManager.showErrorMsg("解压文件夹出错：" + e.getMessage());
            //如果以上解压文件夹的过程出错，应该删除被创建的根文件夹，这里不会误删除原有的根文件夹
            FolderHelper.deleteDirectory(decodeRootFolderPath);
        }finally {
            totalSize = 0;
            handledSize = 0;
        }

    }

    private static void createFolderStructure(FolderTreeNode node, File parentFolder) throws IOException {
        File currentFolder;
        String folderName = node.getFolderName();
        currentFolder = new File(parentFolder, folderName);
        if (!currentFolder.mkdirs()) {
            throw new IOException("创建文件夹错误,文件夹路径：" + currentFolder.getAbsolutePath());
        }
        // 还原当前文件夹中的文件
        for (HuffmanFileData huffmanFileData : node.getCompressedFiles()) {
            //更新全局的HuffmanFileData实例
            decodeHFileData = huffmanFileData;
            decodeFilePath = currentFolder + "\\" + FileHelper.getFileNameWithExtension(huffmanFileData.getFileName());
            //若为空文件，创建空文件后即可返回
            if (isDecodingEmptyFile()) {
                continue;
            }
            //获取哈夫曼树
            huffmanTree = huffmanFileData.getHuffmanTree();
            //对于每一个不同的文件，创建不同的OutputStream
            //不及时关闭资源，则一次运行中无法连续覆盖
            try (FileOutputStream fos = new FileOutputStream(decodeFilePath);
                 BufferedOutputStream bos = new BufferedOutputStream(fos)) {
                //准备工作完成后，开始解压缩
                writeCompressedDataToFile(bos, decodeHFileData.getTotalBytes());
            }
            handledSize+=huffmanFileData.getTotalBytes();
            OutputManager.showProgress(handledSize, totalSize);
        }
        // 递归创建子文件夹
        for (FolderTreeNode child : node.getChildren()) {
            createFolderStructure(child, currentFolder);
        }
    }

    private static void decodeFile() throws IOException {
        long startTime = System.currentTimeMillis();
        OutputManager.outputInConsoleModeOnly("开始解压缩...");
        // 若压缩文件为空，解压文件也为空
        if (isDecodingEmptyFile() && !isDecodingFolder) {
            decodeMsgPrinter(startTime);
            return;
        }
        huffmanTree = decodeHFileData.getHuffmanTree();
        // 获取用户指定的解压文件名
        decodeFilePath = generateDecodeFilePath();

        // 用户不希望覆盖已有的解压缩文件，直接返回
        if (!isCoverFile(decodeFilePath)) {
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(decodeFilePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            writeCompressedDataToFile(bos, decodeHFileData.getTotalBytes());
        }


        decodeMsgPrinter(startTime);
    }

    private static boolean isDecodingEmptyFile() {
        if (decodeHFileData.getTotalBytes() == 0) {
            // 如果在解压单个空文件，用户有命名自由，对于文件夹中的空文件，不可以重命名
            if (!isDecodingFolder) {
                decodeFilePath = generateDecodeFilePath();
            }
            FileHelper.createEmptyFile(decodeFilePath);

            return true;
        }
        return false;
    }

    private static void writeCompressedDataToFile(BufferedOutputStream bos, long totalBytes) throws IOException {
        HuffmanTreeNode nowHuffmanTreeNode = huffmanTree;
        long bytenums = 0;
        while (true) {
            int bit = bitInputStream.readBit();
            // 遇到1向右走，遇到0向左走
            if (!huffmanTree.isLeaf()) {
                if (bit == 1) {
                    nowHuffmanTreeNode = nowHuffmanTreeNode.getRight();
                } else if (bit == 0) {
                    nowHuffmanTreeNode = nowHuffmanTreeNode.getLeft();
                }
                // 到文件末尾
                else if (bit == -1) {
                    break;
                } else {
                    OutputManager.showErrorMsg("错误的比特读入:" + bit);
                }
            }
            // 如果是叶节点，将对应的字节值存储到字节数组中
            if (nowHuffmanTreeNode.isLeaf()) {
                bytenums++;
                bos.write(nowHuffmanTreeNode.getData());
                nowHuffmanTreeNode = huffmanTree;
                // 若当前还原的字节数与原文件原有的字节数一致，退出
                if (bytenums == totalBytes) {
                    bitInputStream.clear();
                    break;
                }
            }
        }
        bos.flush();
    }

    private static void decodeMsgPrinter(long startTime) {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime - waitingForInputTime;
        String decodeMsg = """              
                加密通话已破解
                解压缩时间: %.2f 秒
                """.formatted((double) executionTime / 1000);
        OutputManager.showSuccessMsg(decodeMsg);
    }

    //todo:测试添加的GUI
    private static String generateDecodeFilePath() {
        long startTime = System.currentTimeMillis();
        String decodeFileName;
        String userInputString=OutputManager.getDecodedString();
        //以默认解压缩名解压缩
        if ("\\d".equals(userInputString)||userInputString==null) {
            String originalFileName = FileHelper.getFileNameWithExtension(decodeHFileData.getFileName());
            // 查找最后一个'.'的位置
            int lastDotIndex = originalFileName.lastIndexOf('.');

            // 如果没有找到'.'，直接添加默认解压名
            if (lastDotIndex == -1) {
                decodeFileName = originalFileName + Constants.DEFAULT_DECODE_NAME;
            } else {
                // 在最后一个'.'前添加默认解压名
                String namePart = originalFileName.substring(0, lastDotIndex);
                String extensionPart = originalFileName.substring(lastDotIndex);
                decodeFileName = namePart + Constants.DEFAULT_DECODE_NAME + extensionPart;
            }
        }

        //自定义解压缩文件名
        else {
            String originalExtension = FileHelper.getFileExtension(decodeHFileData.getFileName());
            decodeFileName = userInputString + "." + originalExtension;
        }
        //父文件夹路径+解压后的文件（夹）名->解压文件（夹）路径
        decodeFilePath = parentFilePath + "\\" + decodeFileName;
        long endTime = System.currentTimeMillis();
        waitingForInputTime += endTime - startTime;
        OutputManager.outputInConsoleModeOnly("已获取解压文件名，解压继续进行中...");
        return decodeFilePath;
    }

    //检测文件是否存在，存在则让用户做出选择，用户做选择的时间不包括在程序运行时间内
    private static boolean isCoverFile(String decodeFilePath) {
        long startTime2 = System.currentTimeMillis();
        if (!FileHelper.isCoverExistingFile(decodeFilePath)) {
            OutputManager.outputInConsoleModeOnly("解压缩过程已终止");
            return false;
        }
        waitingForInputTime += System.currentTimeMillis() - startTime2;
        return true;
    }

    public static FolderTreeNode getFolderTree(File file) {
        try {
            folderTreeRootNode = FolderTreeNode.readFromFile(file.getAbsolutePath());
            return folderTreeRootNode;
        } catch (IOException | ClassNotFoundException e) {
            if (e instanceof StreamCorruptedException) {
                OutputManager.showErrorMsg("预览过程已终止\n" + "该文件不能使用此程序预览!");
            } else {
                OutputManager.showErrorMsg("读取文件对象时发生错误：" + e.getMessage());
            }
            return null;
        } catch (ClassCastException e) {
            OutputManager.showErrorMsg("这不是一个文件夹的压缩文件，不可预览！");
            return null;
        }
    }

    //todo:move
    private static void calcTotalSize(FolderTreeNode node){
        for (HuffmanFileData huffmanFileData : node.getCompressedFiles()) {
            totalSize+=huffmanFileData.getTotalBytes();
        }
        for (FolderTreeNode child : node.getChildren()) {
            calcTotalSize(child);
        }
    }
}
