package com.rehab.world;

/**
 * <p>
 * Vector2D supports operations akin to vector math in linear algebra. This class
 * is used heavily in {@link Hitbox} collisions as well as {@link Phys} motions.
 * </p>
 * 
 * <p>
 * A Vector2D maintains two {@link Point}s designated as a base and a head. The
 * base point is by default at origin (0, 0) unless explicitly defined. As such, a
 * Vector2D may be used to represent a line with a displacement from origin. The
 * head point holds the coordinates often associated with the
 * definition of a vector as its point.
 * </p>
 *
 *<p>
 * This class supports some vector operations such as vector addition and 
 * normalization in the way of {@link #add(Vector2D)} and {@link #normalize()},
 * amongst others.
 *</p>
 *
 */
public class Vector2D {
	

	/**
	 * Unit vector in the right direction.
	 */
    public static final Vector2D UNIT_EAST = new Vector2D(1, 0);
    /**
     * Unit vector in the up direction.
     */
    public static final Vector2D UNIT_NORTH = new Vector2D(0, 1);
    /**
     * Unit vector in the left direction.
     */
    public static final Vector2D UNIT_WEST = new Vector2D(0, 1);
    /**
     * Unit vector in the down direction.
     */
    public static final Vector2D UNIT_SOUTH = new Vector2D(0, -1);

	
    // Instance's Points
	private Point mHead;
	private Point mBase;
	
	/**
	 * Constructor for a Vector2D given an x and y-coordinate as the head
	 * point. The base point is at origin.
	 * 
	 * @param x	the x-coordinate.
	 * @param y	the y-coordinate.
	 */
	public Vector2D(double x, double y) {
		mHead = new Point(x, y);
		mBase = new Point(0, 0);
	}
	
	/**
	 * Constructor for a Vector2D given a specific {@link Vector2D.Point} to
	 * be used as the head. The base point is at origin.
	 * 
	 * @param pt	the Point.
	 */
	public Vector2D(Point pt) {
		this(pt.x, pt.y);
	}
	
	/**
	 * Constructor for copying a Vector2D's head and base points into a new
	 * Vector2D.
	 * 
	 * @param v	the Vector2D to copy.
	 */
	public Vector2D(Vector2D v) {
		mHead = new Point(v.mHead);
		mBase = new Point(v.mBase);
	}
	
	/**
	 * Constructor for a Vector2D given two {@link Point}s. The resulting
	 * Vector2D will not originate from origin (0, 0) but from the secondary
	 * coordinates given: x1 and y1 (explicitly passing 0 and 0 for x1 and y1
	 * is still allowed).
	 * 
	 * @param x0	base Point's x-coordinate.
	 * @param y0	base Point's y-coordinate.
	 * @param x1	head Point's x-coordinate.
	 * @param y1	head Point's y-coordinate.
	 * @throws IllegalArgumentException	if any of the given coordinates are
	 * equivalent to {@link Double#NaN}.
	 */
	public Vector2D(double x0, double y0, double x1, double y1) {
		if (x0 == Double.NaN) {
			throw new IllegalArgumentException("x0 must be a number");
		}
		if (y0 == Double.NaN) {
			throw new IllegalArgumentException("y0 must be a number");
		}
		if (x1 == Double.NaN) {
			throw new IllegalArgumentException("x1 must be a number");
		}
		if (y1 == Double.NaN) {
			throw new IllegalArgumentException("y1 must be a number");
		}
		
		mBase = new Point(x0, y0);
		mHead = new Point(x1, y1);
	}
	
	/**
	 * Gets the x-coordinate of the Vector2D's {@link Point}.
	 *
	 * @return	the x-coordinate.
	 * @see #getPoint()
	 */
	public double getX() {
		return mHead.x;
	}
	
	/**
	 * Gets the y-coordinate of the Vector2D's {@link Point}.
	 *
	 * @return	the y-coordinate.
	 * @see #getPoint()
	 */
	public double getY() {
		return mHead.y;
	}
	
	/**
	 * Gets the Point that defines the Vector2D's direction and magnitude.
	 * 
	 * @return	the Point.
	 * @see #getX()
	 * @see #getY()
	 */
	public Point getPoint() {
		return mHead;
	}
	
