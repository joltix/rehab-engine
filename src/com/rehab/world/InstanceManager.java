package com.rehab.world;
import java.util.Hashtable;

public class InstanceManager {

	// The only instance allowed
	private static InstanceManager mManager;
	
	
	// A table containing all instances of Entity in-game
	private Hashtable<Integer, Entity> mGlobalInstanceTable = new Hashtable<Integer, Entity>();
	private int mLatestUsedId = 1;
	
	
	// Tables to categorize instance statuses
	private Hashtable<Integer, Entity> mLoadedInstanceTable = new Hashtable<Integer, Entity>();
	private Hashtable<Integer, Entity> mWaitingInstanceTable = new Hashtable<Integer, Entity>();
	
	
	/**
	 * Ensures that there is only ever one instance of the InstanceManager
	 * @return
	 * 		the InstanceManager.
	 */
	public static InstanceManager getInstance() {
		synchronized (InstanceManager.class) {
			if (mManager == null) mManager = new InstanceManager();
			return mManager;
		}
	}
	
	/**
	 * Prevents direct instantiation
	 */
	private InstanceManager() {

	}
	
	/**
	 * Registers the given Entity with the global Entity list. This allows
	 * easy tracking and retrieval of instances that have been created
	 * thus far.
	 * @param e
	 * 		the Entity to register.
	 * @return
	 * 		true if the given Entity was successfully added to the global
	 * 		list, false if the Entity was already registered previously.
	 */
	public boolean register(Entity e) {
		synchronized (mManager) {
			if (mGlobalInstanceTable.contains(e)) return false;
			// Assign new id
			int id = generateNewId();
			e.setId(id);
			// Add to global list and buffer list
			mGlobalInstanceTable.put(id, e);
			mWaitingInstanceTable.put(id, e);
			return true;
		}
	}
	
	/**
	 * Removes the specified Entity from the global instance list. This will only
	 * succeed if the Entity's status is already Entity.Status.WAITING. This marks
	 * the removal of the Entity from the game as a whole as it can no longer be
	 * looked up from the global / master Entity list.
	 * @param e
	 * 		the Entity to remove from the game.
	 * @return
	 * 		true if the Entity was successfully removed, false if it was not
	 * 		already removed from the game world (the status was not Entity.Status.WAITING)
	 */
	public boolean unregister(Entity e) {
		synchronized (mManager) {
			int id = e.getId();
			
			// Remove from global table and destroy out-reaching links
			if (mWaitingInstanceTable.remove(id) != null) return false;
			mGlobalInstanceTable.remove(id);
			return true;
		}
	}
	
	/**
	 * Moves the given Entity to the game world.
	 * @param e
	 * 		the Entity to put in play.
	 * @return
	 * 		true if the Entity was IDLE, false otherwise and therefore
	 * 		not moved to the game world.
	 * 
	 */
	public boolean load(Entity e) {
		synchronized (mManager) {
			int id = e.getId();
			if (!mWaitingInstanceTable.containsKey(id)) return false;
			mLoadedInstanceTable.put(id, e);		
			return true;
		}
	}
	
	/**
	 * Removes the given Entity from the game world and sets the instance
	 * aside for either recycling or any other operation to occur some
	 * time after leaving the game world.
	 * @param e
	 * 		the Entity to remove from the world.
	 * @return
	 * 		true if the instance was successfully moved from the world
	 * 		to IDLE
	 */
	public boolean unload(Entity e) {
		synchronized (mManager) {
			int id = e.getId();
			if (mLoadedInstanceTable.remove(id) == null) return false;
			mWaitingInstanceTable.put(id, e);
			return true;
		}
	}
	
	/**
	 * Checks whether or not an Entity with the specified instance id
	 * is known to the InstanceManager.
	 * @param id
	 * 		the instance id of an Entity.
	 * @return
	 * 		true if an Entity associated with the specified id is
	 * 		registered, false otherwise.
	 */
	public boolean isRegistered(int id) { return mGlobalInstanceTable.containsKey(id); }
	
	/**
	 * Checks whether or not an Entity with the specified instance id
	 * is in the game world.
	 * @param id
	 * 		the instance id of an Entity.
	 * @return
	 * 		true if an Entity associated with the specified id is
	 * 		currently in the game world, false otherwise.
	 */
	public boolean isLoaded(int id) { return mLoadedInstanceTable.containsKey(id); }
	
	/**
	 * Gets an Iterable containing all Entities currently loaded into the game.
	 * @return
	 * 		the Iterable of Entities.
	 */
	public Iterable<Entity> getLoadedEntities() { return mLoadedInstanceTable.values(); }
	
	/**
	 * Creates an instance id to be used when registering an
	 * Entity with the InstanceManager's global instance list.
	 * Unless manually set, an Entity's id number is guaranteed
	 * to not be used by any other Entity registered with the
	 * InstanceManager.
	 * @return
	 * 		a unique integer id to identify an instance.
	 */
	private int generateNewId() {
		synchronized (mManager) {
			int id = mLatestUsedId;
			mLatestUsedId++;
			return id;
		}
	}
	
}
