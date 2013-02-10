package com.example.towerdefense;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.runnable.RunnableHandler;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.primitive.Vector2;
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

import com.example.towerdefense.SceneManager.SceneType;

import android.util.Log;

public class GameScene extends BaseScene implements IOnSceneTouchListener, IScrollDetectorListener, IPinchZoomDetectorListener{
	
	//static fields referring to specific tiles on the map
	private static final int TILE_WIDTH = 40;
	private static final int TILE_HEIGHT = 40;
	private static final int[] START_TILE_ID = new int[]{5,0};
	private static final int[] END_TILE_ID = new int[]{5,18};
	
	//Reference of Activity context
	private TowerDefenseActivity activity;
	
	private ResourceManager resourceManager;
	
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
	
	//Turret tower option on the HUD
	private TowerTile turretTile;
	
	//Dart tower option on the HUD
	private TowerTile dartTile;
	
	//Flame tower option on the HUD
	private TowerTile flameTile;
	
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
	private List<FlameTower> flameTowers;
	
	private Vector2 downCoords;
	
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
		resourceManager = ResourceManager.getInstance();
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
		tmxLayer.setIgnoreUpdate(true);
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
		
		TowerTile.initializeMap();
		turretTile = new TowerTile(resourceManager.getTurretTowerRegion());
		panel.placeTowerAccess(turretTile, 1);
		
		dartTile = new TowerTile(resourceManager.getDartTowerRegion());
		panel.placeTowerAccess(dartTile, 2);
		
		flameTile = new TowerTile(resourceManager.getFlameTowerRegion());
		panel.placeTowerAccess(flameTile, 3);
		
