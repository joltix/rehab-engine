package com.rehab.user;

import com.rehab.animation.Sprite;

import javafx.scene.input.MouseEvent;

public class Button extends UserInterface {
	
	private boolean clicked;
	
	/**
	 * Constructor of Button class.
	 * @param sprite
	 */
	public Button(Sprite sprite){
		super(sprite);
	}

	/**
	 * A method that uses a MouseEvent to determine if the
	 * object has been clicked on. This method is used when
	 * the mouse presses down on an object.
	 */
	@Override
	public void onMousePress(MouseEvent click) {
		super.onMousePress(click);
		clicked = true;
	}

	/**
	 * A method that uses a MouseEvent to determine if
	 * an object is not being clicked on, in which case
	 * the boolean variable clicked will be set to false.
	 */
	@Override
	public void onMouseRelease(MouseEvent click) {
		super.onMouseRelease(click);
		clicked = false;
	}
	
	/**
	 * A method to determine if the boolean variable clicked
	 * is set to true or false.
	 * @return true if object is clicked on
	 */
	public boolean isClickedOn(){
		return clicked;
	}

	@Override
	public boolean isVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float getRotation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setRotation(float f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isFacingLeft() {
		// TODO Auto-generated method stub
		return false;
	}

}
