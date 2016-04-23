package com.rehab.animation;

import java.util.Hashtable;

import javafx.scene.image.Image;

public class AnimationDatabase {

	// assoc a reels with a name[animation]
	private Hashtable<String, CircularArray<Image>> reelsNameAssoc;
	// assoc an entity with a certain animation,
	//private Hashtable<Integer, String> entAnimation;

	/**
	 * Method to add an entity object with its associated animation in the
	 * Hashtable
	 * 
	 * @param i
	 * @param animation
	 * @return true if added successfully into the Hashtable false otherwise
	 */
//	public boolean addEnt(int i, String animation) {
//		if (entAnimation.containsKey(i)) {
//			return false;
//		} else {
//			entAnimation.put(i, animation);
//			return true;
//		}
//
//	}

	/**
	 * Method that allows to go into the entAnimation Hashtable and change the
	 * animation value that is associated with that entity
	 * 
	 * @param i
	 * @param newAnimation
	 * @return true if it was successfully added into the Hashtable entAnimation
	 *         false otherwise
	 */
//	public boolean changeAnimation(int i, String newAnimation) {
//		if (entAnimation.containsKey(i)) {
//			entAnimation.replace(i, newAnimation);
//			return true;
//		} else {
//			return false;
//		}
//	}

	/**
	 * Method that will add the reels, of images, or animation into
	 * reelsNameAssoc Hashtable with a string name
	 * 
	 * @param id
	 * @param i
	 * @return true if successfully added into the Hashtable false otherwise
	 */
	public boolean addAni(String id, CircularArray<Image> i) {
		if (reelsNameAssoc.containsKey(id)) {
			return false;
		} else {
			reelsNameAssoc.put(id, i); // add a animation
			return true;}
			// each individual sprite will store an instance of the anmation
			// class
			// have each own database
	}

	/**
	 * Method that will get one frame of image from the animation for the given
	 * entity and image index
	 * 
	 * @param s
	 * @param frameID
	 * @return
	 * the image
	 */
	public Image getImage(String entityAniName, int frameID) {

	
		CircularArray<Image> frame = reelsNameAssoc.get(entityAniName);//return the reels

		Image i = frame.get(frameID);//return the frame
		return i; 
	}

}
