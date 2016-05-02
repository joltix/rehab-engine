package com.rehab.world;


public class Arena {

	// Playable bounds on-screen
	private double mWidth, mHeight;
	// Percentage of a real second as the game's smallest unit of time
	private double mUnitTime;
	
	// Title of the level
	private String mName;
	// Default gravity is at Earth level
	private Vector2D mGravDirection = new Vector2D(Vector2D.UNIT_SOUTH);
	private double mGravMagnitude = 0;

	// Entities to keep track of (those in the level)
	private Iterable<Actor> mActList;
	private Iterable<Projectile> mProjList;
	private Iterable<Prop> mPropList;
	
	
	private Actor mPlayer;
	private Prop mFloor;
	
	/**
	 * Constructor for a basic Arena.
	 * 
	 * @param name	a title for the level.
	 * @param width	the playable area's width.
	 * @param height	the playable area's height.
	 * @param floor	Prop representing the game floor.
	 */
	public Arena(String name, double width, double height, Prop floor) {
		mName = name;
		mWidth = width;
		mHeight = height;
		mFloor = floor;

		// Setup gravity Vector
		mGravMagnitude = Physics.getPlanetGravity(Physics.EARTH_MASS, Physics.EARTH_RADIUS);
		mGravDirection.changeMagnitude(mGravMagnitude);
	}
	
	/**
	 * Sets the unit of time to use for Entity physics.
	 * 
	 * @param tickRate	a fraction of a second.
	 */
	public void setTimescale(double tickRate) {
		
		mUnitTime = tickRate;
		// Let all Phys instances know the scale for computations
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
			
			// Disable objects beyond the screen
			if (isOutside(a)) {
				System.out.println("Disabled obj: " + a.getId());
				a.disable();
			}
			
			// Apply gravity
			if (a.isGravityEnabled()) {
				
				a.move();
				
				for (Entity other : mActList) {
					// Skip collision with itself
					if (a == other) continue;
					if (a.collidesWith(other)) {
						System.out.printf("Actor collision: [%d] (%f, %f) with [%d]\n", a.getId(), a.getX(), a.getY(), other.getId());
						// Snap object to floor
						if (other == mFloor) {
							snapToFloor(a);
						}
					}
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
	
	/**
	 * Moves the Actor to the surface of the Arena's floor.
	 * 
	 * @param a	Actor to relocate.
	 */
	private void snapToFloor(Actor a) {
		double locationY = a.getHeight() + mFloor.getHeight();
		System.out.printf("Snapping to location Y %f with actor height(%f) and floor height (%f)\n", locationY, a.getHeight(), mFloor.getHeight());
		a.moveTo(a.getX(), locationY);
		a.setEnableGravity(false);
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
				
				// Test collision with all Actors
				boolean collides = false;
				for (Actor a : mActList) {
					if (collides = p.collidesWith(a)) {
						System.out.printf("Projectile(%d) collision with Actor(%d)! Disabling Projectile %s\n", p.getId(), a.getId(), p);
						p.disable();
						break;
					}
				}
				
				// Test collision with all Props
				for (Prop prop : mPropList) {
					if ((prop != mFloor) && (collides = p.collidesWith(prop))) {
						System.out.printf("Projectile(%d) collision with Prop(%d)! Disabling Projectile %s\n", p.getId(), prop.getId(), p);
						p.disable();
						break;
					}					
				}
				
				// Move again only if no collision
				if (!collides) {
					p.move();
				}
			}
			
		}
	}
	
	/**
	 * Checks whether or not an Entity is below the Arena's boundaries.
	 * 
	 * @param e	the Entity whose location must be checked.
	 * @return true if the Entity is below the Arena, false otherwise.
	 */
	private boolean isBelow(Entity e) {
		double y = e.getY();
		double height = e.getHeight();
		return (y - height) < 0;
	}
	
	/**
	 * Checks whether or not a given Entity is beyond the Arena's boundaries.
	 * 
	 * @param e	the Entity to check for.
	 * @return	true if the Entity is outside the Arena, false otherwise.
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
	 * 
	 * @return the title.
	 */
	public String getName() { return mName; }

	/**
	 * Sets the Entities meant to appear in the level.
	 * 
	 * @param acts	an Iterable of Actors.
	 * @param projs	an Iterable of Projectiles.
	 * @param props an Iterable of Props.
	 */
	public void setEntities(Iterable<Actor> acts, Iterable<Projectile> projs, Iterable<Prop> props) {
		mActList = acts;
		mProjList = projs;
		mPropList = props;
		
		// Set gravity for each Actor
		for (Actor a : mActList) {
			Phys phys = a.getPhysics();
			phys.setAcceleration(mGravMagnitude);
			phys.setVelocity(mGravDirection.getX(), mGravDirection.getY(), 1);
		}
	}
	
	/**
	 * Gets the user-controlled Actor.
	 * 
	 * @return the Actor representing the user.
	 */
	public Actor getPlayer() { return mPlayer; }
	
	/**
	 * Sets the Actor to be controlled by the user.
	 * 
	 * @param player	the user's Actor.
	 */
	public void setPlayer(Actor player) { mPlayer = player; }
	
	/**
	 * Gets the width of the playable area.
	 * 
	 * @return the Arena's width boundary.
	 */
	public double getWidth() { return mWidth; }
	
	/**
	 * Gets the height of the playable area.
	 * 
	 * @return the Arena's height boundary.
	 */
	public double getHeight() { return mHeight; }
}
