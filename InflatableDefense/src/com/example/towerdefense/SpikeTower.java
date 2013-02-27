package com.example.towerdefense;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.modifier.IModifier;

public class SpikeTower extends BaseAnimatedTower{
	
	public static final float SCOPE = 60.0f;
	private static final float TIME_BETWEEN_SHOTS = 0.2f;
	private static final int POWER = 2;
	public static final Integer COST = 150;
	
	private boolean animate;

	public SpikeTower(float pX, float pY, ITiledTextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion, SCOPE, TIME_BETWEEN_SHOTS, POWER, COST);
	}
	
	@Override
	public void onImpact(Enemy enemy) {
		if (this.isAnimationRunning()) return;
		
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
	public void onIdleInWave() {
		this.clearQueue();
		
		if (!animate) {
			this.setCurrentTileIndex(0);
			this.stopAnimation();
		}
	}

}
