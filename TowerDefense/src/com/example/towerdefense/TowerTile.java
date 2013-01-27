package com.example.towerdefense;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.util.Log;

public class TowerTile {

	private static HUD lowerPanel; 
	
	private Sprite sprite;
	private Rectangle frame;
	private boolean isTouched;
	
	public TowerTile(ITextureRegion region, int number) {
		isTouched = false;
		TowerDefenseActivity activity = TowerDefenseActivity.getSharedInstance();
		ZoomCamera camera = activity.getCamera();
		VertexBufferObjectManager vbom = activity.getVertexBufferObjectManager();
		TMXTiledMap map = GameScene.getSharedInstance().getTMXTiledMap();
		
		Float height = camera.getHeight() - map.getTileRows()*map.getTileHeight();
		
		frame = new Rectangle((number-1)*height, lowerPanel.getY(), height, height, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				isTouched = true;
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		frame.setColor(Color.WHITE);
		sprite = new Sprite(0,0, region, vbom);
		sprite.setPosition(frame);
	}

	public Rectangle getFrame() {
		return frame;
	}
	public Sprite getSprite() {
		return sprite;
	}
	public static HUD getLowerPanel() {
		return lowerPanel;
	}
	public boolean getOnTouched() {
		return isTouched;
	}
	public void returnOnTouched() {
		isTouched = false;
	}
	
	public static void setPanelBounds(ZoomCamera camera, TMXTiledMap map) {
		float height = camera.getHeight() - map.getTileRows()*map.getTileHeight();
		
		lowerPanel = new HUD();
		lowerPanel.setPosition(0.0f, camera.getBoundsHeight()/2 + height/2);

		camera.setHUD(lowerPanel);
	}

}
