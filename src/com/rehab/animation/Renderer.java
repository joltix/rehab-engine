package com.rehab.animation;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.rehab.world.Frame;
import com.rehab.world.Frame.Renderable;
import com.rehab.world.LayerManager;

public class Renderer {

	// The singleton instance
	private static Renderer mInstance;
	
	// Manager to sort Renderables
	private LayerManager mLayerMan;
	// Queue of Frames to be drawn onto the screen
	private ConcurrentLinkedQueue<Frame> mFrameBuffer = new ConcurrentLinkedQueue<Frame>();
		
	private boolean mRunning = false;
	
	private Renderer() {
		mLayerMan = new LayerManager();
	}
	
	/**
	 * Gets an instance of the Render.
	 * 
	 * @return	the instance.
	 */
	public static Renderer getInstance() {
		synchronized (Renderer.class) {
			if (mInstance == null) {
				mInstance = new Renderer();
			}
			return mInstance;
		}
	}
	
	/**
	 * Checks whether or not drawing has begun. That is, this method returns
	 * true if {@link #getLayer(int)} has been called at least once.
	 * 
	 * @return true drawing has begun, false otherwise.
	 */
	public static boolean isRunning() {
		return Renderer.getInstance().mRunning;
	}
	
	/**
	 * Stores a Frame filled with Renderables to be drawn on the next
	 * drawing loop.
	 * 
	 * @param frame	the Frame holding all things to draw.
	 */
	public void requestDraw(Frame frame) {
		mFrameBuffer.add(frame);
		
		// Sort the Renderables into proper layers
		for (Renderable renderable : frame.renderables()) {
			mLayerMan.add(renderable);
		}
	}
	
	/**
	 * Gets an Iterable of Renderables that should be associated
	 * with a given layer.
	 * 
	 * @param layerIndex	constant for the layer to draw.
	 * @return	the Iterable of Renderables.
	 */
	public Iterable<Renderable> getLayer(int layerIndex) {
		mRunning = true;
		return mLayerMan.getLayer(layerIndex);
	}
	
	/**
	 * Gets the number of Frames waiting to be drawn.
	 * 
	 * @return	number of idle Frames
	 */
	public int countFrames() {
		return mFrameBuffer.size();
	}
	
}
