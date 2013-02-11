package com.example.towerdefense;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Bullet extends Sprite{
	
	private final static VertexBufferObjectManager vbom = TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager();
	public float speed;

	public Bullet(TextureRegion region, float speed) {
		super(0, 0, region, vbom);
		this.speed = speed;
	}

}
