package com.rehab.world;

public class Actor extends Entity implements Combatant {

	private OnDamageTakenListener mDamageTakenListener;
	private OnDamageDealtListener mDamageDealtListener;
	
	public Actor(double mass, double healthCap) {
		super(mass);
		setMaximumHealth(healthCap);
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
	
}
