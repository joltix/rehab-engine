package com.rehab.user;


import com.rehab.animation.Drawable;
import com.rehab.world.Actor;
import com.rehab.world.Entity;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

/*
 * A class to detect when the mouse is clicked inside of the canvas.
 */
public class Mouse {

	Iterable<Drawable> coordinates;
	
	private Actor mPlayer;

	/**
	 * Constructor to initialize a linked list of Drawable objects and capture
	 * the click of the mouse.
	 * 
	 * @param canvas
	 * @param drawables
	 * @param player
	 */
	public Mouse(Canvas canvas, Iterable<Drawable> drawables, Actor player) {
		coordinates = drawables;
		mPlayer = player;
		
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				double xMouse = click.getX(), yMouse = 480 - click.getY();
				iterate(xMouse, yMouse);
				mPlayer.fireAt(xMouse, yMouse);
			}
		});
	}

	/**
	 * A method to iterate through the linked list of Drawable objects and check
	 * whether the object was cliked on.
	 * 
	 * @param xMouse
	 * @param yMouse
	 */
	public void iterate(double xMouse, double yMouse) {
		
		for (Drawable draw : coordinates) {
			double x = draw.getX();
			double y = draw.getY();
			double width = draw.getWidth();
			double height = draw.getHeight();
			if (xMouse > x && xMouse < width + x) {
				if (yMouse <= y && yMouse >= y - height) {
					draw.onClick();
				}
			}
		}
	}

}
