package com.rehab.world;

import java.util.LinkedList;

import com.rehab.animation.Drawable;

public class LayerManager {
	// The available layers to add to and retrieve
	private LinkedList<Drawable>[] mLayers;
	
	/**
	 * Constructor for a certain number of layers
	 * @param layerCount
	 * 		the number of layers to create.
	 */
	@SuppressWarnings("unchecked")
	public LayerManager(int layerCount) {
		mLayers = new LinkedList[layerCount];
		for (int i = 0; i < mLayers.length; i++)
			mLayers[i] = new LinkedList<Drawable>();
	}
	
	/**
	 * Adds a Drawable to the layer associated with the index returned from calling
	 * getZ() on the given Drawable.
	 * @param drawable
	 * 		the Drawable instance.
	 */
	public void add(Drawable drawable) {
		int z = (int) drawable.getX();
		// Create the head Layerable or append to the end of the list
		if (mLayers[z] == null) mLayers[z] = new LinkedList<Drawable>();
		else mLayers[z].add(drawable);
	}
	
	/**
	 * Gets the layer of Drawables associated with a given layer index.
	 * @param layerIndex
	 * 		the index of the desired layer.
	 * @return
	 * 		an Iterable containing all desired Drawables, or null if
	 * 		no layer is associated with the desired index.
	 */
	public Iterable<Drawable> getLayer(int layerIndex) {
		if (layerIndex >= mLayers.length) return null;
		return mLayers[layerIndex];
	}
	
	/**
	 * Gets the number of layers in the LayerManager instance. This method does not
	 * take into account empty layers. That is, layers with no Entities will still
	 * be returned (only as having a length of 0).
	 * @return
	 * 		the number of layers available to store Drawables in.
	 */
	public int layerCount() { return mLayers.length; }
	
	/**
	 * Gets the number of Drawables in the layer associated with a specified index.
	 * @param layerIndex
	 * 		the index of the layer.
	 * @return
	 * 		the number of Drawables in the layer, or -1 if no layer was associated
	 * 		with the desired index
	 */
	public int getLayerSize(int layerIndex) {
		// -1 if invalid index
		if (layerIndex >= mLayers.length) return -1;
		return mLayers[layerIndex].size();
	}
	
}
