package com.rehab.animation;

import java.nio.ByteBuffer;
import java.util.Hashtable;


/**
 * Sprite Class in which all the games objects comes from the Sprite class, 2D
 * drawable object
 * 
 */
//move the draw code into the byte buffer 
public class Sprite {

	// instance of the ID
	private String mFileName;
	// list or array that can hold multiple reels, {hashtable}
	private Hashtable<String, CircularArray<Texture2>> mReelsNum;
	// hold the images for the animation
	private CircularArray<Texture2> mReels;
	private long mStartTime;
	private long mLocalStartTime;
	private long mSeconds;
	boolean mIsFirstPic = false;
	private AnimationDatabase mData;
	Texture2 mTex;

	@SuppressWarnings("unused")
	private Sprite() {
	}

	/**
	 * Constructor for loading an image from a file. The file must be within the
	 * same directory as the Sprite.java
	 * 
	 * @param fileName
	 *            the name of the image with file type.
	 */
	public Sprite(String fileName) {
		
		//create the texture from the specified filename 
		mTex = new Texture2(fileName); 		
		//add the sprite into the database each time we create one 
		mData = new AnimationDatabase();
	
	}

	/**
	 * Boolean method that add a set of images into a Hashtable
	 * 
	 * @param r	String of the animation name
	 * @param b	the array of texture that is associated with the name
	 * @return false if it can not add, true otherwise
	 */
	public boolean add(String r, CircularArray<Texture2> b) {
		if (mReelsNum.contains(r)) {
			return false;

		} else {
			mReelsNum.put(r, b);
			return true;
		}
	}

	/**
	 * To retrieve the interval of which the images need to be switch during an
	 * animation
	 * 
	 * @param seconds
	 * @return the interval in miliseconds
	 */
	public double getInterval(double seconds) {
		int size = mReels.size();
		long mili = (long) (seconds * 1000);
		return size / mili;
	}

	/**
	 * Gets the pixel data making the image to be shown on the canvas.
	 * 
	 * @param animation	the name of the animation to draw.
	 * @param imgIndex	the index of the image within an animation.
	 * @return a ByteBuffer of pixels to draw.
	 */
	public ByteBuffer getByteBuffer(String animation, int imgIndex){
		
		//get the arr assoc with the name
		CircularArray<Texture2> arr = mData.getTexArr(animation);  

		long currentTime = System.currentTimeMillis();

		long duration = currentTime - mLocalStartTime;
		//if this is the first pic and the array size is one
		if (mIsFirstPic = true && arr.size() == 1) { 	
			//then don't animate 
			mStartTime = 0;										

		}

		// if start is equal or greater than the seconds then stop
		if (duration >= getInterval(mSeconds) && mStartTime != 0) {		

			mLocalStartTime = System.currentTimeMillis();

			//get the reel array of the given animation
			CircularArray<Texture2> updatePic = mData.getTexArr(animation);
			//get that specific index from the texture array
			Texture2 draw = updatePic.get(imgIndex);
			mTex = draw; 
			mIsFirstPic = true;
		}
		// Get ByteBuffer from the selected Texture
		return mTex.getByteBuffer();


	}
	public ByteBuffer getByteBuffer(){
		return mTex.getByteBuffer();
		
	}
	/**
	 * Getter for the width of the texture we already loaded 
	 * 
	 * @return width of the texture
	 */
	public int getWidth(){
		return mTex.getWidth();
	}
	/**
	 * Getter for the Height of the texture 
	 * 
	 * @return int height 
	 */
	public int getHeight(){
		return mTex.getHeight();
	}
	/**
	 * Getter for the transparency of the texture 
	 * 
	 * @return int comp
	 */
	public int getComp(){
		return mTex.getComp();
	}
	/**
	 * To start the timer for animation
	 * 
	 * @return the start time
	 */
	public long startAnime() {
		mStartTime = System.currentTimeMillis();

		mLocalStartTime = mStartTime;

		mIsFirstPic = false;

		return mStartTime;
	}


	/**
	 * Method to set the ID for Sprite
	 * 
	 * @param the Sprite name
	 */
	public void setId(String i) {	mFileName = i;	}


	/**
	 * Getting the ID of a specific Sprite
	 * 
	 * @return the sprite name which is the ID
	 */
	public String getId() {	return mFileName;	}


}
