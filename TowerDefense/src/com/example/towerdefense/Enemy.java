package com.example.towerdefense;

import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

public class Enemy extends Sprite{

	private int health;
	private int maxHealth;
	private float speed;
	private Integer worth;
	private Path path;
	private MoveXModifier beginningModifier;
	private boolean needToUpdatePath;
	private int index;

	public Enemy(ITextureRegion pTextureRegion, float x, float y, int health, float speed, Integer worth) {
		super(x, y, pTextureRegion, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		this.health = health;
		this.maxHealth = health;
		this.speed = speed;
		this.worth = worth;
		this.path = null;
		this.needToUpdatePath = false;
	}

	public float getXReal() {
		return this.getX() + this.getWidthScaled()/2;
	}

	public float getYReal() {
		return this.getY() + this.getHeightScaled()/2;
	}
	
	public void destroy() {
		TowerDefenseActivity.getSharedInstance().runOnUpdateThread(new Runnable() {

			@Override
			public void run() {	
				detachSelf();
				clearEntityModifiers();
				clearUpdateHandlers();
			}
		});
		this.setUserData("dead");
	}
	
	public void returnHealthToNormal() {
		this.health = this.maxHealth;
	}
	
	public void hit(int p) {
		health-=p;
	}
	
	public boolean isDead() {
		return health <= 0;
	}
	
	public float getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public Integer getWorth() {
		return worth;
	}

	public void setWorth(Integer worth) {
		this.worth = worth;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}
	
	public void setBeginningModifier(MoveXModifier m) {
		this.beginningModifier = m;
	}
	
	public MoveXModifier getBeginningModifier() {
		return beginningModifier;
	}
	
	public void setNeedToUpdatePath(boolean needToUpdatePath) {
		this.needToUpdatePath = needToUpdatePath;
	}
	
	public boolean isNeedToUpdatePath() {
		return needToUpdatePath;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
}
