package com.rehab.user;

import com.rehab.animation.Drawable;
import com.rehab.animation.Sprite;
import com.rehab.world.Entity;
import com.rehab.world.OnHealthDecreaseListener;
import com.rehab.world.OnHealthIncreaseListener;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class HealthBar implements Drawable, OnHealthDecreaseListener, OnHealthIncreaseListener {
	
	int width;
	GraphicsContext graphics;
	Canvas canvas;
	double mMaxHealth;
	double mWidth;
	double mHeight;
	Sprite sprite;
	
	public HealthBar(GraphicsContext graphics, Canvas canvas, Entity entity){
		entity.setOnHealthDecreaseListener(this);
		entity.setOnHealthIncreaseListener(this);
		mMaxHealth = entity.getMaximumHealth();
		mWidth = canvas.getWidth() / 4;
		mHeight = canvas.getHeight() / 16;
	}
	
	@Override
	public double getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Sprite getSprite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSprite(Sprite s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHealthIncrease(double oldHealth, double newHealth) {
		graphics.setFill(Color.DARKGREY);
		graphics.fillRect(5, 5, mWidth, mHeight);
		
		double increase = newHealth / mMaxHealth;
		double barWidth = mWidth * increase;
		graphics.setFill(Color.GREEN);
		graphics.fillRect(5, 5, barWidth, mHeight);
		
	}

	@Override
	public void onHealthDecrease(double oldHealth, double newHealth) {
		graphics.setFill(Color.DARKGREY);
		graphics.fillRect(5, 5, mWidth, mHeight);
		
		double decrease = newHealth / mMaxHealth;
		double barWidth = mWidth * decrease;
		graphics.setFill(Color.GREEN);
		graphics.fillRect(5, 5, barWidth, mHeight);
		
	}

}
