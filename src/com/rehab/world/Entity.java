package com.rehab.world;

import com.rehab.animation.Drawable;
import com.rehab.animation.Sprite;

public abstract class Entity implements OnHealthIncreaseListener, OnHealthDecreaseListener, OnMoveListener, Drawable {

	// Profile
	private String label;
	private int mInstanceId;
	private int faction;
	private Sprite mSprite;
	
	
	// Collision data
	private Hitbox mCollision;
	
	
	// Current health and maximum possible
	private double mHealth;
	private double mMaxHealth;
	
	
	// Physics data
	private double mLocationX;
	private double mLocationY;
	private double mDirection; // degrees
	
	private double mMass; // kilograms
	private double mSpeed; // meters/second^2
	
	
	// Callbacks
	private OnHealthIncreaseListener mHealthIncreaseListener;
	private OnHealthDecreaseListener mHealthDecreaseListener;
	private OnMoveListener mMoveListener;
	
	/**
	 * Sets the health of the Entity but obeys the set maximum health boundary. That is,
	 * setting the health of this instance beyond a maximum defined by setMaximumHealth()
	 * will only set the health to the maximum. Similarly, setting health below 0 will
	 * only set the health to 0.
	 * @param health
	 * 		the new amount of health.
	 */
	protected void setHealth(double health) {
		// Entity was set as immortal so health changes don't matter
		if (mMaxHealth <= 0) return;
		
		// Prevent health from going past maximum and disable instance if zeroed health
		if (health > mMaxHealth) {
			health = mMaxHealth;
		} else if (health <= 0) health = 0;
		
		// Update health values
		double oldHealth = mHealth;
		mHealth = health;
		
		// Trigger callbacks depending on change in health
		double diff = health - mHealth;
		if (mHealthIncreaseListener != null)
			if (diff >= 0) mHealthIncreaseListener.onHealthIncrease(oldHealth, mHealth);
			else mHealthDecreaseListener.onHealthDecrease(oldHealth, mHealth);
	}

	/**
	 * Sets the X and Y location of the Entity. This method has no interpolation of
	 * any kind and so location values will "teleport" to the new location given.
	 * @param x
	 * 		the new X coordinate.
	 * @param y
	 * 		the new Y coordinate.
	 */
	public void moveTo(double x, double y) {
		// Update location vals while remembering old
		double oldX = mLocationX;
		double oldY = mLocationY;
		mLocationX = x;
		mLocationY = y;
		
		// Trigger location callback
		if (mMoveListener != null) mMoveListener.onMove(oldX, oldY, mLocationX, mLocationY);
	}
	
	/**
	 * Checks whether or not the instance has collided with a given Entity.
	 * @param e
	 * 		the Entity the instance may have touched.
	 * @return
	 * 		true if the Entity was hit, false otherwise.
	 */
	public boolean collidesWith(Entity e) { return mCollision.intersects(e.mCollision); }
	
	public void destroyLinks() {
		mHealthIncreaseListener = null;
		mHealthDecreaseListener = null;
		mMoveListener = null;
	}
	
	protected void setCollisionModel(Hitbox h) { mCollision = h; }
	
	/**
	 * Sets the maximum boundary at which health is considered 100% or "full". If the
	 * maximum health value given is <= 0, then the Entity is treated as immortal and
	 * whose health cannot be changed, though damage may still be calculated against
	 * the instance. As such, health listeners will not be called but damage listeners
	 * maintain their function. To reverse the effect, set a maximum health > 0.
	 * @param maxHealth
	 * 		the highest number of health points allowed.
	 */
	protected void setMaximumHealth(double maxHealth) { mMaxHealth = maxHealth; }
	
	public void setId(int id) { mInstanceId = id; }
	
	/**
	 * Sets the instance's facing direction ranging from 0 to 360 degrees. Values larger
	 * than 360 will wrap around while negative values will be treated as direction in
	 * reverse. Eg. setDirection(-45) will set a direction of 315 degrees.
	 * @param direction
	 * 		the facing in degrees.
	 */
	public void setDirection(double direction) {
		mDirection = direction - (360 * Math.floor((direction / 360)));
	}
			
	public String getLabel() { return label; }
	
	/**
	 * Gets an instance's unique identifier.
	 * @return
	 * 		the unique identifier for the instance.
	 */
	public int getId() { return mInstanceId; }
	
	/**
	 * Gets the facing direction of an instance.
	 * @return
	 * 		the current direction in degrees.
	 */
	public double getDirection() { return mDirection; }
	
	public Sprite getSprite() { return mSprite; }
	
	public double getX() { return mLocationX; }
	
	public double getY() { return mLocationY; }
			
	public double getSpeed() { return mSpeed; }
	
	public int getFaction() { return faction; }
		
}
