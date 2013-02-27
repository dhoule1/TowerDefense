package com.example.towerdefense;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

import com.example.towerdefense.SceneManager.SceneType;

public class LoadingScene extends BaseScene{

	@Override
	public void createScene() {
		setBackground(new Background(Color.WHITE));
    Text text = new Text(0.0f,0.0f,resourceManager.getBlackFont(), "Loading...", vbom);
    text.setPosition(camera.getWidth()/2 - text.getWidthScaled()/2,camera.getHeight()/2 - text.getHeightScaled()/2);
    this.attachChild(text);
		
	}

	@Override
	public void onBackKeyPressed() {
		return;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}

}
