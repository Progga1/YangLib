package yang.graphics.textures;

import yang.math.objects.Quadruple;

public class TextureCoordBounds extends Quadruple{

	public TextureCoordBounds() {
		super();
	}
	
	public TextureCoordBounds(float left,float top,float width,float height) {
		super(left,top,width,height);
	}
	
	public TextureCoordBounds initBiased(int left,int top,int width,int height,int texWidth,int texHeight, float bias) {
		set((float)(left+bias)/texWidth,(float)(top+bias)/texHeight,(float)(width-bias*2)/texWidth,(float)(height-bias*2)/texHeight);
		return this;
	}

	public float getWidth() {
		return mValues[2];
	}
	
	public float getHeight() {
		return mValues[3];
	}
	
}
