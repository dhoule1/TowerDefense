package com.houledm.inflatabledefense;

import java.util.concurrent.CopyOnWriteArrayList;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.shape.RectangularShape;

public interface ITower {
	public final static Object LOCK = new Object();

	public CopyOnWriteArrayList<Enemy> getQueue();
	public void clearQueue();
	public void addEnemyToQueue(Enemy enemy);
	public void removeEnemyFromQueue(Enemy enemy);
	public void setPosition(float x, float y);
	public float getX();
	public float getY();
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
	public boolean canUpgrade();
	public void upgrade();

}
