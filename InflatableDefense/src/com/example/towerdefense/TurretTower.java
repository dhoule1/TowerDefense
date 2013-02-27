package com.example.towerdefense;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class TurretTower extends BaseAnimatedTower {
	
	public static final float SCOPE = 60.0f;
	private static final float TIME_BETWEEN_SHOTS = 0.4f;
	private static final int POWER = 1;
	public static final Integer COST = 5;

	public TurretTower(float pX, float pY, ITiledTextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion, SCOPE, TIME_BETWEEN_SHOTS, POWER, COST);
	}
	
	@Override
	public void onImpact(Enemy enemy) {
		if (this.isAnimationRunning()) return;
		animate(new long[]{250,50}, 0,1, true);
	}
}
