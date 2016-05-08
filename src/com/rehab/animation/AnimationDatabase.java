package com.rehab.animation;

import java.util.Hashtable;

import javafx.scene.image.Image;

public class AnimationDatabase {

	// assoc a reels with a name[animation]
	private Hashtable<String, CircularArray<Texture>> mReelsNameAssoc;
	//assoc the entity id with the animation name
	private Hashtable<Integer, String> mCurrentAnimation; 
	//assoc the entity id with the image index
	private Hashtable<Integer, Integer> mUpdateCurrAni; 
	boolean isFirstPic = false;
	
	/**
	 * Add the current animation into the hash table
	 * 
	 * @param id
	 * @param aniName
	 */
	public void addCurrAni(Integer id, String aniName){

		if(mCurrentAnimation.containsKey(id)){
			//it contains then we assume that we want to remove it and update it
			mCurrentAnimation.remove(id, aniName);
			mCurrentAnimation.put(id,aniName);
		}
		else if (aniName == null){ // if its not animating 
			mCurrentAnimation.put(id, null);

		}
		mCurrentAnimation.put(id,aniName); //default add 
	}
	/**
	 * Get the string name of the animation
	 * 
	 * @param id
	 * @return the String name
	 */
	public String getAni(Integer id){	return mCurrentAnimation.get(id);	}
		
	
	/**
	 * Get the array of texture 
	 * 
	 * @param name
	 * @return an array of texture(s) 
	 */
	public CircularArray<Texture> getTexArr(String name){		return mReelsNameAssoc.get(name);		}
	

	/**
	 * To retrieve the current image index that is currently animating and then 
	 * updating the current to the next image
	 * 
	 * @param id
	 */
	public 	void changeCurrIndex(Integer id){
		//gives the animation name give the entity id
		String animation= mCurrentAnimation.get(id);
		//gets  the actual array assoc with the name
		CircularArray<Texture> arr = mReelsNameAssoc.get(animation);
		

		if(mUpdateCurrAni.containsKey(id)){				//checks if the key is in the hash table
			
			int imgIndex = mUpdateCurrAni.get(id);		// Retrieve the last draw's image index
			
			mUpdateCurrAni.remove(id);							//remove it 
			
			if(imgIndex < arr.size()-1){						//check if the current index is still within bound
				
				imgIndex++;       							 //increment the index
				
				mUpdateCurrAni.put(id, imgIndex); 	//  put next index in
			}else {
				
				imgIndex=0;  					 //when we are at the last index wrap back to the beginning
			}

		}else {
			
			mUpdateCurrAni.put(id,0);			//else is it not in the table we add with the index being 0

		}


	}

	/**
	 * Method that will add the reels, of images, or animation into
	 * reelsNameAssoc Hashtable with a string name
	 * 
	 * @param id
	 * @param i
	 * @return true if successfully added into the Hashtable false otherwise
	 */
	public boolean addAni(String id, CircularArray<Texture> i) {
		if (mReelsNameAssoc.containsKey(id)) {
			return false;
		} else {
			mReelsNameAssoc.put(id, i); 	// add a animation
			return true;}

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
//	public Image getImage(String entityAniName, int frameID) {
//
//		CircularArray<Texture> frame = mReelsNameAssoc.get(entityAniName);	//return the reels
//
//		Image i = frame.get(frameID);		//return the frame
//			
//		return i; 
//	}

}
