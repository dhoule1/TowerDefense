package com.houledm.inflatabledefense;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.ScrollScene;
import org.andengine.entity.scene.ScrollScene.IOnScrollScenePageListener;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.modifier.ease.EaseBackOut;

import android.content.SharedPreferences;

import com.houledm.inflatabledefense.GameMap.MapType;
import com.houledm.inflatabledefense.SceneManager.SceneType;

public class LevelChooserScene extends BaseScene implements IOnScrollScenePageListener{
	
	private Sprite desertSprite;
	private Sprite grassSprite;
	private Sprite tundraSprite;
	private Sprite caveSprite;
	private Sprite beachSprite;
	
	private float downX;
	private float downY;
	
	private Text levelNumber;	
	private Text levelDescr;
	
	private ScrollScene childScene;
	
	public LevelChooserScene() {

		childScene = new ScrollScene(ResourceManager.getInstance().getCamera().getWidth(), 
				ResourceManager.getInstance().getCamera().getHeight());
		
		ResourceManager resources = ResourceManager.getInstance();
		resources.loadLevelChooserGraphics();
		ZoomCamera camera = resources.getCamera();
		
		childScene.setOffset(450.0f);
		
		childScene.setBackgroundEnabled(true);
		
		Sprite backgroundSprite = new Sprite(0.0f,-20.0f,resources.getMenuBackgroundRegion(),resources.getVbom());
		
		Text text = new Text(0,0,resources.getBubbleFont(),"Level Select",vbom);
		text.setPosition(camera.getWidth()/2 - text.getWidthScaled()/2, camera.getHeight()/10);
		backgroundSprite.attachChild(text);
		
		childScene.setBackground(new SpriteBackground(backgroundSprite));
		
		final float centerX = (childScene.getPageWidth() - resources.getDesertImageProfileRegion().getWidth()) / 2;
		final float centerY = (childScene.getPageHeight() - resources.getDesertImageProfileRegion().getHeight()) / 2;
		
		final Rectangle page1 = new Rectangle(0.0f,0.0f,0.0f,0.0f, resources.getVbom());
		grassSprite = new Sprite(centerX,centerY,resources.getGrassImageProfileRegion(), resources.getVbom());
		page1.attachChild(grassSprite);
		childScene.addPage(page1);
		
		final Rectangle page2 = new Rectangle(0.0f,0.0f,0.0f,0.0f, resources.getVbom());
		TextureRegion desertTexture = (unlocked(1)) ? resources.getDesertImageProfileRegion() : resources.getLockedDesertImageProfileRegion();
		desertSprite = new Sprite(centerX,centerY, desertTexture, resources.getVbom());
		page2.attachChild(desertSprite);
		childScene.addPage(page2);
		
		final Rectangle page3 = new Rectangle(0.0f,0.0f,0.0f,0.0f, resources.getVbom());
	  TextureRegion tundraTexture = (unlocked(2)) ? resources.getTundraImageProfileRegion() : resources.getLockedTundraImageProfileRegion();
		tundraSprite = new Sprite(centerX,centerY,tundraTexture, resources.getVbom());
		page3.attachChild(tundraSprite);
		childScene.addPage(page3);
		
		final Rectangle page4 = new Rectangle(0.0f,0.0f,0.0f,0.0f, resources.getVbom());
	  TextureRegion caveTexture = (unlocked(3)) ? resources.getCaveImageProfileRegion() : resources.getLockedCaveImageProfileRegion();
		caveSprite = new Sprite(centerX,centerY,caveTexture, resources.getVbom());
		page4.attachChild(caveSprite);
		childScene.addPage(page4);
		
		final Rectangle page5 = new Rectangle(0.0f,0.0f,0.0f,0.0f, resources.getVbom());
	  TextureRegion beachTexture = (unlocked(4)) ? resources.getBeachImageProfileRegion() : resources.getLockedBeachImageProfileRegion();
		beachSprite = new Sprite(centerX,centerY,beachTexture, resources.getVbom());
		page5.attachChild(beachSprite);
		childScene.addPage(page5);
		
		childScene.setEaseFunction(EaseBackOut.getInstance());
		childScene.registerScrollScenePageListener(this);
		this.setTouchAreaBindingOnActionDownEnabled(false);
		
		levelNumber = new Text(0.0f,0.0f, resources.getBlackFont(), "Level 999", resources.getVbom());
		levelDescr = new Text(0.0f,0.0f, resources.getBlackFont(), "Grasslandsssssssss", resources.getVbom());
		
		levelNumber.setText("Level 1");
		
		
		
		levelNumber.setPosition(camera.getWidth()/2 - levelNumber.getWidthScaled()/2,
				camera.getHeight() - levelNumber.getHeightScaled() * 2.0f);
		levelDescr.setText("Grasslands");
		levelDescr.setPosition(camera.getWidth()/2 - levelDescr.getWidthScaled()/2,
				camera.getHeight() - levelDescr.getHeightScaled() * 2.5f);
		
		HUD hud = new HUD();
		camera.setHUD(hud);
		hud.attachChild(levelNumber);
		hud.attachChild(levelDescr);
		
		childScene.attachChild(hud);
		
		Rectangle screen = new Rectangle(0.0f,0.0f,camera.getWidth(),camera.getHeight(),resources.getVbom()) {
			@Override
			public boolean onAreaTouched(TouchEvent e, float x, float y) {
				if (e.isActionDown()) {
					downX = e.getX();
					downY = e.getY();
				}
				else if (e.isActionUp()) {
					
					if (grassSprite.contains(x, y) && childScene.getCurrentPage().equals(page1)) {
						if (Math.abs(downX - x) < 10.0f && Math.abs(downY - y) < 10.0f) {
							SceneManager.getInstance().loadGameScene(ResourceManager.getInstance().getEngine(), MapType.GRASS);
						}
					}					
					else if (desertSprite.contains(x, y) && childScene.getCurrentPage().equals(page2) && unlocked(1)) {
						if (Math.abs(downX - x) < 10.0f && Math.abs(downY - y) < 10.0f) {
							SceneManager.getInstance().loadGameScene(ResourceManager.getInstance().getEngine(), MapType.DESERT);
						}
					}
					else if (tundraSprite.contains(x, y) && childScene.getCurrentPage().equals(page3) && unlocked(2)) {
						if (Math.abs(downX - x) < 10.0f && Math.abs(downY - y) < 10.0f) {
							SceneManager.getInstance().loadGameScene(ResourceManager.getInstance().getEngine(), MapType.TUNDRA);
						}
					}
					else if (caveSprite.contains(x, y) && childScene.getCurrentPage().equals(page4) && unlocked(3)) {
						if (Math.abs(downX - x) < 10.0f && Math.abs(downY - y) < 10.0f) {
							SceneManager.getInstance().loadGameScene(ResourceManager.getInstance().getEngine(), MapType.CAVE);
						}
					}
					else if (beachSprite.contains(x, y) && childScene.getCurrentPage().equals(page5) && unlocked(4)) {
						if (Math.abs(downX - x) < 10.0f && Math.abs(downY - y) < 10.0f) {
							SceneManager.getInstance().loadGameScene(ResourceManager.getInstance().getEngine(), MapType.BEACH);
						}
					}
				}
				return false;
			}
		};
		childScene.registerTouchArea(screen);
		
		this.setChildScene(childScene);		
	}
	
