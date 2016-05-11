package com.rehab.animation;


import java.io.IOException;

import com.rehab.user.Mouse;
import com.rehab.user.Movement;
import com.rehab.user.Resolution;
import com.rehab.world.Actor;
import com.rehab.world.Arena;
import com.rehab.world.Hitbox;
import com.rehab.world.InstanceManager;
import com.rehab.world.Projectile;
import com.rehab.world.Prop;
import com.rehab.world.Vector2D;
import com.rehab.world.WorldLoop;

public class LWMain {

	// Desired resolution (if not fullscreen)
	private static int mWidth = Resolution.HD_W;
	private static int mHeight = Resolution.HD_H;

	
	public static void main(String[] args) {

		// Load all image files in "images" folder
		try {
			SpriteManager.loadFolder("images");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		Actor player = initPlayer();
		Actor dummy = initDummy();
		Prop floor = initFloor();
		
		Arena lvl = initLevel(floor, dummy);
				
		// Begin game world
		WorldLoop world = WorldLoop.getInstance(60, lvl);
		world.start();
		
		// Begin rendering
		LWCanvas canvas = LWCanvas.create(mWidth, mHeight, "Rehab", false);
		
		initControls(canvas, player);
		
		canvas.show();
		
	}
	
	/**
	 * Sets up the test Arena.
	 * 
	 * @param floor	the level's floor to fight on.
	 * @param player	the Actor representing the player.
	 * @return	the Arena.
	 */
	private static Arena initLevel(Prop floor, Actor player) {
		Arena arena = new Arena("BASIC-TEST", mWidth, mHeight, floor);
		arena.setPlayer(player);

		
		
		InstanceManager instaMan = InstanceManager.getInstance();
		// Put all game objects into the world
		instaMan.loadAll();
		
		// Extract Entities to set in Arena
		Iterable<Actor> actors = instaMan.getLoadedActors();
		Iterable<Projectile> projs = instaMan.getLoadedProjectiles();
		Iterable<Prop> props = instaMan.getLoadedProps();
		
		arena.setEntities(actors, projs, props);
		return arena;
	}

	/**
	 * Sets up the floor object at the bottom of the screen.
	 * 
	 * @return the floor object.
	 */
	private static Prop initFloor() {
		// Initialize a non moving floor
		InstanceManager instMan = InstanceManager.getInstance();
		
		Prop p = instMan.createProp(400, 32, "bar.jpg");
		
		
		//Move to proper position at bottom
		p.moveTo(64, 32);
		return p;
	}

	/**
	 * Sets up the player object high at the top of the screen.
	 * 
	 * @return the player obj.
	 */
	private static Actor initPlayer() {
		// Initialize and setup the falling obj
		InstanceManager instaMan = InstanceManager.getInstance();
		Actor a = instaMan.createActor(62, 100, "git_icon.jpg");
		
		a.setCollisionModel(new Hitbox(0, 0, 32, 32));
		a.setEnableGravity(true);
		a.moveTo(0, 480);
		
		// Setup Projectile
		Projectile proj = instaMan.createProjectile(a, new Hitbox(0, 0, 24), "git_icon.jpg");
		
		// Setup weapon
		a.arm(1, 5, proj);
		
		return a;
	}

	/**
	 * Sets up the dummy object high at the top of the screen.
	 * 777
	 * @return the dummy obj.
	 */
	private static Actor initDummy() {
		InstanceManager instaMan = InstanceManager.getInstance();
		Actor dummy = instaMan.createActor(62, 100, "twitter_alpha.png");
		Sprite spr = dummy.getSprite();
		dummy.setCollisionModel(new Hitbox(0, 0, spr.getWidth(), spr.getHeight()));
		dummy.moveTo(720, 480);
		
		dummy.setEnableGravity(true);

		Actor clone = instaMan.createActor(dummy);
		clone.moveTo(1000, 500);
		
		System.out.printf("Dummy collision: %s\n", dummy.getCollision());
		
		for (Vector2D edge : dummy.getCollision().edges()) {
			System.out.printf("    Edge: %s\n", edge);
		}
		
		
		
		return dummy;
	}
	
	/**
	 * Initializes the mouse and keyboard inputs.
	 * 
	 * @param canvas	the window.
	 * @param player	the player to control.
	 */
	private static void initControls(LWCanvas canvas, Actor player) {
		Movement keyMap = new Movement(player);
			
		Mouse mouseMap = new Mouse(player);
			
		// Set the control mappings
		canvas.setKeyMap(keyMap);
		canvas.setMouseMap(mouseMap);
	}
	
}
