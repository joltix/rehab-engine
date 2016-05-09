package com.rehab.user;

import org.lwjgl.glfw.GLFW;

/**
 * <p>
 * Subclass of the standard {@link KeyMap} that adds the popular WASD control scheme
 * as convenient callbacks.
 * </p>
 */
public abstract class WASDKeyMap extends KeyMap{
	
	/**
	 * This method is called on every press and release of the
	 * W key.
	 * 
	 * @param release	true if the click was a release, false
	 * if it was a press.
	 */
	public abstract void onW(boolean release);

	/**
	 * This method is called on every press and release of the
	 * A key.
	 * 
	 * @param release	true if the click was a release, false
	 * if it was a press.
	 */
	public abstract void onA(boolean release);
	
	/**
	 * This method is called on every press and release of the
	 * S key.
	 * 
	 * @param release	true if the click was a release, false
	 * if it was a press.
	 */
	public abstract void onS(boolean release);
	
	/**
	 * This method is called on every press and release of the
	 * D key.
	 * 
	 * @param release	true if the click was a release, false
	 * if it was a press.
	 */
	public abstract void onD(boolean release);

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		super.invoke(window, key, scancode, action, mods);
		
		// Trigger the right key callback
		switch (key) {
		case GLFW.GLFW_KEY_W:
			onW(action == GLFW.GLFW_RELEASE); break;
		case GLFW.GLFW_KEY_A:
			onA(action == GLFW.GLFW_RELEASE); break;
		case GLFW.GLFW_KEY_S:
			onS(action == GLFW.GLFW_RELEASE); break;
		case GLFW.GLFW_KEY_D:
			onD(action == GLFW.GLFW_RELEASE); break;
		}
		
	}
	
	

	
	
}
