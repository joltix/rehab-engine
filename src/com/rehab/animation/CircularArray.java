package com.rehab.animation;

import java.util.ArrayList;

public class CircularArray<E> extends ArrayList<E> {

	@Override
	public E get(int index) {
		// TODO Auto-generated method stub
		if(index < 0) //if the index we want to get is less than zero
						//then set index to be index+size
	        index = index + size();
		else if(index == size()) index=0; 

		return super.get(index);
	}
	
	public static void main(String [ ] args){
		CircularArray reels = new CircularArray();
		reels.add(10); //index =0
		reels.add(20); //i=1
		reels.add(30); //i=2
		reels.add(40); //i=3
		
		System.out.println(reels.get(4));
		
	}
}
