package com.example.towerdefense;

import org.andengine.opengl.texture.region.ITextureRegion;

public class TurretTower extends Tower{
	
	private static final float SCOPE = 50.0f;

	public TurretTower(float pX, float pY, ITextureRegion pTextureRegion) {
		super(pX, pY, SCOPE, pTextureRegion, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		// TODO Auto-generated constructor stub
	}

}
