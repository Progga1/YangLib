package yang.graphics.textures;

import yang.graphics.translator.Texture;
import yang.model.Rect;

public class TextureCoordinatesQuad {

	public static float BIASPIXELS = 0.5f;

	public static final TextureCoordinatesQuad FULL_TEXTURE = new TextureCoordinatesQuad().init(0,0,1,1);
	public static final int ROTATE_CW_90 = 1;
	public static final int ROTATE_180 = 2;
	public static final int ROTATE_CCW_90 = 3;
	
	public float x1;
	public float y1;
	public float x2;
	public float y2;
	public float mRatioWidth;
	public float mWidth;
	public float mHeight;
	public float mRatio;
	public float[] mAppliedCoordinates;
	
	public TextureCoordinatesQuad() {
		
	}
	
	public void rotateCoords(int rotation) {
		while(rotation>0) {
			float cx = mAppliedCoordinates[0];
			float cy = mAppliedCoordinates[1];
			mAppliedCoordinates[0] = mAppliedCoordinates[4];
			mAppliedCoordinates[1] = mAppliedCoordinates[5];
			mAppliedCoordinates[4] = mAppliedCoordinates[6];
			mAppliedCoordinates[5] = mAppliedCoordinates[7];
			mAppliedCoordinates[6] = mAppliedCoordinates[2];
			mAppliedCoordinates[7] = mAppliedCoordinates[3];
			mAppliedCoordinates[2] = cx;
			mAppliedCoordinates[3] = cy;
			float h = mWidth;
			mWidth = mHeight;
			mHeight = h;
			rotation--;
		}
	}
	
	public void refreshCoordArray(int rotation) {
		mAppliedCoordinates = new float[8];
		mAppliedCoordinates[0] = x1;
		mAppliedCoordinates[1] = y2;
		mAppliedCoordinates[2] = x2;
		mAppliedCoordinates[3] = y2;
		mAppliedCoordinates[4] = x1;
		mAppliedCoordinates[5] = y1;
		mAppliedCoordinates[6] = x2;
		mAppliedCoordinates[7] = y1;
		if(rotation!=0)
			rotateCoords(rotation);
	}
	
	public void refreshCoordArray() {
		refreshCoordArray(0);
	}
	
	public TextureCoordinatesQuad initBiased(float x1, float y1, float x2, float y2, float biasX, float biasY, int rotation) {
		this.x1 = x1+biasX;
		this.y1 = y1+biasY;
		this.x2 = x2-biasX;
		this.y2 = y2-biasY;
		this.mWidth = x2-x1;
		this.mHeight = y2-y1;
		refreshCoordArray(rotation);
		mRatio = 1;
		mRatioWidth = mWidth/mHeight;
		return this;
	}
	
	public TextureCoordinatesQuad initBiased(float x1, float y1, float x2, float y2, float biasX, float biasY) {
		return initBiased(x1,y1,x2,y2,biasX,biasY,0);
	}
	
	public TextureCoordinatesQuad initBiased(float x1, float y1, float x2, float y2, float bias) {
		return initBiased(x1,y1,x2,y2,bias,bias);
	}
	
	public TextureCoordinatesQuad initBiased(float x1, float y1, float widthAndHeight,float bias) {
		return initBiased(x1,y1,x1+widthAndHeight,y1+widthAndHeight,bias,bias);
	}
	
	public TextureCoordinatesQuad init(float x1, float y1, float x2, float y2) {
		return initBiased(x1,y1,x2,y2,0,0);
	}
	
	public TextureCoordinatesQuad init(float x1, float y1, float widthAndHeight) {
		return initBiased(x1,y1,x1+widthAndHeight,y1+widthAndHeight,0,0);
	}
	
	public TextureCoordinatesQuad init(float x1, float y1, float x2, float y2, float textureWidth, float textureHeight) {
		initBiased((x1) / (float)textureWidth, 
			  (y1) / (float)textureHeight,
			 (x2) / (float)textureWidth, 
			 (y2) / (float)textureHeight, 
			 BIASPIXELS/(float)textureWidth, BIASPIXELS/(float)textureHeight);
		mRatio = (float)textureWidth / textureHeight;
		mRatioWidth = mWidth*mRatio/mHeight;
		return this;
	}
	

	public TextureCoordinatesQuad initBiased(float x1, float y1, float x2, float y2, float textureWidth, float textureHeight, float biasX,float biasY) {
		return init(x1+biasX,y1+biasY,x2-biasX,y2-biasY,textureWidth,textureHeight);
	}
	
	public TextureCoordinatesQuad init(float x1, float y1, float x2, float y2, Texture prefaceTexture) {
		return init(x1,y1,x2,y2,prefaceTexture.getWidth(),prefaceTexture.getHeight());
	}
	
	

	public final float getX1() {
		return x1;
	}
	
	public final float getY1() {
		return y1;
	}
	
	public final float getX2() {
		return x2;
	}
	
	public final float getY2() {
		return y2;
	}

	public final float getWidth() {
		return mWidth;
	}
	
	public final float getHeight() {
		return mHeight;
	}
	
	public final float getRatio() {
		return mRatio;
	}
	
	@Override
	public String toString() {
		return "("+x1+","+y1+","+x2+","+y2+")";
	}

	public TextureCoordinatesQuad intoRect(float left,float top,float width,float height) {
//		float deltaX = width-left;
//		float deltaY = height-top;
		x1 = x1*width+left;
		y1 = y1*height+top;
		x2 = x2*width+left;
		y2 = y2*height+top;
		refreshCoordArray();
		return this;
	}
	
	public TextureCoordinatesQuad intoRect(float[] values) {
		return intoRect(values[0],values[1],values[2],values[3]);
	}
	
	public TextureCoordinatesQuad intoRect(TextureCoordBounds bounds) {
		return intoRect(bounds.mValues);
	}
	
	public TextureCoordinatesQuad intoRect(Rect rect) {
		return intoRect(rect.mLeft,rect.mTop,rect.mRight,rect.mBottom);
	}
	
	@Override
	public TextureCoordinatesQuad clone() {
		return new TextureCoordinatesQuad().init(x1, y1, x2, y2);
	}
	
}
