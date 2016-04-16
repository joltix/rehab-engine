package com.rehab.world;

import java.util.ArrayList;

import com.rehab.world.Vector2D.Point;

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
 * However, these constructors' produce Hitboxes with a finalized shape and
 * so {@link #addEdge(double, double, double, double)} will throw an exception.
 * </p>
 */
public class Hitbox {

	// Minimum number of edges allowed for polygons
	private static final int POLYGON_EDGE_LIMIT = 3;
	
	// Constants for two main axis
	private static final Vector2D AXIS_HORIZONTAL = Vector2D.UNIT_EAST;
	private static final Vector2D AXIS_VERTICAL = Vector2D.UNIT_NORTH;

	// Polygonal edges (null if circle)
	private ArrayList<Vector2D> mEdges;

	// Location and dimensions
	private Point mLocation = new Point(0, 0);
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
		mType = Type.RECTANGLE;
		mWidth = w;
		mHeight = h;
		mLocation.setX(x);
		mLocation.setY(y);
		// Build out the vertices and finalize shape
		mEdges = new ArrayList<Vector2D>();
		mEdges.add(new Vector2D(x, y, x + w, y));
		mEdges.add(new Vector2D(x + w, y, x + w, y - h));
		mEdges.add(new Vector2D(x + w, y - h, x, y - h));
		mEdges.add(new Vector2D(x, y - h, x, y));
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
		mLocation.setX(x);
		mLocation.setY(y);
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
		mEdges = new ArrayList<Vector2D>();
		mType = Type.POLYGON;
	}

	/**
	 * Constructor to clone a Hitbox. The new instance will have the same
	 * values for its variables, including the same shape.
	 * 
	 * @param h the Hitbox to clone.
	 */
	public Hitbox(Hitbox h) {
		// Copy vertices for non-circular
		if (h.getType() != Type.CIRCLE) {
			mEdges = new ArrayList<Vector2D>();
			for (Vector2D v : h.mEdges) {
				mEdges.add(new Vector2D(v));
			}
		}
		// Copy all other values
		mLocation = new Point(h.mLocation);
		mWidth = h.mWidth;
		mHeight = h.mHeight;
		mLock = h.mLock;
		mType = h.mType;
	}

	/**
	 * Adds a vertex to the collision model.
	 * 
	 * @param x	the starting x-coordinate.
	 * @param y	the starting y-coordinate.
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
		double currentX = mLocation.getX();
		double currentY =  mLocation.getY();
		
		// Find origin's x
		currentX = Math.min(currentX, x0);
		currentX = Math.min(currentX, x1);
		
		// Find origin's y
		currentY = Math.max(currentY, y0);
		currentY = Math.max(currentY, y1);
		
		// Apply new origin
		mLocation.setX(currentX);
		mLocation.setY(currentY);

		mEdges.add(new Vector2D(x0, y0, x1, y1));
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
			Vector2D axis = AXIS_HORIZONTAL;
			if (i == 1) axis = AXIS_VERTICAL;

			Point min = null;
			Point max = null;
			for (Vector2D v : mEdges) {
				// Calc projected point
				Point projection = project(v, axis);
				
				// Figure min and max
				if (min == null) min = projection;
				else min = min(min, projection);
				if (max == null) max = projection;
				else max = max(max, projection);
			}
			// Measure the min and max differences
			if (i == 0) mWidth = Math.abs(max.getX() - min.getX());
			else mHeight = Math.abs(max.getY() - min.getY());
		}
	}

	/**
	 * Checks whether or not the Hitbox's boundary is overlapping with another Hitbox's.
	 * 
	 * @param h	the Hitbox whose boundary must be checked for.
	 * @throws IllegalArgumentException	if the given Hitbox is null or it is not
	 * locked.
	 * @throws IllegalStateException	if the calling Hitbox is locked.
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
		boolean collides = false;
		// Polygon or rectangle vs
		if (mType == Type.POLYGON || mType == Type.RECTANGLE) {
			// Polygon vs circle
			if (h.mType == Type.CIRCLE) {
				collides = collisionCombo(h, this);
				// Polygon vs polygon or rectangle
			} else {
				collides = collisionPolygon(this, h);
				if (!collides) {
					collides = collisionPolygon(h, this);
				}
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
	 * SAT implementation for circle-polygon collision.
	 * 
	 * @param circle	the circular Hitbox.
	 * @param other	the polygonal Hitbox.
	 * @return true	if a collision occurred, false otherwise.
	 */
	private static boolean collisionCombo(Hitbox circle, Hitbox other) {
		
		// Compute circle's center
		double radius = circle.getWidth() / 2;
		double centerX = circle.getX() + radius;
		double centerY = circle.getY() - radius;
		
		// Find vertex of polygon closest to circle center
		Point closestVertex = null;
		double closestDist = 0;
		for (Vector2D edge : other.mEdges) {
			Point vertex = edge.getPoint();
			double distTo = distanceBetween(centerX, centerY, vertex.getX(), vertex.getY());
			
			// Assign first vertex as closest
			if (closestVertex == null) {
				closestVertex = vertex;
				closestDist = distTo;
				// Closer vertex becomes closest so far
			} else if (Math.min(closestDist, distTo) == distTo){
				closestVertex = vertex;
				closestDist = distTo;
			}
		}
		
		// Build collision axis from circle center to closest polygon vertex
		Vector2D centerToPolyAxis = new Vector2D(centerX, centerY, closestVertex.getX(), closestVertex.getY());
		centerToPolyAxis.rebase(0, 0, true);
		centerToPolyAxis.normalize();
		
		// Project circle's diameter onto axis
		Vector2D radius0 = new Vector2D(centerToPolyAxis);
		radius0.rebase(centerX, centerY, true);
		radius0.changeMagnitude(radius);
		Point edge0 = project(radius0, centerToPolyAxis);
		Vector2D radius1 = new Vector2D(radius0);
		radius1.reverse();
		Point edge1 = project(radius1, centerToPolyAxis);
		
		// Figure which circle projection is bigger / smaller than other
		Point cirMin = edge0, cirMax = edge1;
		if (min(cirMin, cirMax) == cirMax) {
			cirMin = edge1;
			cirMax = edge0;
		}
		
		// Figure the polygon's projections onto center-poly axis
		Point minPolyVertex = null;
		Point maxPolyVertex = null;
		for (Vector2D vertex1 : other.mEdges) {
			Vector2D vert1 = new Vector2D(vertex1);
			Point proj1 = project(vert1, centerToPolyAxis);
			
			// Figure min projection
			if (minPolyVertex == null) minPolyVertex = proj1;
			else minPolyVertex = min(minPolyVertex, proj1);
			// Figure max projection
			if (maxPolyVertex == null) maxPolyVertex = proj1;
			else maxPolyVertex = max(maxPolyVertex, proj1);
		}

		// Bail out if gap is found
		if (min(cirMax, minPolyVertex) == cirMax ||
				min(maxPolyVertex, cirMin) == maxPolyVertex) {			
			return false;
		}

		return true;
	}

	/**
	 * SAT variant implementation for circle-circle collision.
	 * 
	 * @param caller	a circular Hitbox.
	 * @param other	the other circular Hitbox.
	 * @return true	if a collision occurred, false otherwise.
	 */
	private static boolean collisionCircular(Hitbox caller, Hitbox other) {
		// Compute radii
		double half0 = caller.mWidth / 2;
		double half1 = other.mWidth / 2;
		
		// Form caller's center Point
		double centerX0 = caller.mLocation.getX() + half0;
		double centerY0 = caller.mLocation.getY() + half0;
		
		// Form other's center Point
		double centerX1 = other.mLocation.getX() + half1;
		double centerY1 = other.mLocation.getY() + half1;

		// Measure center Points' distance for collision
		double dist = distanceBetween(centerX0, centerY0, centerX1, centerY1);
		if (dist > (half0 + half1)) {
			return false;
		}
		return true;
	}


	/**
	 * SAT implementation for polygon-polygon collision.
	 * 
	 * @param caller	one polygonal Hitbox.
	 * @param other	the other.
	 * @return true	if a collision occurred, false otherwise.
	 */
	private static boolean collisionPolygon(Hitbox caller, Hitbox other) {

		// Use each edge to figure a normal as an axis
		for (Vector2D edge : caller.mEdges) {
			Vector2D axis = edge.getNormal(true);
			axis.rebase(0, 0, true);
			axis.normalize();
			
			// Project each point / vector onto the normal
			Point minV0 = null;
			Point maxV0 = null;
			for (Vector2D vertex0 : caller.mEdges) {
				Point proj0 = project(vertex0, axis);

				// Figure min projection
				if (minV0 == null) minV0 = proj0;
				else minV0 = min(minV0, proj0);
				// Figure max projection
				if (maxV0 == null) maxV0 = proj0;
				else maxV0 = max(maxV0, proj0);
			}
			
			// Figure the other hitbox's projections
			Point minV1 = null;
			Point maxV1 = null;
			for (Vector2D vertex1 : other.mEdges) {
				Vector2D vert1 = new Vector2D(vertex1);
				Point proj1 = project(vert1, axis);
				
				// Figure min projection
				if (minV1 == null) minV1 = proj1;
				else minV1 = min(minV1, proj1);
				// Figure max projection
				if (maxV1 == null) maxV1 = proj1;
				else maxV1 = max(maxV1, proj1);
			}

			// Bail out if gap is found
			if (min(maxV0, minV1) == maxV0 ||
					min(maxV1, minV0) == maxV1) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Gets the distance between two {@link Point}s in 2D space.
	 * 
	 * @param x0	the first Point's x-coordinate.
	 * @param y0	the first Point's y-coordinate.
	 * @param x1	the second Point's x-coordinate.
	 * @param y1	the second Point's y-coordinate.
	 * @return the distance between two Points.
	 */
	private static double distanceBetween(double x0, double y0, double x1, double y1) {
		double xFactor = Math.abs(Math.pow(x1 - x0, 2));
		double yFactor = Math.abs(Math.pow(y1 - y0, 2));
		return Math.sqrt(xFactor + yFactor);
	}

	/**
	 * Chooses the Point with a lower y-value, preferring a lower x-value
	 * to break even. If both Points' coordinates represent the same points, this
	 * method returns the second Vector.
	 * 
	 * @param e0	one Point.
	 * @param e1	the second Point.
	 * @return the minimum Point.
	 */
	private static Point min(Point p0, Point p1) {
		double endX0 = p0.getX(), endY0 = p0.getY();
		double endX1 = p1.getX(), endY1 = p1.getY();
		if (endY0 < endY1) return p0;
		else if (endY0 > endY1) return p1;
		else if (endX0 < endX1) return p0;
		else return p1;
	}

	/**
	 * Chooses the Point with a higher y-value, preferring a higher x-value
	 * to break even. If both Points' coordinates represent the same points, this
	 * method returns the second Point.
	 * 
	 * @param e0	one Point.
	 * @param e1	the other Point.
	 * @return the maximum Point.
	 */
	private static Point max(Point p0, Point p1) {
		double endX0 = p0.getX(), endY0 = p0.getY();
		double endX1 = p1.getX(), endY1 = p1.getY();
		if (endY0 > endY1) return p0;
		else if (endY0 < endY1) return p1;
		else if (endX0 > endX1) return p0;
		else return p1;
	}

	/**
	 * Calculates the x or y-coordinate of a {@link Vector2D}'s projection onto a given
	 * normalized axis.
	 * 
	 * @param vertex	a Vector2D whose {@link Point} is to be projected.
	 * @param axis	the normalized axis.
	 * @return the projected Point.
	 */
	private static Point project(Vector2D vertex, Vector2D axis) {
		
		// Dot product based off of origin
		double compX = vertex.getX() * axis.getX();
		double compY = vertex.getY() * axis.getY();
		double dotProduct = compX + compY;
		
		// Compute Point along line
		double pX = axis.getX() * dotProduct;
		double pY = axis.getY() * dotProduct;
		
		return new Point(pX, pY);
	}

	/**
	 * Sets the x and y location of the Hitbox. This method has no interpolation of
	 * any kind and so location values will "teleport" to the new coordinates given.
	 * 
	 * @param x	the new x-coordinate.
	 * @param y	the new y-coordinate.
	 * @throws IllegalStateException	if the shape has not been finalized by
	 * a call to lock().
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
			double shiftX = x - mLocation.getX();
			double shiftY = y - mLocation.getY();
			// Shift all edge's points
			for (Vector2D v : mEdges) {
				v.add(shiftX, shiftY);
			}
		}
		// Update location
		mLocation.setX(x);
		mLocation.setY(y);
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
			for (Vector2D v : mEdges) {
				v.add(x, y);
			}
		}
		// Update location
		Point first = mEdges.get(0).getPoint();
		mLocation.setX(first.getX());
		mLocation.setY(first.getY());
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
	 * finalizes the shape and calculates the dimensions of polygons and thus
	 * should be called before actual use of the Hitbox. Some methods may throw
	 * an Exception if called before this method.
	 * 
	 * @throws IllegalStateException	if the Hitbox does not have at least
	 * three edges.
	 * @see #isLocked()
	 * @see #addEdge(double, double, double, double)
	 */
	public void lock() {
		if (mLock) {
			return;
		} else {
			// Enforce 3 edge minimum
			if (getType() == Type.POLYGON && mEdges.size() < POLYGON_EDGE_LIMIT) {
				throw new IllegalStateException("Polygons must have at least three edges");
			}
			
			mLock = true;
		}
		// Calculate dimensions if polygonal or rectangular
		if (mType != Type.CIRCLE && mType != Type.RECTANGLE) calculateDimensions();
	}

	/**
	 * Gets the width of the collision model. This method returns 0 if
	 * the Hitbox has not been locked.
	 * 
	 * @return	the width in pixels.
	 * @see #getHeight()
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
	 * @see #getWidth()
	 * @see #lock
	 * @see #isLocked()
	 */
	public double getHeight() {
		return mHeight;
	}

	/**
	 * Gets the Type of the Hitbox.
	 * 
	 * @return	either {@link Hitbox.Type#POLYGON}, {@link Hitbox.Type#RECTANGLE}, 
	 * or {@link Hitbox.Type#CIRCLE}.
	 */
	public Type getType() {
		return mType;
	}

	/**
	 * Gets the x-coordinate of the Hitbox. This value may not be accurate for
	 * polygonal Hitboxes whose {@link #lock()} has not been called.
	 * 
	 * @return the x-coordinate.
	 * @see #getY()
	 */
	public double getX() {
		return mLocation.getX();
	}

	/**
	 * Gets the y-coordinate of the Hitbox. This value may not be accurate for
	 * polygonal Hitboxes whose {@link #lock()} has not been called.
	 * 
	 * @return the y-coordinate.
	 * @see #getX()
	 */
	public double getY() {
		return mLocation.getY();
	}
	
	/**
	 * Gets the x and y-coordinate of the Hitbox wrapped in a {@link Point}.
	 *
	 * @return	the x and y-coordinates.
	 */
	public Point getLocation() {
		return new Point(mLocation);
	}
	
	/**
	 * Retrieves an {@link Iterable} of {@link Vector2D}s that represent the Hitbox's
	 * edges that form the shape. The returned edges are copies and so any modification
	 * to an edge will have no effect on the Hitbox.
	 *
	 * @return the Iterable of edges.
	 */
	public Iterable<Vector2D> edges() {
		ArrayList<Vector2D> edges = new ArrayList<Vector2D>();
		for (Vector2D edge : mEdges) {
			edges.add(new Vector2D(edge));
		}
		return edges;
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

}
