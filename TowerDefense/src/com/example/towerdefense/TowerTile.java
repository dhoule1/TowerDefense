package com.example.towerdefense;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class TowerTile {
	
	private Sprite sprite;
	private Rectangle frame;
	private boolean isTouched;	
	
	
	//*******************Getters and Setters*********************//
	public Rectangle getFrame() {
		return frame;
	}
	public Sprite getSprite() {
		return sprite;
	}
	public boolean getOnTouched() {
		return isTouched;
	}
	public void returnOnTouched() {
		isTouched = false;
	}
	
	
	//***************Public Methods************************//
	
	/**
	 * This initializes each of the Tower buttons within the HUD
	 * @param region
	 * @param number
	 */
	public TowerTile(ITextureRegion region) {
		isTouched = false;
		TowerDefenseActivity activity = TowerDefenseActivity.getSharedInstance();
		ZoomCamera camera = activity.getCamera();
		VertexBufferObjectManager vbom = activity.getVertexBufferObjectManager();
		TMXTiledMap map = GameScene.getSharedInstance().getTMXTiledMap();
		
		Float height = camera.getHeight() - map.getTileRows()*map.getTileHeight();
		
		//frame = new Rectangle((number-1)*height, lowerPanel.getY(), height, height, vbom) {
		frame = new Rectangle(0.0f,0.0f, height, height, vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				isTouched = true;
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};
		frame.setColor(Color.WHITE);
		sprite = new Sprite(0,0, region, vbom);
	}

}