		startButton = new Sprite(0.0f,
				0.0f, resourceManager.getStartButtonRegion(), activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				startCurrentWave();
				return true;
			}
		};
		
		startButton.setScale(0.473372781f);
		panel.placeStartButton(startButton);
		
	  //Getting texture regions for submenu items
		SubMenuManager.getDeleteRegion(resourceManager.getDeleteOptionRegion());
		SubMenuManager.getUpgradeRegion(resourceManager.getUpgradeOptionRegion());
		SubMenuManager.getReticalRegion(resourceManager.getTowerSightRegion());
		
		//Initializing tower array
		towers = new ArrayList<Tower>();
		flameTowers = new ArrayList<FlameTower>();
		
		downCoords = new Vector2();
		
		blockedTileList = new ArrayList<TMXTile>();
		
		aStarHelper = new AStarPathHelper(mTMXTiledMap, endTile);
		waveGenerator = new WaveHelper();
		waveCount = 0;
		deadEnemies = 0;
		
		waveFinished = true;
		initializedNewWave = false;
		
		//Sets up paths/move modifiers of enemies in the first wave
		initializeNextWave();
		
		FlameTower.initialize(resourceManager.getFlameParticleRegion());
		
		Log.i("Info", "Dead Enemies: "+deadEnemies + " Finished Enemies: "+ aStarHelper.getNumberOfEnemiesFinished() +
				" Current Wave Length: "+currentWave.getEnemies().size());
		
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
	
	//Getters and Setters and Misc. Methods
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
	public List<Tower> getTowers() {
		return towers;
	}
	public List<FlameTower> getFlameTowers() {
		return flameTowers;
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
		return money - m >= 0;
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
	public void loseALife(int i) {
		lives-=i;
		this.panel.setLifeText(lives);
	}
	public Integer getLives() {
		return lives;
	}
	
	/**
	 * Initializes paths and move modifiers of all enemies in the next wave
	 */
	public void initializeNextWave() {	
		
		Log.i("Init wave", "here");
		
		//If the previous wave was eliminated even before all enemies technically started, the time handler
		//that handles their first move modifier would never get unregistered, putting the game in limbo
		this.unregisterUpdateHandler(waveGenerator.getTimeHandler());
		
		waveGenerator.initWave(waveCount);
		currentWave = waveGenerator.get(waveCount);
		waveCount++;
		panel.setWaveText(waveCount);

		this.aStarHelper.finishWave();
		this.deadEnemies = 0;
		
		/**
		 * Just for debugging
		 */
		for (Enemy enemy:currentWave.getEnemies()) {
			if (enemy == null) continue;
			enemy.setUserData(null);
			enemy.returnHealthToNormal();
		}
		for (Tower tower:towers) {
			tower.onWaveEnd();
		}
	}
	
	/**
	 * Removes the dragtower and the currentTile
	 */
	public void removeCurrentTower(boolean removeTile) {
		try {
			if (inLegitimatePosition(currentTile)) addAmount(dragTower.getCost());
		}catch(Exception e){}
		removeTower(dragTower, removeTile);
	}
	
	/**
	 * Checks to see if the wave has commenced. Should only fire once per wave
	 * even though there are 2 distinct ways to end a wave.
	 * If the wave has ended, it calls the method to initialize the next wave.
	 */
	public void seeIfWaveFinished() {		
		if (!initializedNewWave) {
			Log.i("Dead Enemies", "Dead: "+deadEnemies+", Done: "+aStarHelper.getNumberOfEnemiesFinished()+" = "+currentWave.getEnemies().size());
			if  ((deadEnemies + aStarHelper.getNumberOfEnemiesFinished() == currentWave.getEnemies().size())) {
				Log.i("In", deadEnemies+" "+aStarHelper.getNumberOfEnemiesFinished()+" "+currentWave.getEnemies().size());
				initializedNewWave = true;
				waveFinished = true;
				initializeNextWave();
			}
		}
	}	
	
	public void addNewEnemyToField(Enemy enemy) {
		aStarHelper.moveEntity(enemy);
		this.attachChild(enemy);
		this.currentWave.getEnemies().add(enemy);
	}
	
	
	//PRIVATE METHODS
	/**
	 * Attaches the move modifiers to each of the enemies in the next wave 
	 * and attaches the enemies to the scene
	 */
	private void startCurrentWave() {
		if (waveFinished) {
			Log.i("START WAVE", "START WAVE");
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
	public void removeTower(final Tower t, boolean removeTile) {
	  TMXTile tile = tmxLayer.getTMXTileAt(t.getX() + TILE_WIDTH/2, t.getY() + TILE_WIDTH/2);
		if (blockedTileList.contains(tile) && removeTile) blockedTileList.remove(tile);
		activity.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				t.detachSelf();
				SubMenuManager.getReticle(t).detachSelf();
				t.clearEntityModifiers();
				t.clearUpdateHandlers();
			}	
		});
		
		if (flameTowers.contains(t)) {
			flameTowers.remove(t);
		}
		if (towers.contains(t)) {
			towers.remove(t);
		}
		
		//if the user manually deleted the tower, we need to adjust for that for the paths of the enemies
		if (aStarHelper.isNavigating())
			for (Enemy e:currentWave.getEnemies())
			 updateEnemyPaths(true,e);
		else
			for (Enemy e:currentWave.getEnemies())
				 updateEnemyPaths(false,e);
	}	
	
	/**
	 * Detects which enemies are in the range of which towers
	 * Updates every frame
	 */
	private void collisionDetect() {
		if (!waveFinished) {
			
			float dx;
			float dy;
			float angle;
			float realAngle;
			
			for (Tower t:towers) {
				for (Enemy enemy:currentWave.getEnemies()) {
					if (enemy == null) continue;
					if (enemy.getUserData() == "dead") continue;
					t.onImpact(enemy);
					if (stillInRange(t)) {
						enemy = t.getLockedOn();
					}

					if(t.inSights(enemy.getXReal(), enemy.getYReal())) {
						t.setLockedOn(enemy);
						dx = t.getX()-(enemy.getX()-enemy.getWidthScaled());
						dy = t.getY()-(enemy.getY()-enemy.getHeightScaled()/2);
						angle = MathUtils.atan2(dy,dx);
						realAngle = (float)(angle * (180.0f/Math.PI));
						t.setRotation(realAngle);
						t.shoot(enemy);
					} else {
						t.onIdleInWave();
					}
				}
			}
		}
	}
	
	/**
	 * Checks to see if the locked-on enemy is still in range
	 * and is not dead
	 * @param Tower t
	 * @return isInRange
	 */
	private boolean stillInRange(Tower t) {
		if (t.getLockedOn() == null || t.getLockedOn().isDead()) return false;
	  return t.lockedOnInSight();
	}		
	
	/**
	 * When placing a tower on the field, this checks 
	 * if the path needs to be updated for each enemy
	 * @param current
	 * @param inMiddleOfWave
	 */
	private void updateAffectedEnemies(TMXTile current, boolean inMiddleOfWave) {
		List<Enemy> enemies = currentWave.getEnemies();			
		float pX;
		float pY;
		outer:
		for (Enemy enemy:enemies) {
			if (enemy == null) continue;
			Path p = enemy.getPath();
			for(int i = 0; i < p.getCoordinatesX().length; i++) {
				pX = p.getCoordinatesX()[i];
				pY = p.getCoordinatesY()[i];
				
				TMXTile tile = this.tmxLayer.getTMXTileAt(pX, pY);
				if (current.equals(tile)) {
					updateEnemyPaths(inMiddleOfWave, enemy);
					if(!inMiddleOfWave) break outer;
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
			Log.i("Updating path", "Updating path");
			enemy.setNeedToUpdatePath(true);
		}else {
			Path p = aStarHelper.getPath(currentWave.getEnemies().get(0));
			if (p == null) {
				removeCurrentTower(true);
				p = aStarHelper.getPath(currentWave.getEnemies().get(0));
			}
			for (Enemy e:currentWave.getEnemies()) {
				if (e == null) continue;
				e.setPath(p.deepCopy());
			}
		}
	}
	
	/**
	 * This just makes sure the drag tower isn't set on the start tile,
	 * end tile, or any tile already occupied by another tower. There is still
	 * the fact that a player can place a tower that will block the enemies in. 
	 * 
	 * I am taking a reactive approach to this scenario since it is just too costly to
	 * recalculate the enemy paths for each tile the dragtower is placed.
	 * @param currentTile
	 * @return
	 */
	private boolean inLegitimatePosition(TMXTile currentTile) {
		return !(currentTile.equals(endTile) 
		|| currentTile.equals(startTile) 
		|| blockedTileList.contains(currentTile));
	}
	
	/**
	 * Makes sure that one of the turret tiles is actually being touched
	 * before it allows the creation of another tower. In order to save some code
	 * the TowerTile class has a static map linking each instance of itself to a subclass of Tower.
	 * This way this method can just return that class, and an instance can be saved right away.
	 * @return Class<? extends Tower>
	 * 
	 * TODO
	 * There is probably a better way to handle this. The static map must be updated each time a new
	 * tower is added to the game. Also...it's ugly O_O
	 */
	private Class<? extends Tower> pointOnTile() {
		for (TowerTile tile:panel.getTiles()) {
			boolean b = tile.getOnTouched();
			if (b) {
				Log.i("Returning class", "Class: "+tile.getTowerClass());
				return tile.getTowerClass();
			}
		}
		return null;
	}
	
	private Tower pointOnExistingTower(float x, float y) {
		for (int i = 0; i < towers.size(); i++) {
			Tower t = towers.get(i);
			if (t.contains(x, y)) return t;
		}
		return null;
	}
	
	private void detachHighlightTile() {
		activity.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				highlightTile.detachSelf();
				highlightTile = null;
			}});
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
	

	/**
	 * TODO
	 * So ugly :(
	 * Maybe make it its own class?
	 */
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
  	
  	if (pSceneTouchEvent.isActionDown()) {
  		downCoords.set(x, y);
  		
  		SubMenuManager.setReticalPosition(-500.0f, -500.0f);
  		SubMenuManager.remove();
  		this.unregisterTouchArea(SubMenuManager.getDeleteSprite());
  	}
  	
  	
  	Class<? extends Tower> tClass;
  	if (pSceneTouchEvent.isActionMove()) {
			
			tClass = pointOnTile();
			if (tClass != null && !towerMove) {
				dragTower = null;
				towerMove = true;
				if (tClass.equals(TurretTower.class) && canAfford(TurretTower.COST)) {
					dragTower = new TurretTower(x,y, resourceManager.getTurretTowerRegion());
				}
				else if (tClass.equals(DartTower.class) && canAfford(DartTower.COST)) {
					dragTower = new DartTower(x,y, resourceManager.getDartTowerRegion());
				}
				else if (tClass.equals(FlameTower.class) && canAfford(FlameTower.COST)) {
					dragTower = new FlameTower(x,y,resourceManager.getFlameTowerRegion());
				}
				else towerMove = false;
				
				if (dragTower != null) {
					dragTower.setZIndex(2);
					dragTower.setScale(0.5f);
					
					activity.runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							attachChild(dragTower);
							attachChild(SubMenuManager.getReticle(dragTower));
						}
					});
					tClass = null;
				}
				
			}
						
			//Moving an active drag tower
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
						detachHighlightTile();
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
	  			if (dragTower.getClass().equals(FlameTower.class)) flameTowers.add((FlameTower)dragTower);
	  			activity.runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							detachChild(SubMenuManager.getReticle(dragTower));
						}
	  			});
	  			
	  			//need to get it out of the scene so that the next dragtower doesn't have to start with it from where the
	  			//previous tower was placed
	  			SubMenuManager.setReticalPosition(-500.0f, -500.0f);
	  			
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
	  			Log.i("Removing Tower", "Removing");
	  			removeCurrentTower(false);
	  		}
	  		
	  		if (highlightTile != null) {
	  			detachHighlightTile();
	  		}
			} else if (Math.abs(downCoords.x - x) < 15.0f &&
					       Math.abs(downCoords.y - y) < 15.0f){
				
				Tower tower = pointOnExistingTower(x,y);
				if (tower != null) {
					this.attachChild(SubMenuManager.display(tower));
				}
				
			}
		} 	
  	return true;
  }

	@Override
	public void createScene() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {		
		camera.setHUD(null);
	}
}
