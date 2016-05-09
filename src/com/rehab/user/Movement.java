package com.rehab.user;

import com.rehab.world.Actor;

/*
 * 
 * A class to control the movements of
 * a character on the screen. Currently
 * keeps track of movement made using
 * the up, down, right or left arrow keys.
 * 
 */
public class Movement extends WASDKeyMap {

	private Actor mPlayer;
	
	/**
	 * Constructor to handle the movement of a character when the up, down,
	 * left or right arrow keys are clicked.
	 * 
	 * @param player	the Actor to be controlled by the user.s
	 */
	public Movement(Actor player) {
		
		mPlayer = player;
	}

	@Override
	public void onW(boolean release) {
		
	}

	@Override
	public void onA(boolean release) {
		// Move player to the left
		mPlayer.moveBy(-5, 0);
	}

	@Override
	public void onS(boolean release) {
		
	}

	@Override
	public void onD(boolean release) {
		// Move player to the right
		mPlayer.moveBy(5, 0);
	}

	@Override
	public void onEnter(boolean release) {
		
	}

	@Override
	public void onSpace(boolean release) {
		
	}

	@Override
	public void onKey(int key, boolean release) {
		
	}
}
