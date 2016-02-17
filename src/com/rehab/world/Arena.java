package com.rehab.world;

public class Arena {
	
	// Playable bounds on-screen
	private double mWidth, mHeight;
	// Entities to keep track of (those in the level)
	private Iterable<Entity> mEntities;
	// Title of the level
	private String mName;
	// Default gravity is at Earth level
	private double mGrav = Physics.getPlanetGravity(Physics.EARTH_MASS, Physics.EARTH_RADIUS);

	/**
	 * Prevents default instantiation
	 */
	@SuppressWarnings("unused")
	private Arena() {  }
	
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
	}

	/**
	 * Calculates the level's current game state. This includes instance
	 * locations, health and damage dealing, etc.
	 */
	public void step() {
		for (Entity e : mEntities) {
			
			// Apply gravity
			if (e.hasContact()) applyForce(e, mGrav * e.getMass(), 270, 1d);
			
			// Snap the character to the surface of the floor if sinks
			if (isBelowScreen(e)) {
				e.moveTo(e.getX(), mHeight - e.getHeight());
				e.setContact(true);
			}
			
		}
	}
	
	private void applyForce(Entity e, double force, double direction, double timeScale) {
		double acceleration = force / e.getMass();
		e.setSpeed(e.getSpeed() + (acceleration * timeScale));
		double currentSpeed = e.getSpeed();
		
		// Figure next location
		double newX = Physics.atDistanceWithAngleX(direction, currentSpeed);
		double newY = Physics.atDistanceWithAngleY(direction, currentSpeed);
		e.moveBy(newX, newY);
	}
	
	private void jump(Entity e) {
		applyForce(e, e.getMass() * 10, 90, 1d);
	}
	
	/**
	 * Checks whether or not an Entity is below the screen.
	 * @param e
	 * 		the Entity whose location must be checked.
	 * @return
	 * 		true if the Entity is below screen, false otherwise.
	 */
	private boolean isBelowScreen(Entity e) {
		double y = e.getY();
		double height = e.getHeight();
		return (y + height) > mHeight;
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
	 * 		the magnitude of gravity in meters per second squared.
	 */
	public double gravity() { return mGrav; }
	
	/**
	 * Sets the Arena's level of gravity.
	 * @param gravity
	 * 		the magnitude of gravity in meters per second squared.
	 */
	public void setGravity(double gravity) { mGrav = gravity; }
	
	/**
	 * Sets the Entities meant to appear in the level.
	 * @param it
	 * 		an Iterable of Entities in the level.
	 */
	public void setEntities(Iterable<Entity> iter) { mEntities = iter; }
	
}
