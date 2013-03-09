package com.houledm.inflatabledefense;

import org.andengine.entity.Entity;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.particle.BatchedSpriteParticleSystem;
import org.andengine.entity.particle.emitter.PointParticleEmitter;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.UncoloredSprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

public class FlameTower extends BaseTower {

	private static final float SCOPE = 60.0f;
	private static final float TIME_BETWEEN_SHOTS = 0.8f;
	private static final int POWER = 1;
	public static final Integer COST = 0;//225;
	public static final boolean HAS_BULLETS = false;
	public static final int KILLING_COUNT = 2;
	
	private static TextureRegion flameRegion1;
	
	private Entity entity;
	private BatchedSpriteParticleSystem pSystem1;
	private BatchedSpriteParticleSystem pSystem2;
	private BatchedSpriteParticleSystem pSystem3;
	
	private boolean particlesAttached;
	
	private Rectangle psSight;
	
	private boolean firing;
	
	public static void initialize(TextureRegion region) {
		flameRegion1 = region;
	}
	public FlameTower(final float pX, final float pY, final ITextureRegion pTextureRegion) {
		super(pX, pY, SCOPE, TIME_BETWEEN_SHOTS, POWER, COST, HAS_BULLETS,pTextureRegion);
		
		entity = new Entity();
		entity.setZIndex(1);
		
		pSystem3 = new BatchedSpriteParticleSystem(new PointParticleEmitter(0,0), 5, 5, 15, flameRegion1, InflatableDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		pSystem3.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(2.0f));
		pSystem3.addParticleInitializer(new VelocityParticleInitializer<UncoloredSprite>(-75.0f,-80.0f,-20.0f,20.0f));
		pSystem3.addParticleInitializer(new RotationParticleInitializer<UncoloredSprite>(0.0f,180.0f));
		pSystem3.addParticleModifier(new ColorParticleModifier<UncoloredSprite>(1.0f, 3.0f, 1.0f,1.0f, 0.0f,0.0f,0.0f,0.0f)); //Red
		pSystem3.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(0.0f, 1.5f,1.0f, 3.0f));	
		pSystem3.setVisible(false);
		pSystem3.setParticlesSpawnEnabled(false);
		entity.attachChild(pSystem3);

		pSystem1 = new BatchedSpriteParticleSystem(new PointParticleEmitter(0,0), 5, 5, 15, flameRegion1, InflatableDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		pSystem1.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(2.0f));
		pSystem1.addParticleInitializer(new VelocityParticleInitializer<UncoloredSprite>(-75.0f,-80.0f,-10.0f,10.0f));
		pSystem1.addParticleInitializer(new RotationParticleInitializer<UncoloredSprite>(0.0f,180.0f));
		pSystem1.addParticleModifier(new ColorParticleModifier<UncoloredSprite>(1.0f, 3.0f, 1.0f, 1.0f, 0.64706f,0.64706f, 0.0f, 0.0f)); //Orange
		pSystem1.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(0.0f, 1.5f,1.0f, 3.0f));	
		pSystem1.setVisible(false);
		pSystem1.setParticlesSpawnEnabled(false);
		entity.attachChild(pSystem1);
		
		pSystem2 = new BatchedSpriteParticleSystem(new PointParticleEmitter(0,0), 5, 5, 15, flameRegion1, InflatableDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		pSystem2.addParticleInitializer(new ExpireParticleInitializer<UncoloredSprite>(2.0f));
		pSystem2.addParticleInitializer(new VelocityParticleInitializer<UncoloredSprite>(-75.0f,-80.0f,0.0f,0.0f));
		pSystem2.addParticleInitializer(new RotationParticleInitializer<UncoloredSprite>(0.0f,180.0f));
		pSystem2.addParticleModifier(new ColorParticleModifier<UncoloredSprite>(1.0f, 3.0f, 1.0f,1.0f, 1.0f,1.0f,0.0f,0.0f)); //Yellow
		pSystem2.addParticleModifier(new ScaleParticleModifier<UncoloredSprite>(0.0f, 1.5f,1.0f, 3.0f));	
		pSystem2.setVisible(false);
		pSystem2.setParticlesSpawnEnabled(false);
		entity.attachChild(pSystem2);
		
		psSight = new Rectangle(0,-this.getWidthScaled()/3,-this.getHeightScaled()*3,	this.getHeightScaled(),InflatableDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
		psSight.setColor(Color.TRANSPARENT);
		entity.attachChild(psSight);
		
		attachChild(entity);
		
		entity.setX(entity.getX()-this.getWidthScaled()/10);
		entity.setY(entity.getY()+this.getHeightScaled()/3);
		
		particlesAttached = false;
		
		firing = false;
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

	private void setPSystemVisible(final boolean visible, final BatchedSpriteParticleSystem ps) {
		ps.setVisible(visible);
		ps.setParticlesSpawnEnabled(visible);
		particlesAttached = visible;
	}
	
	public BatchedSpriteParticleSystem getFireParticleSystem() {
		return pSystem1;
	}
	
	public void detachFireParticleSystem() {
		InflatableDefenseActivity.getSharedInstance().runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				entity.detachSelf();
				entity.dispose();
			}
		});
	}
	
	@Override
	public boolean inSights(final float x, final float y) {
		boolean a = super.inSights(x, y);
		boolean b = psSight.contains(x, y);
		
		return a || b;
	}

	@Override
	public boolean detachSelf() {
		this.pSystem1.detachSelf();
		return super.detachSelf();
	}
	
	@Override
	public void onImpact(final Enemy enemy) {				
		if (particlesAttached) return;
		
		this.setZIndex(2);
		GameScene.getSharedInstance().sortChildren();
		
		firing = true;
		enableParicleSystem();
		
		DelayModifier delay = new DelayModifier(1.0f, new IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				firing = false;
			}
		});
		this.registerEntityModifier(delay);
		delay.setAutoUnregisterWhenFinished(true);
	}
	
	
	@Override
	public void onIdleInWave() {
		if(firing) return;
		
		disableParticleSystem();
		super.onIdleInWave();
	}
	@Override
	public void onWaveEnd() {
		disableParticleSystem();
		super.onWaveEnd();
	}
	
	@Override
	public void hitEnemy(Enemy e) {
		for (int i = 0; i < queue.size(); i++) {
			final Enemy enemy = queue.get(i);
			if (enemy.isDead()) continue;
			if (psSight.contains(enemy.getXReal(), enemy.getYReal())) {
				enemy.hit(POWER);
				checkForDeadEnemies(enemy);
				if ((i+1) == KILLING_COUNT) return;
			}
		}
	}
}
