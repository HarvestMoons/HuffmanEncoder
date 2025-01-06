package com.huffman.music;

import com.huffman.helper.Constants;
import com.huffman.userIO.OutputManager;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Random;

public class MusicPlayer {
    private static Clip audioClip;
    private static String musicName;

    private static final Random RAND = new Random();

    private static String getRandomMusicPath() {
        // Generate random integers
        int randomInt = RAND.nextInt(Constants.TOTAL_MUSIC_NUM);

        return switch (randomInt) {
            case 0 -> {
                musicName = "It's Gonna Be Me (2000)";
                yield "超级男孩《It's Gonna Be Me (It's Gonna Be May)》.wav";
            }
            case 1 -> {
                musicName = "Never (1984)";
                yield "Footloose Moving Pictures - Never (1984).wav";
            }
            case 2 -> {
                musicName = "These Dreams (1986)";
                yield "红心乐队  Heart - These Dreams 1986年单曲.wav";
            }
            default -> {
                System.out.println("错误的随机曲目序号！序号：" + randomInt);
                yield null;
            }
        };

    }

    public static void playMusic() {
        String musicFile = getRandomMusicPath();
        if (musicFile == null) {
            return;
        }
        // 如果当前有音乐在播放，先停止当前音乐
        if (audioClip != null && audioClip.isRunning()) {
            audioClip.stop();
            audioClip.close();
        }
        try (InputStream audioSrc = MusicPlayer.class.getResourceAsStream("/" + musicFile)) {
            // 从JAR包中的资源加载音频文件
            if (audioSrc == null) {
                System.out.println("音频文件未找到");
                return;
            }
            // 添加缓存
            try (InputStream bufferedIn = new BufferedInputStream(audioSrc);
                 AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn)) {

                AudioFormat format = audioStream.getFormat();
                DataLine.Info info = new DataLine.Info(Clip.class, format);
                audioClip = (Clip) AudioSystem.getLine(info);
                audioClip.open(audioStream);
                System.out.println("正在播放：" + musicName);
                audioClip.start();
            }
        } catch (Exception e) {
            OutputManager.showErrorMsg("播放音乐时出错："+e.getMessage());
        }
    }
}
