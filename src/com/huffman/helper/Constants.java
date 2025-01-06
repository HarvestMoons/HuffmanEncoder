package com.huffman.helper;

import java.util.regex.Pattern;

public class Constants {
    /**
     * Windows 非法字符正则表达式
     */
    public static final Pattern ILLEGAL_CHAR_PATTERN_WINDOWS = Pattern.compile("[<>\"/|?*]");

    /**
     * 不同的用户输入对应的操作码
     */
    public static final int ENCODE_OP = 0;
    public static final int DECODE_OP = 1;
    public static final int CHECK_OP = 2;
    public static final int QUIT_OP = 3;
    public static final int SECRET_MUSIC_OP = 4;

    public static final int BUFFER_SIZE = 4096;
    public static final int MAX_STRING_SIZE = 10000000;

    /**
     * 默认的压缩文件后缀
     */
    public static final String DEFAULT_ENCODE_NAME = "huffman";
    /**
     * 默认的解压缩文件后缀
     */
    public static final String DEFAULT_DECODE_NAME = "(decode)";

    /**
     * 隐藏输入
     */
    public static final String SECRET_INPUT_MUSIC = "music";
    public static final String SECRET_INPUT_ORANGE = "nanagoan arornanag oan aroronoa, " +
            "aeorranr aeaaggor ranna, nro annnna naanrna gree anrae, " +
            "enagae eano, aegro roona eea eeoaaor g";

    /**
     * 可播放的音乐总数
     */
    public static final int TOTAL_MUSIC_NUM = 3;

    /**
     * 输出的模式
     */
    public static final int OUTPUT_NOT_INIT = 0;
    public static final int GUI_OUTPUT = 1;
    public static final int CONSOLE_OUTPUT = 2;

    /**
     * 最大文件路径长度
     */
    public static final int MAX_PATH_STRING_LENGTH = 250;

}