	/**
	 * Gets the Point that acts as origin for the Vector2D. Unless the
	 * Vector2D was constructed using {@link #Vector2D(double, double, double, double)}
	 * or was displaced from origin with {@link #rebase(double, double, boolean)}, this
	 * method will return a {@link Point} whose coordinates are (0,0).
	 *
	 * @return	the base Point.
	 */
	public Point getBasePoint() {
		return mHead;
	}
	
	/**
	 * Computes the magnitude of the Vector2D.
	 * 
	 * @return	the magnitude.
	 * @see #changeMagnitude(double)
	 */
	public double magnitude() {
		double diffX = mHead.x - mBase.x;
		double diffY = mHead.y - mBase.y;
		
		double compX = Math.pow(Math.abs(diffX), 2);
		double compY = Math.pow(Math.abs(diffY), 2);
		return Math.sqrt(compX + compY);
	}
	
	/**
	 * Adds a given Vector2D's coordinates to the calling Vector2D's coordinates. This
	 * method is synonymous with vector addition.
	 *
	 * @param v	the Vector2D to add.
	 * @see #add(double, double)
	 */
	public void add(Vector2D v) {
		mHead.x += v.mHead.x;
		mHead.y += v.mHead.y;
		mBase.x += v.mBase.x;
		mBase.y += v.mBase.y;
	}
	
	/**
	 * Adds an amount to the x and y-coordinates of the Vector2D.
	 *
	 * @param shiftX	a value to shift x-coordinates by.
	 * @param shiftY	a value to shift y-coordinates by.
	 * @throws IllegalArgumentException	if the shift value for either x or y-coordinate
	 * is equivalent to {@link Double#NaN}.
	 * @see #add(Vector2D)
	 */
	public void add(double shiftX, double shiftY) {
		if (shiftX == Double.NaN) {
			throw new IllegalArgumentException("Must shift x by a number");
		}
		if (shiftY == Double.NaN) {
			throw new IllegalArgumentException("Must shift y by a number");
		}
		// Apply coordinate shift
		mHead.x += shiftX;
		mHead.y += shiftY;
		mBase.x += shiftX;
		mBase.y += shiftY;
	}
	
	/**
	 * Multiplies the Vector2D's coordinates by a specified scalar. The Vector2D's base point
	 * is unaffected and so only the head point will scale. For multiplication with another
	 * Vector2D, see {@link #dotProduct(Vector2D)}.
	 *
	 * @param scalar	the multiplication factor.
	 * @throws IllegalArgumentException	if the scalar is equivalent to {@link Double#NaN}.
	 */
	public void multiply(double scalar) {
		if (scalar == Double.NaN) {
			throw new IllegalArgumentException("Scalar must be a number: " + scalar);
		}
		mHead.x *= scalar;
		mHead.y *= scalar;
	}
	
	/**
	 * Computes the dot product of the calling Vector2D and another Vector2D. Both Vector2Ds'
	 * base points will be taken into consideration in operation. For multiplication with a
	 * single digit number, or scalar, see {@link #multiply(double)}.
	 *
	 * @param v	the other Vector2D.
	 * @return	the dot product.
	 */
	public double dotProduct(Vector2D v) {
		double diffX0 = mHead.x - mBase.x;
		double diffY0 = mHead.y - mBase.y;
		
		double diffX1 = v.mHead.x - v.mBase.x;
		double diffY1 = v.mHead.y - v.mBase.y;
		
		double prodX = diffX0 * diffX1;
		double prodY = diffY0 * diffY1;
		return prodX + prodY;
	}
	
	/**
	 * Changes the Vector2D's magnitude to a specific value. Further calls to
	 * {@link #magnitude()} will reflect this new value. If both the head and
	 * base points are (0, 0), this method has no effect. The head point will
	 * be scaled while the base point is conserved.
	 *
	 * @param magnitude	the length of the Vector2D.
	 * @throws IllegalArgumentException	if the given magnitude is equivalent
	 * to {@link Double#NaN}.
	 * @see #magnitude()
	 * @see #normalize()
	 */
	public void changeMagnitude(double magnitude) {
		if (magnitude == Double.NaN) {
			throw new IllegalArgumentException("Magnitude must be a number");
		}
		double originalX = mBase.x;
		double originalY = mBase.y;
		
		rebase(0, 0, true);
		normalize();
		multiply(magnitude);
		rebase(originalX, originalY, true);
	}
	
