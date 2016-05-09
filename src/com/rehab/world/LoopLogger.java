package com.rehab.world;

public class LoopLogger {

	// Num of nanoseconds in 1 second
	private static final long NANO_IN_SECOND = 1000000000;
	
	// Identifying String
	private String mTag;
	
	// Beginning and ending markers calls in nanoseconds
	private long mStart;
	private long mEnd;
	
	// Total duration of events so far in nanoseconds
	private long mInterval = 0;
	// Duration between print statements
	private long mPrintPeriod = 0;
	// Amount of frames so far
	private int mCounter = 0;
	
	/**
	 * Basic constructor with a debugging tag to identify the console statements.
	 * 
	 * @param tag	identifying logging name.
	 * @param seconds	number of seconds between which to print the duration.
	 */
	public LoopLogger(String tag, int seconds) {
		// Must have a valid logging name
		if (tag == null) {
			throw new IllegalArgumentException("Tag may not be null");
		}
		
		// Prevent too big a value for long
		if (seconds >= 99999999) {
			throw new IllegalArgumentException("Period must be < 99999999 seconds");
		}
		
		// Printing period can't be <= 0
		if (seconds < 1) {
			throw new IllegalArgumentException("Printing period must be at least 1 second");
		}
		
		mTag = tag;
		mPrintPeriod = NANO_IN_SECOND * seconds;
	}
	
	/**
	 * Marks the beginning of the loop to time.
	 */
	public void begin() {
		mStart = System.nanoTime();
	}
	
	/**
	 * Marks the ending of a loop to time. If this method is called when the
	 * print interval i
	 */
	public void end() {
		// Current time
		mEnd = System.nanoTime();
		
		// Add duration of event to time
		mInterval += (mEnd - mStart);
		mStart = mEnd;
		
		// Print frame rate to console at intervals
		if (mInterval >= mPrintPeriod) {
			System.out.printf("[%s] fps(%d)\n", mTag, mCounter);
			mCounter = 0;
			mInterval = 0;
		} else {
			// Not yet time to print so keep counting
			mCounter++;
		}
	}
		
}
