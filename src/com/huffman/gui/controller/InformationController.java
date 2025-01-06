package com.huffman.gui.controller;

import com.huffman.gui.GUI_Constants;
import com.huffman.gui.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class InformationController {

    @FXML
    private Label infoLabel;

    private int infoType;

    public void setInfoType(int infoType) {
        this.infoType = infoType;
        updateInfoLabel();
    }

    private void updateInfoLabel() {
        // 初始化时的逻辑
        switch (infoType) {
            case GUI_Constants.DEV_INFO:
                infoLabel.setText(getDeveloperInfo());
                break;
            case GUI_Constants.USAGE_INFO:
                infoLabel.setText(getUsageInfo());
                break;
            default:
                MainApp.showErrorView((Stage) infoLabel.getScene().getWindow(), "X_X 加载文本时出错");
        }
    }

    @FXML
    private void handleClose() {
        // 关闭当前窗口
        Stage stage = (Stage) infoLabel.getScene().getWindow();
        stage.close();
    }

    private String getDeveloperInfo() {
        return """
               Huffman Compression Tool（哈夫曼压缩工具）
               版本号：7.0.0
               发布日期：2024年8月16日
               开发者：程序员大蜜蜂（Archer Marshall）
               个人主页：https://github.com/HarvestMoons/HarvestMoons
               合作机会：如果您有兴趣为此项目做出贡献或与我们合作，请通过GitHub上的联系方式联系开发者。大蜜蜂欢迎任何形式的改进建议和新功能开发。
               开源协议：本项目基于MIT许可证开源，您可以自由地复制、修改、发布，但需要保留原始版权声明。
               """;
    }

    private String getUsageInfo() {
        return """
                压缩文件：
                    1.点击主页面“压缩”键
                    2.输入待操作文件（夹）的绝对地址
                    3.等待压缩完成，即可在同一文件夹下找到压缩后的文件
                解压缩文件：
                    1.点击主页面“解压缩”键
                    2.输入待操作文件的绝对地址（待操作文件只能是本程序产生的压缩包）
                    3.等待压缩完成，即可在同一文件夹下找到压缩后的文件
                预览压缩包文件：
                    1.点击主页面“预览压缩包内容”键
                    2.输入待操作文件的绝对地址（待操作文件只能是本程序产生的文件夹的压缩包）
                    3.在新界面上可直接预览压缩前的文件结构
                如果产生了您无法解决的问题，有可能是程序bug，请及时和程序员大蜜蜂反馈
                """;
    }


}
