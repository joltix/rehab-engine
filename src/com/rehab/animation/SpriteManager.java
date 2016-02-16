package com.rehab.animation;
import java.util.Hashtable;
import com.rehab.world.Entity;
import com.rehab.world.InstanceManager;


/*
 *Sprite Manager Class that tracks all of the Sprite created in the game
 */
public class SpriteManager {

	private static SpriteManager spriteMgmt;

	private  int spriteId = 1; 

	private  Hashtable<Integer, Sprite> spriteNum; 

	/*
	 * Private constructor for Spite Manager to prevent the user from 
	 * actually manipulating the object 
	 */
	private SpriteManager(){                                           }

	/*
	 * Make sure that there is ever only one instance of the Sprite Manager
	 * Enforces the singleton property 
	 */
	public static SpriteManager getInstance() {
		synchronized (spriteMgmt) {
			if (spriteMgmt == null) spriteMgmt = new SpriteManager();
			return spriteMgmt;
		}
	}
/*
 * Checks in the Hashtable and get the count of the size
 * 
 */
	public int getSpriteCount (){
		return spriteNum.size();

	}

/*
 * Generate a unique int ID for each of the Sprite Object 
 */
	private int generateSpriteId() {
		synchronized (spriteMgmt) {
			int id = spriteId;
			spriteId++;
			return id;
		}
	}

	/**
	 * To add a new Sprite into the Hashtable
	 * @param s
	 * @return
	 * True when the sprite is successfully added to the Hashtable 
	 */
	public boolean register(Sprite s){
		//add it to the hashtable
		synchronized (spriteMgmt){
			if(spriteNum.containsKey(s.getId())){
				return false; 
			}
			int id = generateSpriteId(); 
			s.setId(id);
			spriteNum.put(id, s); 


			return true;
		}


	}
/**
 * Remove the Sprite from the Hashtable 
 * @param s
 * @return
 * True if we have successfully remove the Sprite from the Hashtable 
 */
	public boolean unregister(Sprite s){
		synchronized (spriteMgmt){
			int id=s.getId(); 

			//check if the id is actually in the hashtable
			//if it is remove return true
			//if not then return false do nothing

			if(spriteNum.remove(id)==null){ 
				return false; 
			}
			spriteNum.remove(id); 
			return true; 

		}

	}

	public boolean isRegistered(int id){
		synchronized (spriteMgmt){

			return spriteNum.containsKey(id); 
		}
	}

	public Sprite getSprite(int id){
		synchronized (spriteMgmt){
			//given the id return the assoc sprite 
			//if not in the hashtable then true
			if(spriteNum.containsKey(id)){
				return spriteNum.get(id);

			}
			return null;

		}
	}

}
