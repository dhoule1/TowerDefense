package com.example.towerdefense;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXTiledMap;

public class BottomPanel extends HUD{
	
	private float distanceFromMapToScene;
	private ZoomCamera mCamera;
	
	public BottomPanel(ZoomCamera camera, TMXTiledMap map) {
		super();
		
		this.mCamera = camera;
		
		distanceFromMapToScene = camera.getBoundsHeight() - (map.getTileRows()*map.getTileHeight());
		this.setPosition(0.0f, camera.getHeight()/3 - distanceFromMapToScene/2);

		camera.setHUD(this);
	}
	
	public void placeTowerAccess(TowerTile tile, int count) {
		tile.returnOnTouched();
		
		tile.getFrame().setPosition((count-1)*distanceFromMapToScene, this.getY());
		tile.getSprite().setPosition(tile.getFrame());
		
		this.attachChild(tile.getFrame());
		this.attachChild(tile.getSprite());
		
		this.registerTouchArea(tile.getFrame());
	}
	
	public void placeStartButton(Sprite button) {
		this.attachChild(button);
		this.registerTouchArea(button);
		button.setPosition(mCamera.getBoundsWidth() - button.getWidthScaled()*1.55f, this.getY() - button.getHeightScaled() /1.8f);
	}

}
