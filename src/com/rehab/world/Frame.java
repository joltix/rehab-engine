package com.rehab.world;

import java.util.ArrayDeque;

import com.rehab.animation.Drawable;
import com.rehab.animation.Sprite;

/**
 * <p>
 * Frame holds all shallow copies ({@link Frame.Renderable}) of game objects to be rendered.
 * </p>
 */
public class Frame {
	
	// Shallow copies of Drawables
	private ArrayDeque<Renderable> mRenderables = new ArrayDeque<Renderable>(FrameDepot.FRAME_CAPACITY);
	
	/**
	 * Removes a Renderable in the Frame.
	 *
	 * @return the Renderable.
	 * @see #push(Drawable)
	 */
	public Renderable poll() {
		Renderable obj = mRenderables.poll();
		return obj;
	}
	
	/**
	 * Adds a Drawable to the Frame. The Drawable itself is not referenced by the Frame but
	 * a shallow copy is created in the form of a {@link Renderable}.
	 *
	 * @param drawable the Drawable to add.
	 * @see #poll()
	 */
	public void push(Drawable drawable) {
		mRenderables.add(new Renderable(drawable));
	}
	
	/**
	 * Clears the Frame of any Renderables.
	 */
	public void clear() {
		mRenderables.clear();
	}
	
	/**
	 * Checks whether or not the Frame is empty of game objects.
	 *
	 * @return true if the Frame holds no game objects, false otherwise.
	 */
	public boolean isEmpty() {
		return mRenderables.isEmpty();
	}
	
	/**
	 * Gets all Renderables set in the Frame. These Renderables are meant to be drawn shortly
	 * after this method is called and no more Renderables should be added to this Frame.
	 *
	 * @return the Iterable of Renderables.
	 */
	public Iterable<Renderable> renderables() {
		return mRenderables;
	}
	
	/**
	 * <p>
	 * Represents a shallow copy of a Drawable game object to be drawn on the screen.
	 * Renderables are stored within a Frame to be drawn soon after.
	 * </p>
	 */
	public static class Renderable {
		// Data for drawing
		private Sprite sprite;
		private boolean left;
		private float rotation;
		private double x, y;
		private int z;
		
		/**
		 * Constructor to shallow copy a Drawable for the Sprite, x, y, and z
		 * values and stores them for drawing.
		 *
		 * @param drawable	the game object.
		 */
		public Renderable(Drawable drawable) {
			sprite = drawable.getSprite();
			left = drawable.isFacingLeft();
			rotation = drawable.getRotation();
			x = drawable.getX();
			y = drawable.getY();
			z = drawable.getZ();
		}
		
		/**
		 * Gets the Sprite (image) of the game object.
		 *
		 * @return the Sprite to draw.
		 */
		public Sprite getSprite() { return sprite; }
		
		/**
		 * Checks whether or not the Renderable's game object
		 * is facing left.
		 * 
		 * @return true if the object is facing left, false otherwise.
		 */
		public boolean isFacingLeft() { return left; }
		
		/**
		 * Gets the rotation value in degrees.
		 * 
		 * @return	the angle offset.
		 */
		public float getRotation() { return rotation; }
		
		/**
		 * Gets the x value of the game object.
		 *
		 * @return the x coordinate to draw at.
		 */
		public double getX() { return x; }
		
		/**
		 * Gets the y value of the game object.
		 *
		 * @return the y coordinate to draw at.
		 */
		public double getY() { return y; }
		
		/**
		 * Gets the z value (draw layer) of the game object.
		 *
		 * @return the layer to draw the object on.
		 */
		public int getZ() { return z; }
	}
}
