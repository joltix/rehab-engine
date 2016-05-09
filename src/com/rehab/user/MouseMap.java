package com.rehab.user;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

/**
 * <p>
 * MouseMap allows for a hook into the GLFW toolkit's mouse callbacks.
 * Subclasses must implement {@link #onLeftClick(double, double, boolean)},
 * {@link #onMiddleClick(double, double, boolean)}, and {@link #onRightClick(double, double, boolean)}.
 * These methods will allow not only notification of the specific type of click
 * (eg. left, middle, or right) but also the location of the action.
 * </p>
 * 
 * <p>
 * The following example shows how to define a MouseMap and set it to a Canvas.
 * This implementation will print the location the left mouse button was pressed
 * into the console.
 * </p>
 * 
 * <pre>
 * 	<code>
 * LWCanvas canvas = LWCanvas.create(640, 480, "Hello", false);
 * MouseMap mouseMap = new MouseMap(canvas.getHeight()){
 *
 *		@Override
 *		public void onLeftClick(double x, double y, boolean press) {
			if (press) {
				System.out.printf("Location pressed at(%f,%f)\n", x, y);
			}
 *		}
 *
 *		@Override
 *		public void onRightClick(double x, double y, boolean press) {
 *			
 *		}
 *
 *		@Override
 *		public void onMiddleClick(double x, double y, boolean press) {
 *			
 *		}};
 *			
 * canvas.setKeyMap(keyMap);
 * 	</code>
 * </pre>
 */
public abstract class MouseMap extends GLFWMouseButtonCallback {

	// Height of the window to orient mouse origin at bottom left
	private double mWindowHeight;
	
	// Location of mouse
	private double mX = 0, mY = 0;
	
	/**
	 * This callback is meant to continuously update the cursor position on-screen where
	 * origin is bottom left of the LWCanvas.
	 */
	private GLFWCursorPosCallback mLocation = new GLFWCursorPosCallback(){

		@Override
		public void invoke(long window, double xpos, double ypos) {
			mX = xpos;
			// Orient origin to bottom left
			mY = mWindowHeight - ypos;
		}};
	
	/**
	 * Basic constructor for a MouseMap given the window's height
	 * to orient origin position to the bottom left of the window.
	 * 
	 * @param winHeight	height of the window.
	 */
	public MouseMap(double winHeight) {
		mWindowHeight = winHeight;
	}
		
	/**
	 * This method is called on every left click of the mouse.
	 * 
	 * @param x	the x-coordinate of the click.
	 * @param y	the y-coordinate of the click.
	 * @param release	true if the click was a release, false if it was a press.
	 */
	public abstract void onLeftClick(double x, double y, boolean release);
	
	/**
	 * This method is called on every right click of the mouse.
	 * 
	 * @param x	the x-coordinate of the click.
	 * @param y	the y-coordinate of the click.
	 * @param release	true if the click was a release, false if it was a press.
	 */
	public abstract void onRightClick(double x, double y, boolean release);
	
	/**
	 * This method is called on every middle click of the mouse.
	 * 
	 * @param x	the x-coordinate of the click.
	 * @param y	the y-coordinate of the click.
	 * @param release	true if the click was a release, false if it was a press.
	 */
	public abstract void onMiddleClick(double x, double y, boolean release);
	
	/**
	 * Gets the GLFWCursorPosCallback to be set with the LWCanvas to track the
	 * mouse's position during clicks.
	 * 
	 * @return the GLFWCursorPosCallback for location reporting.
	 */
	public GLFWCursorPosCallback getLocationCallback() { return mLocation; }
	
	@Override
	public void invoke(long window, int button, int action, int mods) {
		// Trigger the appropriate callback
		switch (button) {
		case GLFW.GLFW_MOUSE_BUTTON_LEFT:
			onLeftClick(mX, mY, action == GLFW.GLFW_RELEASE); break;
		case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
			onRightClick(mX, mY, action == GLFW.GLFW_RELEASE); break;
		case GLFW.GLFW_MOUSE_BUTTON_MIDDLE:
			onMiddleClick(mX, mY, action == GLFW.GLFW_RELEASE);
		}
	}

}
