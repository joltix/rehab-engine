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
	ByteBuffer image;
	ByteBuffer imageBuffer;
	IntBuffer w ;
    IntBuffer h ;
    IntBuffer c ;
	//add a method to get all of the files from a folder, i.e. for loop get names and put that as the paramter
	
	public Texture2 (String filename){
		 
		    try{
		        imageBuffer = readFile(filename);
		    }
		    catch (IOException e) {
		        throw new RuntimeException(e);
		    }
		    
		    loadTexture(filename);
	}
	
	public int getWidth(){
		return  width = w.get(0);
	}
	
	public int getHeight(){
		return  height = h.get(0);
	}
	
	public int getComp(){
		return comp = c.get(0);
	}
	
	public void rotate(float angle, float x, float y){
		GL11.glRotatef(angle, x, y, 1);
	}
	private int loadTexture(String filename){
	
		w = BufferUtils.createIntBuffer(1);
	     h = BufferUtils.createIntBuffer(1);
	    c = BufferUtils.createIntBuffer(1);
	    
	     image = STBImage.stbi_load_from_memory(imageBuffer, w, h, c, 0);
	    //if can not open the file then throw an error
	    if(image == null){
	        throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
	    }
	    
	    if(getComp() == 3){
	    	//function specifies a two-dimensional texture image
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, getWidth(), getHeight(), 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, image);
	    }
	    else{
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, getWidth(), getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

	        GL11.glEnable(GL11.GL_BLEND);//functions enable  OpenGL capabilities
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	    }
	    //Sets texture parameters, needs a target and valued texture parameter
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    //enable texture 2d
	    GL11.glEnable(GL11.GL_TEXTURE_2D);
	 
	    
	    // function generates texture names
	    return GL11.glGenTextures();
	}
	
	
public 	ByteBuffer getByteBuffer (){
	
	return image;
}
	
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

	    while(fc.read(buffer) != -1);

	    fis.close();
	    fc.close();
	    buffer.flip();

	    return buffer;
	}

}
