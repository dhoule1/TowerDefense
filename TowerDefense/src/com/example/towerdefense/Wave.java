package com.example.towerdefense;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.modifier.PathModifier.Path;


public class Wave {
	
	private Enemy[] enemies;
	private Path currentPath;
	private Path fullPath;
	
	public Wave(Enemy[] enemies) {
		this.enemies = enemies;
	}
	public Wave(List<Enemy[]> listOfEnemies) {
		List<Enemy> enemies = new ArrayList<Enemy>();
		
		for (Enemy[] array:listOfEnemies) {
			for(Enemy e: array) {
				enemies.add(e);
			}
		}
		this.enemies = (Enemy[]) enemies.toArray();
	}
	
	public Enemy[] getEnemies() {
		return enemies;
	}
	
	public void setEnemies(Enemy[] enemies) {
		this.enemies = enemies;
	}
	
	public void setCurrentPath(Path p) {
		this.currentPath = p;
	}
	
	public Path getCurrentPath() {
		return currentPath;
	}
	
	public void setFullPath(Path p) {
		this.fullPath = p;
	}
	
	public Path getFullPath() {
		return this.fullPath;
	}

}
