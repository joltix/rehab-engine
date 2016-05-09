package com.rehab.animation;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import com.rehab.world.Entity;
import com.rehab.world.InstanceManager;
import com.rehab.world.Register;

/*
 *Sprite Manager Class that tracks all of the Sprite created in the game
 */
public class SpriteManager extends Register{

	private static SpriteManager spriteMgmt;


	private Hashtable<String, Sprite> spriteNum = new Hashtable<String, Sprite>();

	/*
	 * Private constructor for Spite Manager to prevent the user from actually
	 * manipulating the object
	 */
	private SpriteManager() {
	}

	/**
	 * Make sure that there is ever only one instance of the Sprite Manager
	 * Enforces the singleton property
	 * 
	 * @return the instance of the Sprite Manager
	 */
	public static SpriteManager getInstance() {
		synchronized (SpriteManager.class) {
			if (spriteMgmt == null)
				spriteMgmt = new SpriteManager();
			return spriteMgmt;
		}
	}
	/**
	 * Method to load multiple files from a folder to texture
	 * 
	 * @param folder
	 * @throws IOException
	 */
	public static void loadFolder(String folderName) throws IOException {

		String path = SpriteManager.class.getResource(folderName).getPath();
		File folder = new File(path);

		File[] files = folder.listFiles(); 		// array of objs

		SpriteManager s = getInstance();

		for (int i = 0; i < files.length; i++) {		// reading in files from the folder

			File file = files[i];
			if (file.isFile() && SpriteManager.isImage(file)) {				// if this is a file then add get the filename
				s.createSprite(file.getPath());


			}

		}

	}

	/**
	 * Checks whether or not a given File is a supported image (jpg or png).
	 * 
	 * @param file	the File to check.
	 * @return true if the File is jpg or png, false otherwise.
	 */
	private static boolean isImage(File file) {
		String name = file.getName();
		int typeIndex = name.indexOf('.') + 1;

		// Not a picture if file name begins with period
		if (typeIndex < 0) {
			return false;
		}

		// Extract file type
		String type = name.substring(typeIndex, name.length());

		// Compare for supported image types
		if (type.equalsIgnoreCase("png") || type.equalsIgnoreCase("jpg")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get the count of total number sprite in the hash table
	 * @return the number of sprite that is currently loaded in the database
	 */
	public int getSpriteCount() {
		return spriteNum.size();

	}



	/**
	 * Method to add a new Sprite into the Hash table
	 * 
	 * @param the name of the image and a Sprite 
	 * @return True when the sprite is successfully added to the Hash table
	 * 
	 */
	private boolean register(String imageName, Sprite s) {
		// add it to the hash table
		synchronized (spriteMgmt) {

			if (spriteNum.containsKey(imageName)) {
				return false;
			}

			s.setId(imageName);
			spriteNum.put(imageName, s);

			return true;
		}

	}

	/**
	 * Remove the Sprite from the Hash table
	 * 
	 * @param Sprite 
	 * @return True if we have successfully remove the Sprite from the Hash table
	 */
	@SuppressWarnings("unused")
	private boolean unregister(Sprite s) {
		synchronized (spriteMgmt) {
			String id = s.getId();


			if (spriteNum.remove(id) == null) {
				return false;
			}
			return true; 
		}

	}

	/**
	 * Check if the Sprite is Registered in the Hash table
	 * 
	 * @param String name of the sprite
	 * @return if the hash table contains the key
	 */
	public boolean isRegistered(String id) {
		synchronized (spriteMgmt) {

			return spriteNum.containsKey(id);
		}
	}

	/**
	 * Method that will get the Sprite object associated with that given ID
	 * 
	 * @param String name of the Sprite which its ID
	 * @return true the ID is in the hash table and if we successfully return it, false otherwise
	 */
	public Sprite getSprite(String id) {
		synchronized (spriteMgmt) {

			if (spriteNum.containsKey(id)) {
				return spriteNum.get(id);

			}
			return null;

		}
	}
	/**
	 * Method that will automatically create a Sprite without the user
	 * touching any of the internal functions
	 * 
	 * @param fileName
	 * @return a Sprite
	 * 
	 */
	private Sprite createSprite(String fileName){
		String file = SpriteManager.extractFilename(fileName);

		Sprite s = new Sprite(fileName);
		register(file, s);
		return s;

	}

	/**
	 * Gets the filename and type from a path (the last element of the path) with a
	 * path that uses backslashes '\'.
	 * 
	 * @param path	the String representing the full path.
	 * @return the String filename with type, or null if the filename
	 * does not exist in the path.
	 */
	private static String extractFilename(String path) {

		int periodIndex = -1;
		for (int i = path.length() - 1; i >= 0; i--) {

			char c  = path.charAt(i);

			// Mark wherever a period is found
			if (c == '.') {
				periodIndex = i;
			}

			if (c == '\\') {
				return path.substring(i + 1);
			}
		}

		// Didn't find backslash implies file in same directory (but found period)
		if (periodIndex != -1) {
			return path;
		}

		// Couldn't find a file
		return null;
	}

}


