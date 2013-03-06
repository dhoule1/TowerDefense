package com.example.towerdefense;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;

import android.util.Log;

/**
 * This moves the towers to the selected tile
 * 
 * @author Dan
 * 
 */
public class AStarPathHelper {
	
	private GameScene scene;

	// TMX variables
	private TMXTile mFinalPosition;

	// Paths
	private org.andengine.util.algorithm.path.Path aStarPath;
	private MyAStarPathFinder mAStarPathFinder;
	protected int mWayPointIndex;
	private boolean mHasFinishedPath;
	private int currentlyFinished;

	// =================Constructor===================
	public AStarPathHelper(TMXTiledMap pTiledMap, TMXTile endTile) {
		
		scene = GameScene.getSharedInstance();
		
		mFinalPosition = endTile;

		mHasFinishedPath = true;

		mWayPointIndex = 0;
		currentlyFinished = 0;

		// Create the needed objects for the AStarPathFinder
		//mAStarPathFinder = new MyAStarPathFinder<TMXLayer>();
		mAStarPathFinder = new MyAStarPathFinder();

	}

	// ===================Public Methods==================================
	
	//Getters and Setters
	public boolean isNavigating() {
		return !mHasFinishedPath;
	}

	public void startWave() {
		mHasFinishedPath = false;
	}
	public void finishWave() {
		mHasFinishedPath = true;
		currentlyFinished = 0;
	}
	
	public int getNumberOfEnemiesFinished() {
		return currentlyFinished;
	}
	public void doneWithPath() {
		mHasFinishedPath = true;
	}
	
	/*
	 * This method moves the sprite to the designated location
	 */
	public Path getPath(Enemy enemy) {
		
		aStarPath = this.mAStarPathFinder.findPath(enemy);

		// Update parameters
		mWayPointIndex = 0;

		// Only loads the path if the AStarPath is not null
			return loadPathFound(enemy.getOffsetX(), enemy.getOffsetY());
	}

	/**
	 * Recursive method that moves each enemy one block at a time
	 * @param enemy
	 * @return
	 */
	public boolean moveEntity(final Enemy enemy) {		
		if (enemy.isDead() || enemy.getUserData() == "dead") return true;
		
		Path path = enemy.getPath();			
		
		if (path == null) {
			scene.removeCurrentTower(true);
			path = getPath(enemy);
		}
		final Path pPath = path;

		mHasFinishedPath = false;
		// Creates a shorter path to follow
		// create a new path with length 2 from current sprite position to next
		Path shortPath = new Path(2);

		// A path from the enemy's location to the next tile in the sequence
		shortPath.to(enemy.getX(), enemy.getY()).to(
				pPath.getCoordinatesX()[mWayPointIndex + 1],
				pPath.getCoordinatesY()[mWayPointIndex + 1]);

		float TRAVEL_SPEED = enemy.getSpeed();

		// Register a new modifier to move the enemy sprite
		PathModifier moveModifier = new PathModifier(TRAVEL_SPEED, shortPath,
				new PathModifier.IPathModifierListener() {

					@Override
					public void onPathWaypointStarted(PathModifier pPathModifier,
							IEntity pEntity, int pWaypointIndex) {
						// Keep the wayPointIndex in a Global Var
						mWayPointIndex = pWaypointIndex;
					}

					@Override
					public void onPathWaypointFinished(PathModifier pPathModifier,
							IEntity pEntity, int pWaypointIndex) {
					}

					@Override
					public void onPathStarted(PathModifier pPathModifier, IEntity pEntity) {
					}

					@Override
					public void onPathFinished(PathModifier pPathModifier, IEntity pEntity) {
						// If the enemy is not on the final tile of the path then refresh
						// the astar path and do the
						
						if (enemy.getX() ==  mFinalPosition.getTileColumn() * GameMap.getTileSize() - GameMap.getTileSize()/4&&
								enemy.getY() == mFinalPosition.getTileRow() * GameMap.getTileSize() - GameMap.getTileSize()/4){
							Log.i("", "FINISH");
							mWayPointIndex = 0;
							enemy.destroy();
							currentlyFinished++;
							
							scene.loseALife(enemy.getEnemyCount());
							scene.showRedScreen();
							scene.seeIfWaveFinished();
						}
						else {
							mHasFinishedPath = false;

							if (enemy.isNeedToUpdatePath()) {
								enemy.setNeedToUpdatePath(false);
								enemy.setPath(getPath(enemy));
								moveEntity(enemy);
							} else {
								float[] x = new float[(int) pPath.getCoordinatesX().length - 1];
								float[] y = new float[(int) pPath.getCoordinatesY().length - 1];
								Path p = null;
								try {
									for (int i = 1; i < pPath.getCoordinatesX().length; i++) {
										x[i - 1] = pPath.getCoordinatesX()[i];
										y[i - 1] = pPath.getCoordinatesY()[i];
									}
									p = new Path(x, y);
								} catch (Exception e) {
									p = getPath(enemy);
								}
								enemy.setPath(p);
								moveEntity(enemy);
							}
						}

					}
				});
		moveModifier.setAutoUnregisterWhenFinished(true);
		enemy.registerEntityModifier(moveModifier);
		enemy.setPathModifier(moveModifier);
		enemy.returnSpeedToNormal();

		return mHasFinishedPath;
	}
	
	
	
	//*************************Private Methods**************************************//
	
	private Path loadPathFound(float offX, float offY) {
		if (aStarPath == null)
			return null;
		Path current = new Path(aStarPath.getLength());
		
		for (int i = 0; i < aStarPath.getLength(); i++) {
			current.to( aStarPath.getX(i) * GameMap.getTileSize() + offX,
					aStarPath.getY(i) * GameMap.getTileSize() + offY);
		}
		return current;
	}
}
