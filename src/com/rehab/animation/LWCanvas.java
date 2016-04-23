package com.rehab.animation;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

/**
 * <p>
 * A windowing class backed by LWJGL's GLFW. This class is currently experimental and
 * should not yet be used. <b>Calling LWCanvas's {@link #show()} will block the main thread</b>.
 * </p>
 * 
 * <p>
 * This class currently has limited functionality. The only supported operations are
 * setting window dimensions, window title, fullscreen or windowed mode, and
 * ESCAPE key to close.
 * </p>
 */
public class LWCanvas {

	private static LWCanvas mInstance = null;
	
	// Canvas properties
	private String mTitle;
	private long mWinId;
	private int mWidth;
	private int mHeight;
	
	// Desktop resolution
	private int mMonResWidth;
	private int mMonResHeight;
	
	// Loop control
	private boolean mRun = true;
	
	private GLFWKeyCallback mKeyInput = new GLFWKeyCallback(){

		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			// Escape key means user wanted to exit
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
				mRun = false;
			}
		}};
	 
	/**
	 * Constructor for a basic Canvas of given dimensions.
	 * 
	 * @param width	width of the Canvas.
	 * @param height	height of the Canvas.
	 * @param title	name of window title bar.
	 * @param fullscreen	true to make the Canvas fullscreen,
	 * false for windowed mode.
	 * @throws RuntimeException	if the window failed to
	 * initialize resources.
	 */
	private LWCanvas(int width, int height, String title, boolean fullscreen) {
		if (GLFW.glfwInit() == GLFW.GLFW_FALSE) {
			throw new RuntimeException("Failed to initialize window resources");
		}
		// Measure screen
		getPrimaryResolution();
		
		// Store properties
		mWidth = width;
		mHeight = height;
		mTitle = title;
		
		// Setup GLFW properties
		initWindowProperties();
		// Create window id and center it
		mWinId = initWindow(fullscreen);
		centerWindow();
	}
	
	/**
	 * Initializes resources required for the creation of a window.
	 *
	 * @param width	window width
	 * @param height	window height
	 * @param title	name in title bar.
	 * @return the created window.
	 * @throws IllegalStateException	if this method is called
	 * more than once.
	 */
	public static LWCanvas create(int width, int height, String title, boolean fullscreen) {
		if (mInstance != null) {
			throw new IllegalStateException("Canvas was already initialized");
		}
		mInstance = new LWCanvas(width, height, title, fullscreen);
		return mInstance;
	}
	
	/**
	 * Gets an instance of the Canvas.
	 *
	 * @return	the instance.
	 */
	public LWCanvas getInstance() {
		synchronized (mInstance) {
			return mInstance;
		}
	}
	
	/**
	 * Initializes properties of the window such as resizability, among others.
	 */
	private void initWindowProperties() {
		// Default properties for window but not resizable and hidden
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_TRUE);
	}
	
	/**
	 * Initializes the GLFW windowing components.
	 */
	private long initWindow(boolean fullscreen) {
		
		long primMonId = MemoryUtil.NULL;
		// Assign main monitor if fullscreen desired
		if (fullscreen) {
			 primMonId = GLFW.glfwGetPrimaryMonitor();
			 mWidth = mMonResWidth;
			 mHeight = mMonResHeight;
		}
		
		// Attempt to create window
		long id = GLFW.glfwCreateWindow(mWidth, mHeight, mTitle, primMonId, MemoryUtil.NULL);
		if (id == MemoryUtil.NULL) {
			throw new IllegalStateException("Failed to initialize window");
		}
		
		// Hook key input
		GLFW.glfwSetKeyCallback(id, mKeyInput);
		
		return id;
		
	}
	
	/**
	 * Centers the window on the screen.
	 */
	private void centerWindow() {
		GLFWVidMode mode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		int centerX = (mode.width() / 2) - (mWidth / 2);
		int centerY = (mode.height() / 2) - (mHeight / 2);
		GLFW.glfwSetWindowPos(mWinId, centerX, centerY);
	}
	
	/**
	 * Makes the Canvas visible on-screen.
	 */
	public void show() {
		GLFW.glfwMakeContextCurrent(mWinId);
		GLFW.glfwShowWindow(mWinId);
		
		// Begin looping
		loop();
	}
	
	/**
	 * Gets the width of the Canvas. If the Canvas is fullscreen, then
	 * this method returns the width of the desktop's resolution.
	 *
	 * @return Canvas' width.
	 */
	public int getWidth() { return mWidth; }
	
	/**
	 * Gets the height of the Canvas. If the Canvas is fullscreen, then
	 * this method returns the height of the desktop's resolution.
	 *
	 * @return Canvas' height.
	 */
	public int getHeight() { return mHeight; }
		
	/**
	 * Gets the current width and height of the desktop.
	 */
	private void getPrimaryResolution() {
		long primMonId = GLFW.glfwGetPrimaryMonitor();
		if (primMonId == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to retrieve primary monitor");
		}
		
		// Retrieve dimensions
		GLFWVidMode mode = GLFW.glfwGetVideoMode(primMonId);
		mMonResWidth = mode.width();
		mMonResHeight = mode.height();
	}
	
	/**
	 * Internal main loop to keep the window open.
	 */
	private void loop() {
		
		while (mRun) {
			// Check for window events (such as close button click on window)
			GLFW.glfwPollEvents();
			
			// User clicked on window close button
			if (GLFW.glfwWindowShouldClose(mWinId) == GLFW.GLFW_TRUE) {
				mRun = false;
			}
		}
		
		// Clean up window
		GLFW.glfwDestroyWindow(mWinId);
	}
	
	/**
	 * Demo and testing
	 *
	 * @param args	command line arguments.
	 */
	public static void main(String[] args) {
		LWCanvas canvas = LWCanvas.create(250, 250, "Rehab", true);
		canvas.show();
	}	
	
	
}
