package com.example.towerdefense;

import java.util.HashMap;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.modifier.IModifier;

import android.util.Log;

public class WaveHelper extends HashMap<Integer, Wave>{
	
	private static final long serialVersionUID = 1L;
	
	private GameScene scene;
	private Wave wave;
	private TMXTile startTile;
	private AStarPathHelper aStarHelper;	
	private TimerHandler timer;
	
	
	
	
	//*************CONSTRUCTOR*******************************//
	
	/**
	 * The constructor of this class initializes all the waves of enemies for the entire game!
	 */
	public WaveHelper() {
		super();
		scene = GameScene.getSharedInstance();
		startTile = scene.getStartTile();
		aStarHelper = scene.getAStarHelper(); 
		
		//this.put(0, new Wave(createEnemyArray(FlameEnemy.class, 3)));
		//this.put(1, new Wave(createEnemyArray(FlameEnemy.class, 5)));
		//this.put(2, new Wave(createEnemyArray(FlameEnemy.class, 7)));
		//this.put(3, new Wave(createEnemyArray(FlameEnemy.class, 9)));
		
		for (int i = 0; i < 100; i++) {
			this.put(i, new Wave(createEnemyArray(FlameEnemy.class, (i+1) *2)));
		}
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
		
		for (int i = 0; i < wave.getEnemies().length; i++) {
			Path beginningPath = new Path(2);
			final Enemy enemy = wave.getEnemies()[i];
			enemy.setPosition(-GameScene.getTileWidth()+(GameScene.getTileWidth()/4), startTile.getTileRow()*GameScene.getTileHeight());
			
			beginningPath.to(enemy.getX(), enemy.getY())
				.to((startTile.getTileColumn()*GameScene.getTileWidth()+(GameScene.getTileWidth()/4)), enemy.getY());
			//PathModifier moveModifier = new PathModifier(enemy.getSpeed(), beginningPath, new PathModifier.IPathModifierListener() {
			
			MoveXModifier moveModifier;
			moveModifier = new MoveXModifier(enemy.getSpeed(), 
					enemy.getX(), (float)(startTile.getTileColumn()*GameScene.getTileWidth()+GameScene.getTileWidth()/4), 
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
			
			while (path == null) {
				Log.i("Setting Path", "Now");
				path = aStarHelper.getPath(null);
				wave.setFullPath(path);
			}
			enemy.setPath(wave.getFullPath().deepCopy());
			enemy.setIndex(i);
		}
			
	}
	
	/**
	 * This method just attaches each enemy to its move modifier and attaches it
	 * to the scene
	 */
	public void startWave() {
		aStarHelper.startWave();
		
		scene.registerUpdateHandler(timer = new TimerHandler(0.5f, true, new ITimerCallback() {

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
			if (enemy.getUserData() == null) {
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
	private Enemy[] createEnemyArray(Class<? extends Enemy> E, int num) {
		
		TextureRegion texture;
		Enemy[] array = null;
		
		if (E == FlameEnemy.class) {
			texture = TowerDefenseActivity.getSharedInstance().getFlameEnemyTextureRegion();	
			array = new FlameEnemy[num];
			for (int i = 0; i < num; i++) {
				array[i] = new FlameEnemy(texture);
			}
		}
		return array;
	}

}
