package com.rehab.world;

import java.util.ArrayList;
import com.rehab.world.Phys.Vector;

/**
 * 
 * Hitbox handles the collision detection layer of a game object. As one
 * of the layers of an Entity, a Hitbox's location is synchronized with
 * its parent Entity object. Following is a typical construction for a
 * polygonal Hitbox.
 * 
 * <pre>
 * 	<code>
 * 
 * Hitbox h = new Hitbox();
 * h.addEdge(0, 0, 16, 32);
 * h.addEdge(16, 32, 32, 0);
 * h.addEdge(32, 0, 0, 0);
 * h.lock();
 * 	</code>
 * </pre>
 * 
 * <p>
 * Calling {@link #lock()} at the end is to finalize the shape and measure its
 * dimensions. Some methods, such as those related to motion, may throw
 * an Exception if called before the Hitbox is finalized by lock().
 * </p>
 * 
 * <p>
 * This finalization also measures the dimensions of the Hitbox. As such,
 * calls to {@link #getWidth()} or {@link #getHeight()} on an unfinalized Hitbox
 * will return 0.
 * </p>
 * 
 * <p>
 * Constructors exist for some common shapes like a circle and a rectangle.
 * However, these constructors' produce Hitboxes with a finalized shape.
 * </p>
 * 
 * <p><b>[note] This class has non-functioning code for {@link #collidesWith(Hitbox)}
 * involving circular Hitboxes</b></p>
 */
public class Hitbox {

	// Constants for two main axis
	private static final Vector AXIS_HORIZONTAL = new Vector(1, 0);
	private static final Vector AXIS_VERTICAL = new Vector(0, 1);

	// Polygonal edges (null if circle)
	private ArrayList<Phys.Vector> mEdges;

	// Location and dimensions
	private double mX, mY;
	private double mWidth, mHeight;
	
	// Lock state to finalize shape
	private boolean mLock = false;
	private Type mType;

	/**
	 * Constructor for a rectangular Hitbox with a complete boundary. Hitboxes
	 * made using this constructor may not have their shapes modified.
	 * 
	 * @param x	origin x coordinate.
	 * @param y	origin y coordinate.
	 * @param w	width of the rectangular boundary.
	 * @param h	height of the rectangular boundary.
	 * @throws IllegalArgumentException	if the width or height is not greater than 0
	 */
	public Hitbox(double x, double y, double w, double h) {
		if (w <= 0) {
			throw new IllegalArgumentException("Width must be > 0");
		}
		
		if (h <= 0) {
			throw new IllegalArgumentException("Height must be > 0");
		}
		
		// Init
		mEdges = new ArrayList<Vector>();
		mType = Type.RECTANGLE;
		mWidth = w;
		mHeight = h;
		mX = x;
		mY = y;
		// Build out the edges and finalize shape
		mEdges = new ArrayList<Vector>();
		mEdges.add(new Vector(x, y, x + w, y));
		mEdges.add(new Vector(x + w, y, x + w, y - h));
		mEdges.add(new Vector(x + w, y - h, x, y - h));
		mEdges.add(new Vector(x, y - h, x, y));
		lock();
	}

	/**
	 * Constructor for a circular Hitbox. Hitboxes made using this constructor
	 * may not have their shapes modified.
	 * 
	 * @param x			the origin x coordinate.
	 * @param y			the origin y coordinate.
	 * @param diameter	the circle's diameter.
	 * @throws IllegalArgumentException	if the diameter is not greater than 0.
	 */
	public Hitbox(double x, double y, double diameter) {
		if (diameter <= 0) {
			throw new IllegalArgumentException("Diameter must be > 0");
		}
		mType = Type.CIRCLE;
		mWidth = diameter;
		mHeight = diameter;
		mX = x;
		mY = y;
		lock();
	}

	/**
	 * Constructor for a blank Hitbox. This constructor is meant to build polygonal
	 * Hitboxes. To build the boundary, addEdge() must be called for each edge
	 * that forms the convex polygon followed by a call to lock() to finalize
	 * the boundary.
	 * 
	 * @see #addEdge(double, double, double, double)
	 */
	public Hitbox() {
		mEdges = new ArrayList<Vector>();
		mType = Type.POLYGON;
	}

	/**
	 * Constructor to clone a Hitbox. The new instance will have the same
	 * values for its variables, including the same shape.
	 * 
	 * @param h the Hitbox to clone.
	 */
	public Hitbox(Hitbox h) {
		// Copy edges for non-circular
		if (h.getType() != Type.CIRCLE) {
			mEdges = new ArrayList<Vector>();
			for (Vector v : h.mEdges) {
				mEdges.add(new Vector(v));
			}
		}
		// Copy all other values
		mX = h.mX;
		mY = h.mY;
		mWidth = h.mWidth;
		mHeight = h.mHeight;
		mLock = h.mLock;
		mType = h.mType;
	}

