package com.example.towerdefense;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.example.towerdefense.GameMap.MapType;

public class SceneManager {
//---------------------------------------------
  // SCENES
  //---------------------------------------------
  
  private BaseScene splashScene;
  private BaseScene menuScene;
  private BaseScene levelChooserScene;
  private BaseScene gameScene;
  private BaseScene loadingScene;
  
  //---------------------------------------------
  // VARIABLES
  //---------------------------------------------
  
  private static final SceneManager INSTANCE = new SceneManager();
  
  private SceneType currentSceneType = SceneType.SCENE_SPLASH;
  
  private BaseScene currentScene;
  
  private Engine engine = ResourceManager.getInstance().getEngine();
  
  public enum SceneType
  {
      SCENE_SPLASH,
      SCENE_MENU,
      SCENE_LEVEL,
      SCENE_GAME,
      SCENE_LOADING,
  }
  
  //---------------------------------------------
  // CLASS LOGIC
  //---------------------------------------------
  
  public void setScene(BaseScene scene)
  {
      engine.setScene(scene);
      currentScene = scene;
      currentSceneType = scene.getSceneType();
  }
  
  public void setScene(SceneType sceneType)
  {
      switch (sceneType)
      {
          case SCENE_MENU:
              setScene(menuScene);
              break;
          case SCENE_GAME:
              setScene(gameScene);
              break;
          case SCENE_SPLASH:
              setScene(splashScene);
              break;
          case SCENE_LOADING:
              setScene(loadingScene);
              break;
          case SCENE_LEVEL:
          		setScene(levelChooserScene);
          default:
              break;
      }
  }
  
  public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {
      ResourceManager.getInstance().loadSplashScreen();
      splashScene = new SplashScene();
      currentScene = splashScene;
      pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
  }
  public void createMenuScene() {
  	ResourceManager.getInstance().loadMenuResources();
    menuScene = new MainMenuScene();
    loadingScene = new LoadingScene();
    setScene(menuScene);
    disposeSplashScene();
  }  
  public void createLevelChooserScene() {
  	ResourceManager.getInstance().loadLevelChooserResources();
  	levelChooserScene = new LevelChooserScene();
  	setScene(levelChooserScene);
  }
  public void loadGameScene(final Engine mEngine, final MapType type) {
  	ResourceManager.getInstance().getCamera().setHUD(null);
  	setScene(loadingScene);
  	
  	ZoomCamera mCamera = ResourceManager.getInstance().getCamera();
  	mCamera.setZoomFactor(1.0f);
  	mCamera.set(0, 0, mCamera.getWidth(), mCamera.getHeight());
  	
  	ResourceManager.getInstance().unloadLevelChooserScene();
  	mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
        public void onTimePassed(final TimerHandler pTimerHandler) {
            mEngine.unregisterUpdateHandler(pTimerHandler);
            ResourceManager.getInstance().loadGameResources();
            gameScene = new GameScene(type);
            setScene(gameScene);
        }
    }));
  }
  
  public void loadMenuSceneFromLevelChooser(final Engine engine) {
  	levelChooserScene.disposeScene();
  	ResourceManager.getInstance().unloadLevelChooserScene();
  	ResourceManager.getInstance().getCamera().setHUD(null);
  	
  	ResourceManager.getInstance().loadMenuTextures();
  	setScene(menuScene);
  }
  
  public void loadMenuSceneFromGame(final Engine mEngine) {
  	
  	ZoomCamera mCamera = ResourceManager.getInstance().getCamera();
  	
  	mCamera.setZoomFactor(1.0f);
  	mCamera.set(0, 0, mCamera.getWidth(), mCamera.getHeight());
		
  	gameScene.disposeScene();
  	ResourceManager.getInstance().unloadGameScene();
      setScene(loadingScene);      
      mEngine.registerUpdateHandler(new TimerHandler(1.0f, new ITimerCallback() {
          public void onTimePassed(final TimerHandler pTimerHandler) {
              mEngine.unregisterUpdateHandler(pTimerHandler);
              ResourceManager.getInstance().loadMenuTextures();
              setScene(menuScene);
          }
      }));
  }
  
  
  public void disposeSplashScene() {
  	if (splashScene == null) return;
  	ResourceManager.getInstance().unloadSplashScene();
    splashScene.disposeScene();
    splashScene = null;
  }
  public void disposeMenuScene() {
  	ResourceManager.getInstance().unloadMenuScene();
  }
  public void disposeLevelChooserScene() {
  	ResourceManager.getInstance().unloadLevelChooserScene();
  	levelChooserScene.disposeScene();
  }
  public void disposeGameScene() {
  	ResourceManager.getInstance().unloadGameScene();
  }
  
  //---------------------------------------------
  // GETTERS AND SETTERS
  //---------------------------------------------
  
  public static SceneManager getInstance()
  {
      return INSTANCE;
  }
  
  public SceneType getCurrentSceneType()
  {
      return currentSceneType;
  }
  
  public BaseScene getCurrentScene()
  {
      return currentScene;
  }

}
