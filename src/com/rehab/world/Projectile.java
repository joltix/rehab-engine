package com.rehab.world;


public class Projectile extends Entity {

	private static final double MASS = 0.1;
	private static final double HEALTH_MAX = 1;
	
	public static final double DAMAGE_DEFAULT = 1d;
	
	private Actor mOwner;
	private double mDamage = 1;

	public Projectile(Actor owner, Hitbox h) {
		super(MASS, HEALTH_MAX);
		// Validate owner
		if (owner == null) throw new IllegalArgumentException("Projectile must have an owner");

		mOwner = owner;
		setCollisionModel(h);
	}
	
	/**
	 * Constructor for a Projectile with the same
	 * @param p
	 */
	public Projectile(Projectile p) {
		super(p);
		mOwner = p.mOwner;
		mDamage = p.mDamage;
	}

	/**
	 * Sets the damage of the Projectile.
	 * @param damage
	 * 		the amount of damage to cause on impact.
	 */
	public void setDamage(double damage) { mDamage = damage; }

	/**
	 * Gets the damage value of the Projectile.
	 * @return
	 * 		the amount of damage caused on impact.
	 */
	public double getDamage() { return mDamage; }


	@Override
	public boolean collidesWith(Entity e) {
		// Ignore collisions with owner
		if (mOwner.getId() == e.getId()) {
			return false;
		}
		return super.collidesWith(e);
	}



	@Override
	public void onMove(double oldX, double oldY, double newX, double newY)
	{
		// TODO - Spawn particles here
	}

	@Override
	public int getZ() {
		return LayerManager.LAYER_FREE_2;
	}

	@Override
	public void onClick() { throw new UnsupportedOperationException("Projectiles should not be clickable"); }

}
