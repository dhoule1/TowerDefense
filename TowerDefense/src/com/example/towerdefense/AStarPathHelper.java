package com.example.towerdefense;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.util.Constants;
import org.andengine.util.algorithm.path.ICostFunction;
import org.andengine.util.algorithm.path.IPathFinderMap;
import org.andengine.util.algorithm.path.astar.ManhattanHeuristic;

import android.util.Log;

/**
 * This moves the towers to the selected tile
 * 
 * @author Dan
 * 
 */
public class AStarPathHelper {

	private static final int MAX_SEARCH_DEPTH = 60;

	// TMX variables
	private TMXTiledMap mTiledMap;
	private TMXLayer layer;
	private TMXTile mFinalPosition;

	// Paths
	private org.andengine.util.algorithm.path.Path aStarPath;
	private MyAStarPathFinder<TMXLayer> mAStarPathFinder;
	private ManhattanHeuristic<TMXLayer> mHeuristic;
	private IPathFinderMap<TMXLayer> mPathFinderMap;
	private ICostFunction<TMXLayer> mCostCallback;
	protected int mWayPointIndex;
	private boolean mHasFinishedPath;
	private float currentlyFinished;

	// =================Constructor===================
	public AStarPathHelper(TMXTiledMap pTiledMap, TMXTile endTile) {
		mTiledMap = pTiledMap;
		layer = mTiledMap.getTMXLayers().get(0);
		mFinalPosition = endTile;

		mHasFinishedPath = true;

		mWayPointIndex = 0;
		currentlyFinished = 0;

		// Create the needed objects for the AStarPathFinder
		mAStarPathFinder = new MyAStarPathFinder<TMXLayer>();

		// No special heuristical needed
		mHeuristic = new ManhattanHeuristic<TMXLayer>();

		// Define block behavior
		mPathFinderMap = new IPathFinderMap<TMXLayer>() {

			@Override
			public boolean isBlocked(int pX, int pY, TMXLayer pTMXLayer) {
				/*
				 * This is where collisions are detected
				 */

				try {
					if (GameScene.getSharedInstance().getBlockedList()
							.contains(pTMXLayer.getTMXTile(pX, pY))) {
						return true;
					}
				} catch (Exception e) {
					return true;
				}
				return false;
			}
		};

		// Define how cost is determined. Cost is not used in this astar path
		mCostCallback = new ICostFunction<TMXLayer>() {
			@Override
			public float getCost(IPathFinderMap<TMXLayer> pPathFinderMap, int pFromX,
					int pFromY, int pToX, int pToY, TMXLayer pEntity) {
				return 0;
			}
		};
	}

	// ===================Public Methods==================================
	
	
	
	//Getters and Setters
	public boolean isNavigating() {
		return !mHasFinishedPath;
	}

	public void startWave() {
		mHasFinishedPath = false;
	}

	
	/*
	 * This method moves the sprite to the designated location
	 */
	public Path getPath(Enemy enemy) {

		int fromCol;
		int fromRow;
		int toCol;
		int toRow;

		if (enemy != null) {
			final float[] enemyCoordinates = new float[] { enemy.getX(), enemy.getY() };

			// Get the tile of the enemy
			TMXTile enemyPosition = layer.getTMXTileAt(
					enemyCoordinates[Constants.VERTEX_INDEX_X],
					enemyCoordinates[Constants.VERTEX_INDEX_Y]);

			// These are the parameters used to determine the AStarPath
			fromCol = enemyPosition.getTileColumn();
			fromRow = enemyPosition.getTileRow();
			toCol = mFinalPosition.getTileColumn();
			toRow = mFinalPosition.getTileRow();
		} else {
			TMXTile startTile = GameScene.getSharedInstance().getStartTile();
			fromCol = startTile.getTileColumn();
			fromRow = startTile.getTileRow();
			toCol = mFinalPosition.getTileColumn();
			toRow = mFinalPosition.getTileRow();
		}

		aStarPath = findPathViaPathFinder(fromCol, fromRow, toCol, toRow);

		// Update parameters
		mWayPointIndex = 0;

		// Only loads the path if the AStarPath is not null
		return loadPathFound();
	}

	/**
	 * Recursive method that moves each enemy one block at a time
	 * @param enemy
	 * @return
	 */
	public boolean moveEntity(final Enemy enemy) {
		final Path pPath = enemy.getPath();

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
						
						if (enemy.getX() ==  mFinalPosition.getTileColumn() * GameScene.getTileWidth()+ GameScene.getTileWidth()/4&&
								enemy.getY() == mFinalPosition.getTileRow() * GameScene.getTileHeight()){
							Log.i("", "FINISH");
							mWayPointIndex = 0;
							enemy.destroy();
							currentlyFinished++;

							if (currentlyFinished == enemy.getCorrespondingWave().getEnemies().length) {
								mHasFinishedPath = true;
								currentlyFinished = 0;

								GameScene scene = GameScene.getSharedInstance();
								scene.initializeNextWave();
							}
						}
						else {
							mHasFinishedPath = false;

							if (enemy.isNeedToUpdatePath()) {
								enemy.setNeedToUpdatePath(false);
								
								Thread t = new Thread(new Runnable() {

									@Override
									public void run() {
										Path p = getPath(enemy);
										enemy.setPath(p);
									}
									
								});
								t.run();
								//try {
									//t.join();
								//} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									//e.printStackTrace();
								//}
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

		return mHasFinishedPath;
	}
	
	
	
	//*************************Private Methods**************************************//
	
	private Path loadPathFound() {
		if (aStarPath == null)
			return null;
		Path current = new Path(aStarPath.getLength());
		for (int i = 0; i < aStarPath.getLength(); i++) {
			current.to(
					(aStarPath.getX(i) * GameScene.getTileWidth())
							+ GameScene.getTileWidth() / 4,
					aStarPath.getY(i) * GameScene.getTileHeight());
		}
		return current;
	}
	
	private org.andengine.util.algorithm.path.Path findPathViaPathFinder(int fromCol, int fromRow, int toCol, int toRow) {
		return mAStarPathFinder.findPath(MAX_SEARCH_DEPTH, mPathFinderMap, 0, 0,
				mTiledMap.getTileColumns(), mTiledMap.getTileRows() - 1, layer,
				fromCol, fromRow, toCol, toRow, false, mHeuristic, mCostCallback);
	}
}
