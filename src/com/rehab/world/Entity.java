package com.rehab.world;

import com.rehab.animation.Drawable;
import com.rehab.animation.Sprite;
import com.rehab.world.Register.Identifiable;

/**
 * <p>
 * Entity represents a game object that interacts with the game world and so is the
 * basis for many of the game's objects. Entities always have a Phys object to
 * represent its location and movement in 2D space but a Hitbox for collision and a
 * Sprite for visuals are optional.
 * </p>
 * 
 * <p>
 * Entities are not meant to be instantiated directly but instead subclassed. One common
 * subclass is {@link Actor}, which represents individuals in the game world and is the
 * class to use for game objects that fight. To instantiate a subclass of Entity, the
 * {@link InstanceManager} provides creation methods as shown below.
 * </p>
 * 
 * <pre>
 * 	<code>
 * InstanceManager manager = InstanceManager.getInstance();
 * Actor a = manager.createActor(62, 100);
 * 	</code>
 * </pre>
 * 
 * <p>
 * The code above creates an Actor with 62 kg of mass and a 100 health point maximum.
 * </p>
 */
public abstract class Entity extends Identifiable implements Drawable, OnMoveListener {

	// Physics and collision data
	private Phys mPhys;
	private Hitbox mCollision;
	private Sprite mSprite;

	// Stats
	private int mOwner;
	private double mMaxHealth;
	private double mHealth;

	// Flags
	private boolean mVisible = true;
	private boolean mDisabled = false;
	private boolean mMovable = true;

	// Callbacks
	private OnHealthIncreaseListener mHealthIncreaseListener;
	private OnHealthDecreaseListener mHealthDecreaseListener;
	private OnCollisionListener mOnCollisionListener;

	/**
	 * Constructor for an Entity with basic physics.
	 * 
	 * @param mass	mass in kilograms.
	 */
	protected Entity(double mass, double healthCap) {
		mPhys = new Phys(mass);
		mMaxHealth = healthCap;
		mHealth = mMaxHealth;
	}
	
	/**
	 * Constructor for cloning an Entity. The new Entity's properties are exactly the
	 * same as the Entity given as a template. This includes collision model, health,
	 * and other properties such as visibility but does not include any set listeners.
	 * 
	 * @param e	the Entity to clone.
	 */
	public Entity(Entity e) {
		// Copy physics, collision model, and sprite
		mPhys = new Phys(e.mPhys);
		mCollision = new Hitbox(e.mCollision);
		mSprite = e.mSprite;
		
		// Copy stats
		mOwner = e.mOwner;
		mMaxHealth = e.mMaxHealth;
		mHealth = e.mHealth;
		
		// Copy flags
		mVisible = e.mVisible;
		mDisabled = e.mDisabled;
		mMovable = e.mMovable;	
	}
	
	/**
	 * Moves the Entity using the current values set in its Phys.
	 * 
	 * @throws IllegalStateException	if the Entity is set as immobile.
	 * @see #moveBy(double, double)
	 * @see #moveTo(double, double)
	 */
	public void move() {
		ensureMobility();
		
		mPhys.move();
		syncModels();
	}

	/**
	 * Moves the Entity by some x and y values. Unlike moveTo(), this method shifts
	 * an Entity instead of snapping to a specific location. Calling this method will
	 * move the instance to the given coordinates but will erase direction and speed.
	 * The instance will relocate standing still.
	 * 
	 * @param x	the x value to shift by.
	 * @param y	the y value to shift by.
	 * @throws IllegalStateException	if the Entity is set as immobile.
	 * @see #moveImpulse(Vector)
	 * @see #moveTo(double, double)
	 */
	public void moveBy(double x, double y) {
		ensureMobility();
		
		mPhys.moveBy(x, y);
		syncModels();
	}
	
