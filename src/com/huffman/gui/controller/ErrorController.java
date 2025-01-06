package com.huffman.gui.controller;

import com.huffman.gui.MainApp;
import com.huffman.userIO.OutputManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class ErrorController {

    @FXML
    private Label errorLabel;

    @FXML
    private void handleOkButtonClick() {
        // 获取当前窗口并关闭
        Stage stage = (Stage) errorLabel.getScene().getWindow();
        stage.close();
    }

    public void setErrorMessage(String message) {
        errorLabel.setText(message);
    }
}
