package yang.graphics.textures;

import yang.graphics.translator.Texture;
import yang.math.objects.Quadruple;

public class TextureCoordBounds extends Quadruple{

	public static final TextureCoordBounds FULL = new TextureCoordBounds(0,0,1,1);

	public TextureCoordBounds() {
		super(0,0,1,1);
	}

	public TextureCoordBounds(float left,float top,float width,float height) {
		super(left,top,width,height);
	}

	public TextureCoordBounds(Texture texture, float rectLeft,float rectTop,float rectWidth,float rectHeight) {
		this(rectLeft/texture.mWidth,rectTop/texture.mHeight,rectWidth/texture.mWidth,rectHeight/texture.mHeight);
	}

	public TextureCoordBounds setBiased(int left,int top,int width,int height,int texWidth,int texHeight, float bias) {
		set((left+bias)/texWidth,(top+bias)/texHeight,(width-bias*2)/texWidth,(height-bias*2)/texHeight);
		return this;
	}

	public void set(Texture texture, float rectLeft,float rectTop,float rectWidth,float rectHeight) {
		set(rectLeft/texture.mWidth,rectTop/texture.mHeight,rectWidth/texture.mWidth,rectHeight/texture.mHeight);
	}


	public float getWidth() {
		return mValues[2];
	}

	public float getHeight() {
		return mValues[3];
	}

	public float getLeft() {
		return mValues[0];
	}

	public float getTop() {
		return mValues[1];
	}

	public float getRight() {
		return mValues[0]+mValues[2];
	}

	public float getBottom() {
		return mValues[1]+mValues[3];
	}
}
