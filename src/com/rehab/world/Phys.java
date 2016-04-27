package com.rehab.world;

import com.rehab.world.Vector2D.Point;

public class Phys {

	// Tickrate of loop
	private static double mTimescale = 0;
	
	// Velocity
	private Vector2D mVelocity = new Vector2D(0, 0);
	private Vector2D mLastVelocity = new Vector2D(0, 0);
		
	// Current position
	private Point mLocation = new Point(0, 0);
	private Point mLastLocation = new Point(0, 0);
	
    // Physics data
	private double mMass;
	private double mSpeed = 0;
	private double mAcceleration = 0;

    // Flags
	private boolean mEnableGravity = false;

    /**
     * Basic constructor for a Phys using (at a minimum) mass without a set velocity.
     * 
     * @param mass	the object's mass in kilograms.
     */
    public Phys(double mass) {
    	mMass = mass;
    }
    
    /**
     * Constructor for cloning a Phys. The new Phys will have all the same values
     * for its properties as the Phys that is given as an argument.
     * 
     * @param phys	the Phys to clone.
     */
    public Phys(Phys phys) {
    	// Copy velocity
    	mLastVelocity = new Vector2D(phys.mLastVelocity);
    	mVelocity = new Vector2D(phys.mVelocity);
    	// Copy stats
    	mMass = phys.mMass;
    	mSpeed = phys.mSpeed;
    	// Copy location
    	mLocation = new Point(phys.mLocation);
    	mLastLocation = new Point(phys.mLastLocation);
    	// Copy flags
    	mEnableGravity = phys.mEnableGravity;
    }
    
    /**
	 * Sets the fraction of time to be used in physics operations.
	 *
	 * @param scale	the percentage of a "moment" in reality, depending
	 * on the unit of time defined as a moment.
	 */
	public static void syncTimescale(double scale) {
		if (mTimescale != 0) {
			throw new IllegalStateException("Timescale must only be set once");
		}
		if (mTimescale < 0 && mTimescale >= 1) {
			throw new IllegalArgumentException("Timescale may only be < 0 or >= 1");
		}
		mTimescale = scale;
	}

	/**
     * Moves the Phys to the next location based off of the current velocity.
     * If the speed is < 0.5 or > -0.5, this method has no effect and the
     * Phys' previous velocity and location remains unchanged. To set an initial
     * direction and speed for velocity, see {@link #setVelocity(double, double, double)}.
     * 
     * @see #moveBy(double, double)
     * @see #moveTo(double, double)
     */
    public void move() {
    	// Don't move if no speed
    	if (mSpeed < 0.5 && mSpeed > -0.5) {
    		return;
    	}
    	// Remember last location before move
    	saveState();
    	
    	// Accelerate
    	mSpeed += mAcceleration;
    	mVelocity.add(mSpeed);
    	
    	// Compute next location according to velocity
    	double locationX = mLocation.getX() + mVelocity.getX();
    	double locationY = mLocation.getY() + mVelocity.getY();
    	    	
    	// Change locations
    	mLocation.setX(locationX);
    	mLocation.setY(locationY);
    }
    
    /**
     * Moves the Phys by some x and y values. The coordinates are shifted by adding the
     * given values to the corresponding coordinates.
     *
     * @param x	the change in x.
     * @param y	the change in y.
     * @throws IllegalArgumentException	when either x or y-coordinate is equivalent to
     * {@link Double#NaN}.
     * @see #move()
     * @see #moveTo(double, double)
     */
    public void moveBy(double x, double y) {
    	ensureCoordinateValidity(x, y);
    	saveState();
    	
    	// Change location
    	mLocation.setX(mLocation.getX() + x);
    	mLocation.setY(mLocation.getY() + y);
    }

	/**
	 * Moves the Entity to the specified x and y coordinates.
	 * 
	 * @param x	the new x coordinate.
	 * @param y	the new y coordinate.
     * @throws IllegalArgumentException	when either x or y-coordinate is equivalent to
     * {@link Double#NaN}.
     * @see #move()
     * @see #moveBy(double, double)
	 */
    public void moveTo(double x, double y) {
    	ensureCoordinateValidity(x, y);
    	saveState();
    	
    	// Change location
    	mLocation.setX(x);
    	mLocation.setY(y);
    }
    
    /**
     * Throws an {@link Exception} if either of the given values are equivalent to {@link Double#NaN}.
     *
     * @param x	the x value.
     * @param y	the y value.
     * @throws IllegalArgumentException	if either value equals {@link Double#NaN}.
     */
    private void ensureCoordinateValidity(double x, double y) {
    	if (x == Double.NaN) {
    		throw new IllegalArgumentException("x must be a number");
    	}
    	if (x == Double.NaN) {
    		throw new IllegalArgumentException("y must be a number");
    	}
    }
    
    /**
     * Saves the Phys's location and velocity for later use or rollback. This method
     * is called whenever the Phys's state is about to change.
     */
    private void saveState() {
    	// Save location
    	mLastLocation.setX(mLocation.getX());
    	mLastLocation.setY(mLocation.getY());
    }