	/**
	 * Adds an edge to the collision model.
	 * 
	 * @param x0	the starting x-coordinate.
	 * @param y0	the starting y-coordinate.
	 * @param x1	the ending x-coordinate.
	 * @param y1	the ending y-coordinate.
	 * @throws IllegalStateException	if lock() has already been called
	 * 									or the Hitbox was created with
	 * 									an immutable constructor.
	 * @see #lock()
	 * @see #isLocked()
	 */
	public void addEdge(double x0, double y0, double x1, double y1) {
		if (mLock) {
			throw new IllegalStateException("Hitbox is already locked");
		}
		if (getType() != Type.POLYGON) {
			throw new IllegalStateException("May only add edges to POLYGON type Hitboxes");
		}

		// Update origin as top left corner
		mX = Math.min(mX, x0);
		mX = Math.min(mX, x1);
		mY = Math.max(mY, y0);
		mY = Math.max(mY, y1);

		mEdges.add(new Vector(x0, y0, x1, y1));
	}

	/**
	 * Measures the Hitbox's width and height in the current orientation of its
	 * points. This method is called on the first call to lock() and only if
	 * the Hitbox's shape is not circular.
	 * 
	 * @see #lock()
	 */
	private void calculateDimensions() {
		for (int i = 0; i < 2; i++) {
			// Determine which axis to use
			Vector axis = AXIS_HORIZONTAL;
			if (i == 1) axis = AXIS_VERTICAL;

			Vector min = null, max = null;
			for (Vector point : mEdges) {
				// Calc projected point
				Vector proj = new Vector(project(true, point, axis), project(false, point, axis));
				// Figure min and max
				if (min == null) min = proj;
				else min = min(min, proj);
				if (max == null) max = proj;
				else max = max(max, proj);
			}
			// Measure the min and max differences
			if (i == 0) mWidth = Math.abs(max.getEndX() - min.getEndX());
			else mHeight = Math.abs(max.getEndY() - min.getEndY());
		}
	}

	/**
	 * Checks whether or not the Hitbox's boundary is overlapping with another Hitbox's.
	 * @param	h		the Hitbox whose boundary must be checked for.
	 * @return	true 	if this instance has collided with the specified Hitbox.
	 * @throws 	IllegalArgumentException	if the given Hitbox is null or it is not
	 * 										locked.
	 * @throws	IllegalStateException		if the calling Hitbox is locked.
	 * @see #lock()
	 * @see #isLocked()
	 */
	public boolean collidesWith(Hitbox h) {
		if (h == null) {
			throw new IllegalArgumentException("Hitbox h must exist");
		}
		if (!isLocked()) {
			throw new IllegalStateException("This Hitbox must be locked");
		}
		if (!h.isLocked()) {
			throw new IllegalArgumentException("Hitbox h must be locked");
		}
		boolean collides;
		// Polygon or rectangle vs
		if (mType == Type.POLYGON || mType == Type.RECTANGLE) {
			// Polygon vs circle
			if (h.mType == Type.CIRCLE) {
				collides = collisionCombo(this, h);
				// Polygon vs polygon or rectangle
			} else {
				collides = collisionPolygon(this, h);
			}
			// Circle vs
		} else {
			// Circle vs polygon or rectangle
			if (h.mType == Type.POLYGON || h.mType == Type.RECTANGLE) {
				collides = collisionCombo(this, h);
				// Circle vs Circle
			} else {
				collides = collisionCircular(this, h);
			}
		}

		return collides;
	}

