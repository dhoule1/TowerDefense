package com.example.towerdefense;

import java.util.List;

import org.andengine.entity.modifier.PathModifier.Path;


public class Wave {
	
	private List<Enemy> enemies;
	private Path fullPath;
	private float timeBetweenEnemies;
	
	public Wave(List<Enemy> enemies, float time) {
		this.enemies = enemies;
		this.timeBetweenEnemies = time/2;
	}
	
	public List<Enemy> getEnemies() {
		return enemies;
	}
	
	public void setEnemies(List<Enemy> enemies) {
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
