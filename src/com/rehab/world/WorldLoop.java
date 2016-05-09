package com.rehab.world;


import com.rehab.animation.Renderer;

public class WorldLoop extends Thread {
	
	// Number of ticks per second
	private int mTarTick;
	// Desired duration in nanoseconds
	private long mTickInterval;
	
	private long mLastTickStart = 0;
	private long mLastTickDuration = 0;
	
	// Whether or not to keep looping
	private boolean mLoop = true;
	
	// The Singleton's instance
	private static WorldLoop mInstance;
	
	// The level to run
	private Arena mLvl;
	// Source of Frames to fill and send for drawing
	private FrameDepot mDepot = FrameDepot.getInstance();
	
		
	/**
	 * Constructor for the World's state loop.
	 * @param tickRate
	 * 		the desired ticks per second.
	 * @param arena
	 * 		the level to simulate.
	 */
	private WorldLoop(int tickRate, Arena arena) {
		mLvl = arena;
		mLvl.setTimescale(1d / tickRate);
		// Measure time needed
		mTarTick = tickRate;
		mTickInterval = 1000000000 / mTarTick;
	}

	/**
	 * Gets an instance of the WorldLoop. This method should only be used if getInstance(int, Arena)
	 * was ever called at least once. Calling this method first will throw an IllegalStateException.
	 * @throws IllegalStateException
	 * 		if getInstance(int, Arena) has never been called.
	 */
	public static WorldLoop getInstance() {
		synchronized (WorldLoop.class) {
			if (mInstance == null) throw new IllegalStateException("getInstance(int, Arena) has never been called");
			return mInstance;
		}
	}
	
	/**
	 * Gets an instance of the WorldLoop.
	 * @param tickRate
	 * 		the desired ticks per second.
	 * @param arena
	 * 		the level to simulate.
	 */
	public static WorldLoop getInstance(int tickRate, Arena arena) {
		synchronized (WorldLoop.class) {
			if (mInstance == null) mInstance = new WorldLoop(tickRate, arena);
			return mInstance;
		}
	}
	
	/**
	 * The set desired ticks per second.
	 * @return
	 * 		the number of ticks per second.
	 */
	public int getTickRate() { return mTarTick; }
	
	/**
	 * Attempts to stop the WorldLoop from running. This method may
	 * be safely called from any {@link Thread}.
	 */
	public static void halt() {
		synchronized (WorldLoop.class) {
			if (mInstance == null) {
				return;
			}
			mInstance.mLoop = false;
		}
	}
	
	@Override
	public void run() {
		super.run();
		
		mLastTickStart = System.nanoTime();
		
		while (mLoop) {
			mLastTickDuration = System.nanoTime() - mLastTickStart;
			mLastTickStart = System.nanoTime();
			
			// Run physics for at least 1 unit (+ more based on previous frame duration)
			do {
				mLvl.stepActors();
				mLvl.stepProjectiles();
				mLastTickDuration -= mTickInterval;
			} while (mLastTickDuration > mTickInterval);
			
			// Send draw requests to update screen
			InstanceManager manager = InstanceManager.getInstance();
			Iterable<Actor> acts = manager.getLoadedActors();
			Iterable<Projectile> projs = manager.getLoadedProjectiles();
			Iterable<Prop> props = manager.getLoadedProps();
			
			Frame frame = mDepot.requestFrame();
			if (frame == null) {
				frame = new Frame();
			}
			
			// Send draw requests for all visible game objs
			for (Actor a : acts) {
				if (a.isVisible()) {
					frame.push(a);
				}
			}
			for (Projectile p : projs) {
				if (p.isVisible()) {
					frame.push(p);
				}
			}
			for (Prop p : props) {
				if (p.isVisible()) {
					frame.push(p);
				}
			}
			Renderer.getInstance().requestDraw(frame);
			
			long currentDuration = System.nanoTime() - mLastTickStart;
			if (currentDuration < mTickInterval) {
				try {
					Thread.sleep((mTickInterval - currentDuration) / 1000000);
				} catch (InterruptedException e) { e.printStackTrace(); }
			} else if (currentDuration > mTickInterval) {
				try {
					Thread.sleep((currentDuration - mTickInterval) / 1000000);
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
		
	}
	
}
