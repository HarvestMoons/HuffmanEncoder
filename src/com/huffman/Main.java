package com.huffman;

import java.io.File;
import java.util.Scanner;

import com.huffman.helper.Constants;
import com.huffman.helper.FileHelper;
import com.huffman.helper.FolderHelper;
import com.huffman.method.HuffmanDecode;
import com.huffman.method.HuffmanEncode;
import com.huffman.music.MusicPlayer;
import com.huffman.myexception.UndefinedOpcodeException;
import com.huffman.userIO.OutputManager;

public class Main {

    public static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        OutputManager.setOutputMode(Constants.CONSOLE_OUTPUT);
        try {
            boolean isContinue = true;
            while (isContinue) {
                int operation = userInputOperation();
                String filePath;
                switch (operation) {
                    case Constants.ENCODE_OP:
                        // 哈夫曼压缩
                        filePath = userInputPath(operation);
                        if (filePath != null) {
                            HuffmanEncode.encode(filePath);
                        }
                        break;
                    case Constants.DECODE_OP:
                        // 哈夫曼解压缩
                        filePath = userInputPath(operation);
                        if (filePath != null) {
                            HuffmanDecode.decode(filePath);
                        }
                        break;
                    case Constants.CHECK_OP:
                        // 预览压缩包结构
                        filePath = userInputPath(operation);
                        if (filePath != null) {
                            File folder = new File(filePath);
                            FolderHelper.printFolderTree(HuffmanDecode.getFolderTree(folder), 0);
                        }
                        break;
                    case Constants.SECRET_MUSIC_OP:
                        MusicPlayer.playMusic();
                        break;
                    case Constants.QUIT_OP:
                        //退出
                        isContinue = false;
                        break;
                    default:
                        throw new UndefinedOpcodeException(operation);
                }
            }
            //睡眠3s,此后程序退出
            Thread.sleep(3000);
            SCANNER.close();
        } catch (UndefinedOpcodeException | InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    private static int userInputOperation() {

        while (true) {
            System.out.println("请输入操作（压缩文件-'e'，解压文件-'d'，预览文件夹压缩包内容-'c'，退出-'q'）：");
            String userInput = SCANNER.nextLine().toLowerCase();

            switch (userInput) {
                case "e":
                    System.out.println("准备执行压缩文件操作");
                    return Constants.ENCODE_OP;
                case "d":
                    System.out.println("准备执行解压文件操作");
                    return Constants.DECODE_OP;
                case "c":
                    System.out.println("准备预览文件夹压缩包结构");
                    return Constants.CHECK_OP;
                case "q":
                    System.out.println("感谢使用!程序即将终止运行。");
                    return Constants.QUIT_OP;
                case Constants.SECRET_INPUT_MUSIC:
                    System.out.println("你触发了一个秘密输入！听听音乐！");
                    return Constants.SECRET_MUSIC_OP;
                default:
                    System.out.println("输入错误，请重新输入。");
            }
        }

    }

    private static String userInputPath(int operation) throws UndefinedOpcodeException {

        switch (operation) {
            case Constants.ENCODE_OP:
                System.out.println("请输入待压缩文件的绝对地址(压缩文件将存放在同一个目录下):");
                break;
            case Constants.DECODE_OP:
                System.out.println("请输入待解压文件的绝对地址(解压文件将存放在同一个目录下):");
                break;
            case Constants.CHECK_OP:
                System.out.println("请输入待预览文件的绝对地址:");
                break;
            case Constants.QUIT_OP, Constants.SECRET_MUSIC_OP:
                return null;
            default:
                throw new UndefinedOpcodeException(operation);
        }
        String filePath = SCANNER.nextLine().replace("\"", "");
        if (FileHelper.isFilePathValid(filePath)) {
            return filePath;
        }
        return null;
    }

}
