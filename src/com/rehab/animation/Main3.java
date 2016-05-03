package com.rehab.animation;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class Main3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(GLFW.glfwInit() !=1){
			System.err.println("GLFW Failed to init!"); //init stuff
			System.exit(1);
			
		}
		
		long win = GLFW.glfwCreateWindow(640,480,"Window",0,0);//creating the window
		
		GLFW.glfwShowWindow(win); //show the window
		
		GLFW.glfwMakeContextCurrent(win);
		
		GL.createCapabilities(); //DO NOT forget to write this in
		
		GL11.glEnable(GL11.GL_TEXTURE_2D); //enables for the 2d image 
		 Texture2 text = new Texture2 ();
		 text.loadTexture("/Users/TrishDaoLe/Documents/workspace/Rehab/src/com/rehab/animation/image/twitter_logo1-300x300.png");
		 while(GLFW.glfwWindowShouldClose(win) !=1){ //to make the window stays open
				
				GLFW.glfwPollEvents();
				
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);//context, setting every pixel to black
				//GL11.glRotatef(45, 0, 0, 1); //putting it here will continously rotate because we are in a loop
				
				GL11.glPushMatrix();
				GL11.glRotatef(45, 0, 0, 1);
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glTexCoord2f(0, 0); //telling the orientation of your 2d image
					GL11.glVertex2f(-0.5f, 0.5f); //the vertices in which your image will lay on
					
					GL11.glTexCoord2f(0, 1);
					GL11.glVertex2f(0.5f, 0.5f);
					
					GL11.glTexCoord2f(1, 1);
					GL11.glVertex2f(0.5f, -0.5f);
					
					GL11.glTexCoord2f(1, 0);
					GL11.glVertex2f(-0.5f, -0.5f);
					
				GL11.glEnd();
				GL11.glPopMatrix();
				
				GLFW.glfwSwapBuffers(win); //swap between buffer window
			}
			GLFW.glfwTerminate(); 
		}
		
	}


