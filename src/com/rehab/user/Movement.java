package com.rehab.user;

import com.rehab.world.Actor;
import com.rehab.world.Main;
import com.rehab.world.SpawnManager;
import com.rehab.world.Vector2D.Point;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

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
	public Movement(Canvas canvas, Actor character, Stage primaryStage) {
		canvas.setFocusTraversable(true);
		canvas.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent click) {
				KeyCode code = click.getCode();
				if (code == KeyCode.RIGHT) {
					// Move player right
					character.moveBy(5, 0);
					
				} else if (code == KeyCode.LEFT) {
					// Move player left
					character.moveBy(-5, 0);
					
				} else if (code == KeyCode.SPACE) {
					// Spawn a new dummy at the top of the screen
					Actor dummy = Main.getDummy();
					SpawnManager.getInstance().immediateSpawn(dummy, new Point(128, 480));
				}
			}
		});		

	}
}
