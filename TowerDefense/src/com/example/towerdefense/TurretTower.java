package com.example.towerdefense;

import org.andengine.opengl.texture.region.ITextureRegion;

public class TurretTower extends Tower{
	
	private static final float SCOPE = 50.0f;
	private static final float TIME_BETWEEN_SHOTS = 1.0f;

	public TurretTower(float pX, float pY, ITextureRegion pTextureRegion) {
		super(pX, pY, SCOPE, TIME_BETWEEN_SHOTS, pTextureRegion, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
	}

}