	/**
	 * Moves the Entity to the specified x and y coordinates.
	 * 
	 * @param x	the new x coordinate.
	 * @param y	the new y coordinate.
	 * @throws IllegalStateException	if the Entity is set
	 * as immobile.
	 * @see #moveBy(double, double)
	 * @see #moveImpulse(Vector)
	 */
	public void moveTo(double x, double y) {
		ensureMobility();
		
		mPhys.moveTo(x, y);
		syncModels();
	}
	
	/**
	 * Throws an Exception if the Entity should not be movable.
	 * 
	 * @throws IllegalStateException	if the Entity is set
	 * as immobile.
	 */
	private void ensureMobility() {
		if (!mMovable) {
			throw new IllegalStateException("Entity is set as immobile");
		}
	}
	
	/**
	 * Synchronizes the x and y coordinates of the collision model with the physics
	 * model.
	 */
	private void syncModels() {
		// Sync collision model
		if (mCollision != null) mCollision.moveTo(mPhys.getX(), mPhys.getY());
	}

	/**
	 * Checks whether or not the Entity has collided with another Entity. If a
	 * collision occurs, only the calling Entity's listener will be triggered.
	 * 
	 * @param e	the other Entity.
	 * @return	true if this Entity has collided, false otherwise if either
	 * Entities lack a Hitbox, either are disabled, or the other Entity is
	 * the calling Entity's owner.
	 * @see #isDisabled()
	 * @see #setOnCollisionListener(OnCollisionListener)
	 */
	public boolean collidesWith(Entity e) {
		// Silent fail when no collision or disabled
		if (mCollision == null || e.mCollision == null
				|| mDisabled || e.mDisabled) {
			return false;
		}
		
		// Don't damage owner
		if (e.getId() == mOwner) {
			return false;
		}

		boolean collides = mCollision.collidesWith(e.mCollision);
		// Trigger collision callback
		if (mOnCollisionListener != null && collides)
			mOnCollisionListener.onCollide(e);

		return collides;
	}

	/**
	 * Gets the instance's amount of health. The health will be between 0 and the
	 * maximum health set by a call to {@link #setMaximumHealth(double)}.
	 * 
	 * @return	the current health.
	 * @see	#setMaximumHealth(double)
	 * @see	#setHealth(double)
	 */
	public double getHealth() {
		return mHealth;
	}

	/**
	 * Sets the health of the Entity but obeys the set maximum health boundary. That is,
	 * setting the health of this instance beyond a maximum defined by {@link #setMaximumHealth(double)}
	 * will only set the health to the defined ceiling. Similarly, setting health less
	 * than 0 will only set the health to 0.
	 * 
	 * This method does not change an Entity's health if the maximum is set to -1. The
	 * effect makes the Entity immortal until a subsequent call changes the maximum
	 * health to a more normal value.
	 * 
	 * @param health	the new amount of health.
	 * @see	#getHealth()
	 * @see	#setMaximumHealth(double)
	 */
	protected void setHealth(double health) {
		// Entity was set as immortal so health changes don't matter
		if (mMaxHealth < 0) return;

		// Prevent health from going past maximum and disable instance if zeroed health
		if (health > mMaxHealth) {
			health = mMaxHealth;
		} else if (health <= 0) {
			health = 0;
		}

		// Update health values
		double oldHealth = mHealth;
		mHealth = health;

		// Trigger callbacks depending on change in health
		double diff = health - mHealth;
		if (mHealthIncreaseListener != null) {
			if (diff >= 0) {
				mHealthIncreaseListener.onHealthIncrease(oldHealth, mHealth);
			} else {
				mHealthDecreaseListener.onHealthDecrease(oldHealth, mHealth);
			}
		}
	}

	/**
	 * Sets the collision model to use for the Entity's collision reactions.
	 * 
	 * @param h	the Hitbox representing the Entity's collision.
	 * @see #collidesWith(Entity)
	 */
	protected void setCollisionModel(Hitbox h) { mCollision = h; }

