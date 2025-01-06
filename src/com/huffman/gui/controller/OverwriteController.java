package com.huffman.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class OverwriteController {

    @FXML
    private Label messageLabel;

    private boolean isCoverFile=false;

    public void handleYesAction() {
        isCoverFile=true;
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.close();
    }

    public void handleNoAction() {
        isCoverFile=false;
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.close();
    }

    public void setMessageLabel(String message) {
        messageLabel.setText(message);
    }

    public boolean getIsCoverFile() {
        return isCoverFile;
    }
}
