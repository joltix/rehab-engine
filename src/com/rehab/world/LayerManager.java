package com.rehab.world;

import java.util.LinkedList;
import java.util.Queue;

import com.rehab.world.Frame.Renderable;


public class LayerManager {
	
	// Layer count
	public static final int NUMBER_OF_LAYERS = 5;
	
	// Layer address constants
	public static final int LAYER_GUI = 0;
	public static final int LAYER_PROP = 1;
	public static final int LAYER_FREE_1 = 2;
	public static final int LAYER_FREE_2 = 3;
	public static final int LAYER_BACKGROUND = 4;
	
	// The available layers to add to and retrieve
	private Queue<Renderable>[] mLayers;
	
	/**
	 * Constructor for a preset number of layers.
	 * 
	 * @see #NUMBER_OF_LAYERS
	 */
	@SuppressWarnings("unchecked")
	public LayerManager() {
		mLayers = new LinkedList[NUMBER_OF_LAYERS];
		for (int i = 0; i < mLayers.length; i++)
			mLayers[i] = new LinkedList<Renderable>();
	}
	
	/**
	 * Adds a Renderable to the layer specified by the Renderable's z value
	 * (layer constant).
	 * 
	 * @param renderable
	 * 		the Renderable instance.
	 */
	public void add(Renderable renderable) {
		int z = renderable.getZ();
		// Append to the end of the layer
		mLayers[z].add(renderable);
	}
	
	/**
	 * Gets the layer of Renderables associated with a given layer constant.
	 * 
	 * @param layerIndex	the layer constant.
	 * @return	an Iterable containing all desired Renderables, or null
	 * 			if no layer is associated with the desired constant.
	 */
	public Queue<Renderable> getLayer(int layerIndex) {
		if (layerIndex >= mLayers.length) return null;
		Queue<Renderable> queue = mLayers[layerIndex];
		mLayers[layerIndex] = new LinkedList<Renderable>();
		return queue;
	}
	
	/**
	 * Gets the number of layers in the LayerManager instance. This method does not
	 * take into account empty layers. That is, layers with no Renderables will still
	 * be returned (only as having a length of 0).
	 * 
	 * @return	the number of layers available to store Renderables.
	 */
	public int layerCount() {
		return mLayers.length;
	}
	
	/**
	 * Gets the number of Renderables in the layer associated with a layer constant.
	 * 
	 * @param layerIndex	the layer constant.
	 * @return	the number of Renderables in the layer, or -1 if no layer was associated
	 * 			with the desired index.
	 * @see #LAYER_GUI
	 * @see #LAYER_PROP
	 * @see #LAYER_FREE_1
	 * @see #LAYER_FREE_2
	 * @see #LAYER_BACKGROUND
	 */
	public int getLayerSize(int layerIndex) {
		// -1 if invalid index
		if (layerIndex >= mLayers.length) return -1;
		return mLayers[layerIndex].size();
	}
	
}
