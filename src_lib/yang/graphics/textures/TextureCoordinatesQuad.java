package yang.graphics.textures;

import yang.graphics.translator.Texture;

public class TextureCoordinatesQuad {

	public static final float BIASPIXELS = 0.5f;

	public static final TextureCoordinatesQuad FULL_TEXTURE = new TextureCoordinatesQuad().init(0,0,1,1);
	
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
	
	protected void rotateCoords() {
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
	}
	
	public void refreshCoordArray(boolean rotate) {
		mAppliedCoordinates = new float[8];
		mAppliedCoordinates[0] = x1;
		mAppliedCoordinates[1] = y2;
		mAppliedCoordinates[2] = x2;
		mAppliedCoordinates[3] = y2;
		mAppliedCoordinates[4] = x1;
		mAppliedCoordinates[5] = y1;
		mAppliedCoordinates[6] = x2;
		mAppliedCoordinates[7] = y1;
		if(rotate)
			rotateCoords();
	}
	
	public void refreshCoordArray() {
		refreshCoordArray(false);
	}
	
	public TextureCoordinatesQuad initBiased(float x1, float y1, float x2, float y2, float biasX, float biasY, boolean rotate) {
		this.x1 = x1+biasX;
		this.y1 = y1+biasY;
		this.x2 = x2-biasX;
		this.y2 = y2-biasY;
		this.mWidth = x2-x1;
		this.mHeight = y2-y1;
		refreshCoordArray(rotate);
		mRatio = 1;
		mRatioWidth = mWidth/mHeight;
		return this;
	}
	
	public TextureCoordinatesQuad initBiased(float x1, float y1, float x2, float y2, float biasX, float biasY) {
		return initBiased(x1,y1,x2,y2,biasX,biasY,false);
	}
	
	public TextureCoordinatesQuad initBiased(float x1, float y1, float x2, float y2, float bias) {
		return initBiased(x1,y1,x2,y2,bias,bias);
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
	
}
