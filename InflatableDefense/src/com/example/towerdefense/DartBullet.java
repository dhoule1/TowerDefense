package com.example.towerdefense;

import org.andengine.opengl.texture.region.TextureRegion;

public class DartBullet extends Bullet{
	public static final float SPEED = 0.25f;

	public DartBullet(TextureRegion region) {
		super(region, SPEED);
		this.setScale(0.15f);
	}

}
