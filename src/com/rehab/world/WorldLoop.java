package com.rehab.world;


public class WorldLoop extends Thread {
	
	// Number of ticks per second
	private int mTarTick;
	// Desired duration in nanoseconds
	private long mTickInterval;
	// Whether or not to keep looping
	private boolean mLoop = true;
	
	// The Singleton's instance
	private static WorldLoop mInstance;
	
	// The level to run
	private Arena mLvl;
		
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
	 * Constructor for the World's state loop.
	 * @param tickRate
	 * 		the desired ticks per second.
	 * @param arena
	 * 		the level to simulate.
	 */
	private WorldLoop(int tickRate, Arena arena) {
		mLvl = arena;
		mLvl.setTime(tickRate);
		// Measure time needed
		mTarTick = tickRate;
		mTickInterval = 1000000000 / mTarTick;
	}
	
	/**
	 * The set desired ticks per second.
	 * @return
	 * 		the number of ticks per second.
	 */
	public int getTickRate() { return mTarTick; }
		
	@Override
	public void run() {
		super.run();
		
		while (mLoop) {
			long tickStart = System.nanoTime();
			
			// Run physics for 1 unit
			mLvl.step();
						
			long tickDur = System.nanoTime() - tickStart;
			

			// Skip frame if tick was too long
			if (tickDur < mTickInterval) {
				try {
					Thread.sleep((mTickInterval - tickDur) / 1000000);
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
		}
		
	}
	
}
