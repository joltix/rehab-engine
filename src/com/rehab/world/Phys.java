package com.rehab.world;

public class Phys {

    // Constants for WASD unit vectors
    public static final Vector UNIT_EAST = new Vector(1, 0);
    public static final Vector UNIT_NORTH = new Vector(0, 1);
    public static final Vector UNIT_WEST = new Vector(0, 1);
    public static final Vector UNIT_SOUTH = new Vector(0, -1);

    // Constants for main 45 degree angles
    public static final Vector UNIT_NE = new Vector(1, 1);
    public static final Vector UNIT_NW = new Vector(-1, 1);
    public static final Vector UNIT_SE = new Vector(1, -1);
    public static final Vector UNIT_SW = new Vector(-1, -1);

    // Physics data
    private double mMass,
                    mSpeed = 0;

    // Current velocity and last velocity cached
    private Vector mVelocity = new Vector(0, 0),
                    mLastVelocity = new Vector(0, 0);

    // Flags
    private boolean mEnableGravity = true;

    /**
     * Basic constructor for a Phys using (at a minimum) mass without a set velocity.
     * @param mass
     * 		the object's mass in kilograms.
     */
    public Phys(double mass) { mMass = mass; }

	/**
	 * Applies a velocity vector to move the instance along.
	 * @param velocity
	 * 		the velocity Vector to affect the Entity.
	 */
    public void moveImpulse(Vector velocity) {
        // Save current velocity
        mLastVelocity.updateFrom(mVelocity);
        // Apply acceleration
        if (mSpeed == 0) mVelocity.add(velocity);
        else mVelocity.changeMagnitude(velocity.getMagnitude() + mVelocity.getMagnitude());
        // Enact location move and cache speed
        moveBy(mVelocity.endX - mVelocity.startX, mVelocity.endY - mVelocity.startY);
        mSpeed = mVelocity.getMagnitude();
    }

    /**
     * Moves the Phys instance by some x and y values.
     * @param x
     * 		the x coordinate shift.
     * @param y
     * 		the y coordinate shift.
     */
    private void moveBy(double x, double y) {
    	// Save velocity
    	mLastVelocity.updateFrom(mVelocity);
    	
    	mVelocity.startX += x;
    	mVelocity.startY += y;
    	mVelocity.endX += x;
    	mVelocity.endY += y;
    }

	/**
	 * Moves the Entity to the specified x and y coordinates. Calling this method will
	 * move the instance to the given coordinates but will erase direction and speed.
	 * The instance will relocate standing still.
	 * @param x
	 * 		the new x coordinate.
	 * @param y
	 * 		the new y coordinate.
	 * @param conserveVelocity
	 * 		true if the relative velocities should be maintained after the relocation,
	 * 		false otherwise.
	 */
    public void moveTo(double x, double y, boolean conserveVelocity) {
    	// Save vals as previous
    	mLastVelocity.updateFrom(mVelocity);
    	
        // Keep velocity after relocation
        if (conserveVelocity) {
        	mVelocity.endX += x - mVelocity.endX;
        	mVelocity.endY += y - mVelocity.endY;
        } else {
        	// Erase velocity
        	mVelocity.endX = x;
        	mVelocity.endY = y;
        }
        // Set new position
        mVelocity.startX = x;
        mVelocity.startY = y;
    }

	/**
	 * Gets the instance's x coordinate.
	 * @return
	 * 		the x location.
	 */
    public double getX() { return mVelocity.startX; }

	/**
	 * Gets the instance's y coordinate.
	 * @return
	 * 		the y location.
	 */
    public double getY() { return mVelocity.startY; }

    /**
     * Gets a unit Vector representing the current heading of the
     * Phys instance.
     * @return
     * 		the unit Vector.
     */
    public Vector getHeading() { return mVelocity.getUnitVector(); }
    
	/**
	 * Gets the instance's mass.
	 * @return
	 * 		mass in kilograms.
	 */
    public double getMass() { return mMass; }

	/**
	 * Gets the instance's speed.
	 * @return
	 * 		speed in meters per second.
	 */
    public double getSpeed() { return mSpeed; }

	/**
	 * Sets the instance's speed. Changing speed will result in a faster travel
	 * in the same direction as any previous heading.
	 * @param metersPerSecond
	 * 		speed in meters per second.
	 */
    public void setSpeed(double metersPerSecond) { mSpeed = metersPerSecond; }

	/**
	 * Checks whether or not the instance is affected by gravity.
	 * @return
	 * 		true if the instance is under the influence of gravity, false
	 *		otherwise.
	 */
    public boolean isGravityEnabled() { return mEnableGravity; }

	/**
	 * Sets whether or not the Entity should be pulled to the source of gravity.
	 * @param hasContact
	 *		true if the Entity should not be affected by gravity, false to
	 *		enable gravity.
	 */
    public void setEnableGravity(boolean enable) { mEnableGravity = enable; }

    public static class Vector {
        // The Vector's two points
        private double startX, startY,
                        endX, endY;

        /**
         * Constructor for a Vector with no velocity set at the origin for
         * location.
         */
        public Vector() {
        	startX = 0;
        	startY = 0;
        	endX = 0;
        	endY = 0;
        }

