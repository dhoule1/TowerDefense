package com.example.towerdefense;

import java.util.IdentityHashMap;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.extension.tmx.TMXTile;

public class WaveHelper extends IdentityHashMap<Integer, Enemy[]>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private TMXTile startTile;
	
	private AStarPathHelper aStarHelper;
	
	public WaveHelper() {
		super();
		GameScene scene = GameScene.getSharedInstance();
		startTile = scene.getStartTile();
		aStarHelper = scene.getAStarHelper();
		
		this.put(0, new Enemy[]{new FlameEnemy(), new FlameEnemy()});
	}
	
	public void initWave(float count) {
		Enemy[] wave = this.get(count);
		
		Path beginningPath = new Path(2);
		
		for (int i = 0; i < wave.length; i++) {
			final Enemy enemy = wave[i];
			enemy.setPosition(i*-10, startTile.getTileRow()*GameScene.getTileHeight());
			
			beginningPath.to(enemy.getX(), enemy.getY()).to(startTile.getTileColumn()*GameScene.getTileWidth()+(GameScene.getTileWidth()/4), enemy.getY());
			PathModifier moveModifier = new PathModifier(enemy.getSpeed(), beginningPath, new PathModifier.IPathModifierListener() {
				
				@Override
				public void onPathWaypointStarted(PathModifier pPathModifier,
						IEntity pEntity, int pWaypointIndex) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onPathWaypointFinished(PathModifier pPathModifier,
						IEntity pEntity, int pWaypointIndex) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onPathStarted(PathModifier pPathModifier, IEntity pEntity) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onPathFinished(PathModifier pPathModifier, IEntity pEntity) {
					enemy.setPath(aStarHelper.getPath());
					
				}
			});
			enemy.registerEntityModifier(moveModifier);
		}
		
		
	}

}
