package com.houledm.inflatabledefense;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Bullet extends Sprite{

	public float speed;
	private static VertexBufferObjectManager vbom = ResourceManager.getInstance().getVbom();
	
	public Bullet(TextureRegion region, float speed) {
		super(0, 0, region, vbom);
		this.speed = speed;
	}
	
	public void destroy() {
		InflatableDefenseActivity.getSharedInstance().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				clearEntityModifiers();
				detachSelf();
			}
		});
	}

}
