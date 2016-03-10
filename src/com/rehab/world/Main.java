package com.rehab.world;


import java.util.LinkedList;

import com.rehab.animation.Drawable;
import com.rehab.animation.Sprite;
import com.rehab.user.Mouse;
import com.rehab.user.Movement;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Prep and display the window
		Canvas canvas = setupWindow(primaryStage);
				
		// The test components
		Actor player = initGameObj();
		initFloor();
		Arena lvl = initLevel(player);
		
		
		// Setup keyboard control
		Movement move = new Movement(canvas, player);
		LinkedList<Drawable> drawables = new LinkedList<Drawable>();
		for (Entity e : InstanceManager.getInstance().getLoadedEntities())
			drawables.add(e);
		
		// Setup mouse control
		Mouse mouse = new Mouse(canvas, drawables, player);
		
		// The logic and draw loops
		WorldLoop world = WorldLoop.getInstance(60, lvl);
		RenderLoop render = RenderLoop.getInstance(60, canvas);

		// Prep game objs then draw
		render.reloadLayers();
		render.start();

		// Begin game world
		world.start();
		
	}
		
	/**
	 * Sets up the game Window and displays it for visualizing the game's
	 * state.
	 * @param primary
	 * 		the Stage given by JavaFX
	 * @return
	 * 		the Canvas needed for drawing.
	 */
	private Canvas setupWindow(Stage primary) {
		Canvas canvas = new Canvas(720, 480);
		Group group = new Group();
		group.getChildren().add(canvas);
		primary.setScene(new Scene(group, 720, 480, Color.DARKBLUE));
		primary.show();
		return canvas;
	}
	
	/**
	 * Sets up the test Arena.
	 * @param player
	 * 		the Actor representing the player.
	 * @return
	 * 		the Arena.
	 */
	private Arena initLevel(Actor player) {
		Arena arena = new Arena("BASIC-TEST", 720, 480);
		arena.setPlayer(player);
		arena.setEntities(InstanceManager.getInstance().getLoadedEntities());
		return arena;
	}
	
	/**
	 * Sets up the floor object at the bottom of the screen.
	 * @return
	 * 		the floor object.
	 */
	private Actor initFloor() {
		// (Foregoing Obstacle for reval)
		// Initialize a non moving floor
		Actor a = new Actor(400, 100);
		a.setEnableGravity(false);
		
		// Setup collision
		Hitbox h = new Hitbox(0, 0, 720, 32);
		a.setCollisionModel(h);
		
		// Load image and move to proper position
		a.moveTo(0, 32);
		a.setSprite(new Sprite("bar.png"));
		
		// Make obj known to manager
		InstanceManager instMan = InstanceManager.getInstance();
		instMan.register(a);
		instMan.load(a);
				
		return a;
	}
	
	/**
	 * Sets up the game object high at the top of the screen.
	 * @return
	 * 		the game obj.
	 */
	private Actor initGameObj() {
		// Initialize and setup the falling obj
		Actor a = new Actor(62, 100);
		a.setCollisionModel(new Hitbox(0, 0, 32, 32));
		a.moveTo(0, 480);
		a.setSprite(new Sprite("git_icon.jpg"));
		
		// Make object known to manager
		InstanceManager instMan = InstanceManager.getInstance();
		instMan.register(a);
		instMan.load(a);
		return a;
	}
	
}
