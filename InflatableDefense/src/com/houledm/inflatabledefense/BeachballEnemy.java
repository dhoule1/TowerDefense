package com.houledm.inflatabledefense;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class BeachballEnemy extends Enemy{
	
private final static int CHILD_COUNT = 0;
	
	private final static int HEALTH = 18;
	private final static float TRAVEL_SPEED = 1.0f; 
	private final static Integer WORTH = 3;

	public BeachballEnemy(ITiledTextureRegion region) {
		super(region, CHILD_COUNT, 0.0f, 0.0f, HEALTH, TRAVEL_SPEED, WORTH);
		this.setScale(0.70f);
		this.setZIndex(2);
	}	
	public BeachballEnemy() {
		super();
	}

}
