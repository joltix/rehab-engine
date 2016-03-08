package com.rehab.world;

import java.util.ArrayList;

import com.rehab.world.Phys.Vector;

public class Hitbox {

	// Constants for two main axis
	private static final Vector AXIS_HORIZONTAL = new Vector(1, 0);
	private static final Vector AXIS_VERTICAL = new Vector(0, 1);

	// All edges
	private ArrayList<Phys.Vector> mEdges = new ArrayList<Vector>();
	private double mSepDist = 0;
	// Dimensions
	private double mWidth, mHeight;
	private boolean mLock = false;

	/**
	 * Constructs a blank Hitbox. To build the boundary, addEdge() must be called
	 * for each edge that forms the convex polygon followed by a call to lock()
	 * to finalize the boundary.
	 */
	public Hitbox() {}

	/**
	 * Constructor for a rectangular Hitbox with a complete boundary.
	 * @param x
	 * 		origin x coordinate.
	 * @param y
	 * 		origin y coordinate.
	 * @param w
	 * 		width of the rectangular boundary.
	 * @param h
	 * 		height of the rectangular boundary.
	 */
	public Hitbox(double x, double y, double w, double h) {
		mWidth = w;
		mHeight = h;
		// Build out the edges
		mEdges.add(new Vector(x, y, x + w, y));
		mEdges.add(new Vector(x + w, y, x + w, y - h));
		mEdges.add(new Vector(x + w, y - h, x, y - h));
		mEdges.add(new Vector(x, y - h, x, y));
	}

	/**
	 * Adds an edge to the collision model.
	 * @param x0
	 * 		the starting x-coordinate.
	 * @param y0
	 * 		the starting y-coordinate.
	 * @param x1
	 * 		the ending x-coordinate.
	 * @param y1
	 * 		the ending y-coordinate.
	 */
	public void addEdge(double x0, double y0, double x1, double y1) {
		if (mLock) throw new IllegalStateException("Hitbox is already locked");
		mEdges.add(new Vector(x0, y0, x1, y1));
	}

	/*
	 * Prevents the Hitbox from changing its general shape. That is, calling
	 * lock() will prevent calls to addEdge() from taking effect. This method
	 * finalizes the shape and thus should be called before actual use of the
	 * Hitbox.
	 */
	public void lock() {
		if (mLock) return;
		else mLock = true;

		calculateDimensions();
	}

	/**
	 * Measures the Hitbox's width and height in the current orientation of its
	 * points. This method should be called each time the orientation of the
	 * Hitbox changes.
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
	 * @param h
	 * 		the Hitbox whose boundary must be checked for.
	 * @return
	 * 		true if this instance has collided with the specified Hitbox.
	 */
	public boolean collidesWith(Hitbox h) {
		// Check collision
		Vector sepDist = isPenetrating(true, h, null);
		// Reset separation dist to 0 if no collision
		if (sepDist == null) mSepDist = 0;
		else mSepDist = sepDist.getMagnitude();
		return mSepDist != 0;
	}

	/**
	 * Runs an implementation of the Separating Axis Theorem (SAT) for detecting convex collision.
	 * @param recursive
	 * 		true to check opposing Hitbox as well as the calling Hitbox.
	 * @param h
	 * 		the calling Hitbox.
	 * @param minSep
	 * 		the shortest separation Vector found so far.
	 * @return
	 * 		the Vector describing the distance needed to separate a collision, or null if no
	 * 		collision was detected.
	 */
	private Vector isPenetrating(boolean recursive, Hitbox h, Vector minSep) {

		Vector minSeparation = minSep;

		// Use each edge to figure a normal as an axis
		for (Vector edge : mEdges) {
			double normalX = edge.getNormalX();
			double normalY = edge.getNormalY();
			double len = Math.sqrt(Math.pow(normalX, 2) + Math.pow(normalY, 2));
			// Wrap the axis' data for easier handling
			Vector axis = new Vector(edge.getNormalX() * (1/len), edge.getNormalY() * (1 / len));

			// Project each point / vector onto the normal
			Vector minEdge = null,
					maxEdge = null;
			for (Vector projection : mEdges) {
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
			for (Vector otherProj : h.mEdges) {
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
				return null;

			// Measure minimum separation distance so far
			if (minSeparation == null) minSeparation = new Vector(Math.abs(maxEdge.getEndX() - otherMinEdge.getEndX()), Math.abs(maxEdge.getEndY() - otherMinEdge.getEndY()));
			else {
				Vector overlap = new Vector(Math.abs(maxEdge.getEndX() - otherMinEdge.getEndX()), Math.abs(maxEdge.getEndY() - otherMinEdge.getEndY()));
				minSeparation = min(minSeparation, overlap);
			}
		}

		// Test the second object's axis'
		if (recursive) return h.isPenetrating(false, this, minSeparation);

		return minSeparation;
	}

	/**
	 * Calculates the distance needed to separate the Hitbox from another Hitbox.
	 * If the distance returned == 0, then there is no collision.
	 * @return
	 * 		the minimum separation distance.
	 */
	public double getSeparationDistance() { return mSepDist; }

	/**
	 * Chooses the minimum Edge between two given Edges.
	 * @param e0
	 * 		one Edge.
	 * @param e1
	 * 		the other Edge.
	 * @return
	 * 		the minimum Edge.
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
	 * Chooses the maximum Edge between two given Edges.
	 * @param e0
	 * 		one Edge.
	 * @param e1
	 * 		the other Edge.
	 * @return
	 * 		the maximum Edge.
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
	 * Calculates the x or y coordinate of the point projected onto a specified
	 * axis.
	 * @param calcX
	 * 		true to calculate the x coordinate of the projected point, false to
	 * 		get the y coordinate.
	 * @param edge
	 * 		an Edge where the end point is to be projected.
	 * @param axis
	 * 		the Edge whose end point represents the axis to project to.
	 * @return
	 */
	private static double project(boolean calcX, Vector edge, Vector axis) {
		double axisEndX = axis.getEndX(), axisEndY = axis.getEndY();
		double normCoord = axisEndX;
		if (!calcX) normCoord = axisEndY;
		return ((edge.getEndX() * axisEndX) + (edge.getEndY() * axisEndY)) * normCoord;
	}

	/**
	 * Sets the X and Y location of the Entity. This method has no interpolation of
	 * any kind and so location values will "teleport" to the new coordinates given.
	 * @param x
	 * 		the new X coordinate.
	 * @param y
	 * 		the new Y coordinate.
	 */
	public void moveTo(double x, double y) {
		// Calculate coordinate shift needed
		Vector originEdge = mEdges.get(0);
		double changeX = x - originEdge.getX();
		double changeY = y - originEdge.getY();
		// Shift all edge's points
		moveBy(changeX, changeY);
	}

	/**
	 * Shifts the X and Y location of the Entity. This method has no interpolation of
	 * any kind and so location values will "teleport" to the new coordinates shifted
	 * by the x and y values given.
	 * @param x
	 * 		the number of pixels to add to the x-coordinate.
	 * @param y
	 * 		the number of pixels to add to the y-coordinate.
	 */
	public void moveBy(double x, double y) {
		// Shift the coordinates of all Edges
		for (Vector e : mEdges) e.add(x, y, x, y);
	}

	/**
	 * Gets the width of the collision model.
	 * @return
	 * 		the width in pixels.
	 */
	public double getWidth() { return mWidth; }

	/**
	 * Gets the height of the collision model.
	 * @return
	 * 		the height in pixels.
	 */
	public double getHeight() { return mHeight; }

}
