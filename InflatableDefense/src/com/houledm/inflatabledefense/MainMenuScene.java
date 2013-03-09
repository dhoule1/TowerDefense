package com.houledm.inflatabledefense;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.util.GLState;

import com.houledm.inflatabledefense.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {
	
	private MenuScene menuChildScene;
	private final int MENU_PLAY = 0;
	private final int CREDITS = 1;

	private void createMenuChildScene() {
	    menuChildScene = new MenuScene(camera);
	    menuChildScene.setPosition(0,0);
	    
	    final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourceManager.getPlay_region(), vbom), 1.2f, 1);
	    SpriteMenuItem credits = new SpriteMenuItem(CREDITS, resourceManager.getCreditsRegion(), vbom);
	    final IMenuItem creditsItem = new ScaleMenuItemDecorator(credits, 1.2f, 1);
	    
	    menuChildScene.addMenuItem(playMenuItem);
	    menuChildScene.addMenuItem(creditsItem);
	    
	    menuChildScene.buildAnimations();
	    menuChildScene.setBackgroundEnabled(false);
	    
	    menuChildScene.setOnMenuItemClickListener(this);
	    
	    setChildScene(menuChildScene);
	}
	
	public void createBackground() {
		Sprite background = new Sprite(0,-20, resourceManager.getMenuBackgroundRegion(), vbom){
      @Override
      protected void preDraw(GLState pGLState, Camera pCamera) 
      {
          super.preDraw(pGLState, pCamera);
          pGLState.enableDither();
      }
  };
  	Text text = new Text(0,0,resourceManager.getBubbleFont(), "Inflatable Defense",vbom);
  	text.setPosition(camera.getWidth()/2 - text.getWidthScaled()/2, camera.getHeight()/10);
		background.attachChild(text);
		attachChild(background);
	}

	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
	}

	@Override
	public void onBackKeyPressed() {
		InflatableDefenseActivity.getSharedInstance().finish();
		System.exit(0);
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
    switch(pMenuItem.getID()) {
    case MENU_PLAY:
    	SceneManager.getInstance().createLevelChooserScene();
        return true;
    case CREDITS:
    	SceneManager.getInstance().createCreditsScene();
    	return true;
    default:
        return false;
    }
	}

}
