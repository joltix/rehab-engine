package com.rehab.animation;
import java.util.ArrayList;
/**
 * Modified Arraylist Class so that it an represent a reel in animation
 * in which a reel in circular and thus when you are at the end of reel
 * it will wrap back to the beginning and not have 
 * @author TrishDaoLe
 *
 * @param <E>
 */
public class CircularArray<E> extends ArrayList<E> {
	/**
	 * @param index
	 */
	@Override
	public E get(int index) {
		// TODO Auto-generated method stub
		//if the index we want to get is less than zero
		//then set index to be index+size
				int size = size();
				int abs = Math.abs(index+size); 
				//index too small will wrap itself back around
				if(index < 0) {  index = abs % size;}
				// Index too big to handle so wrap around
				else if (index >= size) index = index % size;

				return super.get(index);
	}
	
	
}
