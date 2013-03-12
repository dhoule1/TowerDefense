package com.houledm.inflatabledefense;

import java.util.Random;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.modifier.IModifier;

public class IceTower extends BaseAnimatedTower {

	private static final float SCOPE = 60.0f;
	private static final float TIME_BETWEEN_SHOTS = 2.0f;
	private static final int POWER = 0;
	public static final Integer COST = 15;
	public static final boolean HAS_BULLETS = false;
	private static final int STARTING_FRAME = 0;
	private static final int ANIMATION_COUNT = 0;
	
	public static final int KILLING_COUNT = 2;
	
	private IciclePool pool;
	
	private Random r;
	
	private Sound freezeSound;

	public IceTower(float pX, float pY, TiledTextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion, SCOPE, TIME_BETWEEN_SHOTS, POWER, COST, HAS_BULLETS,STARTING_FRAME, ANIMATION_COUNT);
		r = new Random();
		freezeSound = ResourceManager.getInstance().getFreezeSound();
		freezeSound.setVolume(2.0f);
		pool = GameScene.getSharedInstance().getIciclePool();
	}
	
	public void disposeOfIcicle(final Sprite icicle) {
		InflatableDefenseActivity.getSharedInstance().runOnUpdateThread(new Runnable() {
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
			if (e.isFrozen() || e.isDead()) continue;
			if (!e.unregisterEntityModifier(e.getBeginningModifier()) && !e.unregisterEntityModifier(e.getMoveModifier())) {
				continue;
			}
			e.freeze();
			freezeSound.play();
			
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
					try {e.registerEntityModifier(e.getMoveModifier());} catch(Exception e){};
					try	{e.registerEntityModifier(e.getBeginningModifier());} catch(Exception e){};
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
	
	@Override public void upgrade() {
		super.upgrade();
		this.timeBetweenShots *= 0.5f;
	}
}
