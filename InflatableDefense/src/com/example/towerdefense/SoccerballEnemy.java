package com.example.towerdefense;

import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

public class SoccerballEnemy extends Enemy{
	
	private final static int CHILD_COUNT = 0;
	
	private final static int HEALTH = 3;
	private final static float TRAVEL_SPEED = 0.5f; 
	private final static Integer WORTH = 1;

	public SoccerballEnemy(ITiledTextureRegion region) {
		
		super(region, CHILD_COUNT, 0.0f, 0.0f, HEALTH, TRAVEL_SPEED, WORTH);
		this.setScale(0.70f);
	}
	
	public SoccerballEnemy(TiledTextureRegion pTextureRegion, float x, float y) {
		super(pTextureRegion, CHILD_COUNT, x, y, HEALTH, TRAVEL_SPEED, WORTH);
	}
	
	@Override
	public float[] convertLocalToSceneCoordinates(float[] pCoordinates) {
		return super.convertLocalToSceneCoordinates(pCoordinates);
	}
	
	@Override
	public void returnHealthToNormal() {
		this.setHealth(HEALTH);
	}
	

}
