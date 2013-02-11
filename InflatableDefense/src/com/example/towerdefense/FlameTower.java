package com.example.towerdefense;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.Entity;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.color.Color;

public class FlameTower extends BaseTower{
	
	private static final float SCOPE = 60.0f;
	private static final float TIME_BETWEEN_SHOTS = 0.5f;
	private static final int POWER = 1;
	public static final Integer COST = 15;
	public static final boolean HAS_BULLETS = false;
	public static final int KILLING_COUNT = 2;
	
	private static TextureRegion flameRegion1;
	
	private Entity entity;
	private SpriteParticleSystem pSystem1;
	private SpriteParticleSystem pSystem2;
	private SpriteParticleSystem pSystem3;
	
	private boolean particlesAttached;
	
	private Rectangle psSight;
	
	private List<Enemy> queue;
	
	public static void initialize(TextureRegion region) {
		flameRegion1 = region;
	}
	public FlameTower(float pX, float pY, ITextureRegion pTextureRegion) {
		super(pX, pY, SCOPE, TIME_BETWEEN_SHOTS, POWER, COST, HAS_BULLETS,pTextureRegion);
		
		entity = new Entity();
		
		pSystem3 = new SpriteParticleSystem(new PointParticleEmitter(0,0), 5, 5, 40, flameRegion1, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		pSystem3.addParticleInitializer(new ExpireParticleInitializer<Sprite>(5.0f));
		pSystem3.addParticleInitializer(new VelocityParticleInitializer<Sprite>(-75.0f,-80.0f,-20.0f,20.0f));
		pSystem3.addParticleInitializer(new RotationParticleInitializer<Sprite>(0.0f,180.0f));
		pSystem3.addParticleModifier(new ColorParticleModifier<Sprite>(1.0f, 3.0f, 1.0f,1.0f, 0.0f,0.64706f, 0.0f, 1.0f)); //Red
		pSystem3.addParticleModifier(new AlphaParticleModifier<Sprite>(0.0f, 1.8f, 1.0f,0.0f));
		pSystem3.addParticleModifier(new ScaleParticleModifier<Sprite>(0.0f, 1.5f,1.0f, 4.5f));	
		pSystem3.setVisible(false);
		pSystem3.setParticlesSpawnEnabled(false);
		entity.attachChild(pSystem3);

		pSystem1 = new SpriteParticleSystem(new PointParticleEmitter(0,0), 5, 5, 40, flameRegion1, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		pSystem1.addParticleInitializer(new ExpireParticleInitializer<Sprite>(5.0f));
		pSystem1.addParticleInitializer(new VelocityParticleInitializer<Sprite>(-75.0f,-80.0f,-10.0f,10.0f));
		pSystem1.addParticleInitializer(new RotationParticleInitializer<Sprite>(0.0f,180.0f));
		pSystem1.addParticleModifier(new ColorParticleModifier<Sprite>(1.0f, 3.0f, 1.0f, 1.0f, 0.64705f,0.64706f, 0.0f, 1.0f)); //Orange
		pSystem1.addParticleModifier(new AlphaParticleModifier<Sprite>(0.0f, 1.8f, 1.0f,0.0f));
		pSystem1.addParticleModifier(new ScaleParticleModifier<Sprite>(0.0f, 1.5f,1.0f, 4.0f));	
		pSystem1.setVisible(false);
		pSystem1.setParticlesSpawnEnabled(false);
		entity.attachChild(pSystem1);
		
		pSystem2 = new SpriteParticleSystem(new PointParticleEmitter(0,0), 5, 5, 40, flameRegion1, TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		pSystem2.addParticleInitializer(new ExpireParticleInitializer<Sprite>(5.0f));
		pSystem2.addParticleInitializer(new VelocityParticleInitializer<Sprite>(-75.0f,-80.0f,0.0f,0.0f));
		pSystem2.addParticleInitializer(new RotationParticleInitializer<Sprite>(0.0f,180.0f));
		pSystem2.addParticleModifier(new ColorParticleModifier<Sprite>(1.0f, 3.0f, 1.0f,1.0f, 1.0f,0.64706f, 0.0f, 1.0f)); //Yellow
		pSystem2.addParticleModifier(new AlphaParticleModifier<Sprite>(0.0f, 1.8f, 1.0f,0.0f));
		pSystem2.addParticleModifier(new ScaleParticleModifier<Sprite>(0.0f, 1.5f,1.0f, 3.0f));	
		pSystem2.setVisible(false);
		pSystem2.setParticlesSpawnEnabled(false);
		entity.attachChild(pSystem2);
		
		psSight = new Rectangle(0,-this.getWidthScaled()/3,-this.getHeightScaled()*3,	this.getHeightScaled(),TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		psSight.setColor(Color.TRANSPARENT);
		entity.attachChild(psSight);
		
		attachChild(entity);
		
		entity.setX(entity.getX()-this.getWidthScaled()/10);
		entity.setY(entity.getY()+this.getHeightScaled()/3);
		
		particlesAttached = false;
		
		queue = new ArrayList<Enemy>();
	}
	
	private void disableParticleSystem() {
		setPSystemVisible(false, pSystem1);
		pSystem1.reset();
		setPSystemVisible(false, pSystem2);
		pSystem2.reset();
		setPSystemVisible(false, pSystem3);
		pSystem3.reset();
	}
	private void enableParicleSystem() {
		setPSystemVisible(true, pSystem1);
		setPSystemVisible(true, pSystem2);
		setPSystemVisible(true, pSystem3);
	}
	
	
	private void setPSystemVisible(boolean visible, SpriteParticleSystem ps) {
		ps.setVisible(visible);
		ps.setParticlesSpawnEnabled(visible);
		particlesAttached = visible;
	}
	
	@Override
	public boolean inSights(float x, float y) {
		boolean a = super.getSight().contains(x, y);
		boolean b = psSight.contains(x, y);
		
		return a || b;
	}

	@Override
	public boolean detachSelf() {
		this.pSystem1.detachSelf();
		return super.detachSelf();
	}
	
	@Override
	public void onImpact(Enemy enemy) {
		if (!queue.contains(enemy)) queue.add(enemy);
		
		if (particlesAttached) return;
		enableParicleSystem();
	}
	@Override
	public void onIdleInWave() {
		disableParticleSystem();
		queue.clear();
	}
	@Override
	public void onWaveEnd() {
		super.onWaveEnd();
		disableParticleSystem();
		queue.clear();
	}
	
	@Override
	public void hitEnemy(Enemy e) {
		int count = 0;
		for (int i = 0; i < queue.size(); i++) {
			Enemy enemy = queue.get(i);
			if (enemy.isDead()) continue;
			if (psSight.contains(enemy.getXReal(), enemy.getYReal())) {
				enemy.hit(POWER);
				checkForDeadEnemies(enemy);
				count++;
				if (count == KILLING_COUNT) return;
			}
		}
	}

	public SpriteParticleSystem getFireParticleSystem() {
		return pSystem1;
	}
	
	public void detachFireParticleSystem() {
		TowerDefenseActivity.getSharedInstance().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				entity.detachSelf();
				entity.dispose();
			}
		});
	}
}
