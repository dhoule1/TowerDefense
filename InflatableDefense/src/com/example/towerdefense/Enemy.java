package com.example.towerdefense;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

public class Enemy extends AnimatedSprite{
	
	public Enemy[] childArray;
	private int childCount;
	private int health;
	private int maxHealth;
	private float speed;
	private float initialSpeed;
	private Integer worth;
	private Path path;
	private MoveModifier beginningModifier;
	private PathModifier pathModifier;
	private boolean needToUpdatePath;
	private int index;
	private boolean droppedChildren;
	private Sound pop;
	
	private boolean isFrozen;

	public Enemy(ITiledTextureRegion pTextureRegion, int childCount, float x, float y, int health, float speed, Integer worth) {
		super(x, y, pTextureRegion, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		this.childCount = childCount;
		this.childArray = new Enemy[this.childCount];
		this.health = health;
		this.maxHealth = health;
		this.speed = speed;
		this.initialSpeed = speed;
		this.worth = worth;
		this.path = null;
		this.needToUpdatePath = false;
		this.droppedChildren = false;
		this.pop = ResourceManager.getInstance().getPopSound();
		
		isFrozen = false;
	}
	public Enemy() {
		super(0.0f,0.0f, ResourceManager.getInstance().getBasketballRegion(), ResourceManager.getInstance().getVbom());
	}

	public float getXReal() {
		return this.getX() + this.getWidthScaled()/2;
	}

	public float getYReal() {
		return this.getY() + this.getHeightScaled()/2;
	}
	public int getEnemyCount() {
		return (droppedChildren) ? 1 : 1 + childCount;
	}
	public void setChild(int index, Enemy enemy) {
		childArray[index] = enemy;
	}
	
	public void destroy() {
		if (this.isDead()) setCurrentTileIndex(1);
		this.hit(this.maxHealth);
		TowerDefenseActivity.getSharedInstance().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {	
				detachSelf();
				clearEntityModifiers();
				clearUpdateHandlers();
			}
		});
		this.setUserData("dead");
		
		if (this.path.getCoordinatesX().length != 2) {
			dropChildren();
			pop.play();
		}
	}
	
	/**
	 * Figures out where each child enemy should appear on the map,
	 * and from that location, how fast it's speed (duration) should be
	 */
	private void dropChildren() {
		if (path != null && this.getClass() != SoccerballEnemy.class) {
			droppedChildren = true;
			float x = this.getPath().getCoordinatesX()[1];
			int middle = childCount/2;
			for (int i = 0; i < childCount; i++) {				
				float result = ((i - middle) * GameMap.getTileSize()/2);
				double modifier = (i/2 - (double)childCount/2) * -1;
				if (i == middle) modifier = 1;
				if (this.getX() < x) {
					createNewChild(this.getX() + result, this.getY(), modifier, i);
				} else {
					createNewChild(this.getX(), this.getY() + result, modifier, i);
				}
			}
		}
	}
	public void createNewChild(float x, float y, double speedModifier, int index) {	}
	
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
		return speed/2;
	}
	public void returnSpeedToNormal() {
		this.speed = this.initialSpeed;
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
	
	public void setBeginningModifier(MoveModifier m) {
		this.beginningModifier = m;
	}
	
	public MoveModifier getBeginningModifier() {
		return beginningModifier;
	}
	
	public void setPathModifier(PathModifier m) {
		this.pathModifier = m;
	}
	
	public PathModifier getMoveModifier() {
		return pathModifier;
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
	
	public float getOffsetX() {
		return -GameMap.getTileSize()/4;
	}
	public float getOffsetY() {
		return -GameMap.getTileSize()/4;
	}
	
	public void freeze() {
		isFrozen = true;
	}
	public void thaw() {
		isFrozen = false;
	}
	public boolean isFrozen() {
		return isFrozen;
	}
}
