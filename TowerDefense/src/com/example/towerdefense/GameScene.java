package com.example.towerdefense;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.runnable.RunnableHandler;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import android.util.Log;

public class GameScene extends Scene implements IOnSceneTouchListener, IScrollDetectorListener, IPinchZoomDetectorListener{
	
	//static fields referring to specific tiles on the map
	private static final int TILE_WIDTH = 40;
	private static final int TILE_HEIGHT = 40;
	private static final int[] END_TILE_ID = new int[]{5,18};
	
	//Reference of Activity context
	private TowerDefenseActivity activity;
	
	//Makes this a singleton class (only one instance at a time)
	private static GameScene scene;
	
	//Variables to set up the zoom camera
	private ZoomCamera mCamera;
	private SurfaceScrollDetector mScrollDetector;
	private PinchZoomDetector mPinchZoomDetector;
	private float mPinchZoomStartedCameraZoomFactor;
	private float maxZoom;
	
	//The map and layer of the field
	private TMXTiledMap mTMXTiledMap;
	private TMXLayer tmxLayer;
	
	//Turret option on the HUD
	private TowerTile turretTile;
	
	//Start button on the HUD
	private Sprite startButton;
	
	//The tower in 'limbo' when it is dragged from the HUD to the scene itself
	private Tower dragTower;
	//Highlights the tile underneath the dragTower
	private Rectangle highlightTile;
	//To make sure you only drag one tower at a time
	private boolean towerMove;
	
	//List of towers on the field
	private List<Tower> towers;
	
	//A-Star path variables
	private TMXTile startTile;
	private TMXTile endTile;
	private List<TMXTile> blockedTileList;
	private AStarPathHelper aStarHelper;
	
	//Collision detection handler - runs every frame
	private IUpdateHandler collisionDetect;
	
