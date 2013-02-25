package com.example.towerdefense;

import java.io.IOException;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

public class ResourceManager {
	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------

	private static final ResourceManager INSTANCE = new ResourceManager();

	private Engine engine;
	private TowerDefenseActivity activity;
	private ZoomCamera camera;
	private VertexBufferObjectManager vbom;
	
	private TextureRegion splashRegion;
	private BitmapTextureAtlas splashTextureAtlas;
	
	private TextureRegion menuBackgroundRegion;
	private TextureRegion menuTextRegion;
	private TextureRegion play_region;
	private BuildableBitmapTextureAtlas menuTextureAtlas;

	private TextureRegion levelTextRegion;
	private TextureRegion desertImageProfileRegion;
	private TextureRegion grassImageProfileRegion;
	private TextureRegion pageBullets;
	private BuildableBitmapTextureAtlas levelChooserTextureAtlas;
	
	private TiledTextureRegion soccerballRegion;
	private TiledTextureRegion basketballRegion;
	private TiledTextureRegion footballRegion;
	private TiledTextureRegion beachballRegion;
	
	private TiledTextureRegion turretTowerRegion;
	private TextureRegion dartTowerRegion;
	private TextureRegion flameTowerRegion;
	private TextureRegion iceTowerRegion;
	
	private TextureRegion dartBulletRegion;
	private TextureRegion flameParticleRegion;
	private TextureRegion icicleRegion;
	
	//Sub Menu Items
	private TextureRegion towerSightRegion;
	private TextureRegion upgradeOptionRegion;
	private TextureRegion deleteOptionRegion;
	
	private TiledTextureRegion startButtonRegion;
	
	private TextureRegion redScreen;
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	
	private Font font;
	
	private Sound popSound;
	private Sound freezeSound;

	//---------------------------------------------
	// TEXTURES & TEXTURE REGIONS
	//---------------------------------------------

	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------

	public void loadMenuResources() {
		loadMenuGraphics();
		loadMenuAudio();
		loadMenuFonts();
	}
	public void loadLevelChooserResources() {
		loadLevelChooserGraphics();
		loadLevelChooserFonts();
		loadLevelChooserAudio();
	}

