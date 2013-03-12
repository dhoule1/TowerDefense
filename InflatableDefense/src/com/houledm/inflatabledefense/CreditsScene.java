package com.houledm.inflatabledefense;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.font.Font;
import org.andengine.util.color.Color;
import org.andengine.util.modifier.IModifier;

import com.houledm.inflatabledefense.SceneManager.SceneType;

public class CreditsScene extends BaseScene{
	
	public CreditsScene() {
		ResourceManager resources = ResourceManager.getInstance();
		
		Sprite backgroundSprite = new Sprite(0.0f,-20.0f,resources.getMenuBackgroundRegion(),resources.getVbom());
		
		Text text = new Text(0,0,resources.getBubbleFont(),"Credits",vbom);
		text.setPosition(camera.getWidth()/2 - text.getWidthScaled()/2, camera.getHeight()/10);
		backgroundSprite.attachChild(text);
		
		this.setBackground(new SpriteBackground(backgroundSprite));
		
		Font font = resources.getBlackFont();
		
		String celebraterString = "Thanks to Mans Greback for the Celebrater Bubble Font\nhttp://www.mawns.com/wordpress/";
		String lightningString = "Thanks to Honey & Death for the Lightning Volt Font\nhttp://honeyanddeath.web.fc2.com/index.html";
		String snowTileString = "Thanks to Xenodora and Nushio from http://opengameart.org/ for\nthe bridge and snowy tileset. Check out http://lpc.opengameart.org/";
		String caveTileString = "Thanks to MrBeast for the cave tileset\nhttp://opengameart.org/";
		String beachTileString = "Thanks to Sharm for his beach tileset\nhttp://opengameart.org/";
		String soundString = "Thanks to carlSablowEdwards for the popping sound and to the guys at\nhttp://www.soundjay.com for the freezing sound";
		
		Text celebraterText = new Text(0,0, font, celebraterString,vbom);
		celebraterText.setScale(0.50f);
		
		Text lightningText = new Text(0,0,font,lightningString,vbom);
		lightningText.setScale(0.50f);
		
		Text snowTileText = new Text(0,0,font, snowTileString,vbom);
		snowTileText.setScale(0.50f);
		
		Text caveTileText = new Text(0,0,font,caveTileString,vbom);
		caveTileText.setScale(0.50f);
		
		Text beachTileText = new Text(0,0,font,beachTileString,vbom);
		beachTileText.setScale(0.50f);
		
		Text soundText = new Text(0,0,font,soundString,vbom);
		soundText.setScale(0.50f);

		celebraterText.setX(-(celebraterText.getWidth() - celebraterText.getWidthScaled())/2);
		lightningText.setX(-(lightningText.getWidth() - lightningText.getWidthScaled())/2);
		snowTileText.setX(-(snowTileText.getWidth() - snowTileText.getWidthScaled())/2);
		caveTileText.setX(-(caveTileText.getWidth() - caveTileText.getWidthScaled())/2);
		beachTileText.setX(-(beachTileText.getWidth() - beachTileText.getWidthScaled())/2);
		soundText.setX(-(soundText.getWidth() - soundText.getWidthScaled())/2);
		
		final Rectangle rect = new Rectangle(0,camera.getHeight(),camera.getWidth(),celebraterText.getHeightScaled()*9.5f,vbom);
		rect.setColor(Color.WHITE);
		rect.setAlpha(0.8f);
		
		lightningText.setY(celebraterText.getY() + lightningText.getHeightScaled()*1.5f);
		snowTileText.setY(lightningText.getY() + snowTileText.getHeightScaled()*1.5f);
		caveTileText.setY(snowTileText.getY() + caveTileText.getHeightScaled()*1.5f);
		beachTileText.setY(caveTileText.getY() + beachTileText.getHeightScaled()*1.5f);
		soundText.setY(beachTileText.getY() + soundText.getHeightScaled()*1.5f);
		
		rect.attachChild(celebraterText);
		rect.attachChild(lightningText);
		rect.attachChild(snowTileText);
		rect.attachChild(caveTileText);
		rect.attachChild(beachTileText);
		rect.attachChild(soundText);
		
		final MoveYModifier move = new MoveYModifier(13.0f,rect.getY(),-rect.getHeight());
		
		move.addModifierListener(new IEntityModifier.IEntityModifierListener() {

			@Override
			public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				pItem.setY(camera.getHeight());
				pItem.clearEntityModifiers();
				
				move.reset();
				pItem.registerEntityModifier(move);
			}
			
		});
		
		rect.registerEntityModifier(move);
		
		this.attachChild(rect);
		
		
	}
	

	@Override
	public void createScene() {
	}

	@Override
	public void onBackKeyPressed() {
		disposeScene();
		SceneManager.getInstance().loadMenuSceneFromCredits(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_CREDITS;
	}

	@Override
	public void disposeScene() {
		ResourceManager.getInstance().unloadCreditsScene();
	}

}
