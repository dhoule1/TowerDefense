package com.example.towerdefense;

import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class TurretTower extends BaseAnimatedTower {
	
	public static final float SCOPE = 60.0f;
	private static final float TIME_BETWEEN_SHOTS = 0.4f;
	private static final int POWER = 1;
	public static final Integer COST = 15;
	public static final int TOTAL_ANIMATION_TIME = 460;

	public TurretTower(float pX, float pY, ITiledTextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion, SCOPE, TIME_BETWEEN_SHOTS, POWER, COST, TOTAL_ANIMATION_TIME);
	}
	
	@Override public void animate() {
		animate(new long[]{250,70,70,70}, 0,3, true);
	}
}
