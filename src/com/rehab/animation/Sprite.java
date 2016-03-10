package com.rehab.animation;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;

public class Sprite {

	private Image buffer;
	private int id;

	@SuppressWarnings("unused")
	private Sprite() {  }
	
	/**
	 * Constructor for loading an image from a file. The file must be
	 * within the same directory as the Sprite.java
	 * @param fileName
	 * 		the name of the image with file type.
	 */
	public Sprite(String fileName) {
		// store the image
		buffer = new Image(Sprite.class.getResourceAsStream(fileName));
	}

	/**
	 * INCOMPLETE METHOD
	 */
	public Image mirror(Image image) {
		PixelReader pixel = image.getPixelReader();
		
		for (int i = 0; i < image.getWidth(); i++) {
			
			for (int j = 0; j < image.getHeight(); j++) {
				pixel.getArgb(i, j);

			}

		}
		return image;

	}
		
	public void draw(PixelWriter writer, int offsetX, int offsetY) {
		PixelReader reader = buffer.getPixelReader();
		
		// Transfer pixels to the buffer's writer
		for (int x = 0, w = (int) getWidth(); x < w; x++) {
			for (int y = 0, h = (int) getHeight(); y < h; y++) {
				int realX = offsetX + x, realY = offsetY + y;
				// Draw the pixels only if its within the screen
				if (realX >= 0 && realX < 720 && realY >= 0 && realY < 480)
					writer.setArgb(offsetX + x, offsetY + y, reader.getArgb(x, y));
			}
		}
		
	}
	
	public PixelReader getPixelReader() { return buffer.getPixelReader(); }

	public void setId(int i) { id = i; }
	
	public int getId() { return id; }
	
	public double getHeight(){ return buffer.getHeight(); }
	
	public double getWidth() { return buffer.getWidth(); }
}
