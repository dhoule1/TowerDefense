package com.example.towerdefense;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.andengine.entity.modifier.PathModifier.Path;


public class Wave {
	
	private CopyOnWriteArrayList<Enemy> enemies;
	private Path fullPath;
	private float timeBetweenEnemies;
	
	public Wave(CopyOnWriteArrayList<Enemy> enemies, float time) {
		this.enemies = enemies;
		this.timeBetweenEnemies = time/2;
	}
	
	public List<Enemy> getEnemies() {
		return enemies;
	}
	
	public void setEnemies(CopyOnWriteArrayList<Enemy> enemies) {
		this.enemies = enemies;
	}
	
	public void setFullPath(Path p) {
		this.fullPath = p;
	}
	
	public Path getFullPath() {
		return this.fullPath;
	}

	public float getTimeBetweenEnemies() {
		return timeBetweenEnemies;
	}

	public void setTimeBetweenEnemies(float timeBetweenEnemies) {
		this.timeBetweenEnemies = timeBetweenEnemies;
	}

}
