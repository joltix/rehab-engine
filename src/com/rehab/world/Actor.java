package com.rehab.world;

public class Actor extends Entity implements Combatant {

	private OnDamageTakenListener mDamageTakenListener;
	private OnDamageDealtListener mDamageDealtListener;
	// Weapon for firing Projectiles
	private Weapon mWeapon;

	/**
	 * Basic constructor for an unarmed Actor.
	 * 
	 * @param mass	the Actor's mass in kilograms.
	 * @param healthCap	the maximum health.
	 */
	public Actor(double mass, double healthCap) {
		super(mass, healthCap);
	}
	
	/**
	 * Constructor for cloning an Actor's properties. Everything from Hitbox to Phys
	 * and Sprite are copied over.
	 * 
	 * @param a	the Actor to clone.
	 */
	public Actor(Actor a) {
		super(a);
		// Copy Weapon if possible
		if (a.isArmed()) {
			mWeapon = new Weapon(a.mWeapon);
		}
	}

	public void arm(double projSpd, double projDmg, Projectile reference) {
		
		Projectile proj = InstanceManager.getInstance().createProjectile(reference);
		mWeapon = new Weapon(this, projSpd, projDmg, proj);
	}
	
	/**
	 * Checks whether or not the Actor is armed and capable of firing Projectiles.
	 *
	 * @return	true if the Actor can shoot, false otherwise.
	 */
	public boolean isArmed() {
		return mWeapon != null;
	}
	
	@Override
	public void setOnDamageTakenListener(OnDamageTakenListener listener) {
		mDamageTakenListener = listener;
	}

	@Override
	public void setOnDamageDealtListener(OnDamageDealtListener listener) {
		mDamageDealtListener = listener;
	}

	@Override
	public void onMove(double oldX, double oldY, double newX, double newY) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getZ() {
		return LayerManager.LAYER_FREE_1;
	}

	@Override
	public void onClick() {
		System.out.println("Clicked!");

	}

	@Override
	public void fireAt(double x, double y) {
		// Only armed can fight
		if (!isArmed()) {
			throw new IllegalStateException("Actor has not yet been weaponized");
		}
		mWeapon.fireAt(x, y);
	}
	
}
