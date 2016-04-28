package com.rehab.world;

import com.rehab.animation.Sprite;

/**
 * <p>
 * Props are game objects that do not directly have an effect on the game. These
 * game objects always have Sprites, infinite health, no collision, and are not
 * affected by gravity. Methods such as {@link #setHealth(double)} which change the aforementioned values therefore
 * have no effect on a Prop.
 * 
 * Since Props are still Entities, Props retain the primary movement methods
 * {@link #moveBy(double, double)}, {@link #moveTo(double, double)}, and
 * {@link #moveImpulse(com.rehab.world.Phys.Vector)} and so may be given motion
 * or relocated.
 * </p>
 *
 */
public class Prop extends Entity {

	public Prop(Sprite sprite) {
		super(1, -1);
		super.setEnableGravity(false);
		super.setSprite(sprite);
	}
	
	@Override
	public void onMove(double oldX, double oldY, double newX, double newY) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Props do not have collision and so this method will always return false.
	 */
	@Override
	public boolean collidesWith(Entity e) {
		return false;
	}

	
	/**
	 * Props cannot die and so this method will have no effect.
	 */
	@Override
	protected void setMaximumHealth(double maxHealth) {
		return;
	}

	/**
	 * Props have no collision and so this method will have no effect.
	 */
	@Override
	protected void setCollisionModel(Hitbox h) {
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
	public void onClick() {
		// TODO Auto-generated method stub
		
	}
	
}
