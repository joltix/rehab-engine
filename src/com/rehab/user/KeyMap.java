package com.rehab.user;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import com.rehab.animation.LWCanvas;

/**
 * <p>
 * KeyMap allows for a hook into the GLFW toolkit's keyboard callbacks.
 * Subclasses must implement {@link #onEnter(boolean)}, {@link #onSpace(boolean)},
 * and {@link #onKey(int, boolean)} as three basic key callbacks. Subclasses
 * should use the {@link #onKey(int, boolean)} callback to create other
 * convenient callbacks for desired keys compared against GLFW constants such
 * as {@link GLFW#GLFW_KEY_M}.
 * </p>
 * 
 * <p>
 * There is an implicit {@link #onEscape(boolean)} that shuts down the {@link LWCanvas}
 * as a way out of LWCanvas' fullscreen mode. To disable this exit measure and
 * use the ESC key for other purposes, subclasses must override {@link #onEscape(boolean)}.
 * </p>
 * 
 * <p>
 * The following example shows how to define a KeyMap and set it to a Canvas.
 * This implementation will print "Hello" into the console when the spacebar
 * is released.
 * </p>
 * 
 * <pre>
 * 	<code>
 * LWCanvas canvas = LWCanvas.create(640, 480, "Hello", false);
 * 	KeyMap keyMap = new KeyMap(){
 *
 * 		@Override
 *		public void onEnter(boolean release) {
 *			
 *		}
 *
 *		@Override
 *		public void onSpace(boolean release) {
 *			if (release) {
 *				System.out.printf("Hello");
 *			}
 *		}
 *
 *		@Override
 *		public void onKey(int key, boolean release) {
 *			
 *		}};
 *	canvas.setKeyMap(keyMap);
 * 	</code>
 * </pre>
 */
public abstract class KeyMap extends GLFWKeyCallback {

	/**
	 * This method is called on every press and release of the
	 * ESC key.
	 * 
	 * @param release	true if the click was a release, false
	 * if it was a press.
	 */
	public void onEscape(boolean release) {
		// Escape key means user wanted to exit
		if (release) {
			LWCanvas.getInstance().shutdown();
		}
	}
	
	/**
	 * This method is called on every press and release of the
	 * ENTER key.
	 * 
	 * @param release	true if the click was a release, false
	 * if it was a press.
	 */
	public abstract void onEnter(boolean release);
	
	/**
	 * This method is called on every press and release of the
	 * SPACE key.
	 * 
	 * @param release	true if the click was a release, false
	 * if it was a press.
	 */
	public abstract void onSpace(boolean release);
	
	/**
	 * This method is called on every press and release of any
	 * other key which is not supported by other KeyMap callback
	 * methods ({@link #onEscape(boolean)}, {@link #onEnter(boolean)},
	 * {@link #onSpace(boolean)}, and {@link #onKey(int, boolean)}).
	 * 
	 * @param key	one of the GLFW constants referring to a key,
	 * such as {@link GLFW#GLFW_KEY_M}.
	 * @param release	true if the click was a release, false if it
	 * was a press.
	 */
	public abstract void onKey(int key, boolean release);
	
	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		
		// Trigger the appropriate key callbacks
		switch (key) {
		case GLFW.GLFW_KEY_ESCAPE:
			onEscape(action == GLFW.GLFW_RELEASE); break;
		case GLFW.GLFW_KEY_ENTER:
			onEnter(action == GLFW.GLFW_RELEASE); break;
		case GLFW.GLFW_KEY_SPACE:
			onSpace(action == GLFW.GLFW_RELEASE); break;
		default: // Up to subclasses to deal with other keys
		}
		
	}
	
}
