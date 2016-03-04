package com.rehab.animation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

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
	
	public void draw(GraphicsContext g, double x, double y) { draw(g, x, y, true); }

	public void draw(GraphicsContext g, double x, double y, boolean convertCoords) {
		// Convert to standard cartesian
		if (convertCoords) y = 480 - y;
		g.drawImage(buffer, x, y);
		
	}

	public void setId(int i) { id = i; }
	
	public int getId() { return id; }
	
	public double getHeight(){ return buffer.getHeight(); }
	
	public double getWidth() { return buffer.getWidth(); }
}