	/**
	 * Moves the Vector2D's Point such that the Vector2D's magnitude = 1. The Vector2D's
	 * original direction is conserved post-normalize. If both the Vector2D's base and
	 * head points are (0, 0), this method has no effect.
	 * 
	 * @see #changeMagnitude(double)
	 * @see #magnitude()
	 */
	public void normalize() {
		double mag = magnitude();
		
		// Difference from base Point
		double diffX = mHead.x - mBase.x;
		double diffY = mHead.y - mBase.y;
		
		// Shrink to len = 1
		mHead.x = mBase.x + (diffX / mag);
		mHead.y = mBase.y + (diffY / mag);
	}
	
	/**
	 * Gets a unit vector of the Vector2D. The unit vector is a vector with a magnitude
	 * of 1. The returned unit vector's direction is the same as the original Vector2D.
	 *
	 * @return	the unit vector.
	 * @see #magnitude()
	 * @see #normalize()
	 */
	public Vector2D getUnitVector() {
		Vector2D unitV = new Vector2D(this);
		unitV.normalize();
		return unitV;
	}
	
	/**
	 * Gets a normal vector to the Vector2D. The normal vector is a vector with the
	 * same magnitude but whose direction is perpendicular to the calling Vector2D.
	 * If the Vector2D is the zero vector, this method returns a zero vector as well.
	 * The returned Vector2D will have the same base point as the calling Vector2D.
	 *
	 * @param left	true if a normal vector to the left is desired, false for a
	 * vector on the right.
	 * @return	the normal vector.
	 */
	public Vector2D getNormal(boolean left) {
		
		double diffX = mHead.x - mBase.x;
		double diffY = mHead.y - mBase.y;
		
		if (left) {
			return new Vector2D(mBase.x, mBase.y, mBase.x + diffY, mBase.y - diffX);
		} else {
			return new Vector2D(mBase.x, mBase.y, mBase.x - diffY, mBase.y + diffX);
		}
	}
	
	/**
	 * Moves the head {@link Point} of this Vector2D to a mirrored location
	 * in the opposite direction from the base Point. The base Point
	 * remains the same.
	 */
	public void reverse() {
		double diffX = mHead.x - mBase.x;
		double diffY = mHead.y - mBase.y;
		
		mHead.x = mBase.x - diffX;
		mHead.y = mBase.y - diffY;
	}
	
	/**
	 * Changes both the base and head points of the Vector2D with the coordinates
	 * from another Vector2D's points. As such, magnitude is implicitly copied
	 * as well.
	 *
	 * @param v	the Vector2D to copy values from.
	 * @see #updateFrom(double, double, double, double)
	 */
	public void updateFrom(Vector2D v) {
		mHead.x = v.mHead.x;
		mHead.y = v.mHead.y;
		mBase.x = v.mBase.x;
		mBase.y = v.mBase.y;
	}
	
	
	/**
	 * Changes both the base and head points of the Vector2D with a set of
	 * explicit coordinates.
	 *
	 * @param x0	the base x-coordinate.
	 * @param y0	the base y-coordinate.
	 * @param x1	the head x-coordinate.
	 * @param y1	the head y-coordinate.
	 * @throws IllegalArgumentException	if any of the given coordinates are
	 * equivalent to {@link Double#NaN}.
	 * @see #updateFrom(Vector2D)
	 */
	public void updateFrom(double x0, double y0, double x1, double y1) {
		if (x0 == Double.NaN) {
			throw new IllegalArgumentException("x0 must be a number");
		}
		if (y0 == Double.NaN) {
			throw new IllegalArgumentException("y0 must be a number");
		}
		if (x1 == Double.NaN) {
			throw new IllegalArgumentException("x1 must be a number");
		}
		if (y1 == Double.NaN) {
			throw new IllegalArgumentException("y1 must be a number");
		}
		
		mBase.x = x0;
		mBase.y = y0;
		mHead.x = x1;
		mHead.y = y1;
	}
	
