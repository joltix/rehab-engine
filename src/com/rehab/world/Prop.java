package com.rehab.world;

import com.rehab.animation.Sprite;

import javafx.scene.input.MouseEvent;

/**
 * <p>
 * Props are game objects that are not meant to threaten the player. These
 * game objects always have {@link Sprite}s, infinite health, and are not
 * affected by gravity. Methods such as {@link #setHealth(double)} which change
 * the aforementioned values therefore have no effect on a Prop.
 * </p>
 * 
 * <p>
 * Since Props are still {@link Entity} objects, Props retain the primary movement
 * methods {@link #moveBy(double, double)}, {@link #moveTo(double, double)}, and
 * {@link #move()} and so may be given motion or relocated.
 * </p>
 *
 */
public class Prop extends Entity {

	/**
	 * Basic constructor for a non-collidable Prop.
	 * 
	 * @param sprite	Sprite to represent on-screen.
	 */
	public Prop(Sprite sprite) {
		this(sprite, 0, 0);
	}
	
	/**
	 * Constructor for a Prop with a Hitbox collision model. Passing in 0 for
	 * the collision width or height will produce a non-collidable Prop.
	 * 
	 * @param sprite	Sprite visually representing the Prop.
	 * @param collisionW	width of the collision model.
	 * @param collisionH	height of the collision model.
	 */
	public Prop(Sprite sprite, double collisionW, double collisionH) {
		super(1, -1);
		super.setEnableGravity(false);
		super.setSprite(sprite);
		
		// Create a collision model if valid collision dimensions
		if (collisionW > 0 && collisionH > 0) {
			this.setCollisionModel(new Hitbox(0, 0, collisionW, collisionH));
		}
	}
	
	@Override
	public void onMove(double oldX, double oldY, double newX, double newY) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Props cannot die and so this method will have no effect.
	 */
	@Override
	protected void setMaximumHealth(double maxHealth) {
		return;
	}

	/**
	 * Props are not affected by gravity and so this method will have no effect.
	 */
	@Override
	public void setEnableGravity(boolean gravityEnabled) {
		return;
	}

	/**
	 * Props are not affected by gravity and so this method will have no effect. To
	 * make a Prop visible, see {@link #setVisibility(boolean)}.
	 * 
	 * @see #setVisibility(boolean)
	 */
	@Override
	public void enable() {
		return;
	}

	/**
	 * Props are not affected by gravity and so this method will have no effect. To
	 * make a Prop no longer visible, see {@link #setVisibility(boolean)}.
	 * 
	 * @see #setVisibility(boolean)
	 */
	@Override
	public void disable() {
		return;
	}

	@Override
	public int getZ() {
		return LayerManager.LAYER_PROP;
	}

	@Override
	public void onMousePress(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseRelease(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}
