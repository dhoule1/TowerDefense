package com.example.towerdefense;

import org.andengine.entity.primitive.Ellipse;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;



public class Tower extends Sprite{
	private Rectangle sight;
	private Ellipse reticle;
	private float radius;

	public Tower(float pX, float pY, float r, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		radius = r;
		sight = new Rectangle(this.getX(), this.getY(),
				radius*2, radius*2, pVertexBufferObjectManager);
		sight.setColor(Color.YELLOW);
		reticle = new Ellipse(this.getX()+this.getWidthScaled(), this.getY()+this.getHeightScaled(),
				radius, radius, pVertexBufferObjectManager);
		reticle.setLineWidth(2.0f);
		reticle.setColor(Color.BLACK);
	}
	
	@Override 
	public void setPosition(float x, float y) {
		super.setPosition(x,y);
		sight.setPosition(x-30.0f,y-30.0f);
		reticle.setPosition(this.getX()+this.getWidthScaled(), this.getY()+this.getHeightScaled());
	}
	
	public Rectangle getSight() {
		return sight;
	}
	public Ellipse getReticle() {
		return reticle;
	}

}
