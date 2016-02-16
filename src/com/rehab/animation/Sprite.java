package com.rehab.animation;

import java.lang.Object;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class Sprite {
	// a wrapper for the BufferImage
	// mirror image method
	// store bufferimage as class memeber
	private WritableImage buffer;
	// private GraphicsContext gpu;

	private int id;

	private Sprite(WritableImage b) {
		// store the image

		buffer = b;
	}

	public WritableImage mirror(WritableImage image) {
		PixelReader pixel = image.getPixelReader();
		
		for (int i = 0; i < image.getWidth(); i++) {
			
			for (int j = 0; j < image.getHeight(); j++) {
				pixel.getArgb(i, j);

			}

		}
		return image;

	}

	public void draw(GraphicsContext g, int x, int y) {
		g.drawImage(buffer, x, y);

	}

	public int getId() {

		return id;
	}

	public void setId(int i) {

		id = i;
	}
	
	public double getHeight(){
		return buffer.getHeight(); 
		
	}
	
	public double getWidth(){
		
		return buffer.getWidth(); 
	}
}
