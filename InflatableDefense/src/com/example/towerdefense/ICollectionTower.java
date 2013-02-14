package com.example.towerdefense;

import java.util.ArrayList;
import java.util.List;

public interface ICollectionTower {
	public List<Enemy> queue = new ArrayList<Enemy>();
	
	public void addToQueue(Enemy e);
	public void clearQueue();

}
