package com.rehab.world;

import com.rehab.world.Vector2D.Point;

public class Phys {

	private Vector2D mLastVelocity = new Vector2D(0, 0);
	
	private Vector2D mVelocity = new Vector2D(0, 0);
	
    // Physics data
    private double mMass;
    private double mSpeed = 0;

    private Point mLocation = new Point(0, 0);
	private Point mLastLocation = new Point(0, 0);

    // Flags
    private boolean mEnableGravity = true;

    /**
     * Basic constructor for a Phys using (at a minimum) mass without a set velocity.
     * @param mass
     * 		the object's mass in kilograms.
     */
    public Phys(double mass) { mMass = mass; }

	/**
	 * Applies a velocity vector to move the instance along. The Vector to add
	 * must start at origin.
	 * 
	 * @param velocity	the velocity Vector to affect the Entity.
	 */
    public void moveImpulse(Vector2D velocity) {
    	saveState();
    	
    	double x = mLocation.getX();
    	double y = mLocation.getY();
    	mVelocity.add(velocity);
    	mSpeed = mVelocity.magnitude();
    	
    	// Move
    	mLocation.setX(x + mVelocity.getX());
    	mLocation.setY(y + mVelocity.getY());
    }
    
    /**
     * Moves the Phys by some x and y values. The coordinates are shifted by adding the
     * given values to the corresponding coordinates.
     *
     * @param x	the change in x.
     * @param y	the change in y.
     * @throws IllegalArgumentException	when either x or y-coordinate is equivalent to
     * {@link Double#NaN}.
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
    
    public double getLastX() {
    	return mLastLocation.getX();
    }
    
    public double getLastY() {
    	return mLastLocation.getY();
    }

    /**
     * Gets the current velocity vector. Altering the values of the returned
     * Vector2D will not affect the Phys instance's velocity vector.
     * 
     * @return	a copy of the velocity vector.
     * @see #getHeading()
     * @see #getSpeed()
     */
    public Vector2D getVelocity()
    {
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
    public double getMass() {
    	return mMass;
    }

	/**
	 * Gets the instance's speed.
	 * 
	 * @return	speed in meters per second.
	 * @see #getVelocity()
	 */
    public double getSpeed() {
    	return mSpeed;
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
