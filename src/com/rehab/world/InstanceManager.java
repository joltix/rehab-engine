package com.rehab.world;
import java.util.Hashtable;

public class InstanceManager {

	// The only instance allowed
	private static InstanceManager mManager;
	
	
	// Instances of actors and projectiles taken out of play to be recycled or destroyed later
	private static Hashtable<Integer, Projectile> mWaitlistActors;
	private static Hashtable<Integer, Projectile> mWaitlistProjectiles;
	
	
	// A table containing all instances of Entity in-game
	private static Hashtable<Integer, Entity> mGlobalInstanceTable;
	private static int mLatestUsedId;
	
	
	/**
	 * Ensures that there is only ever one instance of the InstanceManager
	 * @return
	 * 		the InstanceManager.
	 */
	public static InstanceManager getInstance() {
		synchronized (mManager) {
			if (mManager == null) mManager = new InstanceManager();
			return mManager;
		}
	}
	
	/**
	 * Private default constructor prevents direct instantiation
	 */
	private InstanceManager() {

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
	public boolean removeFromPlay(Entity e) {
		synchronized (mManager) {
			if (e.status() == Entity.Status.WAITING) {
				// Remove from global table and destroy out-reaching links
				mGlobalInstanceTable.remove(e.id());
				e.destroyLinks();
				return true;
			} else return false;
		}
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
			// Add to global list
			mGlobalInstanceTable.put(id, e);
			return true;
		}
	}
	
	/**
	 * Creates an instance id to be used when registering an
	 * Entity with the InstanceManager's global instance list.
	 * The id number is guaranteed to not be used by an
	 * @return
	 * 		a unique integer id.
	 */
	private int generateNewId() {
		synchronized (mManager) {
			int id = mLatestUsedId;
			mLatestUsedId++;
			return id;
		}
	}
	
}
