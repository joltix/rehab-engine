package com.rehab.world;

import java.util.Hashtable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.rehab.world.Vector2D.Point;

/**
 * <p>
 * Provides a simple way of creating and placing objects in the game world. Game objects
 * generated through the SpawnManager are registered with the {@link InstanceManager} and
 * so are "game-ready" at the moment of placement in the game world.
 * </p>
 * 
 * <p>
 * The code below is an example of using the SpawnManager to place an {@link Actor} whose
 * variable is named <i>enemy</i> (instantiation not shown) at the <i>location (100, 250)</i>.
 * </p>
 * 
 * <pre>
 *  <code>
 * SpawnManager spawnMan = SpawnManager.getInstance();
 * spawnMan.immediateSpawn(enemy, new Point(100, 250));
 *  </code>
 * </pre>
 * 
 * <p>
 * Game objects may also spawn continuously for a period of time. The example below shows
 * the spawning of <i>4</i> enemy Actors at <i>location (100, 250)</i> with a delay of
 * <i>9000ms (9s)</i> before the first spawn and <i>2000ms (2s)</i> between every spawn.
 * </p>
 * <pre>
 *  <code>
 * spawnMan.scheduleSpawn(enemy, new Point(100, 250), 4, 9000, 2000);
 *  </code>
 * </pre>
 */
public class SpawnManager extends Register {
	
	// Initial number of threads allowed to spawn
	private static final int POOL_SIZE = 10;
	// Wait time before scrap idle threads
	private static final int IDLE_TIME_MINS = 2;
	// Unit of time for wait time
	private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;
	
	// Lookup for scheduled spawns
	private Hashtable<Integer, Future> mSpawns = new Hashtable<Integer, Future>();

	// Executor to schedule threads for handling spawning
	private ScheduledThreadPoolExecutor mExecutor;
	
	// Singleton instance
	private static SpawnManager mInstance;
	
	
	/**
	 * Constructor for SpawnManager.
	 */
	private SpawnManager() {
		mExecutor = new ScheduledThreadPoolExecutor(POOL_SIZE);
		// Let executor clean up unused threads
		mExecutor.setKeepAliveTime(IDLE_TIME_MINS, TIME_UNIT);
		mExecutor.allowCoreThreadTimeOut(true);
	}
	
	/**
	 * Gets an instance of the SpawnManager.
	 *
	 * @return the SpawnManager.
	 */
	public static SpawnManager getInstance() {
		synchronized (SpawnManager.class) {
			if (mInstance == null) {
				mInstance = new SpawnManager();
			}
			return mInstance;
		}
	}
	
	/**
	 * Spawns a clone of a given Actor as soon as possible at a designated
	 * location.
	 *
	 * @param actor	the Actor to clone.
	 * @param location	the place to spawn at.
	 * @throws IllegalArgumentException	if either the Actor to spawn or the Point
	 * to spawn at is null.
	 * @see #scheduleSpawn(Actor, Point, int, long, long)
	 */
	public void immediateSpawn(Actor actor, Point location) {
		validateSpawn(actor, location);
		
		// Construct spawnPoint and spawn
		SpawnPoint spawnPoint = new SpawnPoint(actor, location, 1);		
		mExecutor.execute(spawnPoint);
	}
	
	/**
	 * Schedules clones of an Actor to be spawned at a specific location a certain
	 * number of times. This method should be used if spawning of an Actor should
	 * repeat.
	 *
	 * @param a	the Actor whose clones to spawn.
	 * @param numOfInstances	the number of clones to spawn.
	 * @param delay	amount of milliseconds before first spawn.
	 * @param period	milliseconds between spawns.
	 * @throws IllegalArgumentException	if either the Actor to spawn or the Point
	 * to spawn at is null.
	 * @see #immediateSpawn(Actor, Point)
	 */
	public void scheduleSpawn(Actor actor, Point location, int numOfInstances, long delay, long period) {		
		validateSpawn(actor, location);
		
		// Register spawn marker
		SpawnPoint spawnPoint = new SpawnPoint(actor, location, numOfInstances);
		int id = this.putItem(spawnPoint);
		
		// Submit spawn command for execution
		ScheduledFuture future = mExecutor.scheduleWithFixedDelay(spawnPoint, delay, period, TimeUnit.MILLISECONDS);
		mSpawns.put(id, future);
	}
	
	/**
	 * Gets the number of SpawnPoints still active and spawning. This
	 * count does not include SpawnPoints created by calls to {@link #immediateSpawn(Actor, Point)}.
	 *
	 * @return the number of active SpawnPoints.
	 */
	public int numberOfSpawnPoints() { return this.getItemCount(); }
	
	/**
	 * Checks whether or not a given Actor and Point are not null and throws
	 * IllegalArgumentExceptions if either are. This method is a shorthand
	 * for validating a given Actor and Point in methods which require both.
	 *
	 * @param actor	the Actor to check.
	 * @param location	the Point to check.
	 */
	private void validateSpawn(Actor actor, Point location) {
		// Enforce valid Actor
		if (actor  == null) {
			throw new IllegalArgumentException("Cannot spawn null Actors");
		}
		// Enforce valid Point location
		if (location == null) {
			throw new IllegalArgumentException("Cannot spawn at a null Point");
		}
	}

	/**
	 * Represents a location in the game at which to spawn an Actor a certain
	 * number of times.
	 */
	public class SpawnPoint extends Identifiable implements Runnable {
		// What to spawn and where
		private Actor referenceActor;
		private Point location;
		
		// Number of spawns and number allowed
		private int spawnNum = 0;
		private int spawnMax;
		
		/**
		 * Basic constructor for a SpawnPoint.
		 * 
		 * @param reference	the Actor to clone.
		 * @param location	coordinates to spawn at.
		 * @param spawnMax	number of instances to spawn.
		 */
		public SpawnPoint(Actor reference, Point location, int spawnMax) {
			// Wrap to at least one Actor to spawn
			if (spawnMax < 1) spawnMax = 1;

			referenceActor = reference;
			this.location = location;
			this.spawnMax = spawnMax;
		}
		
		/**
		 * Gets the x coordinate of the SpawnPoint.
		 *
		 * @return the x value.
		 * @see #getY()
		 */
		public double getX() { return location.getX(); }
		
		/**
		 * Gets the y coordinate of the SpawnPoint.
		 *
		 * @return the y value.
		 * @see #getX()
		 */
		public double getY() { return location.getY(); }

		/**
		 * Executes the SpawnPoint's spawn instruction and creates and places
		 * the Actor in the game.
		 */
		@Override
		public void run() {
			// Stop spawning when max spawn reached
			if (spawnNum == spawnMax) {
				referenceActor = null;
				location = null;
				
				// Remove SpawnPoint from manager
				int id = this.getId();
				SpawnManager.this.removeItem(id);
				// Remove associated ScheduledFuture and cancels
				Future future = SpawnManager.this.mSpawns.remove(id);
				future.cancel(false);
			}
			
			spawnNum++;
			// Create Actor and move to spawn position
			Actor actor = InstanceManager.getInstance().createActor(referenceActor);
			actor.moveTo(location.getX(), location.getY());
			actor.setEnableGravity(true);
		}
	}
	

}
