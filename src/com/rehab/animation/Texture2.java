package com.rehab.animation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;

import javafx.scene.image.Image;

public class Texture2 {
	private int mWidth;
	private int mHeight;
	private int mComp;

	ByteBuffer mImage;
	ByteBuffer mImageBuffer;
	IntBuffer mW;
	IntBuffer mH;
	IntBuffer mC;

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
		return mWidth = mW.get(0);
	}

	/**
	 * Get Height for the texture
	 * 
	 * @return
	 */
	public int getHeight() {
		return mHeight = mH.get(0);
	}

	/**
	 * Get the transparency of the texture
	 * 
	 * @return
	 */
	public int getComp() {
		return mComp = mC.get(0);
	}

	/**
	 * Helper method to load the image file into a texture to then be drawn on
	 * the Canvas
	 * 
	 * @param filename
	 * @return an integer of the texture data
	 */
	private int loadTexture(String filename) {

		mW = BufferUtils.createIntBuffer(1);
		mH = BufferUtils.createIntBuffer(1);
		mC = BufferUtils.createIntBuffer(1);

		mImage = STBImage.stbi_load_from_memory(mImageBuffer, mW, mH, mC, 0);
		// if can not open the file then throw an error
		if (mImage == null) {
			throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());	}
		

		if (getComp() == 3) {
			// function specifies a two-dimensional texture image
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, getWidth(), getHeight(), 0, GL11.GL_RGB,
					GL11.GL_UNSIGNED_BYTE, mImage);
		} else {
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, getWidth(), getHeight(), 0, GL11.GL_RGBA,
					GL11.GL_UNSIGNED_BYTE, mImage);

			GL11.glEnable(GL11.GL_BLEND);// functions enable OpenGL capabilities
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
		// Sets texture parameters, needs a target and valued texture parameter
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		// enable texture 2d
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		// function generates texture names
		return GL11.glGenTextures();
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
	 * @return ByteBuffer
	 * @throws IOException
	 */
	private ByteBuffer readFile(String resource) throws IOException {
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