	//Wave fields
	private WaveHelper waveGenerator;
	private Wave currentWave;
	private Integer waveCount;
	
	
	//***********************************************************
	//CONSTRUCTOR
	//***********************************************************	
	public GameScene() {
		//Zoom-Camera configuration
		this.setOnAreaTouchTraversalFrontToBack();
		
		this.mScrollDetector = new SurfaceScrollDetector(this);
		
		activity = TowerDefenseActivity.getSharedInstance();
		scene = this;
		
		if (MultiTouch.isSupported(activity)) {
			this.mPinchZoomDetector = new PinchZoomDetector(this);
		} else {
			this.mPinchZoomDetector = null;
		}
		
		this.setOnSceneTouchListener(this);
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		
		try {
			final TMXLoader tmxLoader = new TMXLoader(activity.getAssets(), activity.getEngine().getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, activity.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
						//pTMXTiledMap.getTMXLayers().get(0).getTMXTiles();
				}});
			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/path.tmx");
		} catch (final TMXLoadException e) {
			Debug.e(e);
		}
		
		tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);	
		this.attachChild(tmxLayer);
		
		mCamera = activity.getCamera();
		this.mCamera.setBounds(0, (mCamera.getHeight() - tmxLayer.getHeight()), tmxLayer.getWidth(), tmxLayer.getHeight());
		this.mCamera.setBoundsEnabled(true);
		
		float camera_width = activity.getCamera().getWidth();
		float camera_height = activity.getCamera().getHeight();
    if (camera_width / tmxLayer.getHeight() >= camera_height / tmxLayer.getWidth())
    	maxZoom = camera_width / (tmxLayer.getHeight()*2);
    else
    	maxZoom = camera_height / (tmxLayer.getWidth()*2);
		
    //2-dimensional array of tiles
		TMXTile[][] tiles = tmxLayer.getTMXTiles();
		
		startTile = null;
		endTile = null;
		outer: for (int i = 0; i < tiles.length-1; i++) {
			for (int j = 0; j < tiles[0].length-1; j++) {
				try {
					if (tiles[i][j].getTMXTileProperties(this.mTMXTiledMap).containsTMXProperty("start", "true")) {
						startTile = tiles[i][j];
					}
				}catch (NullPointerException e) {
					//The presence of the TMCTileProperties needs to be handled better by the TMXTile
					  //Ex: hasTMXTileProperties()
				}
			  if (i == END_TILE_ID[0] && j == END_TILE_ID[1]) {
						endTile = tiles[i][j+1];
						break outer;
				}
			}
		}
		
		TowerTile.setPanelBounds(mCamera, mTMXTiledMap);
		
		//Initializes the HUD
		HUD lowerPanel = TowerTile.getLowerPanel();
		this.attachChild(lowerPanel);
		
		turretTile = new TowerTile(activity.getTurretTowerRegion(), 1);
		lowerPanel.attachChild(turretTile.getFrame());
		lowerPanel.registerTouchArea(turretTile.getFrame());
		lowerPanel.attachChild(turretTile.getSprite());
		
		startButton = new Sprite(0.0f,
				0.0f, activity.getStartButtonRegion(), activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				startCurrentWave();
				return true;
			}
		};
		
		/*
		 * TODO fix this shit
		 */
		startButton.setScale(0.473372781f);
		startButton.setPosition(mCamera.getBoundsWidth() - startButton.getWidthScaled()*1.55f, TowerTile.getLowerPanel().getY() - startButton.getHeightScaled()/1.8f);
		lowerPanel.attachChild(startButton);
		lowerPanel.registerTouchArea(startButton);
		
		//Initializing tower array
		towers = new ArrayList<Tower>();
		
		blockedTileList = new ArrayList<TMXTile>();
		
		aStarHelper = new AStarPathHelper(mTMXTiledMap, endTile);
		waveGenerator = new WaveHelper();
		waveCount = 0;
		
		collisionDetect = new RunnableHandler() {
			@Override
			public void onUpdate(float pS) {
				collisionDetect();
			}
		};
		
		this.registerUpdateHandler(collisionDetect);
		
		//Sets up paths/move modifiers of enemies in the first wave
		initializeNextWave();
		
	}
	//**********************************************************
	//USER-CREATED METHODS
	//**********************************************************
	
	//PUBLIC METHODS
	
	//Getters and Setters
	public static GameScene getSharedInstance() {
		if (scene == null) {
			return new GameScene();
		}
		else return scene;
	}
	public static int getTileWidth() {
		return GameScene.TILE_WIDTH;
	}
	public static int getTileHeight() {
		return GameScene.TILE_HEIGHT;
	}
	public TMXTiledMap getTMXTiledMap() {
		return this.mTMXTiledMap;
	}
	public TMXTile getStartTile() {
		return this.startTile;
	}
	public List<TMXTile> getBlockedList() {
		return blockedTileList;
	}
	public Wave getCurrentWave() {
		return currentWave;
	}
	public AStarPathHelper getAStarHelper() {
		return aStarHelper;
	}
	public WaveHelper getWaveHelper() {
		return this.waveGenerator;
	}
	public Integer getWaveCount() {
		return this.waveCount;
	}	
	
	/**
	 * Initializes paths and move modifiers of all enemies in the next wave
	 */
	public void initializeNextWave() {		
		waveGenerator.initWave(waveCount);
		currentWave = waveGenerator.get(waveCount);
		currentWave.setInitialPath(currentWave.getEnemies()[0].getPath());
		
		//waveCount++;
	}
	
	//PRIVATE METHODS
	/**
	 * Attaches the move modifiers to each of the enemies in the next wave 
	 * and attaches the enemies to the scene
	 */
	private void startCurrentWave() {
		if (!aStarHelper.isNavigating()) {
			waveGenerator.startWave();		
			//waveCount++;
		}
	}
	
