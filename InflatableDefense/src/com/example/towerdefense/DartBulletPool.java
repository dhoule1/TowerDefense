package com.example.towerdefense;

import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.pool.GenericPool;

public class DartBulletPool extends GenericPool<DartBullet>{
	
	//Bullet bullet;
	TextureRegion region;
	
	public DartBulletPool(TextureRegion region) {
		if (region == null) throw new IllegalArgumentException("The TextureRegion Cannot Be Null!");
		this.region = region;
		
	}
	

	@Override
	protected DartBullet onAllocatePoolItem() {
		return new DartBullet(region);
	}
	
	@Override
	protected void onHandleRecycleItem(final DartBullet bullet) {
		bullet.setIgnoreUpdate(true);
		bullet.setVisible(false);
	}
	
	@Override
	protected void onHandleObtainItem(final DartBullet bullet) {
		bullet.clearEntityModifiers();
		bullet.clearUpdateHandlers();
		bullet.detachSelf();
	}

}
