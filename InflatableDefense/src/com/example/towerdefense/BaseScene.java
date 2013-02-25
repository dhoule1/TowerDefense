package com.example.towerdefense;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.app.Activity;

import com.example.towerdefense.SceneManager.SceneType;

public abstract class BaseScene extends Scene{
//---------------------------------------------
  // VARIABLES
  //---------------------------------------------
  
  protected Engine engine;
  protected Activity activity;
  protected ResourceManager resourceManager;
  protected VertexBufferObjectManager vbom;
  protected ZoomCamera camera;
  
  //---------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------
  
  public BaseScene()
  {
      this.resourceManager = ResourceManager.getInstance();
      this.engine = resourceManager.getEngine();
      this.activity = resourceManager.getActivity();
      this.vbom = resourceManager.getVbom();
      this.camera = resourceManager.getCamera();
      createScene();
  }
  
  //---------------------------------------------
  // ABSTRACTION
  //---------------------------------------------
  
  public abstract void createScene();
  
  public abstract void onBackKeyPressed();
  
  public abstract SceneType getSceneType();
  
  public abstract void disposeScene();

}
