package com.example.towerdefense;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.modifier.IModifier;

public class SpikeTower extends BaseAnimatedTower{
	
	public static final float SCOPE = 60.0f;
	private static final float TIME_BETWEEN_SHOTS = 1.2f;
	private static final int POWER = 2;
	public static final Integer COST = 150;
	private static final int KILLING_COUNT = 3;
	
	public static final int TOTAL_ANIMATION_TIME = 100;
	
	private boolean animate;

	public SpikeTower(float pX, float pY, ITiledTextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion, SCOPE, TIME_BETWEEN_SHOTS, POWER, COST, TOTAL_ANIMATION_TIME);
	}
	
	@Override 
	public void animate() {
		animate(new long[]{25,25,25,25}, 1,4, true);
		
		animate = true;
		DelayModifier delay = new DelayModifier(1.0f, new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				animate = false;
			}
		});
		this.registerEntityModifier(delay);
		delay.setAutoUnregisterWhenFinished(true);
	}
	
	@Override
	public void hitEnemy(Enemy e) {
		for (int i = 0; i < queue.size(); i++) {
			Enemy enemy = queue.get(i);
			enemy.hit(POWER);
			checkForDeadEnemies(enemy);
			if (i+1 == KILLING_COUNT) break;
		}
	}
	
	@Override
	public void onIdleInWave() {
		this.clearQueue();
		
		if (!animate) {
			this.setCurrentTileIndex(0);
			this.stopAnimation();
		}
	}

}
