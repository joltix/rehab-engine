package com.rehab.world;

import com.rehab.animation.Drawable;
import com.rehab.animation.Sprite;
import com.rehab.world.Phys.Vector;

public abstract class Entity implements OnHealthIncreaseListener, OnHealthDecreaseListener, OnMoveListener, Drawable {

	// Profile
	private int mInstanceId;
	private Sprite mSprite;

	// Physics and collision data
	private Phys mPhys;
	private Hitbox mCollision;

	// Stats
	private double mHealth;
	private double mMaxHealth;

	// Flags
	private boolean mVisible;

	// Callbacks
	private OnHealthIncreaseListener mHealthIncreaseListener;
	private OnHealthDecreaseListener mHealthDecreaseListener;

	/**
	 * Constructor for an Entity with basic physics.
	 * @param mass
	 * 		mass in kilograms.
	 */
	protected Entity(double mass) {
		mPhys = new Phys(mass);
	}

	/**
	 * Applies a force vector to move the instance along.
	 * @param force
	 * 		the force Vector to affect the Entity.
	 */
	public void moveImpulse(Vector force) {
		// Apply force
		mPhys.moveImpulse(force);
		// Synchronize physics and collision's new locations
		syncModels();
	}

	/**
	 * Moves the Entity to the specified x and y coordinates. Calling this method will
	 * move the instance to the given coordinates but will erase direction and speed.
	 * The instance will relocate standing still.
	 * @param x
	 * 		the new x coordinate.
	 * @param y
	 * 		the new y coordinate.
	 */
	public void moveTo(double x, double y) {
		mPhys.moveTo(x, y, false);
		syncModels();
	}

	/**
	 * Moves the Entity by some x and y values. Unlike moveTo(), this method shifts
	 * an Entity instead of snapping to a specific location. Calling this method will
	 * move the instance to the given coordinates but will erase direction and speed.
	 * The instance will relocate standing still.
	 * @param x
	 * 		the x value to shift by.
	 * @param y
	 * 		the y value to shift by.
	 */
	public void moveBy(double x, double y) {
		mPhys.moveBy(x, y);
		//mPhys.moveTo(mPhys.getX() + x, mPhys.getX() + y, false);
		syncModels();
	}

	/**
	 * Synchronizes the x and y coordinates of the collision model with the physics
	 * model.
	 */
	private void syncModels() {
		// Sync collision model
		if (mCollision != null) mCollision.moveTo(mPhys.getX(), mPhys.getY());
	}
	
	public boolean collidesWith(Entity e) {
		if (mCollision == null || e.mCollision == null) return false;
		return mCollision.collidesWith(e.mCollision);
	}
	
	public boolean separate() {
		if (mCollision == null) return false;
		Vector heading = mPhys.getHeading();
		// Reverse and scale the heading's length for separation
		heading.reverse();	
		double sepDist = mCollision.getSeparationDistance();
		heading.changeMagnitude(sepDist);
		heading.rebase(mPhys.getX(), mPhys.getY());
		
		// Move
		moveTo(heading.getEndX(), heading.getEndY());
		return true;
	}

	/**
	 * Gets the instance's amount of health. The health will be between 0 and the
	 * maximum health set by a call to setMaximumHealth().
	 * @return
	 * 		the current health.
	 */
	public double getHealth() { return mHealth; }

