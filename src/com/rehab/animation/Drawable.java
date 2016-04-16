package com.rehab.animation;

public interface Drawable {
	
	double getX ();
	
	double getY ();
	
	double getWidth();
	
	double getHeight();
	
	Sprite getSprite();
	
	void setSprite(Sprite s); 
	
	int getZ();
	
	void onClick();
	
	boolean isVisible();
	
}
