package com.example.towerdefense;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.primitive.Vector2;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
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
import org.andengine.opengl.util.GLState;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.math.MathUtils;

import android.util.Log;

import com.example.towerdefense.GameMap.MapType;
import com.example.towerdefense.SceneManager.SceneType;

public class GameScene extends BaseScene implements IOnSceneTouchListener, IScrollDetectorListener, IPinchZoomDetectorListener{
	
	private GameMap gameMap;
	
	//Reference of Activity context
	private TowerDefenseActivity activity;
	
	private ResourceManager resourceManager;
	
	//Makes this a singleton class (only one instance at a time)
	private static GameScene instance;
	
	//Variables to set up the zoom camera
	private ZoomCamera mCamera;
	
	private SurfaceScrollDetector mScrollDetector;
	private PinchZoomDetector mPinchZoomDetector;
	private float mPinchZoomStartedCameraZoomFactor;
	private float maxZoom;
	
	//The map and layer of the field
	private TMXTiledMap mTMXTiledMap;
	private TMXLayer tmxLayer;
	
	//Provides all the text and images for the HUD within this scene
	private BottomPanel panel;
	
	//Turret tower option on the HUD
	private TowerTile turretTile;
	
	//Dart tower option on the HUD
	private TowerTile dartTile;
	
	//Flame tower option on the HUD
	private TowerTile flameTile;
	
	//Ice tower option on the HUD
	private TowerTile iceTile;
	
	//Spike Tower option on the HUD
	private TowerTile spikeTile;
	
	//Start button on the HUD
	private AnimatedSprite startButton;	
	
	//The tower in 'limbo' when it is dragged from the HUD to the scene itself
	private ITower dragTower;
	//Highlights the tile underneath the dragTower
	private Rectangle highlightTile;
	//To make sure you only drag one tower at a time
	private boolean towerMove;
	
	//List of towers on the field
	private List<ITower> towers;
	
	//Saves the coordinates when the scene is first touched
	private Vector2 downCoords;
	
	//A-Star path variables
	private TMXTile startTile;
	private TMXTile endTile;
	private TMXTile currentTile;
	private List<TMXTile> blockedTileList;
	private AStarPathHelper aStarHelper;
	
	//Collision detection handler - runs every frame
	//private IUpdateHandler collisionDetect;
	private TimerHandler collisionDetect;
	float dx;
	float dy;
	float angle;
	float realAngle;
	
	//Enemy queue handler - updates the enemy queues
	private TimerHandler enemyQueues;
	
	//Wave fields
	private WaveHelper waveGenerator;
	private Wave currentWave;
	private Integer waveCount;
	
	//Saves the number of dead enemies in the current path
	private Integer deadEnemies;
	
	//boolean to check if the wave is finished
	private boolean waveFinished;
	
	//makes sure we do not initialize the next wave twice in a row
	private boolean initializedNewWave;
	
	//Money!
	private Integer money;
	
	//Lives
	private Integer lives;
	
	//Originally set at .5, and then increases to 1.0 during 'fast-forward' mode
	private float speedFactor;
	//checks to see if the next wave has already be initialized
	//so that the player can't hit the 'play' button again
	private boolean readyToPressAgain;
	
	//Object pool for darts
	private DartBulletPool dartBulletPool;
	
	//Object pool for icicles
	private IciclePool iciclePool;
	
