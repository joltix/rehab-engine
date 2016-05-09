package com.rehab.user;

import com.rehab.animation.Sprite;

public class MainMenu extends UserInterface {

	private Button[] array = new Button[20];

	private int numOfButtons;
	private static final int BUTTON_DISTANCE = 5;
	private double xCoordinate = (getWidth() / 2) - (array[0].getWidth() / 2);
	private double mHeight;

	public MainMenu(Sprite sprite) {
		super(sprite);
	}

	public Button createButton(Sprite sprite) {
		array[numOfButtons] = new Button(sprite);
		if(numOfButtons == 0){
			mHeight = array[numOfButtons].getHeight();
		}
		double y = yCoordinate();
		array[numOfButtons].moveTo(xCoordinate, y);
		return array[numOfButtons++];
	}
	
	private double yCoordinate(){
		if(numOfButtons == 1){
		return (BUTTON_DISTANCE);
		}
		return mHeight - BUTTON_DISTANCE;
	}

	public int numberOfButtons() {
		return numOfButtons;
	}

	@Override
	public void moveBy(double x, double y) {
		super.moveBy(x, y);
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null)
				array[i].moveBy(x, y);
		}
	}

	@Override
	public void moveTo(double x, double y) {
		super.moveTo(x, y);
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null)
				array[i].moveTo(x, y);
		}
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
