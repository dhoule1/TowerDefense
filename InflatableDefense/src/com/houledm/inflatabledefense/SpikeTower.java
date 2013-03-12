package com.houledm.inflatabledefense;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class SpikeTower extends BaseAnimatedTower{
	
	public static final float SCOPE = 60.0f;
	private static final float TIME_BETWEEN_SHOTS = 1.2f;
	private static final int POWER = 2;
	public static final Integer COST = 150;
	public static final boolean HAS_BULLETS = false; 
	private static final int ANIMATION_COUNT = 5;
	
	public static final int START_FRAME = 1;

	private int killingCount;

	public SpikeTower(float pX, float pY, ITiledTextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion, SCOPE, TIME_BETWEEN_SHOTS, POWER, COST, HAS_BULLETS, START_FRAME, ANIMATION_COUNT);
		killingCount = 3;
	}
	
	@Override 
	public void animate() {
		animate(new long[]{25,25,25,25}, startAnimationFrame, endAnimationFrame, true);
	}
	
	@Override
	public void hitEnemy(Enemy e) {
		for (int i = 0; i < queue.size(); i++) {
			Enemy enemy = queue.get(i);
			enemy.hit(POWER);
			checkForDeadEnemies(enemy);
			if (i+1 == killingCount) break;
		}
	}
	
	@Override public void upgrade() {
		super.upgrade();
		this.timeBetweenShots *= 0.6f;
	}

}