	private boolean unlocked(int num) {
		SharedPreferences settings = activity.getSharedPreferences("level", 0);
		return settings.getBoolean(num+"", false);
	}

	@Override
	public void createScene() {}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuSceneFromLevelChooser(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LEVEL;
	}

	@Override
	public void disposeScene() {
	}

	@Override
	public void onMoveToPageStarted(int pPageNumber) {}

	@Override
	public void onMoveToPageFinished(int pPageNumber) {
		switch (pPageNumber) {
			case 0: levelNumber.setText("Level 1");
							levelDescr.setText("Grasslands");
			        break;
			case 1: levelNumber.setText("Level 2");
							levelDescr.setText("Desert");
			        break;
			case 2: levelNumber.setText("Level 3");
							levelDescr.setText("Tundra");
							break;
			case 3: levelNumber.setText("Level 4");
							levelDescr.setText("Cave");
							break;
			case 4: levelNumber.setText("Level 5");
							levelDescr.setText("Beach");
							break;
			default:
		}
		levelNumber.setPosition(camera.getWidth()/2 - levelNumber.getWidthScaled()/2,
				camera.getHeight() - levelNumber.getHeightScaled() * 2.0f);
		levelDescr.setPosition(camera.getWidth()/2 - levelDescr.getWidthScaled()/2,
				camera.getHeight() - levelDescr.getHeightScaled() * 2.5f);
	}

}