	/**
	 * Sets the maximum boundary at which health is considered 100% or "full". If the
	 * maximum health value given is <= 0, then the Entity is treated as immortal and
	 * whose health cannot be changed, though damage may still be calculated against
	 * the instance. As such, health listeners will not be called but damage listeners
	 * maintain their function. To reverse the effect, set a maximum health > 0.
	 * 
	 * @param maxHealth	the highest number of health points allowed.
	 * @see #getHealth()
	 * @see #setHealth(double)
	 */
	protected void setMaximumHealth(double maxHealth) { mMaxHealth = maxHealth; }
	
	/**
	 * Gets the maximum health of the entity.
	 * 
	 * @return the max health of the entity.
	 */
	public double getMaximumHealth() { return mMaxHealth; }

	/**
	 * Gets the instance's physics model.
	 * 
	 * @return the Phys instance representing the Entity's physics.
	 */
	public Phys getPhysics() { return mPhys; }
	
	/**
	 * Gets the instance's collision model.
	 *
	 * @return the Hitbox instance representing the Entity's collision.
	 */
	public Hitbox getCollision() { return mCollision; }

	/**
	 * Gets the instance's visual representation.
	 * 
	 * @return	the Sprite to draw on-screen.
	 * @see #setSprite(Sprite)
	 */
	public Sprite getSprite() { return mSprite; }

	/**
	 * Sets the Sprite to represent the Entity on-screen.
	 * 
	 * @param sprite	the Sprite to draw on-screen.
	 * @see #getSprite()
	 */
	public void setSprite(Sprite sprite) { mSprite = sprite; }

	/**
	 * Gets the instance's x-coordinate.
	 * 
	 * @return	the x location.
	 * @see #getY()
	 */
	public double getX() {
		return mPhys.getX();
	}

	/**
	 * Gets the instance's y-coordinate.
	 * 
	 * @return	the y location.
	 * @see	#getX()
	 */
	public double getY() {
		return mPhys.getY();
	}
	
	/**
	 * Gets the instance's center x-coordinate
	 *
	 * @return the center x.
	 */
	public double getXCentered() {
		double w = this.getWidth();
		return mPhys.getX() + (w / 2);
	}

	/**
	 * Gets the instance's center y-coordinate.
	 *
	 * @return the center y.
	 */
	public double getYCentered() {
		double h = this.getWidth();
		return mPhys.getY() - (h / 2);
	}
	
	/**
	 * Gets the collidable width of the instance in pixels. If the Entity has no collision
	 * model set, this method retrieves the width of the Entity's Sprite. If neither are
	 * available, this method returns 0.
	 * 
	 * @return	width in pixels.
	 * @see	#getHeight()
	 */
	public double getWidth() {
		if (mCollision == null) {
			if (mSprite == null) {
				return 0;
			} else {
				return mSprite.getWidth();
			}
		} else {
			return mCollision.getWidth();
		}
	}

	/**
	 * Gets the collidable height of the instance in pixels. If the Entity has no collision
	 * model set, this method retrieves the height of the Entity's Sprite. If neither are
	 * available, this method returns 0.
	 * 
	 * @return	height in pixels.
	 * @see #getWidth()
	 */
	public double getHeight() {
		if (mCollision == null) {
			if (mSprite == null) {
				return 0;
			} else {
				return mSprite.getHeight();
			}
		} else {
			return mCollision.getHeight();
		}
	}

	/**
	 * Gets the instance's speed.
	 * 
	 * @return	speed in meters per second.
	 * @see #setSpeed(double)
	 */
	public double getSpeed() { return mPhys.getSpeed(); }

	/**
	 * Gets the instance's mass.
	 * 
	 * @return	mass in kilograms.
	 */
	public double getMass() { return mPhys.getMass(); }

	/**
	 * Checks whether or not the instance is affected by gravity.
	 * 
	 * @return	true if the instance is under the influence of gravity, false
	 * otherwise.
	 * @see #setEnableGravity(boolean)
	 */
	public boolean isGravityEnabled() { return mPhys.isGravityEnabled(); }

