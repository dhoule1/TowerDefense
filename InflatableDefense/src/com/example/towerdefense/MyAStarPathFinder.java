package com.example.towerdefense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.util.algorithm.path.IPathFinderMap;
import org.andengine.util.algorithm.path.Path;
import org.andengine.util.algorithm.path.astar.ManhattanHeuristic;

import android.util.Log;

public class MyAStarPathFinder {
	
	private static final int MAX_DEPTH = 60;
	
	private SortedList open;
	private List<Node> closed;
	private TMXTiledMap map;
	private TMXLayer layer;
	private TMXTile startTile;
	private List<TMXTile> blockedTiles;
	private Node[][] nodes;
	private ManhattanHeuristic<TMXLayer> mHeuristic;
	private IPathFinderMap<TMXLayer> mPathFinderMap;
	
	private int toX;
	private int toY;
	
	
	public MyAStarPathFinder() {
		open = new SortedList();
		closed = new ArrayList<Node>();
		
		final GameScene scene = GameScene.getSharedInstance();
		
		toX = scene.getEndTile().getTileColumn();
		toY = scene.getEndTile().getTileRow();
		
		startTile = scene.getStartTile();
		
		ManhattanHeuristic<TMXLayer> mHeuristic = new ManhattanHeuristic<TMXLayer>();
		this.mHeuristic = mHeuristic;
		
		mPathFinderMap = new IPathFinderMap<TMXLayer>() {

			@Override
			public boolean isBlocked(int pX, int pY, TMXLayer pTMXLayer) {
				return false;
			}
		};
		
		blockedTiles = scene.getBlockedList();
		
		map = scene.getTMXTiledMap();
		
		layer = map.getTMXLayers().get(0);
		
		TMXTile[][] tiles = layer.getTMXTiles();

		Node[][] nodes = new Node[tiles.length][tiles[0].length];
		
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[0].length; j++) {
				Node node = new Node(j,i);				
				nodes[i][j] = node;
			}
		}
		this.nodes = nodes;
		
	}
	
	public Path findPath(Enemy enemy) {
		
		long start = System.currentTimeMillis();
		
		TMXTile tile = (enemy.getUserData() == "dummy") ? startTile : layer.getTMXTileAt(enemy.getXReal(), enemy.getYReal());
		
		int fromX = tile.getTileColumn();
		int fromY = tile.getTileRow();
		TMXTile fromTile = layer.getTMXTile(fromX, fromY);
		Node fromNode = nodes[fromTile.getTileRow()][fromTile.getTileColumn()];
		fromNode.setCost(0.0f);
		fromNode.setDepth(0);
		fromNode.setParent(null);
		
		//need to reset the parent of the toNode.
		nodes[toY][toX].parent = null;
	
		Node toNode = nodes[toY][toX];
		
		closed.clear();
		open.clear(); 
		open.add(fromNode);
		
		int cycleCount = 0;
		int currentDepth = 0;
		while((open.size() != 0) && (currentDepth < MAX_DEPTH)) {
			cycleCount++;
			
			Node current = (Node)open.first();
			
			if (current == toNode) break;
			
			open.remove(current);
			closed.add(current);
			
			for (int x = -1; x < 2; x++) {
				for (int y = -1; y < 2; y++) {
					if (x == 0 && y == 0) continue;
					if (x != 0 && y !=0) continue;
					
					
					int xp = x + current.getX();
					int yp = y + current.getY();
					
					if (isValidTile(current.getX(), current.getY(), xp, yp)) {
						float nextStepCost = current.getCost() + 1;
						Node neighbor = nodes[yp][xp];
						
						if (nextStepCost < neighbor.getCost()) {
							if (open.contains(neighbor)) open.remove(neighbor);
							if (closed.contains(neighbor)) closed.remove(neighbor);
						}
						if (!(open.contains(neighbor)) && !(closed.contains(neighbor))) {
							neighbor.setCost(nextStepCost);
							neighbor.heuristic = getHeuristicCost(xp, yp, toX, toY);
							
							currentDepth = Math.max(currentDepth, neighbor.setParent(current));
							open.add(neighbor);
						}
					}
				}
			}
		}
		
		//Log.i("Parent of toNode", "Size: "+nodes[toY][toX].parent);
		
		if (nodes[toY][toX].parent == null) return null;
		//if (cycleCount == 1) return null;

		
		Node target = nodes[toY][toX];
		
		int length = 1;
		while (target != nodes[fromY][fromX]) {
			target = target.getParent();
			length++;
		}
		Log.i("Length of Path", "Length: "+length);
		Path result = new Path(length);
		
		target = nodes[toY][toX];
		
		int index = length - 1;
		while(target != nodes[fromY][fromX]) {
			result.set(index, target.getX(), target.getY());
			target = target.getParent();
			index--;
		}
		
		result.set(0, fromNode.getX(), fromNode.getY());
		
		long end = System.currentTimeMillis();
		
		Log.i("Time", end - start+"");
		Log.i("Cycles", cycleCount+"");
		
		return result;
		
	}

	private Float getHeuristicCost(int currentX, int currentY, int possibleX, int possibleY) {
	
		return mHeuristic.getExpectedRestCost(mPathFinderMap, layer, currentX, currentY, possibleX, possibleY);
	}
	
	private boolean isValidTile(int xc, int yc, int xp, int yp) {
		TMXTile tile;
		try{
			tile = layer.getTMXTile(xp, yp);
			}catch(Exception e) {
				return false;
			}
		//Log.i("Valid", "Valid Placement? "+(!(xc == xp && yc == yp) && !blockedTiles.contains(tile)));
		return !(xc == xp && yc == yp) && !blockedTiles.contains(tile);
	}
	
	
	//===============================================================
	//INNER CLASSES
	//===============================================================
	
	/**
	 * TODO
	 * SO UGLY, fix it pl0x
	 * @author Dan
	 *
	 */
	
	private class Node implements Comparable<Node>{
		private Integer x;
		private Integer y;
		private Integer depth;
		private Float cost; 
		private Float heuristic;
		private Node parent;
		
		public Node(Integer x, Integer y) {
			this.x = x;
			this.y = y;
			cost = 0.0f;
		}
		
		public Integer getX() {
			return x;
		}

		public void setX(Integer x) {
			this.x = x;
		}

		public Integer getY() {
			return y;
		}

		public void setY(Integer y) {
			this.y = y;
		}

		public void setCost(Float cost) {
			this.cost = cost;
		}
		public Float getCost() {
			return this.cost;
		}
		public void setDepth(Integer depth) {
			this.depth = depth;
		}
		public Integer getDepth() {
			return this.depth;
		}
		public int setParent(final Node pParent) {
			if (pParent == null) {
				this.parent = null;
				return 0;
			}
			this.depth = pParent.depth + 1;
			this.parent = pParent;

			return this.depth;
		}
		public Node getParent() {
			return this.parent;
		}
		

		@Override
		public int compareTo(Node other) {
			float f = heuristic + cost;
			float of = other.heuristic + other.cost;
			
			if (f < of) return -1;
			else if (f > of) return 1;
			else return 0;
		}
		
	}
	private class SortedList {
		
		
		/** The list of elements */
		private ArrayList<Node> list = new ArrayList<Node>();
		
		/**
		 * Retrieve the first element from the list
		 *  
		 * @return The first element from the list
		 */
		public Object first() {
			return list.get(0);
		}
		
		/**
		 * Empty the list
		 */
		public void clear() {
			list.clear();
		}
		
		/**
		 * Add an element to the list - causes sorting
		 * 
		 * @param o The element to add
		 */
		public void add(Node node) {
			list.add(node);
			Collections.sort(list);
		}
		
		/**
		 * Remove an element from the list
		 * 
		 * @param o The element to remove
		 */
		public void remove(Object o) {
			list.remove(o);
		}
	
		/**
		 * Get the number of elements in the list
		 * 
		 * @return The number of element in the list
 		 */
		public int size() {
			return list.size();
		}
		
		/**
		 * Check if an element is in the list
		 * 
		 * @param o The element to search for
		 * @return True if the element is in the list
		 */
		public boolean contains(Object o) {
			return list.contains(o);
		}
	}

}
