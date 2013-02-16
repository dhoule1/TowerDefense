package com.example.towerdefense;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.RectangularShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.util.modifier.IModifier;

import android.util.Log;

public class TurretTower extends AnimatedSprite implements ITower{
	
	private RectangularShape entity;
	
	public static final float SCOPE = 60.0f;
	private static final float TIME_BETWEEN_SHOTS = 1.0f;
	private static final int POWER = 1;
	public static final Integer COST = 2;
	
	private Rectangle sight;
	
	private Enemy lockedOn;

	public TurretTower(float pX, float pY, ITiledTextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion, ResourceManager.getInstance().getVbom());
		sight = new Rectangle(0,0,
				SCOPE*2, SCOPE*2, ResourceManager.getInstance().getVbom());
		sight.setPosition(this.getX() - sight.getWidthScaled()*10, this.getY());
		
		entity = this;
		entity.setZIndex(1);
	}

	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x,y);
		sight.setPosition((x + this.getWidthScaled()/2) - (sight.getWidthScaled()/2 - this.getWidthScaled() /2), 
				(y + this.getHeightScaled()/2) - (sight.getHeightScaled()/2 - this.getHeightScaled()/2));
		SubMenuManager.getReticle(this).setPosition(x-this.getWidthScaled()/3,y-this.getWidthScaled()/3);
		
	}

	@Override
	public boolean inSights(float x, float y) {
		return sight.contains(x, y);
	}

	@Override
	public boolean lockedOnInSight() {
		return inSights(lockedOn.getXReal(), lockedOn.getYReal());
	}
	
	@Override
	public void onImpact(Enemy enemy) {
		if (this.isAnimationRunning()) return;
		this.setCurrentTileIndex(1);
		animate(new long[]{700,100}, 0,1, true);
	}
	
	@Override
	public void onEnemyOutOfRange(Enemy e){}

	@Override
	public void onIdleInWave() {
		this.setCurrentTileIndex(0);
		this.stopAnimation();
	}

	@Override
	public void onWaveEnd() {
		this.setCurrentTileIndex(0);
		this.stopAnimation();
	}

	@Override
	public void hitEnemy(Enemy e) {
		e.hit(POWER);
	}

	@Override
	public Rectangle getSight() {
		return sight;
	}

	@Override
	public float getRadius() {
		return SCOPE;
	}

	@Override
	public void setLockedOn(Enemy e) {
		lockedOn = e;
	}

	@Override
	public Enemy getLockedOn() {
		return lockedOn;
	}

	@Override
	public Integer getCost() {
		return COST;
	}

	@Override
	public void shoot(final Enemy e) {
		if (this.getEntityModifierCount() == 1) return;
		DelayModifier modifier = new DelayModifier(TIME_BETWEEN_SHOTS, new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				hitEnemy(e);
				checkForDeadEnemies(e);
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {}
			
		});
		this.registerEntityModifier(modifier);
		modifier.setAutoUnregisterWhenFinished(true);
	}

	@Override
	public void checkForDeadEnemies(Enemy e) {
		synchronized(LOCK) {
			if (e.isDead() && e.getUserData()!="dead") {
				GameScene scene = GameScene.getSharedInstance();
				Log.i("Dead Enemy", "Enemy "+e.getIndex()+" is dead");
				scene.addAmount(e.getWorth());
				e.destroy();
				scene.incrementDeadCount();
				scene.seeIfWaveFinished();
			}
		}
	}

	@Override
	public RectangularShape getEntity() {
		return entity;
	}
}