        /**
         * Constructor for a single-point Vector. Coordinates
         * passed in to this constructor can be retrieved via access
         * to the corresponding "end" variables.
         * @param x
         * 		the x coordinate defining heading.
         * @param y
         * 		the y coordinate defining heading.
         */
        public Vector(double x, double y) {
            endX = x;
            endY = y;
        }

        /**
         * Constructor for a dual-point Vector. Coordinates passed in to this
         * constructor can be retrieved via access to the "start" and "end"
         * variables.
         * @param x0
         * 		the starting point's x coordinate.
         * @param y0
         * 		the starting point's y coordinate.
         * @param x1
         * 		the ending point's x coordinate.
         * @param y1
         * 		the ending point's y coordinate.
         */
        public Vector(double x0, double y0, double x1, double y1) {
            startX = x0;
            startY = y0;
            endX = x1;
            endY = y1;
        }

        public Vector(Vector v) {
            startX = v.startX;
            startY = v.startY;
            endX = v.endX;
            endY = v.endY;
        }

        /**
         * Adds a given Vector to the calling Vector instance, shifting the
         * calling Vector's coordinates by the specified amounts.
         * @param v
         *      the Vector that should be added to the calling Vector.
         */
        public void add(Vector v) { add(v.startX, v.startY, v.endX, v.endY); }
        
        /**
         * Adds a set of scalars to the calling Vector instance, shifting the
         * calling Vector's coordinates by the specified amounts.
         * @param x0
         * 		the origin x shift.
         * @param y0
         * 		the origin y shift.
         * @param x1
         * 		the heading x shift.
         * @param y1
         * 		the heading y shift.
         */
        public void add(double x0, double y0, double x1, double y1) {
        	startX += x0;
        	startY += y0;
        	endX += x1;
        	endY += y1;
        }

        /**
         * Multiplies a scalar to both the starting and ending points of the Vector.
         * @param factor
         * 		the factor to multiply throughout the Vector.
         */
        public void multiply(double factor) {
            startX *= factor;
            startY *= factor;
            endX *= factor;
            endY *= factor;
        }

        /**
         * Flips the current Vector's heading in the opposite direction while maintaining
         * the starting point's x and y coordinates.
         */
        public void reverse() {
            endX *= -1;
            endY *= -1;
        }

        /**
         * Copies the values from a given Vector to the calling instance.
         * @param v
         * 		the Vector to take values from.
         */
        public void updateFrom(Vector v) {
            startX = v.startX;
            startY = v.startY;
            endX = v.endX;
            endY = v.endY;
        }

        /**
         * Sets the vector's magnitude to the specified value.
         * @param magnitude
         * 		the new length of the vector.
         */
        public void changeMagnitude(double magnitude) {
            // Calculate new coordinates
            Vector unitV = getUnitVector();
            unitV.multiply(magnitude);
            // Update coords
            endX = startX + (unitV.endX - unitV.startX);
            endY = startY + (unitV.endY - unitV.startY);
        }
        
        /**
         * Changes the starting coordinates to the given x and y value. The
         * heading coordinates will also relocate to maintain the
         * magnitude of the Vector.
         * @param x
         * 		the new x location.
         * @param y
         * 		the new y location.
         */
        public void rebase(double x, double y) {
        	endX += x - startX;
        	endY += y - startY;
        	startX = x;
        	startY = y;
        }

        /**
         * Gets a unit vector based off of the calling instance's heading. If
         * the calling Vector has a magnitude of 0, then a unit vector of the
         * same magnitude is produced.
         * @return
         * 		the unit vector.
         */
        public Vector getUnitVector() {
            double magnitude = getMagnitude();
            double x1 = endX - startX;
            double y1 = endY - startY;
            // Prevent division by 0
            if (magnitude == 0) return new Vector(x1, y1);
            return new Vector(x1 / magnitude, y1 / magnitude);
        }

        /**
         * Gets the magnitude of the Vector. This is also known as the distance
         * between the two points that define the Vector.
         * @return
         * 		the magnitude.
         */
        public double getMagnitude() {
            return Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        }

        /**
         * Returns the x coordinate of the Vector's normal.
         * @return
         * 		the normal's x coordinate.
         */
        public double getNormalX() { return (endY - startY) * -1; }

        /**
         * Returns the y coordinate of the Vector's normal.
         * @return
         * 		the normal's y coordinate.
         */
        public double getNormalY() { return (endX - startX); }
        
        /**
         * Gets the x location.
         * @return
         * 		the x coordinate.
         */
        public double getX() { return startX; }

        /**
         * Gets the y location.
         * @return
         * 		the y coordinate.
         */
        public double getY() { return startY; }

        /**
         * Gets the x coordinate denoting the heading of the Vector. That is,
         * this method returns the x coordinate that is not the starting
         * point for this Vector.
         * @return
         * 		the x heading.
         */
        public double getEndX() { return endX; }

        /**
         * Gets the y coordinate denoting the heading of the Vector. That is,
         * this method returns the y coordinate that is not the starting
         * point for this Vector.
         * @return
         * 		the y heading.
         */
        public double getEndY() { return endY; }
    }

}
