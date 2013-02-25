package com.example.towerdefense;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.ScrollScene;
import org.andengine.entity.scene.ScrollScene.IOnScrollScenePageListener;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.ease.EaseBackOut;

import com.example.towerdefense.GameMap.MapType;
import com.example.towerdefense.SceneManager.SceneType;

public class LevelChooserScene extends BaseScene implements IOnScrollScenePageListener{
	
	private Sprite desertSprite;
	private Sprite grassSprite;
	
	private float downX;
	private float downY;
	
	private Text levelDescr;
	private String levelDescrText;
	
	String levelOneText;
	String levelTwoText;
	
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
		Sprite text = new Sprite(backgroundSprite.getWidth()/2,
				backgroundSprite.getHeight()/3, resources.getLevelTextRegion(), resources.getVbom());
		
		text.setPosition(backgroundSprite.getWidth()/2 - text.getWidth()/2,
				backgroundSprite.getHeight()/6);
		
		backgroundSprite.attachChild(text);
		
		childScene.setBackground(new SpriteBackground(backgroundSprite));
		
		final float centerX = (childScene.getPageWidth() - resources.getDesertImageProfileRegion().getWidth()) / 2;
		final float centerY = (childScene.getPageHeight() - resources.getDesertImageProfileRegion().getHeight()) / 2;
		
		final Rectangle page1 = new Rectangle(0.0f,0.0f,0.0f,0.0f, resources.getVbom());
		grassSprite = new Sprite(centerX,centerY,resources.getGrassImageProfileRegion(), resources.getVbom());
		page1.attachChild(grassSprite);
		childScene.addPage(page1);
		
		final Rectangle page2 = new Rectangle(0.0f,0.0f,0.0f,0.0f, resources.getVbom());
		desertSprite = new Sprite(centerX,centerY,resources.getDesertImageProfileRegion(), resources.getVbom());
		page2.attachChild(desertSprite);
		childScene.addPage(page2);
		
		childScene.setEaseFunction(EaseBackOut.getInstance());
		childScene.registerScrollScenePageListener(this);
		this.setTouchAreaBindingOnActionDownEnabled(false);
		
		levelOneText = "Level 1\nGrasslands";
		levelTwoText = "Level 2\nDesert";
		
		levelDescr = new Text(0.0f,0.0f, resources.getFont(), "Stage 123\nGrasslandsssssssss", resources.getVbom());
		levelDescrText = levelOneText;		
		
		levelDescr.setText(levelDescrText);
		levelDescr.setPosition(camera.getWidth()/2 - levelDescr.getWidthScaled()/2, camera.getHeight() - levelDescr.getHeightScaled() * 1.5f);
		
		HUD hud = new HUD();
		camera.setHUD(hud);
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
					else if (desertSprite.contains(x, y) && childScene.getCurrentPage().equals(page2)) {
						if (Math.abs(downX - x) < 10.0f && Math.abs(downY - y) < 10.0f) {
							SceneManager.getInstance().loadGameScene(ResourceManager.getInstance().getEngine(), MapType.DESERT);
						}
					}
				}
				return false;
			}
		};
		childScene.registerTouchArea(screen);
		
		this.setChildScene(childScene);		
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
			case 0: levelDescr.setText(levelOneText);
			        break;
			case 1: levelDescr.setText(levelTwoText);
			        break;
			default:
		}
	}

}