	/**
	 * Sets whether or not the Entity should be pulled to the source of gravity.
	 * 
	 * @param gravityEnabled	true if the Entity should not be affected by gravity, false to
	 * enable gravity.
	 * @see #isGravityEnabled()
	 */
	public void setEnableGravity(boolean gravityEnabled) {
		mPhys.setEnableGravity(gravityEnabled);
	}

	/**
	 * Checks whether or not the Entity should be drawn on-screen.
	 * 
	 * @return	true if the instance should be drawn and has a
	 * Sprite, false otherwise.
	 * @see #setVisibility(boolean)
	 */
	public boolean isVisible() {
		return mVisible && mSprite != null;
	}
	
	/**
	 * Sets whether or not the Entity should be drawn on-screen.
	 * 
	 * @param visible	true if the instance should be drawn, false otherwise.
	 * @see #isVisible()
	 */
	public void setVisibility(boolean visible) { mVisible = visible; }
	
	/**
	 * Checks whether or not the Entity has been disabled and should no
	 * longer be visible nor affected by gravity.
	 * 
	 * @return	true if disabled, false if enabled.
	 * @see #disable()
	 * @see #enable()
	 */
	public boolean isDisabled() { return mDisabled; }

	/**
	 * Enables an Entity. That is, the Entity becomes visible and is
	 * affected by gravity.
	 * 
	 * @see #disable()
	 * @see #isDisabled()
	 */
	public void enable() {
		mDisabled = false;
		setVisibility(true);
		setEnableGravity(true);
	}
	
	/**
	 * Disables an Entity. That is, the Entity is hidden and no longer
	 * affected by gravity.
	 * 
	 * @see #enable()
	 * @see #isDisabled()
	 */
	public void disable() {
		mDisabled = true;
		setVisibility(false);
		setEnableGravity(false);
	}
	
	/**
	 * Checks whether or not the Entity is not meant to move.
	 *
	 * @return true if the Entity does not move, false otherwise.
	 * @see #setMobility(boolean)
	 */
	public boolean isImmobile() {
		return !mMovable;
	}
	
	/**
	 * Sets whether or not the Entity's movement methods may be
	 * called.
	 *
	 * @param movable	true if the Entity should be movable, false
	 * otherwise.
	 * @see #isImmobile()
	 * 
	 */
	public void setMobility(boolean movable) {
		mMovable = movable;
	}

	/**
	 * Sets a listener for health increases.
	 * 
	 * @param listener	the callback.
	 * @see #setOnHealthDecreaseListener(OnHealthDecreaseListener)
	 */
	public void setOnHealthIncreaseListener(OnHealthIncreaseListener listener) {
		mHealthIncreaseListener = listener;
	}

	/**
	 * Sets a listener for health decreases.
	 * 
	 * @param listener	the callback.
	 * @see #setOnHealthIncreaseListener(OnHealthIncreaseListener)
	 */
	public void setOnHealthDecreaseListener(OnHealthDecreaseListener listener) {
		mHealthDecreaseListener = listener;
	}

	/**
	 * Sets a listener for collisions.
	 * 
	 * @param listener	the callback.
	 * @see #collidesWith(Entity)
	 */
	public void setOnCollisionListener(OnCollisionListener listener) {
		mOnCollisionListener = listener;
	}

	@Override
	public void onMove(double oldX, double oldY, double newX, double newY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getZ() {
		// TODO Auto-generated method stub
		return LayerManager.LAYER_FREE_1;
	}

	@Override
	public void onClick() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Returns a String representing the Entity in the format
	 * "{ [id] (x, y) }".
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{ [");
		builder.append(getId());
		builder.append("] ");
		builder.append('(');
		builder.append(getX());
		builder.append(", ");
		builder.append(getY());
		builder.append(") }");
		return builder.toString();
	}
	
	

}
