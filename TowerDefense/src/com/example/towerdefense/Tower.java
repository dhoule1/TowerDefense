package com.example.towerdefense;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.primitive.Ellipse;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import android.util.Log;



public class Tower extends Sprite{
	private Rectangle sight;
	private Ellipse reticle;
	private float radius;
	private Enemy lockedOn;
	private float timeInbetweenShots;
	private final static Object LOCK = new Object();
	private Integer cost;

	public Tower(float pX, float pY, float r,float t, Integer c, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		radius = r;
		sight = new Rectangle(this.getX(), this.getY(),
				radius*2, radius*2, pVertexBufferObjectManager);
		sight.setColor(Color.YELLOW);
		reticle = new Ellipse(this.getX()+this.getWidthScaled(), this.getY()+this.getHeightScaled(),
				radius, radius, pVertexBufferObjectManager);
		reticle.setLineWidth(2.0f);
		reticle.setColor(Color.BLACK);
		timeInbetweenShots = t;
		cost = c;
	}
	
	@Override 
	public void setPosition(float x, float y) {
		super.setPosition(x,y);
		sight.setPosition(x-30.0f,y-30.0f);
		reticle.setPosition(this.getX()+this.getWidthScaled(), this.getY()+this.getHeightScaled());
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
					e.hit();
					if (e.isDead() && e.getUserData()!="dead") {
						GameScene scene = GameScene.getSharedInstance();
						Log.i("Dead Enemy", "Enemy "+e.getIndex()+" is dead");
						scene.addAmount(e.getWorth());
						e.destroy();
						synchronized(LOCK) {
							scene.incrementDeadCount();
							LOCK.notify();
						}
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
