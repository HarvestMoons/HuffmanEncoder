package com.huffman.gui.controller;

import com.huffman.gui.GUI_Constants;
import com.huffman.gui.MainApp;
import com.huffman.gui.Operator;
import com.huffman.helper.Constants;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {


    @FXML
    private Button compressButton;
    @FXML
    private Button decompressButton;
    @FXML
    private Button previewButton;
    @FXML
    private Button helpButton;

    @FXML
    private void handleCompress() {
        // 压缩操作
        handleOperation(Constants.ENCODE_OP);

    }

    @FXML
    private void handleDecompress() {
        // 解压操作
        handleOperation(Constants.DECODE_OP);
    }

    @FXML
    private void handlePreview() {
        // 预览操作
        handleOperation(Constants.CHECK_OP);
    }

    @FXML
    private void handleHelp() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/gui/help-view.fxml"));
        Parent root = fxmlLoader.load();
        // 创建场景并设置 CSS 样式
        Scene scene = helpButton.getScene();
        Stage stage = (Stage) scene.getWindow();
        stage.setTitle("帮助");
        stage.setScene(new Scene(root, GUI_Constants.HELP_SCENE_WIDTH, GUI_Constants.HELP_SCENE_HEIGHT));
    }

    /**
     * 通用的操作处理方法，减少重复代码
     */
    private void handleOperation(int operation) {
        Operator.setCurrentOperation(operation);
        MainApp.showAddressView((Stage) compressButton.getScene().getWindow(), "请输入文件的绝对地址（完整地址）:",
                GUI_Constants.GET_FILE_PATH);
    }


}

