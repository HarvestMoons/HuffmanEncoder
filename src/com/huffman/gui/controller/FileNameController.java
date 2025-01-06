package com.huffman.gui.controller;

import com.huffman.gui.GUI_Constants;
import com.huffman.gui.MainApp;
import com.huffman.gui.Operator;
import com.huffman.helper.FileHelper;
import com.huffman.userIO.OutputManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class FileNameController {
    @FXML
    private Label infoText;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField addressTextField;

    private int mode;

    private String verifiedInput;

    @FXML
    private void handleConfirm() {
        Stage stage = (Stage) addressTextField.getScene().getWindow();
        //此处不需要OutputManager.setCurrentStage(stage)，该窗口只记录用户输入的解压后文件名即退出，最终的结算界面仍然用address-stage
        String userInputString = addressTextField.getText();
        String specialCharactersRegex = "[/\\\\\"?<>:*|]";
        while (Pattern.compile(specialCharactersRegex).matcher(userInputString).find()) {
            if ("\\d".equals(userInputString)) {
                break;
            }
            MainApp.showErrorView(stage, "不可使用特殊字符 | < > * : ? \\ / \" 为文件命名!请重新输入！");
            addressTextField.clear();
        }
        verifiedInput = userInputString;
        stage.close();

    }

    @FXML
    public void handleCancel() {
        Operator.clear();
        Stage stage = (Stage) addressTextField.getScene().getWindow();
        stage.close();
    }

    public void setInfoText(String info) {
        infoText.setText(info);
    }

    public String getVerifiedInput() {
        return verifiedInput;
    }


}
