package com.rehab.world;

public class Projectile extends Entity implements Combatant {

	private Projectile.Damage mDamageType;
	
	public Projectile(int healthCap) {
		setMaximumHealth(healthCap);
		mDamageType = Damage.DEFAULT;
	}
	
	public Projectile(int id, Projectile.Damage type) {
		mDamageType = type;
	}

	@Override
	public double power() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOnDamageTakenListener(OnDamageTakenListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOnDamageDealtListener(OnDamageDealtListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHealthIncrease(double oldHealth, double newHealth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHealthDecrease(double oldHealth, double newHealth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMove(double oldX, double oldY, double newX, double newY) {
		// TODO Auto-generated method stub
		
	}

	public enum Damage {
		DEFAULT, AREA, CHAIN
	}
	
}