	/**
	 * Gets the Phys's x-coordinate.
	 * 
	 * @return	the x-location.
	 * @see #getY()
	 */
    public double getX() {
    	return mLocation.getX();
    }

	/**
	 * Gets the Phys's y-coordinate.
	 * 
	 * @return	the y-location.
	 * @see #getX()
	 */
    public double getY() {
    	return mLocation.getY();
    }

    /**
     * Gets the current velocity vector. Altering the values of the returned
     * Vector2D will not affect the Phys instance's velocity vector.
     * 
     * @return	a copy of the velocity vector.
     * @see #getHeading()
     * @see #getSpeed()
     */
    public Vector2D getVelocity() {
    	return new Vector2D(mVelocity);
    }
    
    /**
     * Gets a unit vector representing the direction of the Phys's movement.
     * 
     * @return	the unit Vector.
     * @see #getVelocity()
     * @see #getSpeed()
     */
    public Vector2D getHeading() {
    	return mVelocity.getUnitVector();
    }

	/**
	 * Gets the instance's mass.
	 * 
	 * @return	mass in kilograms.
	 */
    public double getMass() { return mMass; }

	/**
	 * Gets the speed used during move calls.
	 * 
	 * @return	speed in feet per second.
	 * @see #getVelocity()
	 */
    public double getSpeed() { return mSpeed; }
    
    /**
     * Sets the speed to be used during move calls. If the current speed is 0, then
     * this method does nothing since direction is not taken into account and so no
     * state is saved. If {@link #isMoving()} returns false, then calling this function
     * will have no effect. To set a path and travel speed, see {@link #setVelocity(double, double, double)}.
	 * This method should be used to alter speed and not initialize it.
     *
     * @param speed	the speed in feet per second.
     * @throw IllegalArgumentException	if the speed is < 0.
     */
    public void setSpeed(double speed) {
    	if (speed < 0) {
    		throw new IllegalArgumentException("Speed must not be negative");
    	} else if (!isMoving()) {
    		// Bail out if no speed (and thus no direction)
    		return;
    	}
    	// Save velocity as past
    	saveState();
    	
    	// Update speed
    	mSpeed = speed;
    	mVelocity.changeMagnitude(speed);
    }
    
    
    /**
	 * Sets the direction quantity of the current velocity while preserving the
	 * speed. If {@link #isMoving()} returns false, then calling this function will
	 * have no effect. To set a path and travel speed, see {@link #setVelocity(double, double, double)}.
	 * This method should be used to alter direction and not initialize it.
	 *
	 * @param x	the x-coordinate of the direction.
	 * @param y	the y-coordinate of the direction.
	 */
	public void setDirection(double x, double y) {
		if (!isMoving()) {
			return;
		}
		saveState();
		
		// Update direction
		Point headPoint = mVelocity.getPoint();
		headPoint.setX(x);
		headPoint.setY(y);
		
		// Apply original speed
		mVelocity.changeMagnitude(mSpeed);
	}

	/**
	 * Sets the direction and speed of the Projectile. The x and y-coordinates
	 * specified will not imply a certain magnitude based off of the current
	 * location and the new direction. The speed parameter determines the
	 * magnitude of the direction. This method will save the Phys' previous
	 * state.
	 *
	 * @param x	the target location's x-coordinate.
	 * @param y	the target location's y-coordinate.
	 * @param speed	magnitude of vector.
	 * @throws IllegalArgumentException	if speed < 0
	 */
	public void setVelocity(double x, double y, double speed) {
    	if (speed < 0) {
    		throw new IllegalArgumentException("Speed must not be negative");
    	}
    	saveState();
    	
    	// Update direction
    	Point heading = mVelocity.getPoint();
    	heading.setX(x);
    	heading.setY(y);
    	
    	// Update speed
    	mSpeed = speed;
    	mVelocity.changeMagnitude(speed);
    }

    /**
     * Gets the acceleration used during move calls.
     *
     * @return	acceleration in feet per second per second.
     */
    public double getAcceleration() { return mAcceleration; }
    
	/**
	 * Sets the acceleration to be used during move calls.
	 *
	 * @param acceleration	the acceleration in feet per second per second.
	 */
	public void setAcceleration(double acceleration) {
		mAcceleration = acceleration * mTimescale;
	}
	
	/**
	 * Checks whether or not the instance's velocity is non-zero. If this method
	 * returns true, sebsequent calls to {@link #move()} will have no effect.
	 *
	 * @return true if the Phys has a non-zero speed, false otherwise.
	 */
	public boolean isMoving() {
		return mSpeed > 0;
	}
	
	/**
	 * Checks whether or not the instance is affected by gravity.
	 * @return
	 * 		true if the instance is under the influence of gravity, false
	 *		otherwise.
	 */
    public boolean isGravityEnabled() {
    	return mEnableGravity;
    }

	/**
	 * Sets whether or not the Entity should be pulled to the source of gravity.
	 * @param hasContact
	 *		true if the Entity should not be affected by gravity, false to
	 *		enable gravity.
	 */
    public void setEnableGravity(boolean enable) {
    	mEnableGravity = enable;
    }

}
