package com.rehab.animation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 * Sprite Class in which all the games objects comes from the Sprite class, 2D
 * drawable object
 * 
 */

public class Sprite {
	//private WritableImage buffer; 
	// instance of the ID
	private int id;
	// list or array that can hold multiple reels, {hashtable}
	private Hashtable<String, CircularArray<Image>> reelsNum;
	// hold the images for the animation
	private CircularArray<Image> reels = new CircularArray<Image>();
	private long startTime;
	private long localStartTime;
	private long seconds;
	boolean isFirstPic = false;
	private AnimationDatabase data;
	private Image[] divergent = new Image[2];
	private static final int IMAGE_RIGHT = 0; 
	private static final int  IMAGE_LEFT = 1; 
	Texture2 tex;
	
	@SuppressWarnings("unused")
	private Sprite() {
	}

	/**
	 * Boolean method that add a set of images into a Hashtable
	 * 
	 * @param r
	 * @param b
	 * @return false if it can not add, true otherwise
	 */
	public boolean add(String r, CircularArray<Image> b) {
		if (reelsNum.contains(r)) {
			return false;
		} else {
			reelsNum.put(r, b);
			return true;
		}
	}

	/**
	 * To retrieve the interval of which the images need to be switch during an
	 * animation
	 * 
	 * @param seconds
	 * @return the interval in miliseconds
	 */
	public double getInterval(double seconds) {
		int size = reels.size();
		long mili = (long) (seconds * 1000);
		return size / mili;
	}

	/**
	 * Constructor for loading an image from a file. The file must be within the
	 * same directory as the Sprite.java
	 * 
	 * @param fileName
	 *            the name of the image with file type.
	 */
	@SuppressWarnings("unused")
	public Sprite(String fileName) {
//		int width =(int) divergent[IMAGE_RIGHT].getWidth();
//		int height = (int)divergent[IMAGE_RIGHT].getHeight();
		// store the image
		//Image img = new Image(Sprite.class.getResourceAsStream(fileName));//index0 facing to the right
		//WritableImage mImg = new WritableImage(width, height);//index1
		//buffer=(WritableImage) img;
//		divergent[IMAGE_RIGHT]=img; 
//		divergent[IMAGE_LEFT]=mImg; 
		tex = new Texture2(fileName);
		data = new AnimationDatabase();
		
	}
	
//	public void rotateObj(float ang, float x, float y){
//		tex.rotate(ang, x, y);
//		
//	}
	
	public ByteBuffer getByteBuffer(){
		 return tex.getByteBuffer();
		
	}
	
	public int getWidth(){
		return tex.getWidth();
	}

	public int getHeight(){
		return tex.getHeight();
	}
	
	public int getComp(){
		return tex.getComp();
	}
	/**
	 * To start the timer for animation
	 * 
	 * @return the start time
	 */
	public long startAnime() {
		startTime = System.currentTimeMillis();
		localStartTime = startTime;
		isFirstPic = false;
		return startTime;

	}

	/**
	 * Draw the object that is being given
	 * 
	 * @param writer
	 * @param offsetX
	 * @param offsetY
	 */
//	public void draw(PixelWriter writer, int offsetX, int offsetY) {
//		//int getPicNum = 0;
//
//		long currentTime = System.currentTimeMillis();
//		long duration = currentTime - localStartTime;
//
//		if (isFirstPic = true && buffer == reels.get(0)) {
//			startTime = 0;
//			//buffer = reels.get(0);
//		}
//
//		// if start is equal or greater than the seconds then stop
//		if (duration >= getInterval(seconds) && startTime != 0) {
//			localStartTime = System.currentTimeMillis();
//			//getPicNum++;
//			//buffer = reels.get(getPicNum);
//			isFirstPic = true;
//		}
//
//		// Transfer pixels to the buffer's writer
//		//PixelReader reader = buffer.getPixelReader();
//		for (int x = 0, w = (int) getWidth(); x < w; x++) {
//			for (int y = 0, h = (int) getHeight(); y < h; y++) {
//				int realX = offsetX + x, realY = offsetY + y;
//				// Draw the pixels only if its within the screen
//				if (realX >= 0 && realX < 720 && realY >= 0 && realY < 480)
//					writer.setArgb(offsetX + x, offsetY + y, reader.getArgb(x, y));
//			}
//		}
//
//	}

	
//	private int[] getPixel(Image img) {
//		PixelReader target = img.getPixelReader();
//		int col = getImageHeight(img);// int j
//		int row = getImageWidth(img);// int i
//		int[] pixel1D = new int[row * col];
//
//		for (int i = 0; i < row; i++) {
//			for (int j = 0; j < col; j++) {
//				pixel1D[i * j] = target.getArgb(i, j);
//			}
//		}
//		return pixel1D;
//
//	}

//	public int getImageWidth(Image i) {
//		i = divergent[IMAGE_RIGHT];
//
//		int width = (int) i.getWidth();
//		return width;
//	}
//
//	public int getImageHeight(Image i) {
//		i = divergent[IMAGE_RIGHT];
//
//		int height = (int) i.getHeight();
//		return height;

