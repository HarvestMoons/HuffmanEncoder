package com.huffman.gui;

import com.huffman.gui.controller.*;
import com.huffman.helper.Constants;
import com.huffman.userIO.OutputManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {


    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/gui/main-view.fxml"));
            Parent root = fxmlLoader.load();
            // 创建main场景并设置 CSS 样式
            Scene scene = new Scene(root, GUI_Constants.MAIN_SCENE_WIDTH, GUI_Constants.MAIN_SCENE_HEIGHT);
            primaryStage.setTitle("大蜜蜂加密通话工具");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            showErrorView(primaryStage,e.getMessage());
        }

    }

    public static void showErrorView(Stage stage, String errorMessage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/gui/error-view.fxml"));
            Parent root = fxmlLoader.load();

            // 获取错误窗口控制器
            ErrorController errorController = fxmlLoader.getController();
            errorController.setErrorMessage(errorMessage);
            stage.setScene(new Scene(root, GUI_Constants.ERROR_SCENE_WIDTH, GUI_Constants.ERROR_SCENE_HEIGHT));
        } catch (Exception e) {
            showErrorView(stage,e.getMessage());
        }
    }

    public static void showSuccessView(Stage stage, String summaryLabel) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/gui/success-view.fxml"));
            Parent root = fxmlLoader.load();
            SuccessController successController = fxmlLoader.getController();
            successController.setSummaryLabel(summaryLabel);
            stage.setScene(new Scene(root, GUI_Constants.SUCCESS_SCENE_WIDTH, GUI_Constants.SUCCESS_SCENE_HEIGHT));
        } catch (IOException e) {
            showErrorView(stage,e.getMessage());
        }
    }

    public static boolean showOverwriteConfirmView(Stage parentStage, String overwriteInfo) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/gui/overwrite-view.fxml"));
            Parent root = fxmlLoader.load();
            OverwriteController overwriteController = fxmlLoader.getController();
            overwriteController.setMessageLabel(overwriteInfo);

            // 创建一个新的Stage用于模态弹窗
            Stage modalStage = new Stage();
            modalStage.setTitle("覆盖确认");

            // 设置模态窗口，阻止与其他窗口交互
            modalStage.initModality(Modality.APPLICATION_MODAL);
            // 使弹窗与主窗口相关联
            modalStage.initOwner(parentStage);

            // 设置弹窗场景
            modalStage.setScene(new Scene(root, GUI_Constants.OVERWRITE_SCENE_WIDTH, GUI_Constants.OVERWRITE_SCENE_HEIGHT));

            // 显示模态弹窗，并等待其关闭
            modalStage.showAndWait();

            // 返回用户选择的是否覆盖结果
            return overwriteController.getIsCoverFile();
        } catch (IOException e) {
            showErrorView(parentStage,e.getMessage());
            return false;
        }
    }

    public static String showFileNameView(Stage parentStage, String info,int mode) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/gui/file-name-view.fxml"));
            Parent root = fxmlLoader.load();
            FileNameController fileNameController= fxmlLoader.getController();
            // 创建一个新的Stage用于模态弹窗
            Stage modalStage = new Stage();
            modalStage.setTitle("解压文件名输入");
            fileNameController.setInfoText(info);

            // 设置模态窗口，阻止与其他窗口交互
            modalStage.initModality(Modality.APPLICATION_MODAL);
            // 使弹窗与主窗口相关联
            modalStage.initOwner(parentStage);

            // 设置弹窗场景
            modalStage.setScene(new Scene(root, GUI_Constants.FILE_NAME_SCENE_WIDTH, GUI_Constants.FILE_NAME_SCENE_HEIGHT));

            // 显示模态弹窗，并等待其关闭
            modalStage.showAndWait();

            return fileNameController.getVerifiedInput();

        } catch (IOException e) {
            showErrorView(parentStage,e.getMessage());
            return null;
        }
    }

    public static void showAddressView(Stage parentStage, String info,int mode) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/gui/address-view.fxml"));
            Parent root = fxmlLoader.load();
            AddressController addressController= fxmlLoader.getController();
            // 创建一个新的Stage用于模态弹窗
            Stage modalStage = new Stage();
            modalStage.setTitle("文件地址输入");
            addressController.setInfoText(info);

            // 设置模态窗口，阻止与其他窗口交互
            modalStage.initModality(Modality.APPLICATION_MODAL);
            // 使弹窗与主窗口相关联
            modalStage.initOwner(parentStage);

            // 设置弹窗场景
            modalStage.setScene(new Scene(root, GUI_Constants.ADDRESS_SCENE_WIDTH, GUI_Constants.ADDRESS_SCENE_HEIGHT));

            // 显示模态弹窗，并等待其关闭
            modalStage.showAndWait();

        } catch (IOException e) {
            showErrorView(parentStage,e.getMessage());
        }
    }


    public static void main(String[] args) {
        OutputManager.setOutputMode(Constants.GUI_OUTPUT);
        launch();
    }
}
