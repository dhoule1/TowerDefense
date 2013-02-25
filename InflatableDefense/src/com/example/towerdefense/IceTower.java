package com.example.towerdefense;

import java.util.Random;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.modifier.IModifier;

public class IceTower extends BaseTower {
	
	private static final Object LOCK = new Object();
	private static final float SCOPE = 60.0f;
	private static final float TIME_BETWEEN_SHOTS = 2.0f;
	private static final int POWER = 0;
	public static final Integer COST = 5;
	public static final boolean HAS_BULLETS = false;
	public static final int KILLING_COUNT = 2;
	
	private IciclePool pool;
	
	private Random r;
	
	private Sound freezeSound;

	public IceTower(float pX, float pY, ITextureRegion pTextureRegion) {
		super(pX, pY, SCOPE, TIME_BETWEEN_SHOTS, POWER, COST, HAS_BULLETS, pTextureRegion);
		r = new Random();
		freezeSound = ResourceManager.getInstance().getFreezeSound();
		freezeSound.setVolume(2.0f);
		pool = GameScene.getSharedInstance().getIciclePool();
	}
	
	public void disposeOfIcicle(final Sprite icicle) {
		TowerDefenseActivity.getSharedInstance().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				pool.recyclePoolItem(icicle);
			}
		});
	}
	
	@Override
	public void hitEnemy(Enemy enemy) {
		
		int count = 0;
		for (final Enemy e:queue) {
			
			synchronized(LOCK) {
				if (e.isFrozen() || e.isDead()) continue;
				e.freeze();
				freezeSound.play();
			}
			e.setIgnoreUpdate(true);
			final Sprite icicle = pool.obtainPoolItem();
			icicle.setScale(0.25f);
			e.attachChild(icicle);
			icicle.setPosition(icicle.getX() - e.getWidthScaled()*4.8f, icicle.getY() - e.getHeightScaled()*4);
			
			icicle.setRotation(r.nextInt(360));
			
			DelayModifier modifier = new DelayModifier(0.8f, new IEntityModifierListener() {
	
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					
				}
	
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
					e.setIgnoreUpdate(false);
					disposeOfIcicle(icicle);
					e.thaw();
				}
				
			});
			GameScene.getSharedInstance().registerEntityModifier(modifier);
			modifier.setAutoUnregisterWhenFinished(true);
			
			count++;
			if (count == KILLING_COUNT) return;
		}
	}
}
