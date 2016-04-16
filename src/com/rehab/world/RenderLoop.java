package com.rehab.world;

import java.util.LinkedList;
import java.util.Queue;

import com.rehab.animation.Drawable;
import com.rehab.animation.Sprite;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class RenderLoop extends Thread {

	// Layers to sort game objects
	private LayerManager mLayers = new LayerManager();

	// Drawing surface and components
	private Canvas mCanvas;
	private GraphicsContext mGfx;
	private WritableImage mBuffer1 = new WritableImage(720, 480),
							mBuffer2 = new WritableImage(720, 480),
							mSelBuffer = mBuffer1;

	// Singleton instance
	private static RenderLoop mInstance;

	// Loop control
	private boolean mLoop = true;
	private long mTarRate, mInterval;

	// Drawing buffer for dynamically created game objects
	private Queue<Drawable> mAddToDraw = new LinkedList<Drawable>();

	/**
	 * Gets an instance of the RenderLoop. This method should only be used when {@link #getInstance(int, Canvas)}
	 * has already been called. Using this method first will throw an IllegalStateException.
	 * 
	 * @return	the RenderLoop instance.
	 * @throws IllegalStateException	if {@link #getInstance(int, Canvas)} has never been called.
	 */
	public static RenderLoop getInstance() {
		synchronized (RenderLoop.class) {
			if (mInstance == null) throw new IllegalStateException("getInstance(int, Canvas) has never been called");
			return mInstance;
		}
	}

	/**
	 * Gets an instance of the RenderLoop. This method should be used before {@link #getInstance()}
	 * to initialize the instance. Further calls to retrieve the RenderLoop instance can then
	 * be done using {@link #getInstance()}.
	 * 
	 * @param fps	the desired frames per second.
	 * @param arena	the Canvas to draw on.
	 */
	public static RenderLoop getInstance(int fps, Canvas c) {
		synchronized (RenderLoop.class) {
			if (mInstance == null) mInstance = new RenderLoop(fps, c);
			return mInstance;
		}
	}

	/**
	 * Checks whether or not the RenderLoop has begun.
	 * 
	 * @return	true if the RenderLoop has begun, false otherwise.
	 */
	public static boolean isRunning() {
		if (mInstance == null) return false;
		return mInstance.isAlive();
	}

	/**
	 * Constructor for specifying a frame rate and drawing surface.
	 * 
	 * @param fps	the desired frames per second.
	 * @param c	the Canvas to draw on.
	 * @throws IllegalArgumentException	if the desired fps is not greater
	 * than 0.
	 */
	private RenderLoop(int fps, Canvas c) {
		if (fps <= 0) {
			throw new IllegalArgumentException("Target framerate should be greater than 0");
		}
		mCanvas = c;
		mGfx = mCanvas.getGraphicsContext2D();
		// Loop time control
		mTarRate = fps;
		mInterval = 1000000000 / mTarRate;
	}

	/**
	 * Add all Drawables to be drawn. This method must only be called once - before
	 * calls to {@link #isRunning()} return true.
	 * 
	 * @throws IllegalStateException	when this method is called after run().
	 * @see #isRunning()
	 */
	public void loadLayers() {
		if (isRunning()) {
			throw new IllegalStateException("loadLayers() must be called before RenderLoop begins");
		}
		
		InstanceManager instaMan = InstanceManager.getInstance();
		// Load actors
		for (Actor a : instaMan.getLoadedActors()) {
			//System.out.printf("Loading Actor %s\n", a);
			mLayers.add(new Renderable(a));
		}
		// Load projectiles
		for (Projectile p : instaMan.getLoadedProjectiles()) {
			//System.out.printf("Loading Projectile %s\n", p);
			mLayers.add(new Renderable(p));
		}
		// Load props
		for (Prop p : instaMan.getLoadedProps()) {
			//System.out.printf("Loading Prop %s\n", p);
			mLayers.add(new Renderable(p));
		}
	}

	/**
	 * Enqueues a Drawable to be drawn in the next draw frame. This method
	 * should be used to submit a game object for rendering after
	 * {@link #start()} has already
	 * been called.
	 * 
	 * @param drawable	the new Drawable to render.
	 * @see #isRunning()
	 * @see #start()
	 */
	public void requestDraw(Drawable drawable) {
		mAddToDraw.add(drawable);
	}

	@Override
	public void run() {
		super.run();

		while (mLoop) {
			long frameStart = System.nanoTime();

			// Swap buffers to work on
			if (mSelBuffer == mBuffer1) mSelBuffer = mBuffer2;
			else mSelBuffer = mBuffer1;

			PixelWriter writer = mSelBuffer.getPixelWriter();
			// Clear the buffer from previous display
			clearBuffer(writer);


			// Draw all layers
			drawLayer(LayerManager.LAYER_BACKGROUND, writer);
			drawLayer(LayerManager.LAYER_FREE_2, writer);
			drawLayer(LayerManager.LAYER_FREE_1, writer);
			drawLayer(LayerManager.LAYER_PROP, writer);
			drawLayer(LayerManager.LAYER_GUI, writer);
			
			
			// Add new Drawables for next draw loop
			Drawable drawable;
			while ((drawable = mAddToDraw.poll()) != null) {
				mLayers.add(new Renderable(drawable));
			}

			// Set the buffer to be displayed on-screen
			mGfx.drawImage(mSelBuffer, 0, 0);

			// Skip next frame if previous took too long
			long frameDur = System.nanoTime() - frameStart;
			if (frameDur > mInterval) {
				try {
					Thread.sleep((frameDur - mInterval) / 1000000);
				} catch (InterruptedException e) { e.printStackTrace(); }
			} else if (frameDur < mInterval) {
				try {
					Thread.sleep((mInterval - frameDur) / 1000000);
				} catch (InterruptedException e) { e.printStackTrace(); }
			}

		}

	}
	
	/**
	 * Draws the layer specified by a given layer constant.
	 * 
	 * @param layerIndex	the layer constant.
	 * @param writer	the buffer Image to draw to.
	 */
	private void drawLayer(int layerIndex, PixelWriter writer) {
		Queue<Renderable> layer = mLayers.getLayer(layerIndex);
		Renderable obj;
		while ((obj = layer.poll()) != null) {
			drawToBuffer(obj, writer);
		}
	}

	/**
	 * Erases the currently selected buffer using a dark grey color.
	 * 
	 * @param writer	the PixelWriter to draw on.
	 */
	private void clearBuffer(PixelWriter writer) {
		// Color in ARGB int format (dark grey)
		int blank = 255;
		blank = (blank << 8) + 40;
		blank = (blank << 8) + 40;
		blank = (blank << 8) + 40;

		// Paint over buffer to erase previous img
		for (int x = 0, w = (int) mSelBuffer.getWidth(); x < w; x++) {
			for (int y = 0, h = (int) mSelBuffer.getHeight(); y < h; y++) {
				writer.setArgb(x, y, blank);
			}	
		}

	}

	/**
	 * Draws a Drawable object to the currently selected buffer.
	 * 
	 * @param obj	the Drawable to draw onto the buffer.
	 * @param writer	the PixelWriter of the buffer.
	 */
	private void drawToBuffer(Renderable obj, PixelWriter writer) {

		// Get location offset relative to overall screen
		int offX = (int) Math.floor(obj.x);
		int offY = (int) Math.floor((480 - obj.y));

		obj.sprite.draw(writer, offX, offY);
	}
	
	public class Renderable {
		
		public Sprite sprite;
		public double x, y;
		public int z;
		
		public Renderable(Drawable drawable) {
			sprite = drawable.getSprite();
			this.x = drawable.getX();
			this.y = drawable.getY();
			this.z = drawable.getZ();
		}
	}


}