	/**
	 * [NOTE] This method does not yet function correctly.
	 * SAT implementation for circle-polygon collision.
	 * 
	 * @param	circle	the circular Hitbox.
	 * @param	other	the polygonal Hitbox.
	 * @return	true	if a collision occurred, false otherwise.
	 */
	private static boolean collisionCombo(Hitbox circle, Hitbox other) {
		double halfDim = circle.mWidth / 2;
		double centerX = circle.mX + halfDim;
		double centerY = circle.mY - halfDim;

		// Find polygon vertex closest to circle's center
		double minX = 0;
		double minY = 0;
		double minDist = -1;
		for (Vector edge : other.mEdges) {
			double x = edge.getX();
			double y = edge.getY();

			// Figure shortest distance so far
			double dist = distanceBetween(centerX, centerY, x, y);
			if (minDist == -1) {
				minDist = dist;
				minX = x;
				minY = y;
			} else {
				// Save closest vertex
				minDist = Math.min(minDist, dist);
				if (minDist == dist) {
					minX = x;
					minY = y;
				}
			}
		}

		// Use each edge to figure a normal as an axis
		Vector axis = new Vector(centerX, centerY, minX, minY);

		// Get min and max points of the circle's edge on axis
		Vector minEdge = axis.getUnitVector();
		minEdge.changeMagnitude(halfDim);
		minEdge.rebase(centerX, centerY);
		Vector maxEdge = new Vector(minEdge);
		maxEdge.rebase(centerX, centerY);
		minEdge.reverse();

		Vector realMin = min(minEdge, maxEdge);
		if (realMin == maxEdge) {
			minEdge = maxEdge;
			maxEdge = minEdge;
		}

		// Figure the other hitbox's projections
		Vector otherMinEdge = null,
				otherMaxEdge = null;
		for (Vector otherProj : other.mEdges) {
			double otherProjX = project(true, otherProj, axis);
			double otherProjY = project(false, otherProj, axis);
			Vector otherProjEdge = new Vector(otherProjX, otherProjY);

			// Figure min projection
			if (otherMinEdge == null) otherMinEdge = otherProjEdge;
			else otherMinEdge = min(otherMinEdge, otherProjEdge);
			// Figure max projection
			if (otherMaxEdge == null) otherMaxEdge = otherProjEdge;
			else otherMaxEdge = max(otherMaxEdge, otherProjEdge);

		}

		// Bail out if gap is found
		if (min(maxEdge, otherMinEdge) == maxEdge ||
				min(otherMaxEdge, minEdge) == otherMaxEdge)
			return false;


		/**
		 * The following code is SAT for polygons
		 */


		// Use each edge to figure a normal as an axis
		for (Vector edge : other.mEdges) {
			Vector otherAxis = createNormalAxis(edge);

			// Project the circle's diameter onto the normal axis
			minEdge = otherAxis.getUnitVector();
			minEdge.changeMagnitude(halfDim);
			minEdge.rebase(centerX, centerY);
			maxEdge = new Vector(minEdge);
			maxEdge.rebase(centerX, centerY);
			minEdge.reverse();

			realMin = min(minEdge, maxEdge);
			if (realMin == maxEdge) {
				minEdge = maxEdge;
				maxEdge = minEdge;
			}

			// Project each point / vector onto the normal
			otherMinEdge = null;
			otherMaxEdge = null;
			for (Vector projection : other.mEdges) {
				double projX = project(true, projection, otherAxis);
				double projY = project(false, projection, otherAxis);
				Vector projectedEdge = new Vector(projX, projY);

				// Figure min projection
				if (otherMinEdge == null) otherMinEdge = projectedEdge;
				else otherMinEdge = min(otherMinEdge, projectedEdge);
				// Figure max projection
				if (otherMaxEdge == null) otherMaxEdge = projectedEdge;
				else otherMaxEdge = max(otherMaxEdge, projectedEdge);
			}

			// Bail out if gap is found
			if (min(maxEdge, otherMinEdge) == maxEdge ||
					min(otherMaxEdge, minEdge) == otherMaxEdge)
				return false;
		}

		return true;
	}

	/**
	 * SAT implementation for circle-circle collision.
	 * 
	 * @param	caller	one circlular Hitbox.
	 * @param	other	the other.
	 * @return	true	if a collision occurred, false otherwise.
	 */
	private static boolean collisionCircular(Hitbox caller, Hitbox other) {
		double calHalf = caller.mWidth / 2;
		double othHalf = other.mWidth / 2;
		double calX = caller.mX + calHalf;
		double calY = caller.mY - calHalf;
		double othX = other.mX + othHalf;
		double othY = other.mY - othHalf;
		double dist = distanceBetween(calX, calY, othX, othY);
		// Compare distances between circle centers
		if (dist > (calHalf + othHalf)) return false;
		return true;
	}


