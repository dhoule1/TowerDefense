package com.example.towerdefense;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;

import android.util.Log;

public class SubMenuManager {
	
	private static GameScene scene;
	
	private static Sprite reticle;
	private static float sightOriginalScale;
	
	private static Sprite upgradeOption;
	private static Sprite sellOption;
	
	private static Tower activeTower;

	private static Entity encapsulatingEntity = new Entity();
	
	public static Tower getActiveTower() {
		return activeTower;
	}
	
	public static void getReticalRegion(TextureRegion region) {
		reticle = new Sprite(-500.0f,-500.0f,region,TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		
		/**
		 * TODO:Not good to hardcode this in
		 */
		sightOriginalScale = 1.3f;
		reticle.setScale(sightOriginalScale);
		reticle.setAlpha(0.5f);
		scene = GameScene.getSharedInstance();
	}
	public static void getUpgradeRegion(TextureRegion region) {
		upgradeOption = new Sprite(0.0f,0.0f, region, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		upgradeOption.setScale(0.20f);
	}
	
	/**
	 * TODO: Fix this glitch
	 * @param region
	 */
	public static void getDeleteRegion(TextureRegion region) {
		sellOption = new Sprite(0.0f,0.0f, region, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent event, float x, float y) {
				Log.i("Touch", "Delete touched");
				scene.addAmount((int)(activeTower.getCost()*0.80));
				scene.unregisterTouchArea(sellOption);
				scene.removeTower(activeTower);
				reticle.setPosition(-500.0f,-500.0f);
				return super.onAreaTouched(event, x, y);
			}
		};
		sellOption.setScale(0.20f);
	}
	
	public static Sprite getReticle(Tower t) {
		reticle.setScale(sightOriginalScale*(t.getRadius()/60));
		return reticle;
	}
	public static Sprite getDeleteSprite() {
		return sellOption;
	}
	
	
	public static Entity display(Tower t) {
		
		scene.registerTouchArea(sellOption);
		
		activeTower = t;
		float radius = t.getRadius();
		
		reticle.setScale(sightOriginalScale*(radius/60));
		reticle.setPosition(t.getX()-t.getWidthScaled()/3,t.getY()-t.getWidthScaled()/3);

		upgradeOption.setPosition(reticle.getX(), reticle.getY());
		sellOption.setPosition(upgradeOption);
		
		upgradeOption.setX((upgradeOption.getX() - t.getWidthScaled()*1.5f) - t.getWidthScaled()/12);
		sellOption.setX(upgradeOption.getX());
		upgradeOption.setY(upgradeOption.getY() - t.getHeightScaled()*1.5f);
		sellOption.setY(upgradeOption.getY());
		
		upgradeOption.setY(upgradeOption.getY() - reticle.getWidthScaled()/3);
		sellOption.setY(sellOption.getY() + reticle.getWidthScaled()/3);
		
		encapsulatingEntity.attachChild(reticle);
		encapsulatingEntity.attachChild(upgradeOption);
		encapsulatingEntity.attachChild(sellOption);
		
		return encapsulatingEntity;
	}
	
	public static void setReticalPosition(float x, float y) {
		reticle.setPosition(x, y);
	}
	
	public static void remove() {
		encapsulatingEntity.detachChildren();
		encapsulatingEntity.detachSelf();
		activeTower = null;
	}
 }
