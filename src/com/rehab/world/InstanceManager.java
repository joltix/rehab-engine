package com.rehab.world;
import java.util.Hashtable;

import com.rehab.animation.Sprite;

/**
 * <p>
 * InstanceManager maintains a thread=safe lookup for types of {@link Entity} used in the game. This class also
 * provides factory methods for creating various Entity subclasses like {@link Actor}. Following is an
 * example of creating an Actor.
 * </p>
 * 
 * <pre>
 * 	<code>
 * InstanceManager manager = InstanceManager.getInstance();
 * Actor a = manager.createActor(62, 100);
 * 	</code>
 * </pre>
 * 
 * <p>
 * The snippet above creates an Actor that is already registered with the InstanceManager.
 * If {@link RenderLoop#start()} has been called, creating an Entity in the way above will
 * automatically load it into the game and drawn in the next available draw frame.
 * </p>
 * 
 * <p>
 * InstanceManager also provides methods such as {@link #registeredCount()} and {@link #loadedCount()}
 * to track the amount of Entities used in the game with more specific variations available for
 * the various Entity subclasses.
 * </p>
 *
 */
public class InstanceManager {

	// The only instance allowed
	private static InstanceManager mManager;

	// All Entities created
	private Hashtable<Integer, Entity> mGlobalInstanceTable = new Hashtable<Integer, Entity>();
	private int mLatestUsedId = 1;

	// Lookup table for Entities in the Arena
	private Hashtable<Integer, Actor> mLoadedActTable = new Hashtable<Integer, Actor>();
	private Hashtable<Integer, Projectile> mLoadedProjTable = new Hashtable<Integer, Projectile>();
	private Hashtable<Integer, Prop> mLoadedPropTable = new Hashtable<Integer, Prop>();
	
	// Counts for registered Entity subclasses
	private int mNumActors = 0;
	private int mNumProjs = 0;
	private int mNumProps = 0;
	
	/**
	 * Private constructor prevents direct instantiation and removes default
	 * constructor from doc.
	 */
	private InstanceManager() {}
	
	/**
	 * Gets an instance of the InstanceManager.
	 * 
	 * @return	the instance.
	 */
	public static InstanceManager getInstance() {
		synchronized (InstanceManager.class) {
			if (mManager == null) mManager = new InstanceManager();
			return mManager;
		}
	}

	/**
	 * Creates a new {@link Actor} instance with a given amount of mass and
	 * a maximum health. The returned Actor is already registered with the
	 * {@link InstanceManager}. If {@link RenderLoop#start()} has been called,
	 * the Actor will be automatically loaded into the game and a draw request
	 * will be sent.
	 * 
	 * @param mass	the mass in kilograms.
	 * @param healthCap	the max health capacity.
	 * @return	the Actor.
	 */
	public Actor createActor(double mass, double healthCap) {
		Actor a = new Actor(mass, healthCap);
		register(a);
		return a;
	}

	/**
	 * Creates a new {@link Projectile} instance with a given owner {@link Actor} and
	 * a {@link Hitbox} for collision. The returned Projectile is already registered
	 * with the {@link InstanceManager}. If {@link RenderLoop#start()} has been called,
	 * the Projectile will be automatically loaded into the game and a draw request will
	 * be sent.
	 * 
	 * @param owner	the Actor who owns the Projectile.
	 * @param collision	the Hitbox for the Projectile.
	 * @return	the Projectile.
	 */
	public Projectile createProjectile(Actor owner, Hitbox collision) {
		Projectile p = new Projectile(owner, collision);
		register(p);
		return p;
	}
	
	/**
	 * Creates a new {@link Prop} instance with a given {@link Sprite} for visuals.
	 * The returned Prop is already registered with the {@link InstanceManager}.
	 * If {@link RenderLoop#start()} has been called, the Prop will be
	 * automatically loaded into the game and a draw request will be sent.
	 * 
	 * @param sprite	the Sprite.
	 * @return	the Prop.
	 */
	public Prop createProp(Sprite sprite) {
		Prop p = new Prop(sprite);
		register(p);
		return p;
	}

