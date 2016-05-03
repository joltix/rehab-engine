package com.rehab.animation;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
public class Texture2 {
	private int width;
	private int height; 
	private int comp;
	private int id; 
	
	public int loadTexture(String filename){
	    ByteBuffer imageBuffer;
	    try{
	        imageBuffer = readFile(filename);
	    }
	    catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	
	    IntBuffer w = BufferUtils.createIntBuffer(1);
	    IntBuffer h = BufferUtils.createIntBuffer(1);
	    IntBuffer comp = BufferUtils.createIntBuffer(1);

	    ByteBuffer image = STBImage.stbi_load_from_memory(imageBuffer, w, h, comp, 0);
	    if(image == null){
	        throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
	    }

	    this.width = w.get(0);
	    this.height = h.get(0);
	    this.comp = comp.get(0);

	    if(this.comp == 3){
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, this.width, this.height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, image);
	    }
	    else{
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    }

	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

	    GL11.glEnable(GL11.GL_TEXTURE_2D);

	    return GL11.glGenTextures();
	}

	private ByteBuffer readFile(String resource) throws IOException{
	    File file = new File(resource);

	    FileInputStream fis = new FileInputStream(file);
	    FileChannel fc = fis.getChannel();

	    ByteBuffer buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);

	    while(fc.read(buffer) != -1);

	    fis.close();
	    fc.close();
	    buffer.flip();

	    return buffer;
	}

}
