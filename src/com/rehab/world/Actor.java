package com.rehab.world;

import com.rehab.animation.Sprite;

public class Actor extends Entity implements Combatant {

	private OnDamageTakenListener mDamageTakenListener;
	private OnDamageDealtListener mDamageDealtListener;

	private Weapon mWeapon;

	public Actor(double mass, double healthCap) {
		super(mass, healthCap);
	}

	public void arm(double projSpd, double projDmg, double projSize) {
		mWeapon = new Weapon(this, projSpd, projDmg, new Hitbox(0, 0, projSize));
	}
	
	public void setProjectileSprite(Sprite sprite) {
		if (!isArmed())
			throw new IllegalStateException("Actor must first be weaponized");
		
		mWeapon.setProjectileSprite(sprite);
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
	
	public boolean isArmed() {
		return mWeapon != null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{ [");
		builder.append(getId());
		builder.append("] ");
		builder.append('(');
		builder.append(getX());
		builder.append(", ");
		builder.append(getY());
		builder.append(") }");
		return builder.toString();
	}
	
}