	//list of the enemies within the current wave
	private CopyOnWriteArrayList<Enemy> enemies;
	
	
	//***********************************************************
	//CONSTRUCTOR
	//***********************************************************	
	public GameScene(MapType type) {	
		instance = this;
		
		gameMap = new GameMap(type);
		
		//Zoom-Camera configuration
		this.setOnAreaTouchTraversalFrontToBack();
		this.mScrollDetector = new SurfaceScrollDetector(this);
		
		activity = TowerDefenseActivity.getSharedInstance();
		resourceManager = ResourceManager.getInstance();
		
		if (MultiTouch.isSupported(activity)) {
			this.mPinchZoomDetector = new PinchZoomDetector(this);
		} else {
			this.mPinchZoomDetector = null;
		}
		
		this.setOnSceneTouchListener(this);
		this.setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		
		String map = "";
		
		if (type == MapType.DESERT) map = "tmx/new_desert_path.tmx";
		else if (type == MapType.GRASS) map = "tmx/grass_path.tmx";
		
		try {
			final TMXLoader tmxLoader = new TMXLoader(activity.getAssets(), activity.getEngine().getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, activity.getVertexBufferObjectManager(), new ITMXTilePropertiesListener() {
				@Override
				public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap, final TMXLayer pTMXLayer, final TMXTile pTMXTile, final TMXProperties<TMXTileProperty> pTMXTileProperties) {
				}});
			this.mTMXTiledMap = tmxLoader.loadFromAsset(map);
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
		outer: for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				
				int[] start = gameMap.getStartTile();
				int[] end = gameMap.getEndTile();
				
				if (i == start[0] && j == start[1]) {
					startTile = tiles[i][j];
				}
				else if (i == end[0] && j == end[1]) {
						endTile = tiles[i][j];
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
		
		iceTile = new TowerTile(resourceManager.getIceTowerRegion());
		panel.placeTowerAccess(iceTile, 2);
		
		dartTile = new TowerTile(resourceManager.getDartTowerRegion());
		panel.placeTowerAccess(dartTile, 3);
		
		flameTile = new TowerTile(resourceManager.getFlameTowerRegion());
		panel.placeTowerAccess(flameTile, 4);
		
		spikeTile = new TowerTile(resourceManager.getSpikeTowerRegion());
		panel.placeTowerAccess(spikeTile, 5);
		
		startButton = new AnimatedSprite(0.0f,
				0.0f, resourceManager.getStartButtonRegion(), activity.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				
				if (readyToPressAgain) {
					startCurrentWave();
					
					readyToPressAgain = false;
					this.registerUpdateHandler(new TimerHandler(1.0f, new ITimerCallback() {
			
						@Override
						public void onTimePassed(TimerHandler pTimerHandler) {
							readyToPressAgain = true;
							unregisterUpdateHandler(pTimerHandler);					
						}
						
					}));
				}
				
				panel.detachTowerTextDescription();
						
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
		towers = new ArrayList<ITower>();
		
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
		
		speedFactor = 0.5f;
		readyToPressAgain = true;
		
		//collisionDetect = new TimerHandler((float)0.0125/speedFactor, true, new ITimerCallback() {
		
		collisionDetect = new TimerHandler(0.025f, true, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				collisionDetect();
			}
			
		});		
		
		enemyQueues = new TimerHandler(0.3f, true, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				addEnemiesToTowerQueues();
			}
		});

