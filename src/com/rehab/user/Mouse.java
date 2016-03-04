package com.rehab.user;

import java.util.LinkedList;

import com.rehab.animation.Drawable;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

/*
 * A class to detect when the mouse is clicked inside of the canvas.
 */
public class Mouse {

	LinkedList<Drawable> coordinates;

	/**
	 * Constructor to initialize a linked list of Drawable objects and capture
	 * the click of the mouse.
	 * 
	 * @param canvas
	 */
	public Mouse(Canvas canvas) {
		coordinates = new LinkedList<Drawable>();
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				iterate(click.getX(), click.getY());
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
		for (int i = 0; i < coordinates.size(); i++) {
			Drawable draw = coordinates.get(i);
			double x = draw.getX();
			double y = draw.getY();
			double width = draw.getWidth();
			double height = draw.getHeight();
			if (xMouse > x && xMouse < width + x) {
				if (yMouse > y && yMouse < height + y) {
					draw.onClick();
				}
			}
		}
	}

}
