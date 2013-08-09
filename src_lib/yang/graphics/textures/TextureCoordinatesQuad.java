package yang.graphics.textures;

import yang.graphics.translator.Texture;
import yang.model.Rect;

public class TextureCoordinatesQuad {

	public static float BIASPIXELS = 0.5f;

	public static final TextureCoordinatesQuad FULL_TEXTURE = new TextureCoordinatesQuad().init(0,0,1,1);
	public static final int ROTATE_NONE = 0;
	public static final int ROTATE_CW_90 = 1;
	public static final int ROTATE_180 = 2;
	public static final int ROTATE_CCW_90 = 3;
	public static final int ID_X1 = 4;
	public static final int ID_Y1 = 5;
	public static final int ID_X2 = 2;
	public static final int ID_Y2 = 3;
	
	public float mLeft;
	public float mTop;
	public float mWidth;
	public float mHeight;
	public float mBiasX=0,mBiasY=0;
	public float mRatioWidth;
	public float mRatio;
	public float[] mAppliedCoordinates;
	public int mRotation;
	
	public static TextureCoordinatesQuad[] createSequence(TextureCoordinatesQuad texCoords,int countX,int countY) {
		int count = countX*countY;
		TextureCoordinatesQuad[] result = new TextureCoordinatesQuad[count];
		float w = texCoords.getWidth();
		float h = texCoords.getHeight();
		
		int c = 0;
		result[0] = texCoords;
		for(int j=0;j<countY;j++) {
			for(int i=0;i<countX;i++) {
				if(c>0)
					result[c] = texCoords.cloneShifted(w*i,h*j);
				c++;
			}
		}
		return result;
	}
	
	public static TextureCoordinatesQuad[] createSequence(TextureCoordinatesQuad texCoords,int countX) {
		return createSequence(texCoords,countX,1);
	}
	
	public TextureCoordinatesQuad() {
		
	}
	
	public void setRotation(int rotation) {
		mRotation = rotation;
		refreshCoordArray();
	}
	
	public void refreshCoordArray() {
		mAppliedCoordinates = new float[8];
		float x = mLeft+mBiasX;
		float x2 = mLeft+mWidth-mBiasX;
		float y = mTop+mBiasY;
		float y2 = mTop+mHeight-mBiasY;
		mAppliedCoordinates[0] = x;
		mAppliedCoordinates[1] = y2;
		mAppliedCoordinates[2] = x2;
		mAppliedCoordinates[3] = y2;
		mAppliedCoordinates[4] = x;
		mAppliedCoordinates[5] = y;
		mAppliedCoordinates[6] = x2;
		mAppliedCoordinates[7] = y;
		if(mRotation!=0) {
			for(int i=0;i<mRotation;i++) {
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
		}
	}
	
	public TextureCoordinatesQuad initBiased(float x1, float y1, float x2, float y2, float biasX, float biasY, int rotation) {
		this.mLeft = x1;
		this.mTop = y1;
		this.mWidth = x2-x1;
		this.mHeight = y2-y1;
		mBiasX = biasX;
		mBiasY = biasY;
		mRatio = 1;
		mRatioWidth = mWidth/mHeight;
		setRotation(rotation);
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
	
	public TextureCoordinatesQuad initBiased(float x1, float y1, float x2, float y2, float textureWidth, float textureHeight, float biasX,float biasY) {
		initBiased((x1) / textureWidth, 
			  (y1) / textureHeight,
			 (x2) / textureWidth, 
			 (y2) / textureHeight, 
			 biasX/textureWidth, biasY/textureHeight);
		mRatio = textureWidth / textureHeight;
		mRatioWidth = mWidth*mRatio/mHeight;
		return this;
	}
	

	public TextureCoordinatesQuad init(float x1, float y1, float x2, float y2, float textureWidth, float textureHeight) {
		float uBias = textureWidth*textureHeight>=64?BIASPIXELS:0;
		return initBiased(x1,y1,x2,y2,textureWidth,textureHeight, uBias,uBias);
	}
	
	public TextureCoordinatesQuad init(float x1, float y1, float x2, float y2, Texture prefaceTexture) {
		return init(x1,y1,x2,y2,prefaceTexture.getWidth(),prefaceTexture.getHeight());
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
		return "("+mLeft+","+mTop+","+mWidth+","+mHeight+")";
	}

	public TextureCoordinatesQuad intoRect(float left,float top,float width,float height) {
		mLeft = mLeft*width+left;
		mTop = mTop*height+top;
		mWidth *= width;
		mHeight *= height;
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
	
	public float getRight() {
		return mLeft+mWidth;
	}
	
	public float getBottom() {
		return mTop+mHeight;
	}
	
	public TextureCoordinatesQuad setBias(float biasX,float biasY) {
		mBiasX = biasX;
		mBiasY = biasY;
		refreshCoordArray();
		return this;
	}
	
	public TextureCoordinatesQuad setBias(float bias) {
		return setBias(bias,bias);
	}
	
	public TextureCoordinatesQuad cloneShifted(float shiftX,float shiftY) {
		return new TextureCoordinatesQuad().initBiased(mLeft+shiftX, mTop+shiftY, getRight()+shiftX, getBottom()+shiftY, mBiasX,mBiasY);
	}
	
	@Override
	public TextureCoordinatesQuad clone() {
		return cloneShifted(0,0);
	}
	
	

	public void setRight(float right) {
		mWidth = right-mLeft;
	}
	
	public void setBottom(float bottom) {
		mHeight = bottom-mTop;
	}

	public float getBiasedLeft() {
		return mLeft+mBiasX;
	}
	
	public float getBiasedTop() {
		return mTop+mBiasY;
	}
	
	public float getBiasedRight() {
		return mLeft+mWidth-2*mBiasX;
	}
	
	public float getBiasedBottom() {
		return mTop+mHeight-2*mBiasY;
	}
	
}
