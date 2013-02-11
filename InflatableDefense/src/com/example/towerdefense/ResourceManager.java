package com.example.towerdefense;

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
	
	private TextureRegion menu_background_region;
	private TextureRegion play_region;
	private BuildableBitmapTextureAtlas menuTextureAtlas;

	
	private TiledTextureRegion soccerballRegion;
	private TiledTextureRegion basketballRegion;
	private TextureRegion blackFlameEnemyRegion;
	
	private TiledTextureRegion turretTowerRegion;
	private TextureRegion dartTowerRegion;
	private TextureRegion flameTowerRegion;
	
	private TextureRegion dartBulletRegion;
	private TextureRegion flameParticleRegion;
	
	//Sub Menu Items
	private TextureRegion towerSightRegion;
	private TextureRegion upgradeOptionRegion;
	private TextureRegion deleteOptionRegion;
	
	private TextureRegion startButtonRegion;
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	
	private Font font;

	//---------------------------------------------
	// TEXTURES & TEXTURE REGIONS
	//---------------------------------------------

	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------

	public void loadMenuResources()
	{
		loadMenuGraphics();
		loadMenuAudio();
		loadMenuFonts();
	}

	public void loadGameResources()
	{
		loadGameGraphics();
		loadGameFonts();
		loadGameAudio();
	}

	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		menu_background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "menu_background.png");
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

    font = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "AeroviasBrasilNF.ttf", 50.0f, true, Color.WHITE, 2, Color.BLACK);
    font.load();
	}

	private void loadMenuAudio()
	{

	}
	
	public void loadMenuTextures() {
		menuTextureAtlas.load();
	}
	

	private void loadGameGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1200, 1200, TextureOptions.DEFAULT);
		
		//ENEMIES
		soccerballRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "soccer_ball_tiled.png", 2, 1); //14x27
		basketballRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "basketball_tiled2.png", 2,1); //14x27
		blackFlameEnemyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "black_flame_enemy.png"); //14x27
		
		//TOWERS
		turretTowerRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "turret_tiled.png", 2,1); //80x160
		dartTowerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "dart_tower.png"); //80x80
		flameTowerRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "flame_tower.png"); //80x80
		
		//BULLETS
		dartBulletRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "dart.png"); //249x71
		flameParticleRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "particle_fire.png"); //32x32
		
		//SUB-MENU ITEMS
		towerSightRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "tower_sight.png"); //100x100
		upgradeOptionRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "up_arrow.png"); //228x221
		deleteOptionRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "red_x.png"); //238x208
		
		//MISC
		startButtonRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "play_button_cropped.png"); //169x169
		
		try {
		    this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
		    this.gameTextureAtlas.load(); }
		catch (final TextureAtlasBuilderException e){Debug.e(e);}

	}

	private void loadGameFonts(){ 

	}

	private void loadGameAudio() {

	}

	public void loadSplashScreen() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1000, 1000, TextureOptions.BILINEAR);
		splashRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}

	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splashRegion = null;
	}
	public void unloadMenuTextures() {
		menuTextureAtlas.unload();
	}
	public void unloadGameTextures() {
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

	public TextureRegion getMenu_background_region() {
		return menu_background_region;
	}

	public void setMenu_background_region(TextureRegion menu_background_region) {
		this.menu_background_region = menu_background_region;
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

	public TextureRegion getBlackFlameEnemyRegion() {
		return blackFlameEnemyRegion;
	}

	public void setBlackFlameEnemyRegion(TextureRegion blackFlameEnemyRegion) {
		this.blackFlameEnemyRegion = blackFlameEnemyRegion;
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

	public TextureRegion getStartButtonRegion() {
		return startButtonRegion;
	}

	public void setStartButtonRegion(TextureRegion startButtonRegion) {
		this.startButtonRegion = startButtonRegion;
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
	
	
	
}
