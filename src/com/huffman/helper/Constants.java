package com.huffman.helper;

public class Constants {

    // 私有构造函数，防止实例化
    private Constants() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**不同的用户输入对应的操作码*/
    public static final int ENCODE_OP=0;
    public static final int DECODE_OP=1;
    public static final int CHECK_OP=2;
    public static final int QUIT_OP=3;
    public static final int SECRET_MUSIC_OP =4;

    public static final int BUFFER_SIZE=4096;
    public static final int MAX_STRING_SIZE=10000000;

    /**默认的压缩文件后缀*/
    public static final String DEFAULT_ENCODE_NAME ="huffman";
    /**默认的解压缩文件后缀*/
    public static final String DEFAULT_DECODE_NAME ="(decode)";

    /**触发音乐播放的隐藏输入*/
    public static final String SECRET_INPUT_MUSIC ="music";
    /**可播放的音乐总数*/
    public static final int TOTAL_MUSIC_NUM =3;

    /**二进制文件的自定义拓展名*/
    public static final String BINARY_FILE_EXTENSION="CXYBIN";

}
