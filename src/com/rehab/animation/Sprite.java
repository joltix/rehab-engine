package com.rehab.animation;
import java.util.Hashtable;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;

/**
 * Sprite Class in which all the games objects comes from the Sprite class, 
 * 2D drawable object 
 * 
 */

public class Sprite {
	//instance of the Image
	private Image buffer;
	//instance of the ID 
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
	


	@SuppressWarnings("unused")
	private Sprite() {
	}

	/**
	 * Boolean method that add a set of images into a Hashtable
	 * 
	 * @param r
	 * @param b
	 * @return 
	 * false if it can not add, 
	 * true otherwise
	 */
	public boolean add(String r, CircularArray<Image> b) {
		if (reelsNum.contains(r)) {
			return false;
		}
		else {
			reelsNum.put(r, b);
			return true;
		}
	}

	/**
	 * To retrieve the interval of which the images need to be switch during an 
	 * animation
	 * @param seconds
	 * @return
	 * the interval in miliseconds
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
	public Sprite(String fileName) {
		// store the image
		buffer = new Image(Sprite.class.getResourceAsStream(fileName));
		data = new AnimationDatabase(); 
		
	}

	/**
	 * To start the timer for animation
	 * @return
	 * the start time
	 */
	public long startAnime() {
		startTime = System.currentTimeMillis();
		localStartTime = startTime;
		isFirstPic = false;
		return startTime;

	}

	/**
	 * Draw the object that is being given 
	 * @param writer
	 * @param offsetX
	 * @param offsetY
	 */
	public void draw(PixelWriter writer, int offsetX, int offsetY) {
		int getPicNum = 0;

		long currentTime = System.currentTimeMillis();
		long duration = currentTime - localStartTime;

		if (isFirstPic = true && buffer == reels.get(0)) {
			startTime = 0;
			buffer = reels.get(0);
		}

		// if start is equal or greater than the seconds then stop
		if (duration >= getInterval(seconds) && startTime != 0) {
			localStartTime = System.currentTimeMillis();
			getPicNum++;
			buffer = reels.get(getPicNum);
			isFirstPic = true;
		}

		// Transfer pixels to the buffer's writer
		PixelReader reader = buffer.getPixelReader();
		for (int x = 0, w = (int) getWidth(); x < w; x++) {
			for (int y = 0, h = (int) getHeight(); y < h; y++) {
				int realX = offsetX + x, realY = offsetY + y;
				// Draw the pixels only if its within the screen
				if (realX >= 0 && realX < 720 && realY >= 0 && realY < 480)
					writer.setArgb(offsetX + x, offsetY + y, reader.getArgb(x, y));
			}
		}

	}

	/**
	 * Calling the PixelReader on a Image
	 * @return
	 * the Pixel reader for reading the pixel of the image 
	 */
	public PixelReader getPixelReader() {
		return buffer.getPixelReader();
	}

	/**
	 * Method to set the ID for Sprite
	 * @param i
	 */
	public void setId(int i) {
		id = i;
	}

	/**
	 * Getting the ID of a specific Sprite
	 * @return
	 * the ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get the Height the image
	 * @return
	 * the value in which is the height
	 */
	public double getHeight() {
		return buffer.getHeight();
	}

	/**
	 * Get the Width of the image
	 * @return
	 * the value of which is the width 
	 */
	public double getWidth() {
		return buffer.getWidth();
	}
}