	/**
	 * SAT implementation for polygon-polygon collision.
	 * 
	 * @param	caller	one polygonal Hitbox.
	 * @param	other	the other.
	 * @return	true	if a collision occurred, false otherwise.
	 */
	private static boolean collisionPolygon(Hitbox caller, Hitbox other) {

		// Use each edge to figure a normal as an axis
		for (Vector edge : caller.mEdges) {
			Vector axis = createNormalAxis(edge);

			// Project each point / vector onto the normal
			Vector minEdge = null,
					maxEdge = null;
			for (Vector projection : caller.mEdges) {
				double projX = project(true, projection, axis);
				double projY = project(false, projection, axis);
				Vector projectedEdge = new Vector(projX, projY);

				// Figure min projection
				if (minEdge == null) minEdge = projectedEdge;
				else minEdge = min(minEdge, projectedEdge);
				// Figure max projection
				if (maxEdge == null) maxEdge = projectedEdge;
				else maxEdge = max(maxEdge, projectedEdge);
			}

			// Figure the other hitbox's projections
			Vector otherMinEdge = null,
					otherMaxEdge = null;
			for (Vector otherProj : other.mEdges) {
				double otherProjX = project(true, otherProj, axis);
				double otherProjY = project(false, otherProj, axis);
				Vector otherProjEdge = new Vector(otherProjX, otherProjY);

				// Figure min projection
				if (otherMinEdge == null) otherMinEdge = otherProjEdge;
				else otherMinEdge = min(otherMinEdge, otherProjEdge);
				// Figure max projection
				if (otherMaxEdge == null) otherMaxEdge = otherProjEdge;
				else otherMaxEdge = max(otherMaxEdge, otherProjEdge);
			}

			// Bail out if gap is found
			if (min(maxEdge, otherMinEdge) == maxEdge ||
					min(otherMaxEdge, minEdge) == otherMaxEdge)
				return false;
		}

		return true;
	}

	/**
	 * Creates a normal axis for a given line.
	 * 
	 * @param edge	the original line, represented as a Vector.
	 * @return		a Vector of the normal axis.
	 */
	private static Vector createNormalAxis(Vector edge) {
		double normalX = edge.getNormalX();
		double normalY = edge.getNormalY();
		double len = Math.sqrt(Math.pow(normalX, 2) + Math.pow(normalY, 2));
		// Wrap the axis' data for easier handling
		return new Vector(edge.getNormalX() * (1/len), edge.getNormalY() * (1 / len));
	}

	/**
	 * Gets the distance between two points in 2D space.
	 * 
	 * @param x0	the first x-coordinate.
	 * @param y0	the first y-coordinate.
	 * @param x1	the second x-coordinate.
	 * @param y1	the second y-coordinate.
	 * @return		the distance between the two points.
	 */
	private static double distanceBetween(double x0, double y0, double x1, double y1) {
		double xFactor = Math.abs(Math.pow(x1 - x0, 2));
		double yFactor = Math.abs(Math.pow(y1 - y0, 2));
		return Math.sqrt(xFactor + yFactor);
	}

	/**
	 * Chooses the Vector with a lower end y-value, preferring a lower end x-value
	 * to break even. If both Vectors' end points represent the same points, this
	 * method returns the second Vector.
	 * 
	 * @param	e0	one Vector.
	 * @param	e1	the second Vector.
	 * @return		the minimum Vector.
	 */
	private static Vector min(Vector e0, Vector e1) {
		double endX0 = e0.getEndX(), endY0 = e0.getEndY();
		double endX1 = e1.getEndX(), endY1 = e1.getEndY();
		if (endY0 < endY1) return e0;
		else if (endY0 > endY1) return e1;
		else if (endX0 < endX1) return e0;
		else return e1;
	}

	/**
	 * Chooses the Vector with a higher end y-value, preferring a higher end x-value
	 * to break even. If both Vectors' end points represent the same points, this
	 * method returns the second Vector.
	 * 
	 * @param	e0	one Vector.
	 * @param	e1	the other Vector.
	 * @return		the maximum Vector.
	 */
	private static Vector max(Vector e0, Vector e1) {
		double endX0 = e0.getEndX(), endY0 = e0.getEndY();
		double endX1 = e1.getEndX(), endY1 = e1.getEndY();
		if (endY0 > endY1) return e0;
		else if (endY0 < endY1) return e1;
		else if (endX0 > endX1) return e0;
		else return e1;
	}

	/**
	 * Calculates the x or y-coordinate of the point projected onto a specified
	 * axis. The kind of point (x or y) depends on the argument calcX.
	 * 
	 * @param	calcX	true to calculate the x-coordinate of the projected point, false to
	 * 					get the y-coordinate.
	 * @param	edge	a Vector whose end point is to be projected.
	 * @param	axis	the Vector whose end point represents the axis to project to.
	 * @return			the projected coordinate.
	 */
	private static double project(boolean calcX, Vector edge, Vector axis) {
		double axisEndX = axis.getEndX(), axisEndY = axis.getEndY();
		double normCoord = axisEndX;
		if (!calcX) normCoord = axisEndY;
		return ((edge.getEndX() * axisEndX) + (edge.getEndY() * axisEndY)) * normCoord;
	}

