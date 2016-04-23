package com.rehab.world;

import com.rehab.animation.Sprite;

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
	 * @param projCollision	Hitbox of projectile.
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
     * Sets the Sprite to use for Projectiles fired by this Weapon.
     * 
     * @param s	the Sprite to use.
     */
	public void setProjectileSprite(Sprite s) {
		mReferenceProj.setSprite(s);
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
		
		// Calc velocity
		Vector2D dir = orientRound(proj, x, y);

        // Accelerate the round
        proj.moveImpulse(dir);
    }
    
    /**
     * Gives a Projectile a direction toward a set of coordinates using the
     * Weapon's currently set speed.
     *
     * @param p	the Projectile.
     * @param x	the target direction's x value.
     * @param y	the target direction's y value.
     * @return the Vector2D describing the velocity.
     */
    private Vector2D orientRound(Projectile p, double x, double y) {
        // Set velocity
		Vector2D heading = new Vector2D(p.getX(), p.getY(), x - (p.getWidth() / 2), y + (p.getHeight() / 2));
        heading = heading.getUnitVector();
        heading.changeMagnitude(mProjSpeed);
        heading.rebase(0, 0, true);
        return heading;
    }

	@Override
	public void onCollide(Entity e) {
		// Deduct health on impact
		e.setHealth(e.getHealth() - mProjDamage);
	}
}
