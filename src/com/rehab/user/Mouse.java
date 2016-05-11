package com.rehab.user;


import com.rehab.animation.Drawable;
import com.rehab.animation.LWCanvas;
import com.rehab.world.Actor;

/*
 * A class to detect when the mouse is clicked inside of the canvas.
 */
public class Mouse extends MouseMap {

	Iterable<Drawable> coordinates;
	
	private Actor mPlayer;

	/**
	 * Constructor to initialize a linked list of Drawable objects and capture
	 * the click of the mouse.
	 * 
	 * @param drawables
	 * @param player
	 */
	public Mouse(Actor player) {
		super(LWCanvas.getInstance().getHeight());
		mPlayer = player;
		
//		canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
//
//			@Override
//			public void handle(MouseEvent press) {
//				iterate(press, true);
//			}}  );
//		
//		canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
//
//			@Override
//			public void handle(MouseEvent release) {
//				double xMouse = release.getX(), yMouse = 480 - release.getY();
//				iterate(release, false);
//				mPlayer.fireAt(xMouse, yMouse);
//			}} );
	}

	/**
	 * A method to iterate through the linked list of Drawable objects and check
	 * whether the object was clicked on.
	 * 
	 * @param xMouse
	 * @param yMouse
	 */
	public void iterate(double x, double y, boolean release) {
		
		for (Drawable draw : coordinates) {
			double objX = draw.getX();
			double objY = draw.getY();
			double width = draw.getWidth();
			double height = draw.getHeight();
			
			// Check if click location is within object's square
			if (x > objX && x < width + objX) {
				if (y <= objY && y >= objY - height) {
					// Trigger the correct callback
					if (release) {
						draw.onMouseRelease();
					} else {
						draw.onMousePress();
					}
				}
			}
		}
	}

	@Override
	public void onLeftClick(double x, double y, boolean release) {
		//iterate(x, y, release);
		
		if (release) {
			mPlayer.fireAt(x, y);
		}
	}

	@Override
	public void onRightClick(double x, double y, boolean release) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMiddleClick(double x, double y, boolean release) {
		// TODO Auto-generated method stub
		
	}

}
