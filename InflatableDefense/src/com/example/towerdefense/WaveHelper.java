package com.example.towerdefense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.modifier.IModifier;

import android.util.Log;

public class WaveHelper extends HashMap<Integer, Wave>{
	
	private static final long serialVersionUID = 1L;
	
	private GameScene scene;
	private Wave wave;
	private TMXTile startTile;
	private AStarPathHelper aStarHelper;	
	private TimerHandler timer;
	
	
	
	
	//======================================================================
	//CONSTRUCTOR
	//======================================================================
	
	/**
	 * The constructor of this class initializes all the waves of enemies for the entire game!
	 */
	public WaveHelper() {
		super();
		scene = GameScene.getSharedInstance();
		startTile = scene.getStartTile();
		aStarHelper = scene.getAStarHelper(); 
	  
	  for (int i = 1; i <= 100; i++) {
	  	if (i < 15) this.put((i-1),new Wave(createEnemyArray(SoccerballEnemy.class, i), (float)(100-i)/100));
	  	else {
	  		int diversity = (i <= 30) ? 30-i : 2;
	  	  this.put((i-1), new Wave(createDiverseEnemyArray(i, diversity), (float)(100-i)/100));
	  	}
	  }
	  
	  IciclePool icePool = new IciclePool(ResourceManager.getInstance().getIcicleRegion());
	  DartBulletPool dartPool = new DartBulletPool(ResourceManager.getInstance().getDartBulletRegion());
	  
	  GameScene.getSharedInstance().setIciclePool(icePool);
	  GameScene.getSharedInstance().setDartBulletPool(dartPool);
	}
	
	
	
	
	//***************************PUBLIC METHODS*********************************//
	
	/**
	 * Prepares the next wave of enemies beforehand so that they are ready on-hand
	 * @param count
	 */
	public void initWave(Integer count) {
		Log.i("Wave", count+"");
		wave = this.get(count);
		Path path = null;
		
		for (int i = 0; i < wave.getEnemies().size(); i++) {
			final Enemy enemy = wave.getEnemies().get(i);
			enemy.setPosition(-GameScene.getTileWidth()-(GameScene.getTileWidth()/4), startTile.getTileRow()*GameScene.getTileHeight() - GameScene.getTileHeight()/4);
			
			MoveXModifier moveModifier;
			moveModifier = new MoveXModifier(enemy.getSpeed(), 
					enemy.getX(), (float)(startTile.getTileColumn()*GameScene.getTileWidth()-GameScene.getTileWidth()/4), 
					new IEntityModifier.IEntityModifierListener() {

						@Override
						public void onModifierStarted(IModifier<IEntity> pModifier,
								IEntity pItem) {	
						}

						@Override
						public void onModifierFinished(final IModifier<IEntity> pModifier,
								IEntity pItem) {
							aStarHelper.moveEntity(enemy);							
						}
				
			});
			moveModifier.setAutoUnregisterWhenFinished(true);
			enemy.setBeginningModifier(moveModifier);
			Enemy e = null;
			if (path == null) {
				e = new Enemy();
				e.setUserData("dummy");
				Log.i("Setting Path", "Now");
				path = aStarHelper.getPath(e);
				wave.setFullPath(path);
			}
			if (wave.getFullPath() == null) {
			  scene.removeCurrentTower(true);
				wave.setFullPath(aStarHelper.getPath(e));
			}
			enemy.setPath(wave.getFullPath().deepCopy());
			enemy.setIndex(i);
		}
			
	}
	
	public TimerHandler getTimeHandler() {
		return this.timer;
	}
	
	/**
	 * This method just attaches each enemy to its move modifier and attaches it
	 * to the scene
	 */
	public void startWave() {
		aStarHelper.startWave();
		
		scene.registerUpdateHandler(timer = new TimerHandler(wave.getTimeBetweenEnemies(), true, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				initializeEnemy();
			}
			
		}));
	}
	
	
	//*****************PRIVATE METHODS***************************************//
	/**
	 * I needed this in a separate function since this method
	 * gets called with the update handler
	 */
	private void initializeEnemy() {
		Log.i("Init enemy", "init enemy");
		int count = 0;
		for (Enemy enemy:wave.getEnemies()) {
			if (enemy.getUserData() == null && !enemy.hasParent()) {
				enemy.setUserData("Started");
				scene.attachChild(enemy);
				enemy.registerEntityModifier(enemy.getBeginningModifier());
				count++;
				break;
			}
		}
		
		if (count == 0) {
			scene.unregisterUpdateHandler(timer);
		}
	}
	
	/**
	 * Returns an array of enemies from Enemy class and number requested
	 * @param E Enemy
	 * @param int num
	 * @return Enemy[]
	 */
	private List<Enemy> createEnemyArray(Class<? extends Enemy> E, int num) {
		
		TiledTextureRegion texture;
		List<Enemy> enemies = new ArrayList<Enemy>();
		
		if (E == SoccerballEnemy.class) {
			texture = ResourceManager.getInstance().getSoccerballRegion();	
			for (int i = 0; i < num; i++) {
				enemies.add(new SoccerballEnemy(texture));
			}
		}
		else if (E== BasketballEnemy.class) {
			texture = ResourceManager.getInstance().getBasketballRegion();
			for (int i = 0; i < num; i++) {
				enemies.add(new BasketballEnemy(texture));
			}
		}
		return enemies;
	}
	
	private List<Enemy> createDiverseEnemyArray(int num, int diversity) {
		List<Enemy> enemies = new ArrayList<Enemy>();
		
		TiledTextureRegion soccerRegion = ResourceManager.getInstance().getSoccerballRegion();
		TiledTextureRegion basketRegion = ResourceManager.getInstance().getBasketballRegion();
		
		for (int i = 0; i < num; i++) {		
			
			if (i != 0 && (double)i%diversity == 0)
				enemies.add(new BasketballEnemy(basketRegion));
			else 
				enemies.add(new SoccerballEnemy(soccerRegion));
		}
		
		return enemies;
	}
}
