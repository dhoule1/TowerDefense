package com.houledm.inflatabledefense;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;

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
		reticle = new Sprite(-500.0f,-500.0f,region,InflatableDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		sightOriginalScale = 1.3f;
		reticle.setScale(sightOriginalScale);
		reticle.setAlpha(0.5f);
		reticle.setZIndex(2);
		scene = GameScene.getSharedInstance();
	}
	public static void getUpgradeRegion(TextureRegion region) {
		upgradeOption = new Sprite(0.0f,0.0f, region, InflatableDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		upgradeOption.setScale(0.15f);
		upgradeOption.setZIndex(3);
	}
	

	public static void getDeleteRegion(TextureRegion region) {
		sellOption = new Sprite(0.0f,0.0f, region, InflatableDefenseActivity.getSharedInstance().getVertexBufferObjectManager()) {
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
		sellOption.setZIndex(3);
	}
	
	public static Sprite getReticle(ITower t) {
		if (t != null) reticle.setScale(sightOriginalScale*(t.getRadius()/60));
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

		upgradeOption.setPosition(reticle.getX(), reticle.getY());
		sellOption.setPosition(upgradeOption);
		
		upgradeOption.setX((upgradeOption.getX() - t.getEntity().getWidthScaled()*1.35f));
		sellOption.setX(upgradeOption.getX() - t.getEntity().getWidthScaled()/10.0f);
		upgradeOption.setY(upgradeOption.getY() - t.getEntity().getHeightScaled()*1.5f);
		sellOption.setY(upgradeOption.getY());
		
		upgradeOption.setY(upgradeOption.getY() - reticle.getWidthScaled()/3);
		sellOption.setY(sellOption.getY() + reticle.getWidthScaled()/3);
		
		encapsulatingEntity.attachChild(upgradeOption);
		encapsulatingEntity.attachChild(sellOption);
		
		encapsulatingEntity.attachChild(reticle);
		encapsulatingEntity.sortChildren();
		
		encapsulatingEntity.setZIndex(1);
		
		return encapsulatingEntity;
	}
	
	public static void setReticalPosition(float x, float y) {
		reticle.setPosition(x, y);
	}
	
	public static void remove() {
		encapsulatingEntity.detachSelf();
		encapsulatingEntity.detachChildren();
		activeTower = null;
	}
 }
