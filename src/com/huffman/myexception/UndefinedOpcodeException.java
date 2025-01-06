package com.huffman.myexception;

public class UndefinedOpcodeException extends Exception {
    public UndefinedOpcodeException(int opcode) {
        super("未知的操作码：" + opcode);
    }
}