	/**
	 * Registers the given Actor with the game's global Actor list.
	 * 
	 * @param a	the Actor to register.
	 * @return	true if the given Actor was successfully added to the global
	 * list, false if the Actor was already registered previously.
	 * @see #isRegistered(int)
	 * @see #unregister(Entity)
	 */
	private boolean register(Actor a) {
		synchronized (mManager) {
			if (mGlobalInstanceTable.contains(a)) return false;
			// Assign new id
			int id = generateNewId();
			a.setId(id);
			// Add to global lookup
			mGlobalInstanceTable.put(id, a);

			// Auto load into game if already began
			if (ProtoRender.isRunning()) {
				load(a);
				ProtoRender.getInstance().requestDraw(a);
			}

			mNumActors++;
			return true;
		}
	}

	/**
	 * Registers the given Projectile with the game's global Projectile list.
	 * 
	 * @param p	the Projectile to register.
	 * @return	true if the given Projectile was successfully added to the global
	 * list, false if the Projectile was already registered previously.
	 * @see #isRegistered(int)
	 * @see #unregister(Entity)
	 */
	private boolean register(Projectile p) {
		synchronized (mManager) {
			if (mGlobalInstanceTable.contains(p)) {
				return false;
			}
			// Assign new id
			int id = generateNewId();
			p.setId(id);
			// Add to global lookup
			mGlobalInstanceTable.put(id, p);

			// Auto load into game if already began
			if (ProtoRender.isRunning()) {
				load(p);
				ProtoRender.getInstance().requestDraw(p);
			}

			mNumProjs++;
			return true;
		}
	}
	
	/**
	 * Registers the given Projectile with the game's global Prop list.
	 * 
	 * @param p	the Projectile to register.
	 * @return	true if the given Projectile was successfully added to the global
	 * list, false if the Projectile was already registered previously.
	 * @see #isRegistered(int)
	 * @see #unregister(Entity)
	 */
	private boolean register(Prop p) {
		synchronized (mManager) {
			if (mGlobalInstanceTable.contains(p)) {
				return false;
			}
			// Assign new id
			int id = generateNewId();
			p.setId(id);
			// Add to global lookup
			mGlobalInstanceTable.put(id, p);

			// Auto load into game if already began
			if (ProtoRender.isRunning()) {
				load(p);
				ProtoRender.getInstance().requestDraw(p);
			}

			mNumProps++;
			return true;
		}
	}

	/**
	 * Removes the specified Actor from the global instance list. This marks
	 * the removal of the Actor from the game as a whole as it can no longer be
	 * looked up from the global Entity list.
	 * @param a	the Actor to remove from the game.
	 * @return	true if the Actor was successfully removed, false if it was not
	 * yet unloaded from the game's current level.
	 * @see #isRegistered(int)
	 * @see	#register(Actor)
	 */
	public boolean unregister(Actor a) {
		synchronized (mManager) {
			int id = a.getId();

			// Fail if still loaded or not in global list
			if (mLoadedActTable.containsKey(id) ||
					!mGlobalInstanceTable.containsKey(id)) {
				return false;
			}

			// Remove from global table if already unloaded
			mGlobalInstanceTable.remove(id);
			mNumActors--;
			return true;
		}
	}
	
	/**
	 * Removes the specified Projectile from the global instance list. This marks
	 * the removal of the Projectile from the game as a whole as it can no longer be
	 * looked up from the global Entity list.
	 * @param p	the Projectile to remove from the game.
	 * @return	true if the Projectile was successfully removed, false if it was not
	 * yet unloaded from the game's current level.
	 * @see #isRegistered(int)
	 * @see	#register(Projectile)
	 */
	public boolean unregister(Projectile p) {
		synchronized (mManager) {
			int id = p.getId();

			// Fail if still loaded or not in global list
			if (mLoadedProjTable.containsKey(id) ||
					!mGlobalInstanceTable.containsKey(id)) {
				return false;
			}

			// Remove from global table if already unloaded
			mGlobalInstanceTable.remove(id);
			mNumProjs--;
			return true;
		}
	}
	
