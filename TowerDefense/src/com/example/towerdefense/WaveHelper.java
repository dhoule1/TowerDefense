package com.example.towerdefense;

import java.util.HashMap;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.util.modifier.IModifier;

import android.util.Log;

public class WaveHelper extends HashMap<Integer, Wave>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GameScene scene;
	private Wave wave;
	private TMXTile startTile;
	private AStarPathHelper aStarHelper;	
	private TimerHandler timer;
	
	public WaveHelper() {
		super();
		scene = GameScene.getSharedInstance();
		startTile = scene.getStartTile();
		aStarHelper = scene.getAStarHelper(); 
		
		this.put(0, new Wave(new Enemy[]{new FlameEnemy(), new FlameEnemy(),new FlameEnemy(), new FlameEnemy(),new FlameEnemy(), new FlameEnemy()}));
	}
	
	public void initWave(Integer count) {
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
							// TODO Auto-generated method stub
							
						}

						@Override
						public void onModifierFinished(final IModifier<IEntity> pModifier,
								IEntity pItem) {
							aStarHelper.moveEntity(enemy);							
						}
				
			});
			moveModifier.setAutoUnregisterWhenFinished(true);
			enemy.setBeginningModifier(moveModifier);
			
			if (path == null) path = aStarHelper.getPath(enemy);
			enemy.setPath(path);
		}
		aStarHelper.setNumEnemies(wave.getEnemies().length);
			
	}
	
	public void startWave() {
		aStarHelper.startWave();
		
		scene.registerUpdateHandler(timer = new TimerHandler(0.5f, true, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				initializeEnemy();
			}
			
		}));
	}
	
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

}
