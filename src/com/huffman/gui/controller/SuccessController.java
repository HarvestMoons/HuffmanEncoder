package com.huffman.gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SuccessController {

    @FXML
    public Button confirmButton;
    @FXML
    private Label summaryLabel;

    public void initialize() {
        summaryLabel.setLayoutX(200);
        summaryLabel.setLayoutY(130);

        confirmButton.setLayoutX(250);
        confirmButton.setLayoutY(330);
    }

    @FXML
    public void handleOkButtonClick(ActionEvent actionEvent) {
        // 获取当前窗口并关闭
        Stage stage = (Stage) summaryLabel.getScene().getWindow();
        stage.close();
    }

    public void setSummaryLabel(String summary) {
        summaryLabel.setText(summary);
    }
}
