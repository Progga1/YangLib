package yang.graphics.textures;

import yang.graphics.translator.Texture;
import yang.math.objects.Quadruple;

public class TextureCoordBounds extends Quadruple{

	public TextureCoordBounds() {
		super(0,0,1,1);
	}
	
	public TextureCoordBounds(float left,float top,float width,float height) {
		super(left,top,width,height);
	}
	
	public TextureCoordBounds(Texture texture, float rectLeft,float rectTop,float rectWidth,float rectHeight) {
		this(rectLeft/texture.mWidth,rectTop/texture.mHeight,rectWidth/texture.mWidth,rectHeight/texture.mHeight);
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
