package com.houledm.inflatabledefense;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class FootballEnemy extends Enemy{
	
	public final static int CHILD_COUNT = 0;
	
	private final static int HEALTH = 2;
	private final static float TRAVEL_SPEED = 0.2f; 
	private final static Integer WORTH = 1;
	
	public FootballEnemy(ITiledTextureRegion region) {
		super(region, CHILD_COUNT, 0.0f, 0.0f, HEALTH, TRAVEL_SPEED, WORTH);
		this.setScale(0.70f);
	}

}