	/**
	 * Moves a Vector2D's base point to specified coordinates. The head point
	 * may or may not follow depending on passing true or false. Giving true
	 * will maintain direction and magnitude post-rebase.
	 *
	 * @param x	the new base x-coordinate.
	 * @param y	the new base y-coordinate.
	 * @param relative	true if the direction and magnitude should be
	 * conserved, false otherwise.
	 */
	public void rebase(double x, double y, boolean relative) {
		if (x == Double.NaN) {
			throw new IllegalArgumentException("x must be a number");
		}
		if (y == Double.NaN) {
			throw new IllegalArgumentException("y must be a number");
		}
		
		// Move the head along with the base point
		if (relative) {
			double diffX = mHead.x - mBase.x;
			double diffY = mHead.y - mBase.y;
			
			// Shift head to match
			mHead.x = x + diffX;
			mHead.y = y + diffY;
		}
		
		// Explicit change to base point while keeping head same
		mBase.x = x;
		mBase.y = y;
	}
	
	/**
	 * Returns a Vector2D as a String showing its x and y-coordinates along with
	 * its magnitude in the format of { (base x,base y) , (head x,head y) , [magnitude] }.
	 *
	 * @return	the Vector2D represented as a String.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{ (");
		builder.append(mBase.x);
		builder.append(',');
		builder.append(mBase.y);
		builder.append(") , (");
		builder.append(mHead.x);
		builder.append(',');
		builder.append(mHead.y);
		builder.append(") , [");
		builder.append(magnitude());
		builder.append("] }");
		return builder.toString();
	}

	/**
	 * Point is a wrapper for a pair of coordinates: x and y.
	 */
	public static class Point {
		// Coordinates
		private double x;
		private double y;
		
		/**
		 * Basic constructor for a Point.
		 * 
		 * @param x	the x-coordinate.
		 * @param y	the y-coordinate.
		 * @throws IllegalArgumentException	if either of the given coordinates
		 * are equivalent to {@link Double#NaN}.
		 */
		public Point(double x, double y) {
			if (x == Double.NaN) {
				throw new IllegalArgumentException("x must be a number");
			}
			if (y == Double.NaN) {
				throw new IllegalArgumentException("y must be a number");
			}
			
			this.x = x;
			this.y = y;
		}
		
		/**
		 * Constructor for copying a Point's values into a new Point.
		 * 
		 * @param p	the Point to copy.
		 */
		public Point(Point p) {
			x = p.x;
			y = p.y;
		}
		
		/**
		 * Gets the x-coordinate of the Point.
		 * 
		 * @return	the x-coordinate.
		 * @see #getY()
		 */
		public double getX() {
			return x;
		}
		
		/**
		 * Gets the y-coordinate of the Point.
		 * 
		 * @return	the y-coordinate.
		 * @see #getX()
		 */
		public double getY() {
			return y;
		}
		
		/**
		 * Sets the x-coordinate of the Point.
		 *
		 * @param x	the x-coordinate.
		 * @throws IllegalArgumentException	if the given x-coordinate
		 * is equivalent to {@link Double#NaN}.
		 */
		public void setX(double x) {
			if (x == Double.NaN) {
				throw new IllegalArgumentException("x must be a number");
			}
			this.x = x;
		}
		
		/**
		 * Sets the y-coordinate of the Point.
		 *
		 * @param y	the y-coordinate.
		 * @throws IllegalArgumentException	if the given y-coordinate
		 * is equivalent to {@link Double#NaN}.
		 */
		public void setY(double y) {
			if (y == Double.NaN) {
				throw new IllegalArgumentException("y must be a number");
			}
			this.y = y;
		}

		/**
		 * Checks whether or not the Point's x and y-coordinates are the same as another
		 * Point's coordinates.
		 * 
		 * @param pt	the other Point.
		 * @return	true if both Points have the same coordinates, false otherwise.
		 */
		public boolean equals(Point pt) {
			if (pt.x == this.x && pt.y == this.y) {
				return true;
			}
			return false;
		}
		
		/**
		 * This method will always return false since Point should only be compared to
		 * other Points.
		 */
		@Override
		public boolean equals(Object obj) {
			return false;
		}

		/**
		 * Returns the Point's coordinates in the format of (x, y).
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("(");
			builder.append(x);
			builder.append(", ");
			builder.append(y);
			builder.append(')');
			return builder.toString();
		}
		
	}
	
}
