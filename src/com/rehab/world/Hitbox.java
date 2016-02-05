package com.rehab.world;

public class Hitbox {
	
	// Top left corner and bottom right corner of rectangular model
	private double mTopLeftX, mTopLeftY,
					mBottomRightX, mBottomRightY;
		
	private double mWidth, mHeight;


	/**
	 * Empty Hitboxes must not be created.
	 */
	private Hitbox() {  }

	/**
	 * Constructor for a Rectangular Hitbox whose dimensions are based off of a given
	 * Entity's image dimensions.
	 * @param e
	 * 		the Entity whose image width and height should model the collidable area.
	 */
	public Hitbox(Entity e) {

		// TODO - Record coordinates based on Entity image
		
	}


	/**
	 * Constructor for a rectangular Hitbox where the host object's image dimensions is
	 * not the true collidable dimensions.
	 * @param leftX
	 * 		the x-coordinate of the top left corner.
	 * @param leftY
	 * 		the y-coordinate of the top left corner.
	 * @param rightX
	 * 		the x-coordinate of the bottom right corner.
	 * @param rightY
	 * 		the y-coordinate of the bottom right corner.
	 */
	public Hitbox(double leftX, double leftY, double rightX, double rightY) {
		mTopLeftX = leftX;
		mTopLeftY = leftY;
		mBottomRightX = rightX;
		mBottomRightY = rightY;
		// Cache dimensions
		mWidth = mBottomRightX - mTopLeftX;
		mHeight = mBottomRightY - mTopLeftY;
	}

	/**
	 * Checks whether or not two rectangular Hitboxes overlap.
	 * @param h
	 * 		the Hitbox to check against.
	 * @return
	 * 		true if the two Hitboxes overlap at some point, false othewise.
	 */
	public boolean intersects(Hitbox h) {
		if (this.mBottomRightX > h.mTopLeftX && this.mBottomRightY > h.mTopLeftY
				&& this.mTopLeftX < h.mBottomRightX && this.mTopLeftY < h.mBottomRightY)
				return true;
		return false;
	}

	/**
	 * Sets the new origin of the Hitbox.
	 * @param x
	 * 		the new x origin.
	 * @param y
	 * 		the new y origin.
	 */
	public void moveTo(double x, double y) {
		mTopLeftX = x;
		mTopLeftY = y;
		mBottomRightX = x + mWidth;
		mBottomRightY = y + mHeight;	
	
		mBottomRightY = y + mHeight;
	}

	/**
	 * Shrinks the actual size of an object's collision model on four optional sides.
	 * @param right
	 * 		how many px to pull back on the right.
	 * @param top
	 * 		how many px to pull back on the top.
	 * @param left
	 * 		how many px to pull back on the left.
	 * @param bottom
	 * 		how many px to pull back on the bottom.
	 */
	public void setPadding(double right, double top, double left, double bottom) {
		mTopLeftX += left;
		mTopLeftY += top;
		mBottomRightX -= right;
		mBottomRightY -= bottom;
	}
}
