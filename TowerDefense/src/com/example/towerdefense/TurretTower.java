package com.example.towerdefense;

import org.andengine.opengl.texture.region.ITextureRegion;

public class TurretTower extends Tower{
	
	public static final float SCOPE = 60.0f;
	private static final float TIME_BETWEEN_SHOTS = 1.0f;
	private static final int POWER = 1;
	public static final Integer COST = 2;
	public static final boolean HAS_BULLETS = false;

	public TurretTower(float pX, float pY, ITextureRegion pTextureRegion) {
		super(pX, pY, SCOPE, TIME_BETWEEN_SHOTS, POWER, COST, HAS_BULLETS, pTextureRegion);
	}

}
