package com.example.towerdefense;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.modifier.IModifier;

import android.util.Log;

import com.example.towerdefense.GameMap.StartSide;

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
	  
	  for (int i = 1; i <= 70; i++) {
	  	
	  	//if (i != 0 && i%4 == 0) this.put((i-1),new Wave(createEnemyArray(FootballEnemy.class, i), (i < 50) ? (float)(50-i)/100 : (float)1/50));
	  	//else if (i != 0 && i%7 == 0) this.put((i-1), new Wave(createEnemyArray(BeachballEnemy.class, (i/7)), (float)75/100));
	  	if (i < 15) {
	  		if (i == 0) this.put((i-1),new Wave(createEnemyArray(SoccerballEnemy.class, i), (float)(100-i)/100));
	  		else if (i%3 == 0) this.put((i-1),new Wave(createEnemyArray(FootballEnemy.class, i), (float)(50-i)/100));
	  		else if (i%3 == 1) this.put((i-1),new Wave(createEnemyArray(SoccerballEnemy.class, i), (float)(100-i)/100));
	  		else this.put((i-1),new Wave(createEnemyArray(BeachballEnemy.class, i), (float)200/100));
	  	}
	  	
	  	else {
	  		int diversity = (i <= 23) ? 25-i : 2;
	  		int mod = i%6;
	  		
	  		switch (mod) {
	  		case (0) : this.put((i-1), new Wave(createDiverseEnemyArray(SoccerballEnemy.class, FootballEnemy.class, i, diversity), (i < 47) ? (float)(50-i)/100 : (float)3/50));
	  		break;
	  		case (1) : this.put((i-1), new Wave(createDiverseEnemyArray(SoccerballEnemy.class, BasketballEnemy.class, i, diversity), (float)(100-i)/100));
	  		break;
	  		case (2) : this.put((i-1), new Wave(createDiverseEnemyArray(SoccerballEnemy.class, BeachballEnemy.class, i, diversity*3), (float)(100-i)/100));
	  		break;
	  		case (3) : this.put((i-1), new Wave(createDiverseEnemyArray(FootballEnemy.class, BasketballEnemy.class, i, diversity), (float)(100-i)/100));
	  		break;
	  		case (4) : this.put((i-1), new Wave(createDiverseEnemyArray(FootballEnemy.class, BeachballEnemy.class, i, diversity*3), (float)(100-i)/100));
	  		break;
	  		case (5) : this.put((i-1), new Wave(createDiverseEnemyArray(BeachballEnemy.class, BasketballEnemy.class, i, diversity), (float)(100-i)/100));
	  		break;
	  		}
	  	}
	  }
	  
	  IciclePool icePool = new IciclePool(ResourceManager.getInstance().getIcicleRegion());
	  DartBulletPool dartPool = new DartBulletPool(ResourceManager.getInstance().getDartBulletRegion());
	  
	  scene.setIciclePool(icePool);
	  scene.setDartBulletPool(dartPool);
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
		
		float size = GameMap.getTileSize();
		StartSide side = scene.getGameMap().getSide();
		float fromX = 0.0f;
		float fromY = 0.0f;
		
		if (side.compareTo(StartSide.LEFT) == 0) {
			fromX = -size - size/4;
			fromY = startTile.getTileRow() * size - size/4;
		}
		else if (side.compareTo(StartSide.UP) == 0) {
			fromX = startTile.getTileColumn() * size - size/4;
			fromY = -size - size/4;
		}	
		
		for (int i = 0; i < wave.getEnemies().size(); i++) {
			final Enemy enemy = wave.getEnemies().get(i);	

			enemy.setPosition(fromX,fromY);
			
			MoveModifier moveModifier = new MoveModifier(enemy.getSpeed(), enemy.getX(), startTile.getTileColumn()*size - size/4, 
					enemy.getY(), startTile.getTileRow() * size - size/4, new IEntityModifier.IEntityModifierListener() {

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
			if (enemy.getClass() == BeachballEnemy.class) {
				enemy.setPath(aStarHelper.getPath(enemy));
			}
			else {
				enemy.setPath(wave.getFullPath().deepCopy());
			}
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
	private CopyOnWriteArrayList<Enemy> createEnemyArray(Class<? extends Enemy> E, int num) {
		
		TiledTextureRegion texture;
		CopyOnWriteArrayList<Enemy> enemies = new CopyOnWriteArrayList<Enemy>();
		
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
		else if (E == FootballEnemy.class) {
			texture = ResourceManager.getInstance().getFootballEnemyRegion();
			for (int i = 0; i < num; i++) {
				enemies.add(new FootballEnemy(texture));
			}
		}
		else if (E == BeachballEnemy.class) {
			texture = ResourceManager.getInstance().getBeachballRegion();
			for (int i = 0; i < num; i++) {
				enemies.add(new BeachballEnemy(texture));
			}
		}
		return enemies;
	}
	
	private CopyOnWriteArrayList<Enemy> createDiverseEnemyArray(Class<? extends Enemy> E1, Class<? extends Enemy> E2, int num, int diversity) {
		CopyOnWriteArrayList<Enemy> enemies = new CopyOnWriteArrayList<Enemy>();
		
		ResourceManager resource = ResourceManager.getInstance();
		TiledTextureRegion soccerR = resource.getSoccerballRegion();
		TiledTextureRegion basketR = resource.getBasketballRegion();
		TiledTextureRegion footR = resource.getFootballEnemyRegion();
		TiledTextureRegion beachR = resource.getBeachballRegion();
		
		for (int i = 0; i < num; i++) {
			boolean second = (i != 0 && (double)i%diversity == 0);
			
			if (!second) {	
				if (E1 == SoccerballEnemy.class) {
				  enemies.add(new SoccerballEnemy(soccerR));
				}else if (E1 == BasketballEnemy.class) {
					enemies.add(new BasketballEnemy(basketR));
				}else if (E1 == FootballEnemy.class) {
					enemies.add(new FootballEnemy(footR));
				}else if (E1 == BeachballEnemy.class) {
					enemies.add(new BeachballEnemy(beachR));
				}
			}else {
				if (E2 == SoccerballEnemy.class) {
				  enemies.add(new SoccerballEnemy(soccerR));
				}else if (E2 == BasketballEnemy.class) {
					enemies.add(new BasketballEnemy(basketR));
				}else if (E2 == FootballEnemy.class) {
					enemies.add(new FootballEnemy(footR));
				}else if (E2 == BeachballEnemy.class) {
					enemies.add(new BeachballEnemy(beachR));
				}
			}
		}
		
		return enemies;
	}
}
