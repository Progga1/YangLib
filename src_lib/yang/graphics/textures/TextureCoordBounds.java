package yang.graphics.textures;

import yang.math.objects.Quadruple;

public class TextureCoordBounds extends Quadruple{

	public TextureCoordBounds() {
		super();
	}
	
	public TextureCoordBounds(float x1,float y1,float width,float height) {
		super(x1,y1,width,height);
	}
	
	public TextureCoordBounds initBiased(int x1,int y1,int width,int height,int texWidth,int texHeight, float bias) {
		set((float)(x1+bias)/texWidth,(float)(y1+bias)/texHeight,(float)(width-bias*2)/texWidth,(float)(height-bias*2)/texHeight);
		return this;
	}

	public float getWidth() {
		return mValues[2];
	}
	
	public float getHeight() {
		return mValues[3];
	}
	
}
