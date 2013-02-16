package com.example.towerdefense;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.ui.activity.BaseGameActivity;

import android.view.KeyEvent;

public class TowerDefenseActivity extends BaseGameActivity {
	private static final int CAMERA_WIDTH = 800;
	private static final int CAMERA_HEIGHT = 480;
	
	private static TowerDefenseActivity instance;
	
	private ZoomCamera mCamera;

	//OVERRIDDEN METHODS
	//@Override
	//public Engine onCreateEngine(EngineOptions pEngineOptions) {
		//return new LimitedFPSEngine(pEngineOptions, 60);
	//}
	
	
	@Override
	public EngineOptions onCreateEngineOptions() {
    this.mCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
    instance = this;
        
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
		engineOptions.getAudioOptions().setNeedsSound(true);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		return engineOptions;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {
		
		try {
		
		ResourceManager.prepareManager(mEngine, this, mCamera, getVertexBufferObjectManager());
		pOnCreateResourcesCallback.onCreateResourcesFinished();
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		try {
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
		this.mEngine.registerUpdateHandler(new FPSLogger());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) {
		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() 
    {
            public void onTimePassed(final TimerHandler pTimerHandler) 
            {
                mEngine.unregisterUpdateHandler(pTimerHandler);
                SceneManager.getInstance().createMenuScene();
            }
    }));
    pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
    if (pKeyCode == KeyEvent.KEYCODE_BACK) {	      
	     SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
    }
    return false;
	}
	
	//GETTERS AND SETTERS
	public static TowerDefenseActivity getSharedInstance() {
		if (instance == null) {
			instance = new TowerDefenseActivity();
		}
	  return instance;
	}	
	public ZoomCamera getCamera() {
		return this.mCamera;
	}
}
