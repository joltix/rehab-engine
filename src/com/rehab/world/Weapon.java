package com.rehab.world;


/**
 * <p>
 * Handles firing {@link Projectile}s from an {@link Actor} to a target location. Every Weapon
 * instance has an owning Actor instance (this relationship is 1:1). Projectiles created
 * by an Actor's Weapon will not collide nor damage the Actor owning the Weapon and so these
 * Projectiles will pass through.
 * </p>
 */
public class Weapon implements OnCollisionListener {

	// Weapon profile
	private Actor mOwner;
	private Projectile mReferenceProj;

	// Projectile profile
	private double mProjSpeed;
	private double mProjDamage;

	/**
	 * Constructor for a basic Weapon.
	 * 
	 * @param a	the owning Entity.
	 * @param projSpd	speed of projectile.
	 * @param projDmg	damage per projectile.
	 * @param reference	a projectile to clone for new projectiles. to fire.
	 * @throws IllegalArgumentException	if no owner is given, the projectile
	 * speed is not greater than 0, or the projectile has no collision.
	 */
    public Weapon(Actor a, double projSpd, double projDmg, Projectile reference) {
		// Validate owner
		if (a == null) {
			throw new IllegalArgumentException("Weapon must have an owner");
		}
		// Validate projectile speed
		if (projSpd <= 0) {
			throw new IllegalArgumentException("Projectile speed must be > 0");
		}
		// Validate reference proj
		if (reference == null) {
			throw new IllegalArgumentException("Weapon must have a reference Projectile");
		}

		// Store Projectile info
		mOwner = a;
		mReferenceProj = reference;
		mProjSpeed = projSpd;
		mProjDamage = projDmg;
    }
    
    /**
     * Constructor for cloning a Weapon. The clone will have all properties of the given
     * Weapon. This includes not only its owner but also information about the
     * Projectile needed to fire.
     * 
     * @param w	the Weapon to clone.
     */
    public Weapon(Weapon w) {
    	mOwner = w.mOwner;
    	mReferenceProj = w.mReferenceProj;
    	mProjSpeed = w.mProjSpeed;
    	mProjDamage = w.mProjDamage;
    }

	/**
	 * Sets the amount of damage done by each Projectile fired from this Weapon.
	 * 
	 * @param damage	the amount of damage to inflict.
	 */
	public void setDamage(double damage) {
		mProjDamage = damage;
	}

	/**
	 * Creates a new Projectile instance and launches it toward the given
	 * coordinates.
	 * 
	 * @param x	the target x coordinate.
	 * @param y	the target y coordinate.
	 */
    public void fireAt(double x, double y) {
        // Setup and register projectile
		Projectile proj = InstanceManager.getInstance().createProjectile(mReferenceProj);
		proj.moveTo(mOwner.getX(), mOwner.getY());
		proj.setOnCollisionListener(this);
		
		// Compute path and speed of projectile
		Phys phys = proj.getPhysics();
		double normalizedX = x - mOwner.getXCentered();
		double normalizedY = y - mOwner.getYCentered();
		
		// Apply new direction
		phys.setAcceleration(50);
		phys.setVelocity(normalizedX, normalizedY, 1);
    }

	@Override
	public void onCollide(Entity e) {
		// Deduct health on impact
		e.setHealth(e.getHealth() - mProjDamage);
	}
}
