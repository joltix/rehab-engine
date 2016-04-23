package com.rehab.world;

import java.util.Hashtable;

/**
 * <p>
 * This class is meant to provide basic lookup and unique id assignment for managers and
 * classes which keep track of some amount of objects in a thread-safe manner. Though all
 * current managers such as {@link InstanceManager} and {@link SpriteManager} follow
 * singleton patterns as convention, extending the Register does not require implementing
 * a singleton.
 * </p>
 * 
 * <p>Note: This class is thread-safe.</p>
 */
public abstract class Register {
	
	/**
	 * Constant for objects without an assigned id.
	 */
	public static final int UNREGISTERED = 0;

	// All registered Identifiables
	private Hashtable<Integer, Identifiable> mItems = new Hashtable<Integer, Identifiable>();
	// The next unused id
	private int mNextFreeId = 1;
	
	/**
	 * Gets an item that has been put in the Register.
	 *
	 * @param id	the Item's id.
	 * @return	the Item.
	 */
	public Identifiable getItem(int id) {
		if (id < 1) {
			return null;
		}
		return mItems.get(id);
	}
	
	/**
	 * Assigns a unique integer id to a given Identifiable.
	 *
	 * @param obj	the Identifiable to id.
	 * @throws IllegalArgumentException	if the given object has already been assigned
	 * an id.
	 */
	protected int putItem(Identifiable obj) {
		int id = obj.id;
		if (mItems.containsKey(id)) {
			throw new IllegalArgumentException("Object has already been identified: " + obj.id);
		}
		
		// Come up with new id  and assign
		id = generateNewId();
		obj.id = id;
		mItems.put(id, obj);
		
		return id;
	}
	
	/**
	 * Removes an Identifiable with a given id from the Register.
	 *
	 * @param id	the id associated with the Identifiable.
	 * @return the removed Identifiable, or null if no Identifiable
	 * was found for the given id.
	 */
	protected Identifiable removeItem(int id) {
		if (id < 1) {
			throw new IllegalArgumentException("Item id must be > 1: " + id);
		}
		
		return mItems.remove(id);
	}
	
	/**
	 * Gets the number of Identifiables known to the Register.
	 *
	 * @return the Register's Identifiable count.
	 */
	protected int getItemCount() { return mItems.size(); }
	
	/**
	 * Creates a unique id to be used when putting an Identifiable in the Register.
	 * The id number generated is guaranteed to be unique in comparison to any other
	 * id created by the same Register.
	 * 
	 * @return	a unique integer id.
	 */
	private int generateNewId() {
		synchronized (mItems) {
			int id = mNextFreeId;
			mNextFreeId++;
			return id;
		}
	}

	/**
	 * <p>
	 * Allows subclasses of Identifiable to be stored in a Register and assigned an
	 * integer id unique to that Register.
	 * </p>
	 * 
	 * <p>This class is <b>not</b> thread-safe.</p>
	 */
	protected static abstract class Identifiable {

		// id of obj
		private int id;

		/**
		 * Gets an instance's unique identifier.
		 * 
		 * @return	the unique identifier for the instance.
		 */
		public int getId() { return id; }
	}
	
}
