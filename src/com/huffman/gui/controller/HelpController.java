package com.huffman.gui.controller;

import com.huffman.gui.GUI_Constants;
import com.huffman.gui.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HelpController {

    public Button infoButton;
    public Button usageButton;
    public Button secretButton;
    public Button returnButton;


    @FXML
    private void handleDeveloperInfo() throws IOException {
        handleInfoButton(GUI_Constants.DEV_INFO);
    }

    @FXML
    private void handleUsageInstructions() throws IOException {
        handleInfoButton(GUI_Constants.USAGE_INFO);
    }


    private void handleInfoButton(int infoType) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/gui/information-view.fxml"));
        Parent root = fxmlLoader.load();

        // 获取控制器
        InformationController informationController = fxmlLoader.getController();
        informationController.setInfoType(infoType);

        // 创建新窗口并显示
        Stage infoStage = new Stage();
        infoStage.setScene(new Scene(root, GUI_Constants.INFO_SCENE_WIDTH, GUI_Constants.INFO_SCENE_HEIGHT));
        infoStage.setTitle("信息");
        infoStage.show();
    }


    @FXML
    public void handleSecret() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/gui/redeem-view.fxml"));
            Parent root = fxmlLoader.load();

            // 创建一个新的 Stage
            Stage newStage = new Stage();

            // 创建新的场景并设置 CSS 样式
            Scene scene = new Scene(root, GUI_Constants.REDEEM_SCENE_WIDTH, GUI_Constants.REDEEM_SCENE_HEIGHT);
            newStage.setTitle("神秘打野点");
            newStage.setScene(scene);

            // 设置模态窗口，阻止用户与原窗口进行交互
            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.initOwner(returnButton.getScene().getWindow());

            newStage.showAndWait();  // showAndWait 会阻塞，直到该窗口关闭
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void handleReturn() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/gui/main-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) returnButton.getScene().getWindow();
            // 创建main场景并设置 CSS 样式
            Scene scene = new Scene(root, GUI_Constants.MAIN_SCENE_WIDTH, GUI_Constants.MAIN_SCENE_HEIGHT);
            stage.setTitle("大蜜蜂加密通话工具");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
