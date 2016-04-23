package com.rehab.world;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <p>
 * Holds all {@link Frame}s to be used when Rendering objects on-screen. Frames that
 * have already been rendered are "cleaned" for reuse through calls to {@link #recycleFrame(Frame)},
 * which will fill the FrameDepot's store of Frames for calls to {@link #requestFrame()}
 * to return an empty Frame for the next draw call.
 * </p>
 */
public class FrameDepot {
	
	// How many Renderable objs each Frame can hold
	public static final int FRAME_CAPACITY = 100;
	// How many Frames are instantiated in the beginning
	private static final int INITIAL_LOAD = 120;
	// Frames to be reused
	private ConcurrentLinkedQueue<Frame> mEmptyFrames = new ConcurrentLinkedQueue<Frame>();
	
	private static FrameDepot mInstance = new FrameDepot();
	
	/**
	 * Constructor for a FrameDepot.
	 */
	private FrameDepot() {
		for (int i = 0; i < INITIAL_LOAD; i++) {
			mEmptyFrames.add(new Frame());
		}
	}
	
	/**
	 * Gets an instance of the FrameDepot for handling Frame usage between
	 * the game world and the render loop.
	 *
	 * @return the FrameDepot.
	 */
	public static FrameDepot getInstance() {
		synchronized (FrameDepot.class) {
			if (mInstance == null) {
				mInstance = new FrameDepot();
			}
			return mInstance;
		}
	}
	
	/**
	 * Gets the next empty Frame available.
	 *
	 * @return the Frame, or null if none is available.
	 */
	public Frame requestFrame() {
		return mEmptyFrames.poll();
	}
	
	/**
	 * Empties a Frame and prepares it to be reused in a future draw cycle.
	 *
	 * @param frame	the Frame to reuse.
	 */
	public void recycleFrame(Frame frame) {
		frame.clear();
		mEmptyFrames.add(frame);
	}

}
