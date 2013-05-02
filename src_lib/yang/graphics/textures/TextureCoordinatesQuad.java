package yang.graphics.textures;

import yang.graphics.translator.Texture;

public class TextureCoordinatesQuad {

	private static final float BIASPIXELS = 0;

public static final TextureCoordinatesQuad FULL_TEXTURE = new TextureCoordinatesQuad().init(0,0,1,1);
	
	public float x1;
	public float y1;
	public float x2;
	public float y2;
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
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.mWidth = x2-x1;
		this.mHeight = y2-y1;
		refreshCoordArray(rotate);
		mRatio = 1;
		return this;
	}
	
	public TextureCoordinatesQuad initBiased(float x1, float y1, float x2, float y2, float biasX, float biasY) {
		return initBiased(x1,y1,x2,y2,biasX,biasY,false);
	}
	
	public TextureCoordinatesQuad init(float x1, float y1, float x2, float y2) {
		return initBiased(x1,y1,x2,y2,0,0);
	}
	
	public TextureCoordinatesQuad init(float x1, float y1, float widthAndHeight) {
		return initBiased(x1,y1,x1+widthAndHeight,y1+widthAndHeight,0,0);
	}
	
	public TextureCoordinatesQuad initI(int x1, int y1, int x2, int y2, int textureWidth, int textureHeight) {
		initBiased((x1+0.5f) / (float)textureWidth, 
			  (y1+0.5f) / (float)textureHeight,
			 (x2-0.5f) / (float)textureWidth, 
			 (y2-0.5f) / (float)textureHeight, 
			 BIASPIXELS/(float)textureWidth, BIASPIXELS/(float)textureHeight);
		mRatio = (float)textureWidth / textureHeight;
		return this;
	}
	
	public TextureCoordinatesQuad init(int x1, int y1, int x2, int y2, Texture prefaceTexture) {
		return initI(x1,y1,x2,y2,prefaceTexture.getWidth(),prefaceTexture.getHeight());
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
