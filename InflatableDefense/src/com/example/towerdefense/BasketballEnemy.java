package com.example.towerdefense;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class BasketballEnemy extends Enemy{
	
	public final static int CHILD_COUNT = 3;
	
	private final static int HEALTH = 3;
	private final static float TRAVEL_SPEED = 0.5f; 
	private final static Integer WORTH = 2;

/*	public BasketballEnemy(ITiledTextureRegion pTextureRegion, float x, float y,
			int health, float speed, Integer worth) {
		super(pTextureRegion, CHILD_COUNT, x, y, health, speed, worth);
		this.setScale(0.70f);
	}*/
	
	public BasketballEnemy(ITiledTextureRegion region) {
		super(region, CHILD_COUNT, 0.0f, 0.0f, HEALTH, TRAVEL_SPEED, WORTH);
		this.setScale(0.70f);
		
		for (int i = 0; i < CHILD_COUNT; i++) {
			childArray[i] = new SoccerballEnemy(ResourceManager.getInstance().getSoccerballRegion());
		}
	}
	
	@Override
	public void createNewChild(float x, float y, double modifier, int index) {
	  final SoccerballEnemy child = (SoccerballEnemy)childArray[index];
		child.setPosition(x, y);
		child.setPath(this.getPath());
		child.setSpeed((float)(child.getSpeed() * modifier));
		GameScene.getSharedInstance().addNewEnemyToField(child);
	}

}
