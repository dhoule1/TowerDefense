package com.houledm.inflatabledefense;

import org.andengine.opengl.texture.region.TiledTextureRegion;

public class BowlingballEnemy extends Enemy{
	
	public final static int CHILD_COUNT = 5;
	
	private final static int HEALTH = 75;
	private final static float TRAVEL_SPEED = 1.8f; 
	private final static Integer WORTH = 10;
	
	public BowlingballEnemy(TiledTextureRegion region) {
		super(region, CHILD_COUNT, 0.0f, 0.0f, HEALTH, TRAVEL_SPEED, WORTH);
		this.setScale(0.70f);
		
		for (int i = 0; i < CHILD_COUNT; i++) {
			childArray[i] = new FootballEnemy(ResourceManager.getInstance().getFootballEnemyRegion());
		}
	}
	
	@Override
	public void createNewChild(float x, float y, double modifier, int index) {
	  final FootballEnemy child = (FootballEnemy)childArray[index];
		child.setPosition(x, y);
		child.setPath(this.getPath());
		child.setSpeed((float)(child.getSpeed() * modifier));
		GameScene.getSharedInstance().addNewEnemyToField(child);
	}

}
