package com.example.towerdefense;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.graphics.Color;
import android.view.KeyEvent;

public class TowerDefenseActivity extends SimpleBaseGameActivity {
	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;
	
	private static TowerDefenseActivity instance;
	
	private ZoomCamera mCamera;
	
	private Scene currentScene;
	
	private TMXTiledMap mTMXTiledMap;
	private TMXLayer tmxLayer;
	
	private TextureRegion flameEnemyRegion;
	
	private TextureRegion turretTowerRegion;
	private TextureRegion dartTowerRegion;
	private TextureRegion dartBulletRegion;
	
	private TextureRegion startButtonRegion;
	
	private Font inGameFont;

	//OVERRIDDEN METHODS
	@Override
	public EngineOptions onCreateEngineOptions() {
    this.mCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
    instance = this;
        
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		return engineOptions;
	}

	@Override
	protected void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		final BitmapTextureAtlas mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1000, 1000, TextureOptions.DEFAULT);
		int w = 0;
		int h = 0;
		
		//ENEMIES
		flameEnemyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "flame.png",w,h); //14x27
		
		//TOWERS
		turretTowerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "turret.png",w+=15,h); //80x80
		dartTowerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "dart_tower.png",w+=81,h); //80x80
		
		//BULLETS
		dartBulletRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "dart.png",w+=81,h); //249x71
		
		//MISC
		startButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "play_button_cropped.png",w+=250,h); //169x169
		mBitmapTextureAtlas.load();	
		
		ITexture fontTexture = new BitmapTextureAtlas(this.getTextureManager(),200,200);
		FontFactory.setAssetBasePath("font/");
		inGameFont = FontFactory.createFromAsset(this.getFontManager(), fontTexture, this.getAssets(), "AeroviasBrasilNF.ttf", 50, true, Color.WHITE);
		//fontTexture.load();
		inGameFont.load();
	}

	@Override
	protected Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		this.currentScene = new GameScene();
		return currentScene;
	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			return super.onKeyDown(pKeyCode, pEvent);
		}
		return false;
	}
	
	//GETTERS AND SETTERS
	public static TowerDefenseActivity getSharedInstance() {
		if (instance == null) {
			instance = new TowerDefenseActivity();
		}
	  return instance;
	}	
	public ZoomCamera getCamera() {
		return this.mCamera;
	}
	public TMXTiledMap getTiledMap() {
		return this.mTMXTiledMap;
	}
	public TMXLayer getTMXLayer() {
		return this.tmxLayer;
	}
	public TextureRegion getFlameEnemyTextureRegion() {
		return this.flameEnemyRegion;
	}
	public TextureRegion getTurretTowerRegion() {
		return this.turretTowerRegion;
	}
	public TextureRegion getDartTowerRegion() {
		return this.dartTowerRegion;
	}
	public TextureRegion getDartBulletRegion() {
		return this.dartBulletRegion;
	}
	public TextureRegion getStartButtonRegion() {
		return this.startButtonRegion;
	}
	public Font getInGameFont() {
		return this.inGameFont;
	}
}
