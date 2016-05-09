package com.rehab.animation;

import com.rehab.world.Actor;
import com.rehab.world.Arena;
import com.rehab.world.Hitbox;
import com.rehab.world.InstanceManager;
import com.rehab.world.Projectile;
import com.rehab.world.Prop;
import com.rehab.world.WorldLoop;

public class LWMain {

	// Desired resolution (if not fullscreen)
	private static int mWidth = 1920;
	private static int mHeight = 1080;

	
	public static void main(String[] args) {

		Actor dummy = initDummy();
		Prop floor = initFloor();
		floor.setVisibility(false);
		
		Arena lvl = initLevel(floor, dummy);
		
		
		// Begin game world
		WorldLoop world = WorldLoop.getInstance(60, lvl);
		world.start();
		
		// Begin rendering
		LWCanvas canvas = LWCanvas.create(mWidth, mHeight, "Rehab", false);
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
		Prop p = instMan.createProp(new Sprite("bar.jpg"), 400, 32);
		
		Sprite spr = p.getSprite();
		System.out.printf("Floor w(%d) h(%d)\n", spr.getWidth(), spr.getHeight());
		
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
		Actor a = instaMan.createActor(62, 100);
		
		a.setCollisionModel(new Hitbox(0, 0, 32, 32));
		a.setEnableGravity(true);
		a.moveTo(0, 480);
		a.setSprite(new Sprite("git_icon.jpg"));
		
		// Setup Projectile
		Projectile proj = instaMan.createProjectile(a, new Hitbox(0, 0, 24));
		proj.setSprite(new Sprite("git_icon.jpg"));
		
		// Setup weapon
		a.arm(1, 5, proj);
		
		return a;
	}

	/**
	 * Sets up the dummy object high at the top of the screen.
	 * 
	 * @return the dummy obj.
	 */
	private static Actor initDummy() {
		InstanceManager instaMan = InstanceManager.getInstance();
		Actor dummy = instaMan.createActor(62, 100);
		dummy.setCollisionModel(new Hitbox(0, 0, 32, 32));
		dummy.moveTo(360, 480);
		dummy.setSprite(new Sprite("twitter_alpha.png"));
		dummy.setEnableGravity(true);

		Actor clone = instaMan.createActor(dummy);
		clone.moveTo(128, 500);
		
		System.out.println("Dummy id: " + dummy.getId());
		return dummy;
	}
	
}
