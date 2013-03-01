package com.example.towerdefense;

public class GameMap {	
	
	public enum MapType {
		DESERT,GRASS;
	}
	public enum StartSide {
		LEFT,
		RIGHT,
		UP,
		DOWN;
	}
	
	
	private static final int TILE_SIZE = 40;
	
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
		}
		else if (type.compareTo(MapType.GRASS) == 0) {
			startTile = new int[]{5,0};
			endTile = new int[]{5,19};
			side = StartSide.LEFT;
		}
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
	

}
