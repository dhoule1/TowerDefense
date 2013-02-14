package com.example.towerdefense;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.modifier.IModifier;

import android.util.Log;

public class IceTower extends BaseTower implements ICollectionTower{
	
	private static final float SCOPE = 60.0f;
	private static final float TIME_BETWEEN_SHOTS = 2.0f;
	private static final int POWER = 0;
	public static final Integer COST = 5;
	public static final boolean HAS_BULLETS = false;
	public static final int KILLING_COUNT = 2;

	public IceTower(float pX, float pY, ITextureRegion pTextureRegion) {
		super(pX, pY, SCOPE, TIME_BETWEEN_SHOTS, POWER, COST, HAS_BULLETS, pTextureRegion);
	}
	
	public void disposeOfIcicle(final Sprite icicle) {
		TowerDefenseActivity.getSharedInstance().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				icicle.detachSelf();
				icicle.dispose();
			}
		});
	}
	
	@Override
	public void hitEnemy(final Enemy e) {
		if (e.isFrozen()) return;
		e.freeze();
		e.setIgnoreUpdate(true);
		final Sprite icicle = new Sprite(0.0f,0.0f,ResourceManager.getInstance().getIcicleRegion(),ResourceManager.getInstance().getVbom());
		icicle.setScale(0.2f);
		icicle.setPosition(e.getX() - e.getWidthScaled()*3.2f, e.getY() - e.getHeightScaled()*3.5f);
		GameScene.getSharedInstance().attachChild(icicle);
		
		Log.i("Freeze in", e.getIndex()+" is now frozen");
		
		DelayModifier modifier = new DelayModifier(1.0f, new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				e.setIgnoreUpdate(false);
				disposeOfIcicle(icicle);
				e.thaw();
				Log.i("Freeze out", e.getIndex()+" is now thawed");
			}
			
		});
		GameScene.getSharedInstance().registerEntityModifier(modifier);
		modifier.setAutoUnregisterWhenFinished(true);
	}

	@Override
	public void addToQueue(Enemy e) {
		if (!queue.contains(e)) queue.add(e);
	}

	@Override
	public void clearQueue() {
		queue.clear();
	}

}
