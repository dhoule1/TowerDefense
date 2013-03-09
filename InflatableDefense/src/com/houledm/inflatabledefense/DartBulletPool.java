package com.houledm.inflatabledefense;

import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.pool.GenericPool;

public class DartBulletPool extends GenericPool<DartBullet>{
	
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
		bullet.setVisible(false);
		bullet.destroy();
		bullet.setIgnoreUpdate(true);
	}
	
	@Override
	protected void onHandleObtainItem(final DartBullet bullet) {
		bullet.setIgnoreUpdate(false);
		bullet.setVisible(true);
	}

}
