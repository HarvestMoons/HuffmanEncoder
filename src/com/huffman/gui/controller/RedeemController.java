package com.huffman.gui.controller;

import com.huffman.gui.MainApp;
import com.huffman.helper.Constants;
import com.huffman.music.MusicPlayer;
import com.huffman.userIO.OutputManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RedeemController {

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    @FXML
    private TextField redeemCodeField;

    @FXML
    private void handleCancel()  {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleConfirm() {

        Stage stage = (Stage) confirmButton.getScene().getWindow();

        OutputManager.setCurrentStage(stage);

        // 获取输入的兑换码
        String redeemCode = redeemCodeField.getText();

        // 如果输入内容大于10个字符，截断为10个字符
        //if (redeemCode.length() > 10) {
        //    redeemCode = redeemCode.substring(0, 10);
        //}

        switch (redeemCode) {
            case Constants.SECRET_INPUT_MUSIC:
                MusicPlayer.playMusic();
                break;
            case Constants.SECRET_INPUT_ORANGE:
                //TODO:
                System.out.println("TEST");
                break;
            default:
                MainApp.showErrorView(stage, "X_X 无效的兑换码");
        }
    }

}
