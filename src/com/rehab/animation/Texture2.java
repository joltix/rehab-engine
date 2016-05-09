package com.rehab.animation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;


public class Texture2 {

	private ByteBuffer mImage;
	private ByteBuffer mImageBuffer;
	private IntBuffer mW;
	private IntBuffer mH;
	private IntBuffer mC;

	/**
	 * Texture Class that takes in a filename and convert to Byte Buffer
	 * 
	 * @param filename
	 */
	public Texture2(String filename) {

		try {
			mImageBuffer = readFile(filename);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		loadTexture(filename);
	}

	

	/**
	 * Get Width method for the texture
	 * 
	 * @return
	 */
	public int getWidth() {
		return mW.get(0);
	}

	/**
	 * Get Height for the texture
	 * 
	 * @return
	 */
	public int getHeight() {
		return mH.get(0);
	}

	/**
	 * Get the transparency of the texture
	 * 
	 * @return
	 */
	public int getComp() {
		return mC.get(0);
	}

	/**
	 * Helper method to load the image file into a texture to then be drawn on
	 * the Canvas
	 * 
	 * @param filename
	 */
	private void loadTexture(String filename) {

		mW = BufferUtils.createIntBuffer(1);
		mH = BufferUtils.createIntBuffer(1);
		mC = BufferUtils.createIntBuffer(1);

		mImage = STBImage.stbi_load_from_memory(mImageBuffer, mW, mH, mC, 0);
		// if can not open the file then throw an error
		if (mImage == null) {
			throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
		}
		
	}

	/**
	 * Getter for the ByteBuffer from an image we want from
	 * 
	 * @return ByteBuffer of the image we want to load
	 */
	public ByteBuffer getByteBuffer() {	return mImage;	}
	
/**
 * Helper method that reads in a file specifying a filename 
 * 
 * @param resource
 * @return
 * ByteBuffer
 * @throws IOException
 */
	private ByteBuffer readFile(String resource) throws IOException{
	    File file = new File(resource);	   

		FileInputStream fis = new FileInputStream(file);
		FileChannel fc = fis.getChannel();

		ByteBuffer buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);

		while (fc.read(buffer) != -1)	;

		fis.close();
		fc.close();
		buffer.flip();

		return buffer;
	}
	
}
