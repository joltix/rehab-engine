package com.rehab.animation;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import com.rehab.world.Frame.Renderable;
import com.rehab.world.InstanceManager;
import com.rehab.world.LayerManager;
import com.rehab.world.LoopLogger;
import com.rehab.world.WorldLoop;

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
	
	// Singleton instance
	private static LWCanvas mInstance = null;
	// Framerate logging
	private LoopLogger mFrameLog = new LoopLogger(LWCanvas.class.getCanonicalName(), 2);
	// Render for layering objects to draw
	private Renderer mRender = Renderer.getInstance();
	
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
		GLFW.glfwSwapInterval(1);
		GLFW.glfwShowWindow(mWinId);
		
		// Begin looping
		loop();
	}
	
	/**
	 * Signals to the LWCanvas that the window must be closed.
	 */
	public void shutdown() {
		mRun = false;
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
		
		// Prepare OpenGL
		initGL();
		GL11.glLoadIdentity();
		
		// Loop to keep the window open on-screen
		while (mRun) {
			
			mFrameLog.begin();
			
			// Extract the 
			Iterable<Renderable> background = mRender.getLayer(LayerManager.LAYER_BACKGROUND);
			Iterable<Renderable> free1 = mRender.getLayer(LayerManager.LAYER_FREE_2);
			Iterable<Renderable> free2 = mRender.getLayer(LayerManager.LAYER_FREE_1);
			Iterable<Renderable> props = mRender.getLayer(LayerManager.LAYER_PROP);
			Iterable<Renderable> gui = mRender.getLayer(LayerManager.LAYER_GUI);
			
			// Clear the screen
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			
			// Draw all layers of the game
			drawLayer(background);
			drawLayer(free1);
			drawLayer(free2);
			drawLayer(props);
			drawLayer(gui);
			
			// Ensure buffer is emptied for next draw
			GL11.glFlush();
			
			// Change for double buffer
			GLFW.glfwSwapBuffers(mWinId);
			
			// Check for window events (such as close button click on window)
			GLFW.glfwPollEvents();
			
			// User clicked on window close button
			if (GLFW.glfwWindowShouldClose(mWinId) == GLFW.GLFW_TRUE) {
				mRun = false;
			}
			
			mFrameLog.end();
		}
		
		// Clean up window and stop the game world
		GLFW.glfwDestroyWindow(mWinId);
		WorldLoop.getInstance().halt();
	}
	
	/**
	 * Sets up OpenGL and prepares for drawing.
	 */
	private void initGL() {
	    
		// Change view to flatten screen for 2D
		GL.createCapabilities();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, mWidth, 0, mHeight, -1, 1);
		GL11.glViewport(0, 0, mWidth, mHeight);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		// Setup texture mode
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glGenTextures();
		
		// Prepare for transparency
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	
	/**
	 * Draws all Renderables within a given Iterable using the drawing
	 * parameters stored within each Renderable. E.g. whether or not
	 * the object to be drawn should be rotated, whether or not to mirror,
	 * and others.
	 * 
	 * @param layer	the Iterable of Renderables.
	 */
	private void drawLayer(Iterable<Renderable> layer) {
		
		// Data to draw
		Sprite spr;
		double x;
		double y;
		double rot;
		boolean mirror;
		
		// Draw each Renderable in the layer
		for (Renderable renderable : layer) {
			
			// Pull drawing data out
			spr = renderable.getSprite();
			x = renderable.getX();
			y = renderable.getY();
			rot = renderable.getRotation();
			mirror = renderable.isFacingLeft();
			
			// Draw on screen
			draw(spr, x, y, rot, mirror);
		}

	}
	
	/**
	 * Draws a given Sprite onto the screen at a specific x and y location with
	 * a rotation angle. For an image to 
	 * 
	 * @param sprite	Sprite containing pixels to draw.
	 * @param x	x-coordinate to draw at.
	 * @param y	y-coordinate to draw at.
	 * @param rotation	angle offset to rotate image.
	 * @param mirror	true to show the image mirrored along the vertical axis.
	 */
	private void draw(Sprite sprite, double x, double y, double rotation, boolean mirror) {
		
		// Compute coords opposite of location
		double width = sprite.getWidth();
		double height = sprite.getHeight();
		
		// Set image's pixel data
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, sprite.getWidth(),
				sprite.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, sprite.getByteBuffer());
				
		// Apply rotation and game location
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, 0);
		GL11.glRotated(rotation, 0, 0, 1);
		GL11.glBegin(GL11.GL_QUADS);
		

		// Map the top left corner
		if (mirror) {
			GL11.glTexCoord2d(1, 1);
		} else {
			GL11.glTexCoord2d(0, 1);
		}
		GL11.glVertex2d(0, 0);
		
		
		// Map bottom left
		if (mirror) {
			GL11.glTexCoord2d(1, 0);
		} else {
			GL11.glTexCoord2d(0, 0);
		}
		GL11.glVertex2d(0, height);
		
		
		// Map bottom right
		if (mirror) {
			GL11.glTexCoord2d(0, 0);
		} else {
			GL11.glTexCoord2d(1, 0);
		}
		GL11.glVertex2d(width, height);
		
		
		// Map top right
		if (mirror) {
			GL11.glTexCoord2d(0, 1);
		} else {
			GL11.glTexCoord2d(1, 1);
		}
		GL11.glVertex2d(width, 0);
		
		
		// Finalize object and reset for next draw
		GL11.glEnd();
		GL11.glPopMatrix();
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
