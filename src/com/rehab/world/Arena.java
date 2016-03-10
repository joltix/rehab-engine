package com.rehab.world;

import com.rehab.world.Phys.Vector;

public class Arena {

	// Playable bounds on-screen
	private double mWidth, mHeight;
	// Percentage of a real second as the game's smallest unit of time
	private double mUnitTime;
	
	// Title of the level
	private String mName;
	// Default gravity is at Earth level
	private Vector mGrav;

	// Entities to keep track of (those in the level)
	private Iterable<Entity> mEntities;
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
		mGrav = new Vector();
		mGrav.updateFrom(Phys.UNIT_SOUTH);
		mGrav.changeMagnitude(gravStrength);
	}
	
	/**
	 * Sets the unit of time to use for Entity physics.
	 * @param tickRate
	 * 		a fraction of a second.
	 */
	public void setTime(double tickRate) { mUnitTime = 1d / tickRate; }

	/**
	 * [INCOMPLETE] Calculates the level's current game state. This includes instance locations, health
	 * and damage dealing, etc. In its current form, this method should only be used to
	 * test various reactions and / or functions and their values in a one-shot manner.
	 * Once an Entity reaches the floor of the Arena (getHeight() = 0), nothing more can
	 * be said of what will occur.
	 */
	public void step() {
		for (Entity e : mEntities) {

			// Disable objects beyond the screen
			if (isOutside(e))
				disable(e);
			
			// Apply gravity
			if (e.isGravityEnabled()) {
				applyGravity(e);
				for (Entity other : mEntities) {
					// Check collisions
					if (e == other) continue;
					if (e.collidesWith(other)) {
						// Snap object to floor
						e.moveTo(e.getX(), other.getY() + e.getHeight());
						e.setEnableGravity(false);
					}
				}
 			}
			
			// Snap the character to the surface of the floor if sinks
			if (isBelow(e)) {
				double h = e.getHeight();
				e.moveTo(e.getX(), h);
				e.setEnableGravity(false);
			}

		}
	}

	/**
	 * Applies the Arena's gravity vector to a given Entity.
	 * @param e
	 * 		the Entity to affect.
	 */
	private void applyGravity(Entity e) {
		Vector force = new Vector(mGrav);
		// Scale force by time
		force.multiply(mUnitTime);
		e.moveImpulse(force);
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
		if (x + e.getWidth() < 0 || x > mWidth || y - e.getHeight() > mHeight || y < 0)
			return true;
		return false;
	}
	
	/**
	 * Sets the Entity to no longer be drawn, disables gravity, and unloads the
	 * instance with the InstanceManager.
	 * @param e
	 * 		the Entity to disable.
	 */
	private void disable(Entity e) {
		e.setVisibility(false);
		e.setEnableGravity(false);
		// Take it out of the game
		InstanceManager.getInstance().unload(e);
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
	 * 		a Vector representing gravity in meters per second squared.
	 */
	public Vector getGravity() { return mGrav; }

	/**
	 * Sets the Arena's level of gravity.
	 * @param gravity
	 * 		the magnitude of gravity in meters per second squared.
	 */
	public void setGravity(double gravity) {
		if (mGrav == null) mGrav = new Vector();
		mGrav = new Vector();
		mGrav.updateFrom(Phys.UNIT_SOUTH);
	}

	/**
	 * Sets the Entities meant to appear in the level.
	 * @param it
	 * 		an Iterable of Entities in the level.
	 */
	public void setEntities(Iterable<Entity> iter) { mEntities = iter; }
	
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
