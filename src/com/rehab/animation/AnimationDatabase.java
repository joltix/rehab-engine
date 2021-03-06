package com.rehab.animation;

import java.util.Hashtable;


public class AnimationDatabase {

	// assoc a reels with a name[animation]
	private Hashtable<String, CircularArray<Texture2>> mReelsNameAssoc;
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
	public CircularArray<Texture2> getTexArr(String name){		return mReelsNameAssoc.get(name);		}


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
		CircularArray<Texture2> arr = mReelsNameAssoc.get(animation);


		//checks if the key is in the hash table
		if(mUpdateCurrAni.containsKey(id)){				
			int imgIndex = mUpdateCurrAni.get(id);		// Retrieve the last draw's image index

			mUpdateCurrAni.remove(id);							//remove it 

			if(imgIndex < arr.size()-1){						//check if the current index is still within bound

				imgIndex++;       							 //increment the index

				mUpdateCurrAni.put(id, imgIndex); 	//  put next index in
			}else {

				imgIndex=0;  					 //when we are at the last index wrap back to the beginning
			}

		}else {
			//else is it not in the table we add with the index being 0
			mUpdateCurrAni.put(id,0);			
		}


	}

	/**
	 * Method that will add the reels, of images, or animation into
	 * reelsNameAssoc Hashtable with a string name
	 * 
	 * @param String name of the animation
	 * @param The reels of textures 
	 * @return true if successfully added into the Hashtable false otherwise
	 */
	public boolean addAni(String id, CircularArray<Texture2> i) {
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
		public Texture2 getImage(String entityAniName, int frameID) {
	
			CircularArray<Texture2> frame = mReelsNameAssoc.get(entityAniName);	//return the reels
	
			Texture2 i = frame.get(frameID);		//return the frame
				
			return i; 
		}

}