	//}// call this method to reverse the pixel given the pixel array

//	private int[] mirror(int pixels[], int width, int height) {
//		int newPixels[] = null;
//		newPixels = new int[width * height];
//		int index = 0;
//		// Loop forwards through all the rows
//		for (int y = 0; y < height; y++)
//			// for each row loop backwards through the columns
//			for (int x = width - 1; x >= 0; x--) {
//				newPixels[index] = pixels[y * width + x];
//				index++;
//			}
//		return newPixels;
//	}
	//call the two helper methods mirror pixel to 
	//reverse the image
//	public WritableImage reverse(Image imi){
//		int [] img = getPixel(imi); //pixel array from the buffer
//		int width= getImageWidth(imi);
//		int height = getImageHeight(imi);
		
//		PixelWriter writer = buffer.getPixelWriter(); //the writer
		//loop through the array and draw them onto an writable image
//		for(int i=0; i<width;i++){
//			for(int j=0; j<height;j++){
//				writer.setArgb(i, j,writer. );//no idea what to pass as the third parameter
//				
//			}
//		}
//		int[] almostThere = mirror(img, width, height);//get a 1D array 
//		//get a number form the array to a setARB
//		
//		WritableImage k = new WritableImage(width, height);  
//		PixelWriter pk = k.getPixelWriter();
//		 for(int i =0; i<width; i++){
//			 for(int j=0; j<height; j++){
//				//go thru 1d and get all the pixel and draw 
//				 pk.setArgb(i, j, almostThere[i*height+j] );
//			 }
//		 }
//		return k;
//		 
//		
//		
//		
//	}
	//draw method for the reverse images. 
	//given the matrix need to write the images onto the canvas
//	public Image drawRev (){
//		
//		return buffer;
//		
//		
//	}

	// given an angle rotate the image in that manner
//	public int[] getRotational(double angle, int[] pixels, int width, int height) {
//		final double radians = Math.toRadians(angle); //convert to radian
//		final double cos = Math.cos(radians);
//		final double sin = Math.sin(radians);
//		final int[] pixels2 = new int[pixels.length];
//		for (int pixel = 0; pixel < pixels2.length; pixel++) {
//
//			pixels2[pixel] = 0xFFFFFF;
//
//		}
//		for (int x = 0; x < width; x++) {
//			for (int y = 0; y < height; y++) {
//				final int centerx = width / 2;
//				final int centery = height / 2;
//				final int m = x - centerx;
//				final int n = y - centery;
//				final int j = ((int) (m * cos + n * sin)) + centerx;
//				final int k = ((int) (n * cos - m * sin)) + centery;
//				if (j >= 0 && j < width && k >= 0 && k < height) {
//					pixels2[(y * width + x)] = pixels[(k * width + j)];
//
//				}
//			}
//		}
//
//		return pixels2;
//
//	}

	/**
	 * Calling the PixelReader on a Image
	 * 
	 * @return the Pixel reader for reading the pixel of the image
	 */
//	public PixelReader getPixelReader() {
//		return buffer.getPixelReader();
//	}

	/**
	 * Method to set the ID for Sprite
	 * 
	 * @param i
	 */
	public void setId(int i) {
		id = i;
	}

	/**
	 * Getting the ID of a specific Sprite
	 * 
	 * @return the ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get the Height the image
	 * 
	 * @return the value in which is the height
	 */
//	public double getHeight() {
//		return buffer.getHeight();
//	}
//
//	/**
//	 * Get the Width of the image
//	 * 
//	 * @return the value of which is the width
//	 */
//	public double getWidth() {
//		return buffer.getWidth();
//	}
	

	/**
	 * Method to get and retrieve the files in a given Folder
	 * 
	 * @param folder
	 */
//	public ArrayList<BufferedImage> getFolder(final File folder){
//		File[] files = folder.listFiles(); //array of objs
//		ArrayList<BufferedImage> image = new ArrayList<BufferedImage>();
//		
//		
//		for(int i=0; i<files.length;i++){//reading in files from the array of files
//			if (files[i].isFile()){//if this is a file then add the image to the arraylist
//				try {
//					image.add(i,ImageIO.read(files[i]));//this gives me a bufferedImage
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//			
//		}
//		
//		 return image;//return the arraylist of BufferedImages
//		
//	}

//	public WritableImage getBuffer(){
//		
//		return buffer; 
//	}
//	public void setImage(WritableImage i){
//		buffer =i; 
//	}

}
