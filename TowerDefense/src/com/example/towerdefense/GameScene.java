package com.example.towerdefense;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.camera.ZoomCamera;
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
	private static final int[] START_TILE_ID = new int[]{5,0};
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
	
	
	private BottomPanel panel;
	
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
	private TMXTile currentTile;
	private List<TMXTile> blockedTileList;
	private AStarPathHelper aStarHelper;
	
	//Collision detection handler - runs every frame
	private IUpdateHandler collisionDetect;
	
	//Wave fields
	private WaveHelper waveGenerator;
	private Wave currentWave;
	private Integer waveCount;
	
	private Integer deadEnemies;
	private boolean waveFinished;
	private boolean initializedNewWave;
	
	
	//Money!
	private Integer money;
	
	//Lives
	private Integer lives;
	
	
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
				}});
			this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/new_grass_path.tmx");
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
/*				try {
					if (tiles[i][j].getTMXTileProperties(this.mTMXTiledMap).containsTMXProperty("start", "true")) {
						startTile = tiles[i][j];
					}
				}catch (NullPointerException e) {
					//The presence of the TMCTileProperties needs to be handled better by the TMXTile
					  //Ex: hasTMXTileProperties()
				}*/
				if (i == START_TILE_ID[0] && j == START_TILE_ID[1]) {
					startTile = tiles[i][j];
				}
				else if (i == END_TILE_ID[0] && j == END_TILE_ID[1]) {
						endTile = tiles[i][j+1];
						break outer;
				}
			  
			}
		}
		
		money = 10;
		lives = 20;
		
	  //Initializes the HUD
		panel = new BottomPanel(mCamera, mTMXTiledMap);		
		this.attachChild(panel);
		
		turretTile = new TowerTile(activity.getTurretTowerRegion());
		panel.placeTowerAccess(turretTile, 1);
		
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
		panel.placeStartButton(startButton);
		
		//Initializing tower array
		towers = new ArrayList<Tower>();
		
		blockedTileList = new ArrayList<TMXTile>();
		
		aStarHelper = new AStarPathHelper(mTMXTiledMap, endTile);
		waveGenerator = new WaveHelper();
		waveCount = 0;
		deadEnemies = 0;
		
		waveFinished = true;
		initializedNewWave = false;
		
		//Sets up paths/move modifiers of enemies in the first wave
		initializeNextWave();
		
		Log.i("Info", "Dead Enemies: "+deadEnemies + " Finished Enemies: "+ aStarHelper.getNumberOfEnemiesFinished() +
				" Current Wave Length: "+currentWave.getEnemies().length);
		
		collisionDetect = new RunnableHandler() {
			@Override
			public void onUpdate(float pS) {
				collisionDetect();
			}
		};
		
		this.registerUpdateHandler(collisionDetect);
		
		
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
	public TMXTile getEndTile() {
		return this.endTile;
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
	public void incrementDeadCount() {
		this.deadEnemies++;
	}
	public Integer getMoney() {
		return money;
	}
	public boolean canAfford(Integer m) {
		return money - m > 0;
	}
	public void addAmount(Integer cost) {
		money+=cost;
		this.panel.setMoneyText(money);
	}
	public void payAmount(Integer cost) {
		if (cost <= money) {
			money -= cost;
			this.panel.setMoneyText(money);
		}
	}
	public void loseALife() {
		lives--;
		this.panel.setLifeText(lives);
	}
	public Integer getLives() {
		return lives;
	}
	
	/**
	 * Initializes paths and move modifiers of all enemies in the next wave
	 */
	public void initializeNextWave() {		
		Log.i("Here Upping The Count", "Here");
		Log.i("WaveFinished", waveFinished+"");
		waveGenerator.initWave(waveCount%4);
		currentWave = waveGenerator.get(waveCount%4);
		waveCount++;
		panel.setWaveText(waveCount);

		this.aStarHelper.resetNumberOfEnemiesFinished();
		this.aStarHelper.finishWave();
		this.deadEnemies = 0;
		
		for (Enemy enemy:currentWave.getEnemies()) {
			enemy.setUserData(null);
		}
	}
	
	public void removeCurrentTower() {
		try {
			if (inLegitimatePosition(currentTile)) addAmount(dragTower.getCost());
			removeTower(dragTower);
		}catch(Exception e){}
	}
	
	//PRIVATE METHODS
	/**
	 * Attaches the move modifiers to each of the enemies in the next wave 
	 * and attaches the enemies to the scene
	 */
	private void startCurrentWave() {
		if (waveFinished) {
			waveFinished = false;
			initializedNewWave = false;
			waveGenerator.startWave();		
		}
	}
	
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
		if (!waveFinished) {
			for (Tower t:towers) {
				for (Enemy enemy:currentWave.getEnemies()) {
					if (enemy.getUserData() == "dead") continue;
					
					boolean inRange = false;
					if (stillInRange(t)) {
						//enemy = t.getLockedOn();
						//inRange = true;
					}
					
					if(!inRange && t.getSight().contains(enemy.getX(), enemy.getY())) {
						t.setLockedOn(enemy);
						float dx = t.getX()-(enemy.getX()-enemy.getWidthScaled());
						float dy = t.getY()-(enemy.getY()-enemy.getHeightScaled()/2);
						float angle = MathUtils.atan2(dy,dx);
						t.setRotation((float)(angle * (180.0f/Math.PI)));
						t.shoot(enemy);
					}
				}
			}
		}
	}
	
	private boolean stillInRange(Tower t) {
		if (t.getLockedOn() == null || t.getLockedOn().getUserData() == "dead") return false;
	  return t.getSight().contains(t.getLockedOn().getX(), t.getLockedOn().getY());
	}
	
	protected void seeIffWaveFinished() {
		if (((deadEnemies + aStarHelper.getNumberOfEnemiesFinished() == currentWave.getEnemies().length) || waveFinished) == true) {
			Log.i("Ending Wave", "Calculating why...");
			if ((deadEnemies + aStarHelper.getNumberOfEnemiesFinished() == currentWave.getEnemies().length) == true) {
				Log.i("Answer", "Correct Number of Dead Enemies");
			}
			if (waveFinished) {
				Log.i("Answer", "The variable 'waveFinished' is set to true");
			}
		}
		
		if (!initializedNewWave) {
			if  ((deadEnemies + aStarHelper.getNumberOfEnemiesFinished() == currentWave.getEnemies().length)) {
				initializedNewWave = true;
				waveFinished = true;
				initializeNextWave();
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
		
		Enemy[] enemies = currentWave.getEnemies();		
		if (aStarHelper.getPath(enemies[0]) == null) {
			enemies[0].setNeedToUpdatePath(true);
			this.removeCurrentTower();
			return;
		}
		
		
		outer:
		for (Enemy enemy:enemies) {
			Path p = enemy.getPath();
			for(int i = 0; i < p.getCoordinatesX().length; i++) {
				float pX = p.getCoordinatesX()[i];
				float pY = p.getCoordinatesY()[i];
				
				TMXTile tile = this.tmxLayer.getTMXTileAt(pX, pY);
				
				if (current.equals(tile)) {
					updateEnemyPaths(inMiddleOfWave, enemy);
					if(inMiddleOfWave) break;
					break outer;
				}
			}
		}
	}
	
	/**
	 * This method actually notifies which enemies need to update their paths
	 * @param inMiddleOfWave
	 * @param enemy
	 */
	private void updateEnemyPaths(boolean inMiddleOfWave, Enemy enemy) {		
		if (inMiddleOfWave) {
			enemy.setNeedToUpdatePath(true);
		}else {
			Path p = aStarHelper.getPath(currentWave.getEnemies()[0]);
			if (p == null) {
				removeCurrentTower();
				p = aStarHelper.getPath(currentWave.getEnemies()[0]);
			}
			for (Enemy e:currentWave.getEnemies()) {
				e.setPath(p.deepCopy());
			}
		}
	}
	
	private boolean inLegitimatePosition(TMXTile currentTile) {
		return !(currentTile.equals(endTile) 
		|| currentTile.equals(startTile) 
		|| blockedTileList.contains(currentTile));
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
	}
	

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		Float x = pSceneTouchEvent.getX();
		Float y = pSceneTouchEvent.getY();
		
		currentTile = this.tmxLayer.getTMXTileAt(x,y);
		
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
			if (turretTile.getOnTouched() && !towerMove && canAfford(TurretTower.COST)) {
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

					
					if (!inLegitimatePosition(currentTile)) {
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

	  		if (currentTile != null && highlightTile != null && highlightTile.getColor().equals(Color.BLUE)) {
	  			//Add the tile to the blocked list
	  			blockedTileList.add(currentTile);
	  			
	  			towers.add(dragTower);
	  			this.detachChild(dragTower.getReticle());
	  			
	  			//Nothing is free in this world
					this.payAmount(dragTower.getCost());
	  			
	  			//If we are in the middle of a wave, the AStarPath class must update
	  			//the path since there is now a new tower on the field
	  			if (aStarHelper.isNavigating()) {
	  				updateAffectedEnemies(currentTile, true);
	  			}else {
	  				updateAffectedEnemies(currentTile, false);
	  			}
	  		}
	  		
	  		else {
	  			removeCurrentTower();
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
