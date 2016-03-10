package com.rehab.world;

import com.rehab.animation.Sprite;
import com.rehab.world.Phys.Vector;

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

	@Override
	public int getZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onClick() {
		System.out.println("Clicked!");
		
	}

	@Override
	public void fireAt(double x, double y) {
		// TODO Auto-generated method stub
		Projectile proj = new Projectile(0.2, 5);
		proj.setSprite(new Sprite("git_icon.jpg"));
		
		System.out.println("FIRING");
		
		Vector heading = new Vector(getX(), getY(), x, y);
		heading.changeMagnitude(15);
		InstanceManager.getInstance().register(proj);
		proj.moveImpulse(heading);
	}
	
}
