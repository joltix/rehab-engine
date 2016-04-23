package com.rehab.world;

public class FPSLogger {

	// num of nanoseconds in 1 second
	private static final long NANO_IN_SECOND = 1000000000;
	
	// identifying String
	private String mTag;
	
	// begin() and end() calls in nanoseconds
	private long mStart;
	private long mEnd;
	
	// total duration of events so far in nanoseconds
	private long mInterval = 0;
	
	private int mFPS = 0;
	
	public FPSLogger(String tag) {
		mTag = tag;
	}
	
	public void begin() {
		mStart = System.nanoTime();
	}
	
	public void end() {
		
		mEnd = System.nanoTime();
		
		long duration = mEnd - mStart;
		System.out.printf("[%s] fps(%f)\n", mTag, Math.floor(NANO_IN_SECOND / duration));
		mStart = mEnd;
		
//		mEnd = System.nanoTime();
//		
//		// Add duration of event to time
//		mInterval += (mEnd - mStart);
//		
//		mStart = mEnd;
//		if (mInterval >= NANO_IN_SECOND) {
//			System.out.printf("[%s] fps(%d)\n", mTag, mFPS);
//			mFPS = 0;
//			mInterval = 0;
//		} else {
//			//System.out.printf("FPS++ INTERVAL %d ACCUMULATED\n", mInterval);
//			mFPS++;
//		}
	}
		
}
