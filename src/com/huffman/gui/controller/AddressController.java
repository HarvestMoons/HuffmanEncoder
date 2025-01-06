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

public class AddressController {
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

        OutputManager.setCurrentStage(stage);
        String userInput = addressTextField.getText().replace("\"", "");
        if (!FileHelper.isFilePathValid(userInput)) {
            addressTextField.clear();
            return;
        }
        Operator.setCurrentFilePath(userInput);
        Operator.executeOperation();


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


}
