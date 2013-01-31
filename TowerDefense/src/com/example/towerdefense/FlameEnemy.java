package com.example.towerdefense;

import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.opengl.texture.region.ITextureRegion;

public class FlameEnemy extends Enemy{
	
	private final static ITextureRegion pTextureRegion = TowerDefenseActivity.getSharedInstance().getFlameEnemyTextureRegion();
	private final static float HEALTH = 3;
	private final static float TRAVEL_SPEED = 0.5f; 
	private Path mPath;

	public FlameEnemy(ITextureRegion region) {
		
		super(region, 0.0f, 0.0f, HEALTH, TRAVEL_SPEED);
		this.setScale(2.0f);
	}
	
	public FlameEnemy(float x, float y) {
		super(pTextureRegion, x, y, HEALTH, TRAVEL_SPEED);
		this.setScale(2.0f);
	}
	
	@Override
	public float[] convertLocalToSceneCoordinates(float[] pCoordinates) {
		return super.convertLocalToSceneCoordinates(pCoordinates);
	}
	
	@Override
	public void returnHealthToNormal() {
		this.setHealth(HEALTH);
	}
	
	public void setPath(Path pPath) {
		mPath = pPath;
	}
	public Path getPath() {
		return mPath;
	}
	public float getSpeed() {
		return TRAVEL_SPEED;
	}
	

}
