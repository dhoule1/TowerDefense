package com.example.towerdefense;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

public class SceneManager {
//---------------------------------------------
  // SCENES
  //---------------------------------------------
  
  private BaseScene splashScene;
  private BaseScene menuScene;
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
  public void loadGameScene(final Engine mEngine) {
  	setScene(loadingScene);
  	ResourceManager.getInstance().unloadMenuTextures();
  	mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
    {
        public void onTimePassed(final TimerHandler pTimerHandler) 
        {
            mEngine.unregisterUpdateHandler(pTimerHandler);
            ResourceManager.getInstance().loadGameResources();
            gameScene = new GameScene();
            setScene(gameScene);
        }
    }));
  }
  
  public void loadMenuScene(final Engine mEngine)
  {
  	ResourceManager.getInstance().getCamera().setZoomFactor(1.0f);
      setScene(loadingScene);
      gameScene.disposeScene();
      ResourceManager.getInstance().unloadGameTextures();
      mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
      {
          public void onTimePassed(final TimerHandler pTimerHandler) 
          {
              mEngine.unregisterUpdateHandler(pTimerHandler);
              ResourceManager.getInstance().loadMenuTextures();
              setScene(menuScene);
          }
      }));
  }
  
  
  
  
  
  
  public void disposeSplashScene() {
  	if (splashScene == null) return;
  	ResourceManager.getInstance().unloadSplashScreen();
    splashScene.disposeScene();
    splashScene = null;
  }
  public void disposeMenuScene() {
  	ResourceManager.getInstance().unloadMenuTextures();
  }
  public void disposeGameScene() {
  	ResourceManager.getInstance().unloadGameTextures();
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
