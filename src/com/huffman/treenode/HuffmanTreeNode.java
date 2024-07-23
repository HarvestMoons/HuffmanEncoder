package com.huffman.treenode;

import java.io.Serializable;

public class HuffmanTreeNode implements Comparable<HuffmanTreeNode>,Serializable {

	private static final long serialVersionUID = 1L;
	private byte data;
	private int frequency;
	private HuffmanTreeNode left, right;
	private boolean isRealNode;

	public HuffmanTreeNode(byte data, int frequency, boolean isRealNode) {
		this.data = data;
		this.frequency = frequency;
		this.isRealNode = isRealNode;
		left = right = null;
	}

	@Override
	public int compareTo(HuffmanTreeNode node) {
		return this.frequency - node.frequency;
	}

	public void setLeft(HuffmanTreeNode left) {
		this.left = left;
	}

	public void setRight(HuffmanTreeNode right) {
		this.right = right;
	}

	public byte getData() {
		return data;
	}

	public int getFrequency() {
		return frequency;
	}

	public HuffmanTreeNode getLeft() {
		return left;
	}

	public HuffmanTreeNode getRight() {
		return right;
	}

	public boolean isLeaf() {
		return isRealNode;
	}

}