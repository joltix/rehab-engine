package com.rehab.world;


public class Arena {

	// Playable bounds on-screen
	private double mWidth, mHeight;
	// Percentage of a real second as the game's smallest unit of time
	private double mUnitTime;
	
	// Title of the level
	private String mName;
	// Default gravity is at Earth level
	private Vector2D mGrav = new Vector2D(0, 0);

	// Entities to keep track of (those in the level)
	private Iterable<Actor> mActList;
	private Iterable<Projectile> mProjList;
	private Actor mPlayer;

	/**
	 * Constructor for a basic Arena.
	 * @param name
	 * 		a title for the level.
	 * @param width
	 * 		the playable area's width.
	 * @param height
	 * 		the playable area's height.
	 */
	public Arena(String name, double width, double height) {
		mName = name;
		mWidth = width;
		mHeight = height;

		// Setup gravity Vector
		double gravStrength = Physics.getPlanetGravity(Physics.EARTH_MASS, Physics.EARTH_RADIUS);
		mGrav.updateFrom(Vector2D.UNIT_SOUTH);
		mGrav.changeMagnitude(gravStrength);
	}
	
	/**
	 * Sets the unit of time to use for Entity physics.
	 * @param tickRate
	 * 		a fraction of a second.
	 */
	public void setDeltaTime(double tickRate) {
		mUnitTime = tickRate;
		Phys.syncTimescale(mUnitTime);
	}

	/**
	 * [INCOMPLETE] Calculates the level's current game state. This includes instance locations, health
	 * and damage dealing, etc. In its current form, this method should only be used to
	 * test various reactions and / or functions and their values in a one-shot manner.
	 * Once an Entity reaches the floor of the Arena (getHeight() = 0), nothing more can
	 * be said of what will occur.
	 */
	public void stepActors() {
		for (Actor a : mActList) {

			// Print dummy location
//			if (a.getId() == 2) {
//				System.out.printf("Dummy location (%f, %f)\n", a.getX(), a.getY());
//				for (Vector2D edge : a.getCollision().edges()) {
//					System.out.printf("    Edge: %s\n", edge);
//				}
//			}
			
			// Disable objects beyond the screen
			if (isOutside(a)) {
				System.out.println("Disabled obj: " + a.getId());
				a.disable();
			}
			
			// Apply gravity
			if (a.isGravityEnabled()) {
				applyGravity(a);
				for (Entity other : mActList) {
					// Skip collision with itself
					if (a == other) continue;
//					if (a.collidesWith(other)) {
//						System.out.printf("Actor collision: [%d] (%f, %f) with [%d]\n", a.getId(), a.getX(), a.getY(), other.getId());
//						// Snap object to floor
//						if (other == Main.getFloor()) {
//							
//							snapToFloor(a);
//							a.setEnableGravity(false);
//						}
//						//a.moveTo(a.getX(), other.getY() + a.getHeight());
//						//a.setEnableGravity(false);
//					}
				}
 			}
			
			// Snap the character to the surface of the floor if sinks
			if (isBelow(a)) {
				snapToFloor(a);
				a.setEnableGravity(false);
				System.out.printf("Actor below screen: %s\n", a);
			}

		}
	}
	
	private void snapToFloor(Actor a) {
		Actor floor = Main.getFloor();
		a.moveTo(a.getX(), a.getHeight() + floor.getHeight());
	}

	public void stepProjectiles() {
		for (Projectile p : mProjList) {

			// Skip disabled projectiles
			if (p.isDisabled()) {
				continue;
			}
			
			// Disable projectiles beyond the screen
			if (isOutside(p)) {
				System.out.printf("Projectile beyond screen! Disabling...\n");
				p.disable();
			} else {
				
				// Test collision with all Actor and disable projectile
				boolean collides = false;
				for (Actor a : mActList) {
					
					if (a != Main.getFloor() && (collides = p.collidesWith(a))) {
						System.out.printf("Projectile[%d] collision with [%d]! Disabling Projectile %s\n", p.getId(), a.getId(), p);
						p.disable();
						break;
					}
				}
				
				// Move again only if no collision
				if (!collides) {
					Vector2D velo = p.getPhysics().getVelocity();
					Vector2D direction = velo.getUnitVector();
					//direction.multiply(p.getSpeed());
					p.move();
					//p.moveBy(1, 1);
				}
			}
			
		}
	}
	
	/**
	 * Applies the Arena's gravity vector to a given Entity.
	 * 
	 * @param e	the Entity to affect.
	 */
	private void applyGravity(Entity e) {
		e.moveBy(0, -2);
	}

	/**
	 * Checks whether or not an Entity is below the Arena's boundaries.
	 * @param e
	 * 		the Entity whose location must be checked.
	 * @return
	 * 		true if the Entity is below the Arena, false otherwise.
	 */
	private boolean isBelow(Entity e) {
		double y = e.getY();
		double height = e.getHeight();
		return (y - height) < 0;
	}
	
	/**
	 * Checks whether or not a given Entity is beyond the Arena's boundaries.
	 * @param e
	 * 		the Entity to check for.
	 * @return
	 * 		true if the Entity is outside the Arena, false otherwise.
	 */
	private boolean isOutside(Entity e) {
		double x = e.getX(), y = e.getY();
		if (x + e.getWidth() < 0 || x > mWidth || y - e.getHeight() > mHeight || y < 0) {
			System.out.printf("Projectile outside @ (%f, %f)\n", x, y);
			return true;
		}
		return false;
	}

	/**
	 * Gets the title of the arena.
	 * @return
	 * 		the title.
	 */
	public String getName() { return mName; }

	/**
	 * Gets the magnitude of acceleration due to gravity for the particular level.
	 * @return
	 * 		a Vector2D representing gravity in meters per second squared.
	 */
	public Vector2D getGravity() {
		return mGrav;
	}

	/**
	 * Sets the Arena's level of gravity.
	 * @param gravity
	 * 		the magnitude of gravity in meters per second squared.
	 */
	public void setGravity(double gravity) {
		mGrav.changeMagnitude(gravity);
	}

	/**
	 * Sets the Entities meant to appear in the level.
	 * @param acts
	 * 		an Iterable of Actors.
	 * @param projs
	 * 		an Iterable of Projectiles.
	 */
	public void setEntities(Iterable<Actor> acts, Iterable<Projectile> projs)
	{
		mActList = acts;
		mProjList = projs;
	}
	
	/**
	 * Gets the user-controlled Actor.
	 * @return
	 * 		the Actor representing the user.
	 */
	public Actor getPlayer() { return mPlayer; }
	
	/**
	 * Sets the Actor to be controlled by the user.
	 * @param player
	 * 		the user's Actor.
	 */
	public void setPlayer(Actor player) { mPlayer = player; }
	
	/**
	 * Gets the width of the playable area.
	 * @return
	 * 		the Arena's width boundary.
	 */
	public double getWidth() { return mWidth; }
	
	/**
	 * Gets the height of the playable area.
	 * @return
	 * 		the Arena's height boundary.
	 */
	public double getHeight() { return mHeight; }
}
