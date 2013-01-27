package com.example.towerdefense;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class TowerDefenseActivity extends SimpleBaseGameActivity {
	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;
	
	private static TowerDefenseActivity instance;
	
	private ZoomCamera mCamera;
	
	private Scene currentScene;
	
	private TMXTiledMap mTMXTiledMap;
	private TMXLayer tmxLayer;
	
	private ITextureRegion flameEnemyRegion;
	private ITextureRegion turretTowerRegion;
	private ITextureRegion startButtonRegion;

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
		final BitmapTextureAtlas mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 289, 289, TextureOptions.DEFAULT);
		int w = 0;
		int h = 0;
		flameEnemyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "flame.png",w,h); //14x27
		turretTowerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "turret.png",w+15,h); //80x80
		startButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this, "play_button_cropped.png",w+96,h); //169x169
		mBitmapTextureAtlas.load();	
	}

	@Override
	protected Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		this.currentScene = new GameScene();
		return currentScene;
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
	public ITextureRegion getFlameEnemyTextureRegion() {
		return this.flameEnemyRegion;
	}
	public ITextureRegion getTurretTowerRegion() {
		return this.turretTowerRegion;
	}
	public ITextureRegion getStartButtonRegion() {
		return this.startButtonRegion;
	}
}
