package com.example.towerdefense;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

public class InGameMenuScene extends MenuScene implements IOnMenuItemClickListener{
	
	private final int MENU = 0;
	private final int RESTART = 1;
	private final int QUIT = 2;
	
	public InGameMenuScene(ZoomCamera camera) {
		super(camera);
		ResourceManager resourceManager = ResourceManager.getInstance();
		VertexBufferObjectManager vbom = resourceManager.getVbom();
		
		final IMenuItem backToMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU, resourceManager.getMenuButtonRegion(), vbom), 1.2f, 1);
		final IMenuItem restartItem = new ScaleMenuItemDecorator(new SpriteMenuItem(RESTART, resourceManager.getRestartButtonRegion(), vbom), 1.2f, 1);
		final IMenuItem quitItem = new ScaleMenuItemDecorator(new SpriteMenuItem(QUIT, resourceManager.getQuitButtonRegion(), vbom), 1.2f, 1);
		
		this.addMenuItem(backToMenuItem);
		this.addMenuItem(restartItem);
		this.addMenuItem(quitItem);
		
		this.buildAnimations();
		this.setBackgroundEnabled(false);
		this.setOnMenuItemClickListener(this);
	}
	
	@Override
	public void onAttached() {
		super.onAttached();
		Log.i("Here", "Here");
		
		this.getParent().getParent().setIgnoreUpdate(true);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		Engine engine = ResourceManager.getInstance().getEngine();
		
		switch(pMenuItem.getID()) {
		case MENU:
			SceneManager.getInstance().loadMenuSceneFromGame(engine);
			return true;
		case RESTART:
			SceneManager.getInstance().loadGameScene(engine, GameScene.getSharedInstance().getMapType());
			return true;
		case QUIT:
			TowerDefenseActivity.getSharedInstance().finish();
			System.exit(0);
			return true;
		default: return false;
		}
	}

}