	/**
	 * Sets the x and y location of the Hitbox. This method has no interpolation of
	 * any kind and so location values will "teleport" to the new coordinates given.
	 * 
	 * @param	x	the new x-coordinate.
	 * @param	y	the new y-coordinate.
	 * @throws IllegalStateException	if the shape has not been finalized by
	 * 									a call to lock().
	 * @see #lock
	 * @see #isLocked()
	 */
	public void moveTo(double x, double y) {
		if (!mLock) {
			throw new IllegalStateException("Hitbox must be locked");
		}
		// Shift edges over for non-circular
		if (getType() != Type.CIRCLE) {
			// Calc shift
			double shiftX = x - mX;
			double shiftY = y - mY;
			// Shift all edge's points
			for (Vector e : mEdges) {
				e.add(shiftX, shiftY, shiftX, shiftY);
			}
		}
		// Update origin
		mX = x;
		mY = y;
	}

	/**
	 * Shifts the X and Y location of the Hitbox. This method has no interpolation of
	 * any kind and so location values will "teleport" to the new coordinates shifted
	 * by the x and y values given.
	 * 
	 * @param x	the number of pixels to add to the x-coordinate.
	 * @param y	the number of pixels to add to the y-coordinate.
	 * @throws IllegalStateException	if the shape has not been finalized by a call
	 * 									to lock().
	 * @see #lock
	 * @see #isLocked()
	 */
	public void moveBy(double x, double y) {
		if (!mLock) {
			throw new IllegalStateException("Hitbox must be locked");
		}
		if (getType() != Type.CIRCLE) {
			// Shift the coordinates of all Edges
			for (Vector e : mEdges) {
				e.add(x, y, x, y);
			}
		}
		// Update origin
		mX = x;
		mY = y;
	}

	/**
	 * Checks whether or not the Hitbox's shape has been finalized by a
	 * call to lock().
	 * 
	 * @return true if the Hitbox's shape is immutable, false otherwise.
	 * @see #lock()
	 */
	public boolean isLocked() {
		return mLock;
	}

	/**
	 * Prevents the Hitbox from changing its general shape. That is, calling
	 * lock() will prevent calls to addEdge() from taking effect. This method
	 * finalizes the shape and thus should be called before actual use of the
	 * Hitbox. Some methods may throw an Exception if called before this
	 * method.
	 * 
	 * @throws IllegalStateException	if the Hitbox does not have at least
	 * 									three edges.
	 * 
	 * @see #isLocked()
	 * @see #addEdge(double, double, double, double)
	 */
	public void lock() {
		if (mLock) {
			return;
		} else {
			// Enforce 3 edge minimum
			if (getType() == Type.POLYGON && mEdges.size() <= 3) {
				throw new IllegalStateException("Polygons must have at least three edges");
			}
			
			mLock = true;
		}
		// Calculate dimensions if polygonal or rectangular
		if (getType() != Type.CIRCLE) calculateDimensions();
	}

	/**
	 * Gets the width of the collision model. This method returns 0 if
	 * the Hitbox has not been locked.
	 * 
	 * @return	the width in pixels.
	 * @see #lock
	 * @see #isLocked()
	 */
	public double getWidth() {
		return mWidth;
	}

	/**
	 * Gets the height of the collision model. This method returns 0 if
	 * the Hitbox has not been locked.
	 * 
	 * @return	the height in pixels.
	 * @see #lock
	 * @see #isLocked()
	 */
	public double getHeight() {
		return mHeight;
	}

	/**
	 * Gets the Type of the Hitbox.
	 * 
	 * @return	either POLYGON, RECTANGLE, or CIRCLE.
	 */
	public Type getType() {
		return mType;
	}

	/**
	 * Gets the x-coordinate of the Hitbox.
	 * 
	 * @return the x-coordinate.
	 */
	public double getX() {
		return mX;
	}

	/**
	 * Gets the y-coordinate of the Hitbox.
	 * 
	 * @return the y-coordinate.
	 */
	public double getY() {
		return mY;
	}

	/**
	 *	Types representing different kinds of Hitboxes. These types
	 *	determine a Hitbox's reaction to other Hitboxes.
	 */
	public static enum Type {
		/**
		 * Hitbox with at least three edges.
		 */
		POLYGON,
		
		/**
		 * Hitbox with exactly four edges.
		 */
		RECTANGLE,
		
		/**
		 * Hitbox representing a circle. This type has no edges.
		 */
		CIRCLE
	}
	
	public static void main(String[] args) {
		Hitbox square = new Hitbox(0, 10, 10, 10);
		Hitbox circle = new Hitbox(15, 5, 15);
		System.out.println("Collision: " + circle.collidesWith(square));
	}

}
