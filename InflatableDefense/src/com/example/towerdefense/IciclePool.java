package com.example.towerdefense;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.pool.GenericPool;

public class IciclePool extends GenericPool<Sprite>{
	
	private TextureRegion region;
	
	public IciclePool(TextureRegion region) {
		this.region = region;
	}

	@Override
	protected Sprite onAllocatePoolItem() {
		return new Sprite(0.0f,0.0f,region,ResourceManager.getInstance().getVbom());
	}
	
	@Override
	protected void onHandleRecycleItem(final Sprite icicle) {
		icicle.detachSelf();
		icicle.clearEntityModifiers();
		icicle.clearUpdateHandlers();
		icicle.setPosition(0.0f,0.0f);
	}
	
	@Override
	protected void onHandleObtainItem(final Sprite icicle) {}

}