/*	private boolean blocksExit(TMXTile tile) {
		Path p = enemy.getPath();
		
		TMXTile tileBefore = null;
		boolean onPath = false;
		for(int i = 0; i < p.getCoordinatesX().length; i++) {
			float x = p.getCoordinatesX()[i];
			float y = p.getCoordinatesY()[i];
			
			if (x == tile.getTileColumn()*tile.getTileWidth()) {
				if (y == tile.getTileRow()*tile.getTileHeight()) {
					onPath = true;
					try {
						tileBefore = tmxLayer.getTMXTileAt(p.getCoordinatesX()[i-1], p.getCoordinatesY()[i-1]);
					}catch(Exception e) {}
				}
			}
		}
		if (!onPath) return false;
		
		int blockedSides = blockedSideCount(tileBefore);
		
		if (blockedSides >= 2) return true;
		
		return false;
	}
	
	private int blockedSideCount(TMXTile tile) {
		
		if (tile == null) {
			int count = blockedSideCount(startTile);
			return count-1;
		}
		
		int result = 0;
		
		int c = tile.getTileColumn();
		int r = tile.getTileRow();
		
		TMXTile[] surroundingTiles = new TMXTile[4];
		
		int disparity = 0;
		try {
			surroundingTiles[0] = tmxLayer.getTMXTile(c-1,r);
		}catch(Exception e){disparity++;result++;}
		try {
			surroundingTiles[1-disparity] = tmxLayer.getTMXTile(c+1,r);
		}catch(Exception e){disparity++;result++;}
		try {
			surroundingTiles[2-disparity] = tmxLayer.getTMXTile(c,r-1);
		}catch(Exception e){disparity++;result++;}
		try {
			surroundingTiles[3-disparity] = tmxLayer.getTMXTile(c,r+1);
		}catch(Exception e){disparity++;result++;}
		
		for(TMXTile t:surroundingTiles) {
			if (blockedTileList.contains(t)) result++;
		}
		
		if (!tile.equals(startTile)) return result;
		else return result-1;
	}	*/
	
	/**
	 * Checks to see if a x,y coordinate is on the TMXTiledMap
	 * @param x
	 * @param y
	 * @return isOnMap
	 */
	private boolean pointOnMap(float x, float y) {
		return (x < this.mTMXTiledMap.getTileColumns()*TILE_WIDTH
				&& y < this.mTMXTiledMap.getTileRows()*TILE_HEIGHT);
	}
	
	/**
	 * Removes a tower from the field
	 * @param Tower t
	 */
	private void removeTower(final Tower t) {
		activity.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				t.detachSelf();
				t.getReticle().detachSelf();
				t.clearEntityModifiers();
				t.clearUpdateHandlers();
			}	
		});
		
		if (towers.contains(t)) {
			towers.remove(t);
		}
	}	
	
	/**
	 * Detects which enemies are in the range of which towers
	 * Updates every frame
	 */
	private void collisionDetect() {
		if (aStarHelper.isNavigating()) {
			for (Tower t:towers) {
				for (Enemy enemy:currentWave.getEnemies()) {
					if(t.getSight().contains(enemy.getX(), enemy.getY())) {
						float dx = t.getX()-enemy.getX();
						float dy = t.getY()-enemy.getY();
						float angle = MathUtils.atan2(dy,dx);
						t.setRotation((float)(angle * (180.0f/Math.PI)));
					}
				}
			}
		}
	}
	
	/**
	 * When placing a tower on the field, this checks 
	 * if the path needs to be updated for each enemy
	 * @param current
	 * @param inMiddleOfWave
	 */
	private void updateAffectedEnemies(TMXTile current, boolean inMiddleOfWave) {
		for (Enemy enemy:currentWave.getEnemies()) {
			Path p = enemy.getPath();
			for(int i = 0; i < p.getCoordinatesX().length; i++) {
				float pX = p.getCoordinatesX()[i];
				float pY = p.getCoordinatesY()[i];
				
				TMXTile tile = this.tmxLayer.getTMXTileAt(pX, pY);
				
				if (current.equals(tile)) {					
					if (inMiddleOfWave) enemy.setNeedToUpdatePath(true);
					else enemy.setPath(aStarHelper.getPath(enemy));
					break;
				}
			}
		}
	}
	
	//***************************************************
	//OVERRIDEN METHODS
	//***************************************************
	@Override
	public void onPinchZoomStarted(PinchZoomDetector pPinchZoomDetector,
			TouchEvent pSceneTouchEvent) {
		this.mPinchZoomStartedCameraZoomFactor = this.mCamera.getZoomFactor();
		//clicked = false; // stop the sprite from walking to the touched area when zooming
	}

	@Override
	public void onPinchZoom(PinchZoomDetector pPinchZoomDetector,
			TouchEvent pTouchEvent, float pZoomFactor) {
	// zoom, but make sure the camera does not zoom outside the TMX bounds by using maxZoom
		this.mCamera.setZoomFactor(Math.min(
						Math.max(maxZoom, this.mPinchZoomStartedCameraZoomFactor
                        * pZoomFactor), 
                        2));
		this.mCamera.setBounds(0, 0, tmxLayer.getWidth(), tmxLayer.getHeight());
	}

	@Override
	public void onPinchZoomFinished(PinchZoomDetector pPinchZoomDetector,
			TouchEvent pTouchEvent, float pZoomFactor) {
	// zoom, but make sure the camera does not zoom outside the TMX bounds by using maxZoom
		this.mCamera.setZoomFactor(Math.min(
                Math.max(maxZoom, this.mPinchZoomStartedCameraZoomFactor
                        * pZoomFactor)
                        ,2));
		if (mCamera.getZoomFactor() == 1.0f) {
			this.mCamera.setBounds(0, (mCamera.getHeight() - tmxLayer.getHeight()), tmxLayer.getWidth(), tmxLayer.getHeight());
			this.mCamera.offsetCenter(0,0);
		}	
	}

	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		final float zoomFactor = this.mCamera.getZoomFactor();
		this.mCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);	
	}
	
	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID,
			float pDistanceX, float pDistanceY) {
		// TODO Auto-generated method stub
	}
	

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		Float x = pSceneTouchEvent.getX();
		Float y = pSceneTouchEvent.getY();
		
	// if the user pinches or dragtouches the screen then...
  	if(this.mPinchZoomDetector != null) {
			
  		this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);
			if(this.mPinchZoomDetector.isZooming()) {
				this.mScrollDetector.setEnabled(false);
			} else if(!towerMove){
				if(pSceneTouchEvent.isActionDown()) {
					this.mScrollDetector.setEnabled(true);
				}
				this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
			}
		
  	} else {
			this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
  	}
		if (pSceneTouchEvent.isActionMove()) {
			if (turretTile.getOnTouched() && !towerMove) {
				turretTile.returnOnTouched();
				/**
				 * TODO See if we actually need a check here
				 */
				//if (currentWave.getPath() == null)
					//currentWave;
				
				towerMove = true;
				dragTower = new TurretTower(x,y, activity.getTurretTowerRegion());		
				dragTower.setZIndex(2);
				dragTower.setScale(0.5f);
				this.attachChild(dragTower);
				this.attachChild(dragTower.getReticle());
			}			
			else if (towerMove) {
				if (pointOnMap(x, y)) {
					TMXTile currentTile = this.tmxLayer.getTMXTileAt(x,y);
					
					dragTower.setPosition(currentTile.getTileX() - TILE_WIDTH/2,
							currentTile.getTileY() - TILE_HEIGHT/2);
					
					if (highlightTile == null) {				
						highlightTile = new Rectangle(currentTile.getTileX(), currentTile.getTileY(), 
								TILE_WIDTH, TILE_HEIGHT, activity.getVertexBufferObjectManager());
						highlightTile.setTag(777);
						highlightTile.setZIndex(1);
						this.attachChild(highlightTile);
						this.sortChildren();
					}
					else {
						highlightTile.setPosition(currentTile.getTileX(), currentTile.getTileY());
					}

					
					if (currentTile.equals(endTile) 
							|| currentTile.equals(startTile) 
							|| blockedTileList.contains(currentTile)) {
						highlightTile.setColor(Color.RED);
					}
					else {
						highlightTile.setColor(Color.BLUE);
					}
					
					//if you drag the dragtower off the map, and then back on, we need to be able to tag it
					//so we can display the highlight tile again
					if (this.getChildByTag(777) == null) {
						this.attachChild(highlightTile);
						this.sortChildren();
					}
				}
				//if point NOT on map
				else {
					if (highlightTile != null){
						highlightTile.detachSelf();
					}
					dragTower.setPosition(pSceneTouchEvent.getX() - dragTower.getWidth()/2,
							pSceneTouchEvent.getY() - dragTower.getHeight()/2);
				}
			}			
		}
		
		else if (pSceneTouchEvent.isActionUp()) {
			
			if (towerMove) {
	  		towerMove = false;
	  		turretTile.returnOnTouched();
	  		
	  		TMXTile currentTile = this.tmxLayer.getTMXTileAt(x,y);

	  		if (currentTile != null && highlightTile != null && highlightTile.getColor().equals(Color.BLUE)) {
	  			//Add the tile to the blocked list
	  			blockedTileList.add(currentTile);
	  			
	  			towers.add(dragTower);
	  			this.detachChild(dragTower.getReticle());
	  			
	  			//If we are in the middle of a wave, the AStarPath class must update
	  			//the path since there is now a new tower on the field
	  			if (aStarHelper.isNavigating()) {
	  				updateAffectedEnemies(currentTile, true);
	  			}else {
	  				updateAffectedEnemies(currentTile, false);
	  			}
	  			
	  			if (currentWave.getPath() == null) {
		  			blockedTileList.remove(currentTile);
		  			removeTower(dragTower);
	  			}
	  			
	  		}
	  		else {
	  			removeTower(dragTower);
	  		}
	  		
	  		if (highlightTile != null) {
	  			highlightTile.detachSelf();
	  			highlightTile = null;
	  		}
			} 		
		} 
		
  	return true;
  }

}