	/**
	 * Removes the specified Prop from the global instance list. This marks
	 * the removal of the Prop from the game as a whole as it can no longer be
	 * looked up from the global Entity list.
	 * @param a	the Prop to remove from the game.
	 * @return	true if the Prop was successfully removed, false if it was not
	 * yet unloaded from the game's current level.
	 * @see #isRegistered(int)
	 * @see	#register(Prop)
	 */
	public boolean unregister(Prop p) {
		synchronized (mManager) {
			int id = p.getId();

			// Fail if still loaded or not in global list
			if (mLoadedPropTable.containsKey(id) ||
					!mGlobalInstanceTable.containsKey(id)) {
				return false;
			}

			// Remove from global table if already unloaded
			mGlobalInstanceTable.remove(id);
			mNumProps--;
			return true;
		}
	}

	/**
	 * Moves the given Actor to the game world.
	 * 
	 * @param a	the Actor to put in play.
	 * @return	true if the Actor was not already loaded, false otherwise and
	 * therefore not moved to the game world.
	 * @see #isLoaded(int)
	 * @see #unload(Entity)
	 */
	public boolean load(Actor a) {
		synchronized (mManager) {
			int id = a.getId();
			if (!mGlobalInstanceTable.containsKey(id) ||
					mLoadedActTable.containsKey(id)) {
				return false;
			}
			
			mLoadedActTable.put(id, a);
			return true;
		}
	}

	/**
	 * Moves the given Projectile to the game world.
	 * 
	 * @param p	the Projectile to put in play.
	 * @return	true if the Projectile was not already loaded, false otherwise and
	 * therefore not moved to the game world.
	 * @see	#isLoaded(int)
	 * @see	#unload(Entity)
	 */
	public boolean load(Projectile p) {
		synchronized (mManager) {
			int id = p.getId();
			if (!mGlobalInstanceTable.containsKey(id) ||
					mLoadedProjTable.containsKey(id)) {
				return false;
			}

			mLoadedProjTable.put(id, p);
			return true;
		}
	}
	
	/**
	 * Moves the given Prop to the game world.
	 * 
	 * @param p	the Prop to put in play.
	 * @return	true if the Prop was not already loaded, false otherwise and
	 * therefore not moved to the game world.
	 * @see #isLoaded(int)
	 * @see #unload(Entity)
	 */
	public boolean load(Prop p) {
		synchronized (mManager) {
			int id = p.getId();
			if (!mGlobalInstanceTable.containsKey(id) ||
					mLoadedPropTable.containsKey(id)) {
				return false;
			}

			mLoadedPropTable.put(id, p);
			return true;
		}
	}

	/**
	 * Removes the given Entity from the game world but leaves the Entity
	 * registered with the global Entity list.
	 * 
	 * @param e	the Entity to remove from the world.
	 * @return	true if the instance was successfully removed from the world,
	 * false if it was not already loaded.
	 * @see	#isLoaded(int)
	 * @see #load(Actor)
	 * @see #load(Projectile)
	 * @see #load(Prop)
	 */
	public boolean unload(Entity e) {
		synchronized (mManager) {
			int id = e.getId();
			if (mLoadedProjTable.remove(id) == null) return false;
			if (mLoadedActTable.remove(id) == null) return false;
			if (mLoadedPropTable.remove(id) == null) return false;

			return true;
		}
	}

	/**
	 * Checks whether or not an Entity with the specified instance id
	 * is known to the InstanceManager.
	 * 
	 * @param id	the instance id of an Entity.
	 * @return	true if an Entity associated with the specified id is
	 * registered, false otherwise.
	 * @see	#register(Actor)
	 * @see #register(Projectile)
	 * @see #register(Prop)
	 */
	public boolean isRegistered(int id) {
		return mGlobalInstanceTable.containsKey(id);
	}

