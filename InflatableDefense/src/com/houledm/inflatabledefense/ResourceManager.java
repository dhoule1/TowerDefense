package com.houledm.inflatabledefense;

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
	private InflatableDefenseActivity activity;
	private ZoomCamera camera;
	private VertexBufferObjectManager vbom;
	
	private TextureRegion splashRegion;
	private BitmapTextureAtlas splashTextureAtlas;
	
	private TextureRegion menuBackgroundRegion;
	private TextureRegion playRegion;
	private TextureRegion creditsRegion;
	private BuildableBitmapTextureAtlas menuTextureAtlas;

	private TextureRegion desertImageProfileRegion;
	private TextureRegion lockedDesertImageProfileRegion;
	private TextureRegion grassImageProfileRegion;
	private TextureRegion tundraImageProfileRegion;
	private TextureRegion lockedTundraImageProfileRegion;
	private BuildableBitmapTextureAtlas levelChooserTextureAtlas;
	
	private TiledTextureRegion soccerballRegion;
	private TiledTextureRegion basketballRegion;
	private TiledTextureRegion footballRegion;
	private TiledTextureRegion beachballRegion;
	private TiledTextureRegion bowlingballRegion;
	
	private TiledTextureRegion turretTowerRegion;
	private TextureRegion dartTowerRegion;
	private TextureRegion flameTowerRegion;
	private TextureRegion iceTowerRegion;
	private TiledTextureRegion spikeTowerRegion;
	
	private TextureRegion dartBulletRegion;
	private TextureRegion flameParticleRegion;
	private TextureRegion icicleRegion;
	
	//Sub Menu Items
	private TextureRegion towerSightRegion;
	private TextureRegion upgradeOptionRegion;
	private TextureRegion deleteOptionRegion;
	
	private TiledTextureRegion startButtonRegion;
	
	private TextureRegion redScreen;
	
	//In-Game Menu Items
	private TextureRegion menuButtonRegion;
	private TextureRegion restartButtonRegion;
	private TextureRegion quitButtonRegion;
	
	private TextureRegion fireworksRegion;
	
	private BuildableBitmapTextureAtlas gameTextureAtlas1;
	private BuildableBitmapTextureAtlas gameTextureAtlas2;
	
	private Font whiteFont;
	private Font blackFont;
	private Font bubbleFont;
	
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
	public void loadCreditsResources() {
		loadCreditsGraphics();
		loadCreditsFonts();
		loadCreditsAudio();
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
		playRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "start_button.png");
		creditsRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "credits_button.png");
		       
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
    final ITexture whiteFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    whiteFont = FontFactory.createFromAsset(activity.getFontManager(), whiteFontTexture, activity.getAssets(), "shruti.ttf", 40.0f, true, Color.WHITE);
    whiteFont.load();
    
    final ITexture blackFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    blackFont = FontFactory.createFromAsset(activity.getFontManager(), blackFontTexture, activity.getAssets(), "shruti.ttf", 40.0f, true, Color.BLACK);
    blackFont.load();
    
    final ITexture bubbleFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
    bubbleFont = FontFactory.createFromAsset(activity.getFontManager(), bubbleFontTexture, activity.getAssets(), "celebrater.ttf", 80.0f, true, Color.YELLOW);
    bubbleFont.load();
	}

	private void loadMenuAudio() {}
	
	public void loadMenuTextures() {
		menuTextureAtlas.load();
	}
	
	public void loadLevelChooserGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/level/");
		levelChooserTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.DEFAULT);
		desertImageProfileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelChooserTextureAtlas, activity, "new_desert_image.png");
		lockedDesertImageProfileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelChooserTextureAtlas, activity, "desert_image_locked.png");
		grassImageProfileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelChooserTextureAtlas, activity, "grass_image.png");
		tundraImageProfileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelChooserTextureAtlas, activity, "tundra_image.png");
		lockedTundraImageProfileRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(levelChooserTextureAtlas, activity, "tundra_image_locked.png");
		
		try {
			levelChooserTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			levelChooserTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			e.printStackTrace();
		}
	}
	public void loadLevelChooserFonts(){}
	public void loadLevelChooserAudio(){}
	
	public void loadCreditsGraphics(){}
	public void loadCreditsFonts(){}
	public void loadCreditsAudio(){}
	
	

	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		gameTextureAtlas1 = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1200, 1200, TextureOptions.DEFAULT);
		gameTextureAtlas2 = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 800, 480, TextureOptions.DEFAULT);
		
		//ENEMIES
		soccerballRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas1, activity, "soccer_ball_tiled_2.png", 2, 1);
		basketballRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas1, activity, "basketball_tiled2.png", 2,1); 
		footballRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas1, activity, "football_tiled.png",2,1); 
		beachballRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas1, activity, "beach_ball_tiled_2.png",2,1);
		bowlingballRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas1, activity, "bowling_ball_tiled.png",2,1);
		
		//TOWERS
		turretTowerRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas1, activity, "new_turret_tiled.png", 4,1); 
		dartTowerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "dart_tower.png"); 
		flameTowerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "flame_tower.png"); //80x80
		iceTowerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "ice_tower.png"); //80x80
		spikeTowerRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas1, activity, "spike_tower_tiled.png", 5,1); //80x160
		
		//BULLETS
		dartBulletRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "new_dart.png"); //249x71
		flameParticleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "particle_fire.png"); //32x32
		icicleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "icicle_2_light_trans.png");
		
		//SUB-MENU ITEMS
		towerSightRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "tower_sight.png"); //100x100
		upgradeOptionRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "up_arrow.png"); //228x221
		deleteOptionRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "red_x.png"); //238x208
		
		//MISC
		startButtonRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas1, activity, "play_fast.png",2,2); //169x169
		redScreen = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "red_screen.png");
		
		//In-Game Menu Buttons
		menuButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "menu_button.png");
		restartButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "restart_button.png");
		quitButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas1, activity, "quit_button.png");
		
		//fireworks screen
		fireworksRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas2, activity, "fireworks.png");
		
		try {
		    this.gameTextureAtlas1.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
		    this.gameTextureAtlas1.load(); 
		    this.gameTextureAtlas2.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
		    this.gameTextureAtlas2.load();
		    }
		catch (final TextureAtlasBuilderException e){Debug.e(e);}

	}

	private void loadGameFonts(){ 

	}

	private void loadGameAudio() {
		try {
			popSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity,"sfx/pop2.ogg");
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
		splashRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "zapdroid.png", 0, 0);
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
	public void unloadCreditsScene() {}
	public void unloadGameScene() {
		gameTextureAtlas1.unload();
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
	public static void prepareManager(Engine engine, InflatableDefenseActivity activity, ZoomCamera camera, VertexBufferObjectManager vbom)
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

	public InflatableDefenseActivity getActivity() {
		return activity;
	}

	public void setActivity(InflatableDefenseActivity activity) {
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
	public TextureRegion getDesertImageProfileRegion() {
		return desertImageProfileRegion;
	}
	public void setDesertImageProfileRegion(TextureRegion desertImageProfileRegion) {
		this.desertImageProfileRegion = desertImageProfileRegion;
	}
	public TextureRegion getLockedDesertImageProfileRegion() {
		return lockedDesertImageProfileRegion;
	}
	public void setLockedDesertImageProfileRegion(
			TextureRegion lockedDesertImageProfileRegion) {
		this.lockedDesertImageProfileRegion = lockedDesertImageProfileRegion;
	}
	public TextureRegion getGrassImageProfileRegion() {
		return grassImageProfileRegion;
	}
	public void setGrassImageProfileRegion(TextureRegion grassImageProfileRegion) {
		this.grassImageProfileRegion = grassImageProfileRegion;
	}
	public TextureRegion getTundraImageProfileRegion() {
		return tundraImageProfileRegion;
	}
	public void setTundraImageProfileRegion(TextureRegion tundraImageProfileRegion) {
		this.tundraImageProfileRegion = tundraImageProfileRegion;
	}
	public TextureRegion getLockedTundraImageProfileRegion() {
		return lockedTundraImageProfileRegion;
	}
	public void setLockedTundraImageProfileRegion(
			TextureRegion lockedTundraImageProfileRegion) {
		this.lockedTundraImageProfileRegion = lockedTundraImageProfileRegion;
	}
	public BuildableBitmapTextureAtlas getLevelChooserTextureAtlas() {
		return levelChooserTextureAtlas;
	}
	public void setLevelChooserTextureAtlas(
			BuildableBitmapTextureAtlas levelChooserTextureAtlas) {
		this.levelChooserTextureAtlas = levelChooserTextureAtlas;
	}
	
	public TextureRegion getPlay_region() {
		return playRegion;
	}

	public void setPlay_region(TextureRegion play_region) {
		this.playRegion = play_region;
	}

	public TextureRegion getCreditsRegion() {
		return creditsRegion;
	}
	public void setCreditsRegion(TextureRegion creditsRegion) {
		this.creditsRegion = creditsRegion;
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
	
	public TiledTextureRegion getBowlingballRegion() {
		return bowlingballRegion;
	}
	
	public void setBowlingballRegion(TiledTextureRegion bowlingballRegion) {
		this.bowlingballRegion = bowlingballRegion;
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

	public TiledTextureRegion getSpikeTowerRegion() {
		return spikeTowerRegion;
	}
	
	public void setSpikeTowerRegion(TiledTextureRegion spikeTowerRegion) {
		this.spikeTowerRegion = spikeTowerRegion;
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
	
	public TextureRegion getMenuButtonRegion() {
		return menuButtonRegion;
	}
	public void setMenuButtonRegion(TextureRegion menuButtonRegion) {
		this.menuButtonRegion = menuButtonRegion;
	}
	public TextureRegion getRestartButtonRegion() {
		return restartButtonRegion;
	}
	public void setRestartButtonRegion(TextureRegion restartButtonRegion) {
		this.restartButtonRegion = restartButtonRegion;
	}
	public TextureRegion getQuitButtonRegion() {
		return quitButtonRegion;
	}
	public void setQuitButtonRegion(TextureRegion quitButtonRegion) {
		this.quitButtonRegion = quitButtonRegion;
	}
	public TextureRegion getFireworksRegion() {
		return fireworksRegion;
	}
	public void setFireworksRegion(TextureRegion fireworksRegion) {
		this.fireworksRegion = fireworksRegion;
	}
	public BuildableBitmapTextureAtlas getGameTextureAtlas() {
		return gameTextureAtlas1;
	}

	public void setGameTextureAtlas(BuildableBitmapTextureAtlas gameTextureAtlas1) {
		this.gameTextureAtlas1 = gameTextureAtlas1;
	}

	public Font getWhiteFont() {
		return whiteFont;
	}
	public void setWhiteFont(Font whiteFont) {
		this.whiteFont = whiteFont;
	}
	public Font getBlackFont() {
		return blackFont;
	}
	public void setBlackFont(Font blackFont) {
		this.blackFont = blackFont;
	}
	
	public Font getBubbleFont() {
		return bubbleFont;
	}
	public void setBubbleFont(Font bubbleFont) {
		this.bubbleFont = bubbleFont;
	}
	public Sound getPopSound() {
		return popSound;
	}
	public Sound getFreezeSound() {
		return freezeSound;
	}
	
	
}