	public void loadGameResources() {
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}

	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		menuBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "main_level_background.png");
		menuTextRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "title_text.png");
		play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "start_button.png");
		       
		try 
		{
		    this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
		    this.menuTextureAtlas.load();
		} 
		catch (final TextureAtlasBuilderException e)
		{
		        Debug.e(e);
		}

	}
	private void loadMenuFonts() {
		FontFactory.setAssetBasePath("font/");
    final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

    font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "micross.ttf", 40.0f, true, Color.WHITE, 2, Color.BLACK);
    font.load();
	}

	private void loadMenuAudio() {}
	
	public void loadMenuTextures() {
		menuTextureAtlas.load();
	}
	
	public void loadLevelChooserGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/level/");
		levelChooserTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.DEFAULT);
		levelTextRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelChooserTextureAtlas, activity, "level_select_text.png");
		desertImageProfileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelChooserTextureAtlas, activity, "new_desert_image.png");
		grassImageProfileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelChooserTextureAtlas, activity, "grass_image.png");
		pageBullets = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelChooserTextureAtlas, activity, "bullets.png");
		
		try {
			levelChooserTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			levelChooserTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
	}
	public void loadLevelChooserFonts(){}
	public void loadLevelChooserAudio(){}
	

	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1200, 1200, TextureOptions.DEFAULT);
		
		//ENEMIES
		soccerballRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "soccer_ball_tiled.png", 2, 1); //14x27
		basketballRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "basketball_tiled2.png", 2,1); //14x27
		footballRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "football.png",2,1); //14x27
		beachballRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "beach_ball_tiled.png",2,1); //14x27
		
		//TOWERS
		turretTowerRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "turret_tiled.png", 2,1); //80x160
		dartTowerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "dart_tower.png"); //80x80
		flameTowerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "flame_tower.png"); //80x80
		iceTowerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "ice_tower.png"); //80x80
		
		//BULLETS
		dartBulletRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "dart.png"); //249x71
		flameParticleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "particle_fire.png"); //32x32
		icicleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "icicle_2_light_trans.png");
		
		//SUB-MENU ITEMS
		towerSightRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "tower_sight.png"); //100x100
		upgradeOptionRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "up_arrow.png"); //228x221
		deleteOptionRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "red_x.png"); //238x208
		
		//MISC
		startButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "play_fast.png",2,1); //169x169
		redScreen = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "red_screen.png");
		
		try {
		    this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
		    this.gameTextureAtlas.load(); }
		catch (final TextureAtlasBuilderException e){Debug.e(e);}

	}

	private void loadGameFonts(){ 

	}

	private void loadGameAudio() {
		try {
			popSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity,"sfx/pop.ogg");
			freezeSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity,"sfx/freeze.ogg");
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/splash/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR);
		splashRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}

	public void unloadSplashScene() {
		splashTextureAtlas.unload();
	}
	public void unloadMenuScene() {
		menuTextureAtlas.unload();
	}
	public void unloadLevelChooserScene() {
		levelChooserTextureAtlas.unload();
	}
	public void unloadGameScene() {
		gameTextureAtlas.unload();
	}

	/**
	 * @param engine
	 * @param activity
	 * @param camera
	 * @param vbom
	 * <br><br>
	 * We use this method at beginning of game loading, to prepare Resources Manager properly,
	 * setting all needed parameters, so we can latter access them from different classes (eg. scenes)
	 */
	public static void prepareManager(Engine engine, TowerDefenseActivity activity, ZoomCamera camera, VertexBufferObjectManager vbom)
	{
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}

	//---------------------------------------------
	// GETTERS AND SETTERS
	//---------------------------------------------

	public static ResourceManager getInstance()
	{
		return INSTANCE;
	}

	public Engine getEngine() {
		return engine;
	}

	public TowerDefenseActivity getActivity() {
		return activity;
	}

	public void setActivity(TowerDefenseActivity activity) {
		this.activity = activity;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	public ZoomCamera getCamera() {
		return camera;
	}

	public void setCamera(ZoomCamera camera) {
		this.camera = camera;
	}

	public VertexBufferObjectManager getVbom() {
		return vbom;
	}

	public void setVbom(VertexBufferObjectManager vbom) {
		this.vbom = vbom;
	}

	public TextureRegion getSplashRegion() {
		return splashRegion;
	}

	public void setSplashRegion(TextureRegion splashRegion) {
		this.splashRegion = splashRegion;
	}

	public BitmapTextureAtlas getSplashTextureAtlas() {
		return splashTextureAtlas;
	}

	public void setSplashTextureAtlas(BitmapTextureAtlas splashTextureAtlas) {
		this.splashTextureAtlas = splashTextureAtlas;
	}

	public TextureRegion getMenuBackgroundRegion() {
		return menuBackgroundRegion;
	}

	public void setMenuBackgroundRegion(TextureRegion menuBackgroundRegion) {
		this.menuBackgroundRegion = menuBackgroundRegion;
	}  

	public TextureRegion getMenuTextRegion() {
		return menuTextRegion;
	}
	public void setMenuTextRegion(TextureRegion menuTextRegion) {
		this.menuTextRegion = menuTextRegion;
	}
	public TextureRegion getLevelTextRegion() {
		return levelTextRegion;
	}
	public void setLevelTextRegion(TextureRegion levelTextRegion) {
		this.levelTextRegion = levelTextRegion;
	}
	public TextureRegion getDesertImageProfileRegion() {
		return desertImageProfileRegion;
	}
	public void setDesertImageProfileRegion(TextureRegion desertImageProfileRegion) {
		this.desertImageProfileRegion = desertImageProfileRegion;
	}
	public TextureRegion getGrassImageProfileRegion() {
		return grassImageProfileRegion;
	}
	public void setGrassImageProfileRegion(TextureRegion grassImageProfileRegion) {
		this.grassImageProfileRegion = grassImageProfileRegion;
	}
	public TextureRegion getPageBullets() {
		return pageBullets;
	}
	public void setPageBullets(TextureRegion pageBullets) {
		this.pageBullets = pageBullets;
	}
	public BuildableBitmapTextureAtlas getLevelChooserTextureAtlas() {
		return levelChooserTextureAtlas;
	}
	public void setLevelChooserTextureAtlas(
			BuildableBitmapTextureAtlas levelChooserTextureAtlas) {
		this.levelChooserTextureAtlas = levelChooserTextureAtlas;
	}
	public TextureRegion getPlay_region() {
		return play_region;
	}

	public void setPlay_region(TextureRegion play_region) {
		this.play_region = play_region;
	}

	public BuildableBitmapTextureAtlas getMenuTextureAtlas() {
		return menuTextureAtlas;
	}

	public void setMenuTextureAtlas(BuildableBitmapTextureAtlas menuTextureAtlas) {
		this.menuTextureAtlas = menuTextureAtlas;
	}

	public TiledTextureRegion getSoccerballRegion() {
		return soccerballRegion;
	}

	public void setSoccerballRegion(TiledTextureRegion soccerballRegion) {
		this.soccerballRegion = soccerballRegion;
	}

	public TiledTextureRegion getBasketballRegion() {
		return basketballRegion;
	}

	public void setBasketballRegion(TiledTextureRegion basketballRegion) {
		this.basketballRegion = basketballRegion;
	}

	public TiledTextureRegion getFootballEnemyRegion() {
		return this.footballRegion;
	}

	public void setFootballEnemyRegion(TiledTextureRegion footballRegion) {
		this.footballRegion = footballRegion;
	}

	public TiledTextureRegion getBeachballRegion() {
		return beachballRegion;
	}
	
	public void setBeachballRegion(TiledTextureRegion beachballRegion) {
		this.beachballRegion = beachballRegion;
	}
	
	public TiledTextureRegion getTurretTowerRegion() {
		return turretTowerRegion;
	}

	public void setTurretTowerRegion(TiledTextureRegion turretTowerRegion) {
		this.turretTowerRegion = turretTowerRegion;
	}

	public TextureRegion getDartTowerRegion() {
		return dartTowerRegion;
	}

	public void setDartTowerRegion(TextureRegion dartTowerRegion) {
		this.dartTowerRegion = dartTowerRegion;
	}

	public TextureRegion getFlameTowerRegion() {
		return flameTowerRegion;
	}

	public void setFlameTowerRegion(TextureRegion flameTowerRegion) {
		this.flameTowerRegion = flameTowerRegion;
	}
	
	public TextureRegion getIceTowerRegion() {
		return iceTowerRegion;
	}

	public void setIceTowerRegion(TextureRegion iceTowerRegion) {
		this.iceTowerRegion = iceTowerRegion;
	}

	public TextureRegion getDartBulletRegion() {
		return dartBulletRegion;
	}

	public void setDartBulletRegion(TextureRegion dartBulletRegion) {
		this.dartBulletRegion = dartBulletRegion;
	}

	public TextureRegion getFlameParticleRegion() {
		return flameParticleRegion;
	}

	public void setFlameParticleRegion(TextureRegion flameParticleRegion) {
		this.flameParticleRegion = flameParticleRegion;
	}
	
	public TextureRegion getIcicleRegion() {
		return icicleRegion;
	}

	public void setIcicleRegion(TextureRegion icicleRegion) {
		this.icicleRegion = icicleRegion;
	}

	public TextureRegion getTowerSightRegion() {
		return towerSightRegion;
	}

	public void setTowerSightRegion(TextureRegion towerSightRegion) {
		this.towerSightRegion = towerSightRegion;
	}

	public TextureRegion getUpgradeOptionRegion() {
		return upgradeOptionRegion;
	}

	public void setUpgradeOptionRegion(TextureRegion upgradeOptionRegion) {
		this.upgradeOptionRegion = upgradeOptionRegion;
	}

	public TextureRegion getDeleteOptionRegion() {
		return deleteOptionRegion;
	}

	public void setDeleteOptionRegion(TextureRegion deleteOptionRegion) {
		this.deleteOptionRegion = deleteOptionRegion;
	}

	public TiledTextureRegion getStartButtonRegion() {
		return startButtonRegion;
	}

	public void setStartButtonRegion(TiledTextureRegion startButtonRegion) {
		this.startButtonRegion = startButtonRegion;
	}

	public TextureRegion getRedScreen() {
		return redScreen;
	}
	
	public void setRedScreen(TextureRegion redScreen) {
		this.redScreen = redScreen;
	}
	
	public BuildableBitmapTextureAtlas getGameTextureAtlas() {
		return gameTextureAtlas;
	}

	public void setGameTextureAtlas(BuildableBitmapTextureAtlas gameTextureAtlas) {
		this.gameTextureAtlas = gameTextureAtlas;
	}

	public Font getFont() {
		return font;
	}
	public void setFont(Font font) {
		this.font = font;
	}
	
	public Sound getPopSound() {
		return popSound;
	}
	public Sound getFreezeSound() {
		return freezeSound;
	}
	
	
}
