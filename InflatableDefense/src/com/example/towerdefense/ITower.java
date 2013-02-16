package com.example.towerdefense;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.RectangularShape;

public interface ITower {
	public final static Object LOCK = new Object();
	
	public void setPosition(float x, float y);
	public boolean inSights(float x, float y);
	public boolean lockedOnInSight();
	public void onImpact(Enemy enemy);
	public void onEnemyOutOfRange(Enemy e);
	public void onIdleInWave();
	public void onWaveEnd();
	public void hitEnemy(Enemy e);
	public Rectangle getSight();
	public float getRadius();
	public void setLockedOn(Enemy e);
	public Enemy getLockedOn();
	public Integer getCost();
	public void shoot(Enemy e);
	public void checkForDeadEnemies(Enemy e);
	public RectangularShape getEntity();

}
