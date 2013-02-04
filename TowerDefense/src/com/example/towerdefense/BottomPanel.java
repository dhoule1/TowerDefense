package com.example.towerdefense;

import java.util.ArrayList;
import java.util.List;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.opengl.font.Font;
import org.andengine.util.color.Color;

public class BottomPanel extends HUD{
	
	private List<TowerTile> tiles;
	private float distanceFromMapToScene;
	private ZoomCamera mCamera;
	private Text money;
	private String moneyText;
	private Text wave;
	private String waveText;
	private Text life;
	private String lifeText;
	
	public BottomPanel(ZoomCamera camera, TMXTiledMap map) {
		super();
		
		tiles = new ArrayList<TowerTile>();
		
		this.mCamera = camera;
		
		distanceFromMapToScene = camera.getBoundsHeight() - (map.getTileRows()*map.getTileHeight());
		this.setPosition(0.0f, camera.getHeight()/3 - distanceFromMapToScene/2);

		camera.setHUD(this);
		
		TowerDefenseActivity activity = TowerDefenseActivity.getSharedInstance();
		GameScene scene = GameScene.getSharedInstance();
		
		Font font = activity.getInGameFont();
		
		moneyText = activity.getString(R.string.money_text);
		money = new Text(0, 0, font, "Money: $1234567890",
				activity.getVertexBufferObjectManager());
		money.setText(moneyText.concat(scene.getMoney()+""));
		money.setScale(0.7f);
		money.setPosition(mCamera.getBoundsWidth()/2 + money.getWidthScaled(), mCamera.getBoundsHeight()*3/4 - money.getHeightScaled()*1.3f);
		this.attachChild(money);
		
		waveText = activity.getString(R.string.wave_text);
		wave = new Text(0, 0, font, "Wave: 123",
				activity.getVertexBufferObjectManager());
		wave.setText(waveText+" "+scene.getWaveCount());
		wave.setScale(0.7f);
		wave.setPosition(money);
		wave.setY(money.getY() + wave.getHeightScaled());
		this.attachChild(wave);
		
		lifeText = activity.getString(R.string.life_text);
		life = new Text(0, 0, font, "Lives: 123",
				activity.getVertexBufferObjectManager());
		life.setText(lifeText+" "+scene.getLives());
		life.setScale(0.7f);
		life.setPosition(mCamera.getBoundsWidth()*2/3 + life.getWidthScaled(), -1* mCamera.getBoundsHeight() + life.getHeightScaled()*3);
		life.setColor(Color.BLACK);
		this.attachChild(life);
		
	}
	
	public void placeTowerAccess(TowerTile tile, int count) {
		tiles.add(tile);
		
		tile.returnOnTouched();
		
		if (count > 1) {
			Line line = new Line(0,0,0,0,TowerDefenseActivity.getSharedInstance().getVertexBufferObjectManager());
			line.setColor(Color.BLACK);
			line.setLineWidth(10.0f);
			line.setPosition(((count-1)*tile.getFrame().getWidthScaled()) + line.getLineWidth()/4.9f, this.getY() + tile.getFrame().getHeightScaled(),
					((count-1)*tile.getFrame().getWidthScaled()) + line.getLineWidth()/4.9f, this.getY());
			this.attachChild(line);
		}
		
		tile.getFrame().setPosition((count-1)*tile.getFrame().getWidthScaled(), this.getY());
		tile.getSprite().setPosition(tile.getFrame());
		
		this.attachChild(tile.getFrame());
		this.attachChild(tile.getSprite());
		
		this.registerTouchArea(tile.getFrame());
	}
	
	public void placeStartButton(Sprite button) {
		this.attachChild(button);
		this.registerTouchArea(button);
		button.setPosition(mCamera.getBoundsWidth() - button.getWidthScaled()*1.55f, this.getY() - button.getHeightScaled() /1.8f);
	}
	
	public List<TowerTile> getTiles() {
		return tiles;
	}
	public void setMoneyText(Integer text) {
		money.setText(moneyText+text);
	}
	public void setWaveText(Integer text) {
		wave.setText(waveText+" "+text);
	}
	public void setLifeText(Integer text) {
		life.setText(lifeText+" "+text);
	}

}
