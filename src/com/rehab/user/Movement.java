package com.rehab.user;

import com.rehab.world.Actor;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/*
 * 
 * A class to control the movements of
 * a character on the screen. Currently
 * keeps track of movement made using
 * the up, down, right or left arrow keys.
 * 
 */
public class Movement {

	/**
	 * Constructor to handle the movement of a character when the up, down,
	 * left or right arrow keys are clicked.
	 * 
	 * @param canvas
	 * @param character
	 */
	public Movement(Canvas canvas, Actor character) {
		canvas.setFocusTraversable(true);
		canvas.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent click) {
				if (click.getCode() == KeyCode.UP) {
					character.moveBy(0, 1);
					System.out.println(click.getCode());
					System.out.println(character.getX());
					System.out.println(character.getY());
				}
			}
		});

		canvas.setFocusTraversable(true);
		canvas.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent click) {
				if (click.getCode() == KeyCode.DOWN) {
					character.moveBy(0, -1);
					System.out.println(click.getCode());
					System.out.println(character.getX());
					System.out.println(character.getY());
				}
			}
		});

		canvas.setFocusTraversable(true);
		canvas.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent click) {
				if (click.getCode() == KeyCode.LEFT) {
					character.moveBy(-1, 0);
					System.out.println(click.getCode());
					System.out.println(character.getX());
					System.out.println(character.getY());
				}
			}
		});

		canvas.setFocusTraversable(true);
		canvas.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent click) {
				if (click.getCode() == KeyCode.RIGHT) {
					character.moveBy(1, 0);
					System.out.println(click.getCode());
					System.out.println(character.getX());
					System.out.println(character.getY());
				}
			}
		});
	}
}
