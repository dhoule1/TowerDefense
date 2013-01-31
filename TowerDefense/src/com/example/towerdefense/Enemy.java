package com.example.towerdefense;

import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;

public class Enemy extends Sprite{

	private float health;
	private float speed;
	private Path path;
	private MoveXModifier beginningModifier;
	private boolean needToUpdatePath;
	private boolean needToAdjustPath;
	private Wave wave;
	private int index;
	private int positionOnPath;

	public Enemy(ITextureRegion pTextureRegion, float x, float y, float health, float speed) {
		super(x, y, pTextureRegion, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		this.health = health;
		this.speed = speed;
		this.path = null;
		this.needToUpdatePath = false;
		this.needToAdjustPath = false;
		this.positionOnPath = 0;
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
		this.returnHealthToNormal();
	}
	
	public void returnHealthToNormal() {}
	
	public void hit() {
		health--;
	}
	
	public boolean isDead() {
		return health <= 0;
	}
	
	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
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

	public void setNeedToAdjustPath(boolean needToAdjustPath) {
		this.needToAdjustPath = needToAdjustPath;
	}
	
	public boolean isNeedToAdjustPath() {
		return needToAdjustPath;
	}
	
	public void setCorrespondingWave(Wave wave) {
		this.wave = wave;
	}
	
	public Wave getCorrespondingWave() {
		return wave;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}

	public int getPositionOnPath() {
		return positionOnPath;
	}

	public void setPositionOnPath(int positionOnPath) {
		this.positionOnPath = positionOnPath;
	}
	public void incrementPosOnPath() {
		this.positionOnPath++;
	}
}