	/**
	 * Sets the health of the Entity but obeys the set maximum health boundary. That is,
	 * setting the health of this instance beyond a maximum defined by setMaximumHealth()
	 * will only set the health to the defined maximum. Similarly, setting health less
	 * than 0 will only set the health to 0.
	 * @param health
	 * 		the new amount of health.
	 */
	protected void setHealth(double health) {
		// Entity was set as immortal so health changes don't matter
		if (mMaxHealth < 0) return;

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
	 * Sets the health decrease listener for the entity.
	 * @param listener
	 */
	public void setOnHealthDecreaseListener(OnHealthDecreaseListener listener){
		mHealthDecreaseListener = listener;
	}
	
	/**
	 * Sets the health increase listener for the entity.
	 * @param listener
	 */
	public void setOnHealthIncreaseListener(OnHealthIncreaseListener listener){
		mHealthIncreaseListener = listener;
	}

	/**
	 * Sets the collision model to use for the Entity's collision reactions.
	 * @param h
	 * 		the Hitbox representing the Entity's collision.
	 */
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
	
	/**
	 * Gets the maximum health of the entity.
	 * @return
	 * 		the max health of the entity.
	 */
	public double getMaximumHealth() {return mMaxHealth;}

	/**
	 * Gets the instance's physics model.
	 * @return
	 * 		the Phys instance representing the Entity's physics.
	 */
	public Phys getPhysics() { return mPhys; }

	/**
	 * Gets the instance's visual representation.
	 * @return
	 * 		the Sprite to draw on-screen.
	 */
	public Sprite getSprite() { return mSprite; }

	/**
	 * Sets the Sprite to represent the Entity on-screen.
	 * @param sprite
	 * 		the Sprite to draw on-screen.
	 */
	public void setSprite(Sprite sprite) { mSprite = sprite; }

	/**
	 * Gets an instance's unique identifier.
	 * @return
	 * 		the unique identifier for the instance.
	 */
	public int getId() { return mInstanceId; }

	/**
	 * Sets an instance's unique identifier.
	 * @param id
	 * 		the unique identifier for the instance.
	 */
	public void setId(int id) { mInstanceId = id; }

	/**
	 * Gets the instance's x coordinate.
	 * @return
	 * 		the x location.
	 */
	public double getX() { return mPhys.getX(); }

	/**
	 * Gets the instance's y coordinate.
	 * @return
	 * 		the y location.
	 */
	public double getY() { return mPhys.getY(); }

	/**
	 * Gets the collidable width of the instance in pixels. If the Entity has no collision
	 * model set, this method returns 0.
	 * @return
	 * 		width in pixels.
	 */
	public double getWidth() {
		if (mCollision == null)
			if (mSprite == null) return 0;
			else return mSprite.getWidth();
		else return mCollision.getWidth();
	}

	/**
	 * Gets the collidable height of the instance in pixels. If the Entity has no collision
	 * model set, this method returns 0.
	 * @return
	 * 		height in pixels.
	 */
	public double getHeight() {
		if (mCollision == null)
			if (mSprite == null) return 0;
			else return mSprite.getHeight();
		else return mCollision.getHeight();
	}

	/**
	 * Gets the instance's speed.
	 * @return
	 * 		speed in meters per second.
	 */
	public double getSpeed() { return mPhys.getSpeed(); }

	/**
	 * Sets the instance's speed. Changing speed will result in a faster travel
	 * in the same direction as any previous heading.
	 * @param metersPerSecond
	 * 		speed in meters per second.
	 */
	public void setSpeed(double metersPerSecond) { mPhys.setSpeed(metersPerSecond); }

	/**
	 * Gets the instance's mass.
	 * @return
	 * 		mass in kilograms.
	 */
	public double getMass() { return mPhys.getMass(); }

	/**
	 * Checks whether or not the instance is affected by gravity.
	 * @return
	 * 		true if the instance is under the influence of gravity, false
	 *		otherwise.
	 */
	public boolean isGravityEnabled() { return mPhys.isGravityEnabled(); }

	/**
	 * Sets whether or not the Entity should be pulled to the source of gravity.
	 * @param hasContact
	 *		true if the Entity should not be affected by gravity, false to
	 *		enable gravity.
	 */
	public void setEnableGravity(boolean gravityEnabled) { mPhys.setEnableGravity(gravityEnabled); }

	/**
	 * Checks whether or not the Entity should be drawn on-screen.
	 * @return
	 * 		true if the instance should be drawn, false otherwise.
	 */
	public boolean isVisible() { return mVisible; }

	/**
	 * Sets whether or not the Entity should be drawn on-screen.
	 * @param visible
	 * 		true if the instance should be drawn, false otherwise.
	 */
	public void setVisibility(boolean visible) { mVisible = visible; }

}
