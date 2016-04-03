package com.rehab.world;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * StatTracker keeps track of gameplay values such as "score" for the game's points
 * or "deaths" for the player. Many of this class' methods will throw an
 * {@link IllegalArgumentException} if the {@link String} that is supposed to be
 * associated with a statistic has not yet been assigned one. That is, {@link #createStat(String)}
 * must first be called before operating upon such a statistic. This is to ensure
 * that access to statistics is deliberate. The following snippet demonstrates the
 * creation of statistics with {@link #createStat(String)}.
 * </p>
 * 
 * <pre>
 *  <code>
 *  StatTracker tracker = StatTracker.getInstance();
 *  
 *  // All creations begin with a value of 0
 *  tracker.createStat("score");
 *  tracker.createStat("kills");
 *  tracker.createStat("deaths");
 *  </code>
 * </pre>
 * 
 * <p>
 * There are a few methods for manipulating a statistic's value. The snippet below
 * presumes the snippet above has occurred and demonstrates
 * {@link #setValueOf(String, double)}, {@link #getValueOf(String)}, 
 * {@link #addToStat(String, double)}, {@link #subtractFromStat(String, double)}.
 * </p>
 * 
 * <pre>
 *  <code>
 *  StatTracker tracker = StatTracker.getInstance();
 *  
 *  // Direct value replacement
 *  tracker.setValueOf("score", 20);
 *  
 *  // Addition
 *  tracker.addToStat("score", 10);
 *  
 *  // Zero out the value by subtracting from itself
 *  double val = tracker.getValueOf("score");
 *  tracker.subtractFromStat("score", val);
 *  </code>
 * </pre>
 * 
 * <p>
 * This class is thread-safe.
 * </p>
 */
public class StatTracker {

	// Table of statistics and their values
	private HashMap<String, Double> mStats = new HashMap<String, Double>();
	// StatTracker singleton instance
	private StatTracker mInstance;
		
	/**
	 * Gets an instance of the StatTracker.
	 *
	 * @return the StatTracker.
	 */
	public StatTracker getInstance() {
		synchronized (StatTracker.class) {
			if (mInstance == null) {
				mInstance = new StatTracker();
			}
			return mInstance;
		}
	}
	
	/**
	 * Creates a statistic with the given name. If a statistic with the same
	 * name already exists, this method has no effect.
	 *
	 * @param name	the name of the statistic.
	 */
	public void createStat(String name) {
		synchronized (mInstance) {
			if (mStats.containsKey(name)) {
				return;
			}
			mStats.put(name, 0d);
		}
	}
	
	/**
	 * Adds an amount to a specified statistic.
	 *
	 * @param name	name of the statistic.
	 * @param val	amount to add.
	 * @throws IllegalArgumentException	if no statistic exists with the given
	 * name.
	 * @see #subtractFromStat(String, double)
	 */
	public void addToStat(String name, double val) {
		synchronized (mInstance) {
			ensureValidStat(name);
			// Pull value and add before storing again
			double storedVal = mStats.get(name);
			mStats.put(name, storedVal + val);
		}
	}
	
	/**
	 * Subtracts an amount from a specified statistic.
	 *
	 * @param name	name of the statistic.
	 * @param val	amount to subtract.
	 * @throws IllegalArgumentException	if no statistic exists with the given
	 * name.
	 * @see #addToStat(String, double)
	 */
	public void subtractFromStat(String name, double val) {
		synchronized (mInstance) {
			ensureValidStat(name);
			// Pull value and subtract before storing again
			double storedVal = mStats.get(name);
			mStats.put(name, storedVal - val);
		}
	}
	
	/**
	 * Gets the stored value of a statistic.
	 *
	 * @param name	the name of the statistic.
	 * @return	the value.
	 * @throws IllegalArgumentException	if no statistic exists with the given
	 * name.
	 * @see #setValueOf(String, double)
	 */
	public double getValueOf(String name) {
		synchronized (mInstance) {
			ensureValidStat(name);
			return mStats.get(name);
		}
	}

	/**
	 * Sets the value of a statistic. Any previous value written in the statistic's
	 * record will be replaced with the value passed here.
	 *
	 * @param name	the name of the statistic.
	 * @param val	the value.
	 * @throws IllegalArgumentException	if no statistic exists with the given
	 * name.
	 * @see #getValueOf(String)
	 */
	public void setValueOf(String name, double val) {
		synchronized (mInstance) {
			ensureValidStat(name);
			mStats.put(name, val);
		}
	}
	
	/**
	 * Gets an {@link Iterable} of Strings containing the name of each statistic
	 * stored.
	 *
	 * @return an Iterable of statistic names.
	 * @see #numberOfStats()
	 */
	public Iterable<String> statistics() {
		synchronized (StatTracker.class) {
			ArrayList<String> stats = new ArrayList<String>();
			// Transfer stat names over
			for (String name : mStats.keySet()) {
				stats.add(name);
			}
			return stats;
		}
	}

	/**
	 * The count of statistics in the StatTracker.
	 *
	 * @return the number of statistics.
	 * {@link #statistics()}
	 */
	public int numberOfStats() {
		synchronized (mInstance) {
			return mStats.size();
		}
	}
	
	/**
	 * Throws an {@link IllegalArgumentException} if the given name is not
	 * the name of a statistic. This method is meant to be called at the
	 * beginning of any method that needs a statistic to exist.
	 *
	 * @param name	the name to check.
	 * @throws IllegalArgumentException	if no statistic exists with the given
	 * name.
	 */
	private void ensureValidStat(String name) {
		if (!mStats.containsKey(name)) {
			StringBuilder builder = new StringBuilder("Stat ");
			builder.append(name);
			builder.append(" does not exist");
			throw new IllegalArgumentException(builder.toString());
		}
	}

	/**
	 * There should only ever be a single StatTracker instance. Therefore, there should be
	 * no need to compare an instance of StatTracker with anything else.
	 * @throws UnsupportedOperationException	when this method is called.
	 */
	@Override
	public boolean equals(Object obj) {
		throw new UnsupportedOperationException("There should only be a single StatTracker");
	}

	/**
	 * There should only ever be a single StatTracker instance.
	 * @throws CloneNotSupportedException	when this method is called.
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("There should only be a single StatTracker");
	}

	/**
	 * Returns a String showing the number of stored statistics.
	 */
	@Override
	public String toString() {
		synchronized (mInstance) {
			StringBuilder builder = new StringBuilder("{ (numberOfStats() = ");
			builder.append(numberOfStats());
			builder.append(") }");
			return builder.toString();
		}
	}
	
	
}
