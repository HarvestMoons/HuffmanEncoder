package com.huffman.bitstream;

import com.huffman.userIO.OutputManager;

import java.io.*;

public class BitOutputStream implements AutoCloseable {

	/** 当前字节 */
	private int currentByte;
	/** 当前字节中已填充的比特位数 */
	private int numBitsFilled;

	private final BufferedOutputStream bos;

	public BitOutputStream(FileOutputStream fos) {

		bos = new BufferedOutputStream(fos);
		
		currentByte = 0;
		numBitsFilled = 0;
	}

	public void writeBits(String bits) {
		for (char bit : bits.toCharArray()) {
			try {
				writeBit(bit == '1');
			} catch (IOException e) {
				OutputManager.showErrorMsg(e.getMessage());
			}
		}
	}

	public void writeBit(boolean bit) throws IOException {
		if (numBitsFilled == 8) {
			//System.out.println("write "+currentByte);
			bos.write(currentByte);
			currentByte = 0;
			numBitsFilled = 0;
		}

		if (bit) {
			currentByte |= (1 << (7 - numBitsFilled));
		}

		numBitsFilled++;
	}
	
	public void flush() throws IOException {
		// Write the last byte (if any) and close the stream
		if (numBitsFilled > 0) {
			//System.out.println(currentByte);
			bos.write(currentByte);
			currentByte = 0;
			numBitsFilled = 0;
		}
		
	}


	@Override
	public void close() throws IOException {
		flush();
		bos.close();
	}
}