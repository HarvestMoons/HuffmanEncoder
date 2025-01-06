package com.huffman.userIO;

import com.huffman.Main;
import com.huffman.gui.GUI_Constants;
import com.huffman.gui.MainApp;
import com.huffman.helper.Constants;
import javafx.stage.Stage;

import java.util.Scanner;
import java.util.regex.Pattern;

public class OutputManager {
    private static int outputMode = Constants.OUTPUT_NOT_INIT;
    private static Stage currentStage = null;

    /**
     * 接收并展示出错信息，有GUI和控制台两种模式
     */
    public static void showErrorMsg(String errorMsg) {
        switch (outputMode) {
            case Constants.GUI_OUTPUT:
                MainApp.showErrorView(currentStage, errorMsg);
                break;
            case Constants.CONSOLE_OUTPUT:
                System.err.println(errorMsg);
                break;
            default:
                System.err.println("X_X 未知的输出模式");
        }
    }

    /**
     * 接收并展示结算信息，有GUI和控制台两种模式
     */
    public static void showSuccessMsg(String successMsg) {
        switch (outputMode) {
            case Constants.GUI_OUTPUT:
                MainApp.showSuccessView(currentStage, successMsg);
                break;
            case Constants.CONSOLE_OUTPUT:
                System.out.println("----------------------------------------");
                System.out.println(successMsg);
                System.out.println("----------------------------------------");
                break;
            default:
                showErrorMsg("X_X 未知的输出模式");
        }
    }

    public static boolean overwriteConfirm(String filePath) {
        switch (outputMode) {
            case Constants.GUI_OUTPUT:
                String overwriteInfo = String.format("""
                        文件(可能是过时的): %s 已存在！
                        是否覆盖此文件?
                        """, filePath);
                return MainApp.showOverwriteConfirmView(currentStage, overwriteInfo);
            case Constants.CONSOLE_OUTPUT:
                Scanner scanner = Main.SCANNER;
                System.out.println("文件(可能是过时的):" + filePath + "已存在！");
                System.out.println("是否覆盖此文件?(Y/N)");
                // 将输入转为小写(大小写不限)
                String userInput = scanner.next().toUpperCase();
                char option = userInput.charAt(0);
                while (userInput.length() > 1 || (option != 'Y' && option != 'N')) {
                    System.out.println("请输入Y或N!(大小写不限)");
                    userInput = scanner.next().toUpperCase();
                    option = userInput.charAt(0);
                }
                scanner.nextLine();
                return option == 'Y';
            default:
                showErrorMsg("X_X 未知的输出模式");
                return false;
        }
    }

    /**
     * 进度条显示方法
     */
    public static void showProgress(long current, long total) {
        switch (outputMode) {
            case Constants.GUI_OUTPUT:
                //TODO
                break;
            case Constants.CONSOLE_OUTPUT:
                showConsoleProgress(current, total);
                break;
            default:
                showErrorMsg("X_X 未知的输出模式");
        }
    }

    public static void showConsoleProgress(long current, long total) {
        // 进度条长度
        int progressBarLength = 50;
        int completed = (int) ((double) current / total * progressBarLength);

        StringBuilder progressBar = new StringBuilder();
        // '\r' 将光标移动到行首，覆盖前一次的输出
        progressBar.append("\r[");

        for (int i = 0; i < progressBarLength; i++) {
            if (i < completed) {
                // 已完成部分
                progressBar.append("#");
            } else {
                // 未完成部分
                progressBar.append("-");
            }
        }
        progressBar.append("] ");
        int percentage = (int) ((double) current / total * 100);
        progressBar.append(String.format("%d%%", percentage));

        // 输出进度条到命令行
        System.out.print(progressBar);
    }


    public static void outputInConsoleModeOnly(String message) {
        if (outputMode == Constants.CONSOLE_OUTPUT) {
            System.out.println(message);
        }
    }

    public static String getDecodedString() {
        String decodePromote="请输入解压缩文件名(输入\\d将以原文件名解压缩):";
        switch (outputMode) {
            case Constants.GUI_OUTPUT:
                return MainApp.showFileNameView(currentStage,decodePromote
                        , GUI_Constants.GET_DECODED_FILE_NAME);
                case Constants.CONSOLE_OUTPUT:
                    Scanner scanner = Main.SCANNER;
                    System.out.println(decodePromote);
                    String userInputString = scanner.nextLine();
                    String specialCharactersRegex = "[/\\\\\"?<>:*|]";
                    while (Pattern.compile(specialCharactersRegex).matcher(userInputString).find()) {
                        if ("\\d".equals(userInputString)) {
                            break;
                        }

                        System.out.println("不可使用特殊字符 | < > * : ? \\ / \" 为文件命名!请重新输入！");
                        userInputString = scanner.nextLine();
                    }
                    return userInputString;
            default:
                showErrorMsg("X_X 未知的输出模式");
                return null;
        }
    }

    public static void setOutputMode(int outputMode) {
        OutputManager.outputMode = outputMode;
    }

    public static void setCurrentStage(Stage currentStage) {
        OutputManager.currentStage = currentStage;
    }
}
