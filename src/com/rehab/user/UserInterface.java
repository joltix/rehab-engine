package com.rehab.user;

import com.rehab.animation.Drawable;
import com.rehab.animation.Sprite;
import com.rehab.world.Phys;

import javafx.scene.input.MouseEvent;

public abstract class UserInterface implements Drawable {
	
	private Phys mPhys = new Phys(0);
	private Sprite mSprite;
	
	private OnClickListener listener;
	
	public UserInterface(Sprite sprite){
		mSprite = sprite;
	}
	
	public void moveBy(double x, double y){
		mPhys.moveBy(x, y);
	}
	
	public void moveTo(double x, double y){
		mPhys.moveTo(x, y);
	}
	
	public void setOnClickListener(OnClickListener clickListener){
		listener = clickListener;
	}

	@Override
	public double getX() {
		// TODO Auto-generated method stub
		return mPhys.getX();
	}

	@Override
	public double getY() {
		// TODO Auto-generated method stub
		return mPhys.getY();
	}

	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return mSprite.getWidth();
	}

	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return mSprite.getHeight();
	}

	@Override
	public Sprite getSprite() {
		return mSprite;
	}

	@Override
	public void setSprite(Sprite s) {
		mSprite = s;
	}

	@Override
	public int getZ() {
		return 0;
	}

	@Override
	public void onMousePress(MouseEvent press) {
		if(listener == null){
			return;
		}
		listener.onClick();
	}

	@Override
	public void onMouseRelease(MouseEvent release) {
		if(listener == null){
			return;
		}
		listener.onClick();
	}
	
	

}
