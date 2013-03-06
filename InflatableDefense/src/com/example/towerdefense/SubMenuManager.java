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
	
	private static ITower activeTower;

	private static Entity encapsulatingEntity = new Entity();
	
	public static ITower getActiveTower() {
		return activeTower;
	}
	
	public static void getReticalRegion(TextureRegion region) {
		reticle = new Sprite(-500.0f,-500.0f,region,TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		sightOriginalScale = 1.3f;
		reticle.setScale(sightOriginalScale);
		reticle.setAlpha(0.5f);
		reticle.setZIndex(0);
		scene = GameScene.getSharedInstance();
	}
	public static void getUpgradeRegion(TextureRegion region) {
		upgradeOption = new Sprite(0.0f,0.0f, region, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		upgradeOption.setScale(0.15f);
	}
	

	public static void getDeleteRegion(TextureRegion region) {
		sellOption = new Sprite(0.0f,0.0f, region, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent event, float x, float y) {
				scene.addAmount((int)(activeTower.getCost()*0.80));
				scene.unregisterTouchArea(sellOption);
				scene.removeTower(activeTower, true);
				reticle.setPosition(-500.0f,-500.0f);
				return super.onAreaTouched(event, x, y);
			}
		};
		sellOption.setScale(0.15f);
	}
	
	public static Sprite getReticle(ITower t) {
		reticle.setScale(sightOriginalScale*(t.getRadius()/60));
		return reticle;
	}
	public static Sprite getDeleteSprite() {
		return sellOption;
	}
	
	
	public static Entity display(ITower t) {
		
		scene.registerTouchArea(sellOption);
		
		activeTower = t;
		float radius = t.getRadius();
		
		reticle.setScale(sightOriginalScale*(radius/TurretTower.SCOPE));
		reticle.setPosition(t.getEntity().getX()-t.getEntity().getWidthScaled()/3.5f,
				t.getEntity().getY()-t.getEntity().getHeightScaled()/3.5f);
		reticle.setZIndex(1);

		upgradeOption.setPosition(reticle.getX(), reticle.getY());
		sellOption.setPosition(upgradeOption);
		
		upgradeOption.setX((upgradeOption.getX() - t.getEntity().getWidthScaled()*1.5f) - t.getEntity().getWidthScaled()/12);
		sellOption.setX(upgradeOption.getX() - sellOption.getWidthScaled()/12);
		upgradeOption.setY(upgradeOption.getY() - t.getEntity().getHeightScaled()*1.5f);
		sellOption.setY(upgradeOption.getY());
		
		upgradeOption.setY(upgradeOption.getY() - reticle.getWidthScaled()/3);
		sellOption.setY(sellOption.getY() + reticle.getWidthScaled()/3);
		
		upgradeOption.setZIndex(2);
		sellOption.setZIndex(2);
		
		Log.i("Attaching Reticle Now", "NOW");
		encapsulatingEntity.attachChild(upgradeOption);
		encapsulatingEntity.attachChild(sellOption);
		
		encapsulatingEntity.attachChild(reticle);
		encapsulatingEntity.sortChildren();
		
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
