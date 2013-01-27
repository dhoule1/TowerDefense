package com.example.towerdefense;

import org.andengine.entity.modifier.PathModifier.Path;


public class Wave {
	
	private Enemy[] enemies;
	private Path path;
	
	public Wave(Enemy[] enemies) {
		this.enemies = enemies;
	}
	
	public Enemy[] getEnemies() {
		return enemies;
	}
	
	public void setEnemies(Enemy[] enemies) {
		this.enemies = enemies;
	}
	
	public void setInitialPath(Path p) {
		this.path = p;
	}
	
	public Path getPath() {
		return path;
	}

}
