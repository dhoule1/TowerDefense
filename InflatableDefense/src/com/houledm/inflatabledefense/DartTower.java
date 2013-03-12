package com.houledm.inflatabledefense;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.modifier.IModifier;

public class DartTower extends BaseAnimatedTower{

	private static final float SCOPE = 100.0f;
	private static float TIME_BETWEEN_SHOTS = 1.0f;
	private static final int POWER = 3;
	public static final Integer COST = 30;
	public static final boolean HAS_BULLETS = true;
	public static final int START_FRAME = 0;
	public static final int ANIMATION_COUNT = 0;
	
	private DartBulletPool bulletPool;

	public DartTower(float pX, float pY,TiledTextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion, SCOPE, TIME_BETWEEN_SHOTS, POWER, COST, HAS_BULLETS, START_FRAME,ANIMATION_COUNT);
		bulletPool = GameScene.getSharedInstance().getDartBulletPool();
	}
	
	@Override
	public void fireBullets (final Enemy e) {
		
		final DartBullet b = bulletPool.obtainPoolItem();
		b.setPosition((this.getX() + this.getWidthScaled()/2) - (b.getWidthScaled() + this.getWidthScaled()*3/2),
				(this.getY() + this.getHeightScaled()/2) - (this.getHeightScaled()/2));
		b.setRotation(this.getRotation());
		MoveModifier mmodify = new MoveModifier(DartBullet.SPEED, b.getX(), e.getX() - b.getWidthScaled()*2, b.getY(), e.getYReal() - e.getHeightScaled()*2/3,
				new IEntityModifier.IEntityModifierListener() {

					@Override
					public void onModifierStarted(IModifier<IEntity> pModifier,
							IEntity pItem) {
						
					}

					@Override
					public void onModifierFinished(IModifier<IEntity> pModifier,
							IEntity pItem) {
						bulletPool.recyclePoolItem(b);
						if (!e.isDead()) {
							e.hit(POWER);
							checkForDeadEnemies(e);
						}
						
					}
			
		});
		mmodify.setAutoUnregisterWhenFinished(true);
		b.registerEntityModifier(mmodify);
		
		if (!b.hasParent()) GameScene.getSharedInstance().attachChild(b);
	}
	
	@Override
	public void upgrade() {
		super.upgrade();
		timeBetweenShots *= 0.70f;
	}
}