	/**
	 * Checks whether or not an Entity with the specified instance id
	 * is in the game world.
	 * 
	 * @param id	the instance id of an Entity.
	 * @return	true if an Entity associated with the specified id is
	 * currently in the game world, false otherwise.
	 * @see #load(Actor)
	 * @see #load(Projectile)
	 * @see #load(Prop)
	 * @see #unload(Entity)
	 */
	public boolean isLoaded(int id) {
		if (mLoadedActTable.containsKey(id) ||
				mLoadedProjTable.containsKey(id)) {
			return true;
		}
		return false;
	}

	/**
	 * Gets an Iterable containing all Actors currently loaded into the game.
	 * 
	 * @return	the Iterable of Actors.
	 */
	public Iterable<Actor> getLoadedActors() {
		return mLoadedActTable.values();
	}

	/**
	 * Gets an Iterable containing all Projectiles currently loaded into the game.
	 * 
	 * @return	the Iterable of Projectiles.
	 */
	public Iterable<Projectile> getLoadedProjectiles() {
		return mLoadedProjTable.values();
	}
	
	/**
	 * Gets an Iterable containing all Props currently loaded into the game.
	 * 
	 * @return	the Iterable of Props.
	 */
	public Iterable<Prop> getLoadedProps() {
		return mLoadedPropTable.values();
	}
	
	/**
	 * Gets the number of loaded Actors.
	 * 
	 * @return	loaded Actor count.
	 */
	public int loadedActorCount() {
		return mNumActors;
	}
	
	/**
	 * Gets the number of loaded Projectiles.
	 * 
	 * @return	loaded Projectile count.
	 */
	public int loadedProjectileCount() {
		return mNumProjs;
	}
	
	/**
	 * Gets the number of loaded Props.
	 * 
	 * @return	loaded Prop count.
	 */
	public int loadedPropCount() {
		return mNumProps;
	}
	
	/**
	 * Gets the number of registered Actors.
	 * 
	 * @return	registered Actor count.
	 */
	public int registeredActorCount() {
		return mNumActors - mLoadedActTable.size();
	}
	
	/**
	 * Gets the number of registered Projectiles.
	 * 
	 * @return	registered Projectile count.
	 */
	public int registeredProjectileCount() {
		return mNumProjs - mLoadedProjTable.size();
	}
	
	/**
	 * Gets the number of registered Props.
	 * 
	 * @return	registered Prop count.
	 */
	public int registeredPropCount() {
		return mNumProps - mLoadedPropTable.size();
	}
	
	/**
	 * Gets the number of registered Entities. This includes Actors, Projectiles,
	 * and Props.
	 * 
	 * @return registered Entity count.
	 */
	public int registeredCount() {
		int act = registeredActorCount();
		int proj = registeredProjectileCount();
		int prop = registeredPropCount();
		return act + proj + prop;
	}
	
	/**
	 * Gets the number of loaded Entities. This includes Actors, Projectiles,
	 * and Props.
	 * 
	 * @return	loaded Entity count.
	 */
	public int loadedCount() {
		int act = loadedActorCount();
		int proj = loadedProjectileCount();
		int prop = loadedPropCount();
		return act + proj + prop;
	}

	/**
	 * Creates an instance id to be used when registering an
	 * Entity with the InstanceManager's global instance list.
	 * Unless manually set, an Entity's id number is guaranteed
	 * to not be used by any other Entity registered with the
	 * InstanceManager.
	 * 
	 * @return	a unique integer id to identify an instance.
	 */
	private int generateNewId() {
		synchronized (mManager) {
			int id = mLatestUsedId;
			mLatestUsedId++;
			return id;
		}
	}

	/**
	 * Super class for {@link Entity} that allows {@link InstanceManager} exclusive access
	 * to set an Entity's unique ID. This class facilitates the InstanceManager's registration
	 * of an Entity's uniqueness in the game.
	 */
	protected static abstract class IdentifiableEntity {

		// Entity id
		private int mId;

		/**
		 * Sets an instance's unique identifier.
		 * 
		 * @param id	the unique identifier for the instance.
		 */
		protected void setId(int id) {
			mId = id;
		}

		/**
		 * Gets an instance's unique identifier.
		 * 
		 * @return	the unique identifier for the instance.
		 */
		public int getId() {
			return mId;
		}
	}

}
