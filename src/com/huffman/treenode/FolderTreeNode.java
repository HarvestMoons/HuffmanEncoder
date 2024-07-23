package com.huffman.treenode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.huffman.compressed_data.HuffmanFileData;

//由文件夹组成树
public class FolderTreeNode implements Serializable{

	private static final long serialVersionUID = 1L;
		/**本文件夹自身*/
		private File folder;	    
		/**该文件夹中包含的文件夹，是其子节点*/
	    private List<FolderTreeNode> children;
	    /**该文件夹下的文件对应的HuffmanFileData类的实例的集合*/
	    private List<HuffmanFileData> compressedfiles;
	    
	    public FolderTreeNode(File folder) {
	    	this.folder=folder;
	        this.compressedfiles=new ArrayList<>();
	        this.children = new ArrayList<>();
	    }

	    public void addChild(FolderTreeNode child) {
	        children.add(child);
	    }

	    public void addCompressedFiles(HuffmanFileData huffmanFileData) {
	        compressedfiles.add(huffmanFileData);
	    }

	    public List<FolderTreeNode> getChildren() {
	        return children;
	    }

	    public List<HuffmanFileData> getCompressedFiles() {
	        return compressedfiles;
	    }
	    
	    public File getFolder() {
			return folder;
		}
	    
		public static void writeToFile(FolderTreeNode folderTreeNode, String filePath) {
			try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
				outputStream.writeObject(folderTreeNode);			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	    
		public static FolderTreeNode readFromFile(String filePath) throws IOException, ClassNotFoundException {
			try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath))) {
                return (FolderTreeNode) inputStream.readObject();
			}
		}
}