/*		SoccerballEnemy enemy = new SoccerballEnemy(resourceManager.getSoccerballRegion());
		enemy.setPosition(TILE_WIDTH*5 - TILE_WIDTH/4, TILE_HEIGHT*5 - TILE_HEIGHT/4);
		Sprite icicle = new Sprite(0.0f,0.0f,resourceManager.getIcicleRegion(),resourceManager.getVbom());
		icicle.setScale(0.2f);
		icicle.setPosition(enemy.getX() - enemy.getWidthScaled()*3.2f, enemy.getY() - enemy.getHeightScaled()*3.5f);
		this.attachChild(enemy);
		this.attachChild(icicle);*/
		
	}
	//**********************************************************
	//USER-CREATED METHODS
	//**********************************************************
	
	//PUBLIC METHODS
	
	//Getters and Setters and Misc. Methods
	public static GameScene getSharedInstance() {
		return instance;
	}
	public GameMap getGameMap() {
		return this.gameMap;
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
	public List<ITower> getTowers() {
		return towers;
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
	public MapType getMapType() {
		return this.gameMap.getMapType();
	}
	public void setDartBulletPool(DartBulletPool pool) {
		this.dartBulletPool = pool;
	}
	public DartBulletPool getDartBulletPool() {
		return this.dartBulletPool;
	}
	public void setIciclePool(IciclePool pool) {
		this.iciclePool = pool;
	}
	public IciclePool getIciclePool() {
		return this.iciclePool;
	}
	
	public void showRedScreen() {
		final Sprite redScreen = new Sprite(0.0f,0.0f,ResourceManager.getInstance().getRedScreen(),ResourceManager.getInstance().getVbom()) {
	      @Override
	      protected void preDraw(GLState pGLState, Camera pCamera) 
	      {
	          super.preDraw(pGLState, pCamera);
	          pGLState.enableDither();
	      }
		};
		
		this.attachChild(redScreen);
		
		this.registerUpdateHandler(new TimerHandler(0.25f, new ITimerCallback() {
			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				redScreen.detachSelf();
				unregisterUpdateHandler(pTimerHandler);					
			}
			
		}));
		
	}
	
	/**
	 * Initializes paths and move modifiers of all enemies in the next wave
	 */
	public void initializeNextWave() {	
		
		Log.i("Init wave", "here");
		
		//If the previous wave was eliminated even before all enemies technically started, the time handler
		//that handles their first move modifier would never get unregistered, putting the game in limbo
		this.unregisterUpdateHandler(waveGenerator.getTimeHandler());
		
		if (startButton.getCurrentTileIndex() > 1) startButton.setCurrentTileIndex(startButton.getCurrentTileIndex()-2);
		
		waveGenerator.initWave(waveCount);
		currentWave = waveGenerator.get(waveCount);
		enemies = (CopyOnWriteArrayList<Enemy>)currentWave.getEnemies();
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
		for (ITower tower:towers) {
			tower.onWaveEnd();
		}
	}
	
	/**
	 * Removes a tower from the field
	 * @param Tower t
	 */
	public void removeTower(final ITower t, boolean removeTile) {
	  TMXTile tile = tmxLayer.getTMXTileAt(t.getEntity().getX() + GameMap.getTileSize()/2, t.getEntity().getY() + GameMap.getTileSize()/2);
	  
		if (blockedTileList.contains(tile) && removeTile) {
			blockedTileList.remove(tile);
		}
		activity.runOnUpdateThread(new Runnable() {

			@Override
			public void run() {
				t.getEntity().detachSelf();
				SubMenuManager.getReticle(t).detachSelf();
				t.getEntity().clearEntityModifiers();
				t.getEntity().clearUpdateHandlers();
			}	
		});
		if (towers.contains(t)) {
			towers.remove(t);
		}
		
		//if the user manually deleted the tower, we need to adjust for that for the paths of the enemies
		if (aStarHelper.isNavigating())
			for (Enemy e:currentWave.getEnemies())
			 updateEnemyPaths(true,e);
		else
			updateEnemyPaths(false,null);
	}	
	
	/**
	 * Removes the dragtower and the currentTile
	 */
	public void removeCurrentTower(boolean removeTile) {
		if (removeTile) this.addAmount(dragTower.getCost());
		removeTower(dragTower, removeTile);
	}
	
	/**
	 * Checks to see if the wave has commenced. Should only fire once per wave
	 * even though there are 2 distinct ways to end a wave.
	 * If the wave has ended, it calls the method to initialize the next wave.
	 */
	public void seeIfWaveFinished() {		
		if (!initializedNewWave) {
			if  ((deadEnemies + aStarHelper.getNumberOfEnemiesFinished() == currentWave.getEnemies().size())) {
				initializedNewWave = true;
				waveFinished = true;
				endInWaveUpdateHandlers();
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
	
	private void toggleSpeedFactor() {
		speedFactor = (speedFactor == 0.5f) ? 1.0f : 0.5f;
		
		int startIndex = startButton.getCurrentTileIndex();
		
		if (startIndex < 2) startButton.setCurrentTileIndex((startIndex+1)%2);
		else {
			if (startIndex == 2) startButton.setCurrentTileIndex(3);
			else startButton.setCurrentTileIndex(2);
		}
	}
	
	/**
	 * Attaches the move modifiers to each of the enemies in the next wave 
	 * and attaches the enemies to the scene
	 */
	private void startCurrentWave() {
		if (waveFinished) {
			startButton.setCurrentTileIndex(startButton.getCurrentTileIndex()+2);
			Log.i("START WAVE", "START WAVE");
			waveFinished = false;
			initializedNewWave = false;
			waveGenerator.startWave();
			startInWaveUpdateHandlers();
			Log.i("Registering Timer Handler", "NOW");
		} else {
			toggleSpeedFactor();
		}
	}
	
	private void startInWaveUpdateHandlers() {
		this.registerUpdateHandler(collisionDetect);
		this.registerUpdateHandler(enemyQueues);
	}
	private void endInWaveUpdateHandlers() {
		this.unregisterUpdateHandler(collisionDetect);
		this.unregisterUpdateHandler(enemyQueues);
	}
	
	/**
	 * Checks to see if a x,y coordinate is on the TMXTiledMap
	 * @param x
	 * @param y
	 * @return isOnMap
	 */
	private boolean pointOnMap(float x, float y) {
		return (x < this.mTMXTiledMap.getTileColumns()*GameMap.getTileSize()
				&& y < this.mTMXTiledMap.getTileRows()*GameMap.getTileSize());
	}
	
	private void addEnemiesToTowerQueues() {
		
		for (ITower tower:towers) {
			tower.clearQueue();
			
			for (Enemy enemy:enemies) {
				if (enemy.isDead() || enemy.getUserData() == "dead") continue;		
				
				if (tower.inSights(enemy.getXReal(), enemy.getYReal())) {
						tower.addEnemyToQueue(enemy);
				}
			}
		}
	}
	
	/**
	 * Each tower fires at the first enemy in their respective queue
	 * Runs every .025 seconds
	 */
	private void collisionDetect() {
		
		for (ITower t:towers) {
		  Enemy enemy = t.getLockedOn();
			
			if (enemy == null) {
				t.onIdleInWave();
				continue;
			}
				
			if (enemy.isDead()) {
				t.removeEnemyFromQueue(enemy);
				continue;
			}

			if(t.inSights(enemy.getXReal(), enemy.getYReal())) {
				
				t.onImpact(enemy);
				t.shoot(enemy);
				
				/**
				 * TODO: Make a "canRotate" variable for each class so this isn't such a hack
				 */
				if (t.getClass() == IceTower.class || t.getClass() == SpikeTower.class) continue;
				
				dx = t.getEntity().getX()-(enemy.getX()-enemy.getWidthScaled()/3.5f);
				dy = t.getEntity().getY()-(enemy.getY()-enemy.getHeightScaled()/3.5f);
				angle = MathUtils.atan2(dy,dx);
				realAngle = (float)(angle * (180.0f/Math.PI));
				t.getEntity().setRotation(realAngle);
			} else {
				t.removeEnemyFromQueue(enemy);
				t.onEnemyOutOfRange(enemy);
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
		List<Enemy> enemies = currentWave.getEnemies();			
		Path p;
		float pX;
		float pY;
		outer:
		for (Enemy enemy:enemies) {
			if (enemy == null) continue;
			p = enemy.getPath();
			for(int i = 0; i < p.getCoordinatesX().length; i++) {
				pX = p.getCoordinatesX()[i];
				pY = p.getCoordinatesY()[i];
				
				TMXTile tile = this.tmxLayer.getTMXTileAt(pX-enemy.getOffsetX(), pY-enemy.getOffsetY());
				if (current.equals(tile)) {
					updateEnemyPaths(inMiddleOfWave, enemy);
					if(!inMiddleOfWave) break outer;
					else break;
				}
			}
		}
		
		if (inMiddleOfWave) {
			Enemy dummy = new Enemy();
			dummy.setUserData("dummy");
			Path path = aStarHelper.getPath(dummy);
			
			if (path == null) {
				removeCurrentTower(true);
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
	private Class<?> pointOnTile(int type) {
		for (TowerTile tile:panel.getTiles()) {
			
			if (type == TouchEvent.ACTION_DOWN)  {
				if (tile.getOnTouched())
					return tile.getTowerClass();
			}
			
			
			else if (type == TouchEvent.ACTION_MOVE) {
				if (tile.getOnMoved())
					return tile.getTowerClass();
			}
		}
		return null;
	}
	
	private ITower pointOnExistingTower(float x, float y) {
		for (int i = 0; i < towers.size(); i++) {
			ITower t = towers.get(i);
			if (t.getEntity().contains(x, y)) return t;
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
	public void onManagedUpdate(float p) {
		super.onManagedUpdate(p*speedFactor);
	}
	
	@Override
	public void onPinchZoomStarted(PinchZoomDetector pPinchZoomDetector,
			TouchEvent pSceneTouchEvent) {
		this.mPinchZoomStartedCameraZoomFactor = this.mCamera.getZoomFactor();
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
		
		if (mCamera.getZoomFactor() <= 1.0006f) {
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
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		final Float x = pSceneTouchEvent.getX();
		final Float y = pSceneTouchEvent.getY();
		
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
  		
  		panel.detachTowerUpgradeDeleteText();
  		panel.detachTowerTextDescription();
  		panel.attachTowerTextDescription(pointOnTile(TouchEvent.ACTION_DOWN));
  	}
  	
  	Class<?> tClass;
  	if (pSceneTouchEvent.isActionMove()) {
			
			tClass = pointOnTile(TouchEvent.ACTION_MOVE);
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
				else if (tClass.equals(IceTower.class) && canAfford(IceTower.COST)) {
					dragTower = new IceTower(x,y,resourceManager.getIceTowerRegion());
				}
				else if (tClass.equals(SpikeTower.class) && canAfford(SpikeTower.COST)) {
					dragTower = new SpikeTower(x,y,resourceManager.getSpikeTowerRegion());
				}
				else towerMove = false;
				
				if (dragTower != null) {
					dragTower.getEntity().setZIndex(2);
					dragTower.getEntity().setScale(0.5f);
					
					activity.runOnUpdateThread(new Runnable() {
						@Override
						public void run() {
							attachChild(dragTower.getEntity());
							
							try {attachChild(SubMenuManager.getReticle(dragTower));} catch(Exception e){}
						}
					});
					tClass = null;
				}
				
			}
						
			//Moving an active drag tower
			else if (towerMove) {
				if (pointOnMap(x, y)) {
					
					dragTower.setPosition(currentTile.getTileX() - GameMap.getTileSize()/2,
							currentTile.getTileY() - GameMap.getTileSize()/2);
					
					if (highlightTile == null) {				
						highlightTile = new Rectangle(currentTile.getTileX(), currentTile.getTileY(), 
								GameMap.getTileSize(), GameMap.getTileSize(), activity.getVertexBufferObjectManager());
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
					dragTower.setPosition(pSceneTouchEvent.getX() - dragTower.getEntity().getWidth()/2,
							pSceneTouchEvent.getY() - dragTower.getEntity().getHeight()/2);
				}
			}			
		}
		
		else if (pSceneTouchEvent.isActionUp()) {
			this.sortChildren();
			
			if (towerMove) {
	  		towerMove = false;
	  		turretTile.returnOnTouched();

	  		if (currentTile != null && highlightTile != null && highlightTile.getColor().equals(Color.BLUE)) {	  			
	  			
	  			//Add the tile to the blocked list
	  			blockedTileList.add(currentTile);
	  			
	  			towers.add(dragTower);
	  			
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
					updateAffectedEnemies(currentTile, aStarHelper.isNavigating());
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
				
				final ITower tower = pointOnExistingTower(x,y);
				if (tower != null) {
					this.attachChild(SubMenuManager.display(tower));
					panel.attachTowerUpgradeDeleteText(tower);
					
					if (Math.abs(camera.getYMin()-0.0f) < 0.00005f) {
						
						final float displacement = tower.getRadius() - tower.getEntity().getHeightScaled()/2;
						if (tower.getY() == -20.0f) {
							camera.set(camera.getXMin(), camera.getYMin()-displacement, camera.getXMax(), camera.getYMax()-displacement);
						} else if (tower.getY() == 340.0f) {
							camera.set(camera.getXMin(), camera.getYMin()+displacement, camera.getXMax(), camera.getYMax()+displacement);
						}
					}
				}
				
			}
		} 	
  	return true;
  }

	@Override
	public void createScene() {	}

	@Override
	public void onBackKeyPressed() {
		if (this.hasChildScene()) {
			this.clearChildScene();
			this.setChildrenIgnoreUpdate(false);
			this.attachChild(panel);
			this.setOnSceneTouchListener(this);
		}
		else {
			this.setChildrenIgnoreUpdate(true);
			this.setChildScene(new InGameMenuScene(this.camera));
			this.setOnSceneTouchListener(null);
		}
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {		
		camera.setHUD(null);
		ResourceManager.getInstance().unloadGameScene();
	}
}
