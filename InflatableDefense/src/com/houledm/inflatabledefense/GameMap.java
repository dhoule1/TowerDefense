package com.houledm.inflatabledefense;

import org.andengine.extension.tmx.TMXTiledMap;

public class GameMap {	
	
	public enum MapType {
		DESERT,GRASS,TUNDRA,CAVE,BEACH;
	}
	public enum StartSide {
		LEFT,
		RIGHT,
		UP,
		DOWN;
	}
	
	
	private static final int TILE_SIZE = 40;
	
	private TMXTiledMap map;
	private int mapID;
	
	private MapType type;
	
	private int[] startTile;
	private int[] endTile;
	
	private StartSide side;
	
	public GameMap(MapType type) {
		this.type = type;
		if (type.compareTo(MapType.DESERT) == 0) {
			startTile = new int[]{0,3};
			endTile = new int[]{9,16};
			side = StartSide.UP;
			mapID = 1;
		}
		else if (type.compareTo(MapType.GRASS) == 0) {
			startTile = new int[]{5,0};
			endTile = new int[]{5,19};
			side = StartSide.LEFT;
			mapID = 0;
		}
		else if (type.compareTo(MapType.TUNDRA) == 0) {
			startTile = new int[]{0,9};
			endTile = new int[]{9,9};
			side = StartSide.UP;
			mapID = 2;
		}else if (type.compareTo(MapType.CAVE) == 0) {
				startTile = new int[]{0,6};
				endTile = new int[]{5,0};
				side = StartSide.UP;
				mapID = 3;
		}else if (type.compareTo(MapType.BEACH) == 0) {
			startTile = new int[]{0,5};
			endTile = new int[]{0,14};
			side = StartSide.UP;
			mapID = 4;
		}
	}
	
	public int getMapID() {
		return mapID;
	}
	
	public MapType getMapType() {
		return this.type;
	}

	public int[] getStartTile() {
		return startTile;
	}

	public void setStartTile(int[] startTile) {
		this.startTile = startTile;
	}

	public int[] getEndTile() {
		return endTile;
	}

	public void setEndTile(int[] endTile) {
		this.endTile = endTile;
	}

	public static int getTileSize() {
		return TILE_SIZE;
	}

	public StartSide getSide() {
		return side;
	}

	public void setSide(StartSide side) {
		this.side = side;
	}
	
	public void setMap(TMXTiledMap map) {
		this.map = map;
	}
	public TMXTiledMap getMap() {
		return this.map;
	}
	

}
