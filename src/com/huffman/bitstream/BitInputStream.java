package com.huffman.bitstream;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class BitInputStream {
	
	private final BufferedInputStream bis;
	/** 当前字节*/
	private int currentByte;
	/**当前字节中剩余未读的比特位数*/
	private int numBitsFilled;

	public BitInputStream(FileInputStream fis) {
		bis=new BufferedInputStream(fis);
		currentByte = 0;
		numBitsFilled = 0;
	}

	public int readBit() throws IOException {
		if (numBitsFilled == 0) {
			currentByte = bis.read();
			//System.out.println( "currentByte read:"+currentByte);
			if (currentByte == -1) {
				return -1;
			}
			numBitsFilled = 8;
		}

		int bit = ((currentByte >> (numBitsFilled - 1)) & 1);
		numBitsFilled--;
		return bit;
	}

	public void clear() {
		numBitsFilled = 0;
		currentByte = 0;
	}

}
