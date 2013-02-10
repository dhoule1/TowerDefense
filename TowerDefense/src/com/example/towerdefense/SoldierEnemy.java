package com.example.towerdefense;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;

public class SoldierEnemy extends Enemy{
	
	public final static int CHILD_COUNT = 3;
	
	private final static int HEALTH = 3;
	private final static float TRAVEL_SPEED = 0.45f; 
	private final static Integer WORTH = 2;

	public SoldierEnemy(ITextureRegion pTextureRegion, float x, float y,
			int health, float speed, Integer worth) {
		super(pTextureRegion, CHILD_COUNT, x, y, health, speed, worth);
	}
	
	public SoldierEnemy(TextureRegion region) {
		super(region, CHILD_COUNT, 0.0f, 0.0f, HEALTH, TRAVEL_SPEED, WORTH);
		this.setScale(2.0f);
	}
	
	@Override
	public void createNewChild(float x, float y, double modifier) {
		final GruntEnemy child = new GruntEnemy(ResourceManager.getInstance().getRedFlameEnemyRegion());
		child.setPosition(x, y);
		child.setPath(this.getPath());
		child.setSpeed((float)(child.getSpeed() * modifier));
		GameScene.getSharedInstance().addNewEnemyToField(child);
	}

}
