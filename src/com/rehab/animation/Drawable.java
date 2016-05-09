package com.rehab.animation;

import javafx.scene.input.MouseEvent;

public interface Drawable {
	
	double getX ();
	
	double getY ();
	
	double getWidth();
	
	double getHeight();
	
	Sprite getSprite();
	
	void setSprite(Sprite s); 
	
	int getZ();
		
	boolean isVisible();
	
	float getRotation();
	
	void setRotation(float f);
	
	boolean isFacingLeft();
	
	void onMousePress(MouseEvent event);
	
	void onMouseRelease(MouseEvent event);	
}
