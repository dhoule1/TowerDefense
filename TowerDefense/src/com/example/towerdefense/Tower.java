package com.example.towerdefense;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.primitive.Ellipse;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import android.util.Log;


public class Tower extends Sprite{
	
	public final static Object LOCK = new Object();
	
	private Rectangle sight;
	private Ellipse reticle;
	private float radius;
	private Enemy lockedOn;
	private float timeInbetweenShots;
	private int power;
	private Integer cost;
	private boolean hasBullets;

	public Tower(float pX, float pY, float r,float t, int p, Integer c, boolean hasBullets, ITextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		radius = r;
		sight = new Rectangle(0,0,
					radius*2, radius*2, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		sight.setPosition(this.getX() - sight.getWidthScaled()*10, this.getY());
		sight.setColor(Color.YELLOW);
		reticle = new Ellipse(this.getX()+this.getWidthScaled(), this.getY()+this.getHeightScaled(),
				radius, radius, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		reticle.setLineWidth(2.0f);
		reticle.setColor(Color.BLACK);
		timeInbetweenShots = t;
		power = p;
		cost = c;
		this.hasBullets = hasBullets;
	}
	
	@Override 
	public void setPosition(float x, float y) {
		super.setPosition(x,y);
		sight.setPosition((x + this.getWidthScaled()/2) - (sight.getWidthScaled()/2 - this.getWidthScaled() /2), 
				(y + this.getHeightScaled()/2) - (sight.getHeightScaled()/2 - this.getHeightScaled()/2));
		reticle.setPosition(this.getX()+this.getWidthScaled(), this.getY()+this.getHeightScaled());
	}
	
	public void fireBullets(final Enemy e) {
		
	}
	
	public Rectangle getSight() {
		return sight;
	}
	public Ellipse getReticle() {
		return reticle;
	}
	public void setLockedOn(Enemy e) {
		this.lockedOn = e;
	}
	public Enemy getLockedOn() {
		return this.lockedOn;
	}
	public Integer getCost() {
		return this.cost;
	}
	
	
	public void shoot(final Enemy e) {
		if (this.getEntityModifierCount() == 1) return;
			DelayModifier modifier = new DelayModifier(timeInbetweenShots, new IEntityModifierListener() {
	
				@Override
				public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
					if (!hasBullets) e.hit(power);
					else fireBullets(e);
					
					if (e.isDead() && e.getUserData()!="dead") {
						GameScene scene = GameScene.getSharedInstance();
						Log.i("Dead Enemy", "Enemy "+e.getIndex()+" is dead");
						scene.addAmount(e.getWorth());
						e.destroy();
						scene.incrementDeadCount();
						scene.seeIffWaveFinished();
					}
					
					Log.i("Shooting", "Pew, pew at "+e.getIndex());
				}
	
				@Override
				public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {}
				
			});
			this.registerEntityModifier(modifier);
			modifier.setAutoUnregisterWhenFinished(true);
	}

}
