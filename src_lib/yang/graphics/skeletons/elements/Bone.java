package yang.graphics.skeletons.elements;

import java.util.ArrayList;

import yang.graphics.skeletons.Skeleton2D;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.GraphicsTranslator;


public class Bone {

	//Properties
	public String mName;
	public Joint mJoint1;
	public Joint mJoint2;
	public GraphicsTranslator mGraphics;
	public float strength = 0.1f;
	public int mCurTexCoords;
	public ArrayList<TextureCoordinatesQuad> mTexCoords;
	public ArrayList<TextureCoordinatesQuad> mContourTexCoords;
	public float mWidth1,mWidth2;
	public float mShiftX1,mShiftY1,mShiftX2,mShiftY2;
	public float mAnchorX1,mAnchorY1,mAnchorX2,mAnchorY2;
	public float mContourX1,mContourY1,mContourX2,mContourY2,mContourX3,mContourY3,mContourX4,mContourY4;
	public boolean mCelShading;
	
	//State
	public float mDistX, mDistY, mDistZ;
	public float mDistance;
	public float mNormDirX,mNormDirY,mNormDirZ,mPrevNormDirX,mPrevNormDirY,mPrevNormDirZ;
	public float mOrthNormX;
	public float mOrthNormY;
	public float mBoneDirX1,mBoneDirY1,mBoneDirX2,mBoneDirY2;
	public float mResShiftX1,mResShiftY1,mResShiftX2,mResShiftY2;
	public float mVertX1,mVertY1,mVertX2,mVertY2,mVertX3,mVertY3,mVertX4,mVertY4;
	public float mWidthFac;
	public boolean mVisible;
	
	
	public Bone(GraphicsTranslator graphics,String name,Joint bone1,Joint bone2) {
		mName = name;
		mJoint1 = bone1;
		mJoint2 = bone2;
		mGraphics = graphics;
		mCurTexCoords = 0;
		mTexCoords = new ArrayList<TextureCoordinatesQuad>(2);
		mContourTexCoords = new ArrayList<TextureCoordinatesQuad>(2);
		//putTextureCoords(0,0,1,1);
		refreshVisualVars();
		mCelShading = true;
		mVisible = true;
		mWidthFac = 1;
		setWidthByJoints();
		setShift(0,0,0,0);
		setContour(1);
	}
	
	public TextureCoordinatesQuad getTextureCoordinates() {
		if(mTexCoords.size()==0)
			return null;
		else
			return mTexCoords.get(mCurTexCoords);
	}
	
	public TextureCoordinatesQuad getContourTextureCoordinates() {
		return mContourTexCoords.get(mCurTexCoords);
	}
	
	public void setTextureCoordinatesIndex(int newIndex) {
		mCurTexCoords = newIndex;
		((Skeleton2D)mJoint1.mSkeleton).updatedTextureCoords();
	}
	
	public void incTextureCoordinatesIndex() {
		mCurTexCoords++;
		if(mCurTexCoords>=mTexCoords.size())
			mCurTexCoords=0;	
	}
	
	public void putTextureCoords(TextureCoordinatesQuad texCoords) {
		mTexCoords.add(texCoords);
		mContourTexCoords.add(texCoords);
	}
	
	public void setContourTexture(TextureCoordinatesQuad texCoords) {
		mContourTexCoords.set(mContourTexCoords.size()-1,texCoords);
	}
	
	public void putTextureCoords(float x1,float y1,float x2,float y2,int rotation) {
		TextureCoordinatesQuad texCoords = new TextureCoordinatesQuad().initBiased(x1,y1,x2,y2,0,0,rotation);
		putTextureCoords(texCoords);
	}
	
	public void putTextureCoords(float x1,float y1,float x2,float y2) {
		putTextureCoords(x1,y1,x2,y2,0);
	}
	
	public void setContour(float contourX1,float contourY1,float contourX2,float contourY2,float contourX3,float contourY3,float contourX4,float contourY4) {
		mContourX1 = contourX1;
		mContourY1 = contourY1;
		mContourX2 = contourX2;
		mContourY2 = contourY2;
		mContourX3 = contourX3;
		mContourY3 = contourY3;
		mContourX4 = contourX4;
		mContourY4 = contourY4;
	}
	
	public void setAngle(float angle) {
		mJoint2.setPosByAngle(mJoint1, this, angle);
	}
	
	public void setContour(float contourX1,float contourY1,float contourX2,float contourY2) {
		setContour(contourX1,contourY1,contourX2,contourY1,contourX1,contourY2,contourX2,contourY2);
	}
	
	public void setContour(float contourX,float contourY) {
		setContour(contourX,contourY,contourX,contourY);
	}

	public void copyContour(Bone bone) {
		mContourX1 = bone.mContourX1;
		mContourY1 = bone.mContourY1;
		mContourX2 = bone.mContourX2;
		mContourY2 = bone.mContourY2;
		mContourX3 = bone.mContourX3;
		mContourY3 = bone.mContourY3;
		mContourX4 = bone.mContourX4;
		mContourY4 = bone.mContourY4;
	}
	
	public void setContour(float contour) {
		setContour(contour,contour);
	}
	
	public void refreshDistance() {
		mPrevNormDirX = mNormDirX;
		mPrevNormDirY = mNormDirY;
		mPrevNormDirZ = mNormDirZ;
		
		mDistX = mJoint2.mPosX - mJoint1.mPosX;
		mDistY = mJoint2.mPosY - mJoint1.mPosY;
		mDistZ = mJoint2.mPosZ - mJoint1.mPosZ;
		mDistance = (float)Math.sqrt(mDistX*mDistX + mDistY*mDistY + mDistZ*mDistZ);
		
		if(mDistance!=0) {
			float d = 1 / mDistance;
			mNormDirX = mDistX * d;
			mNormDirY = mDistY * d;
			mNormDirZ = mDistZ * d;
			mOrthNormX = mNormDirY;
			mOrthNormY = -mNormDirX;
		}
	}
	
	public void refreshVisualVars() {
		refreshDistance();

		mResShiftX1 = -mOrthNormX * mShiftX1 - mNormDirX * mShiftY1;
		mResShiftY1 = -mOrthNormY * mShiftX1 - mNormDirY * mShiftY1;
		mResShiftX2 = -mOrthNormX * mShiftX2 - mNormDirX * mShiftY2;
		mResShiftY2 = -mOrthNormY * mShiftX2 - mNormDirY * mShiftY2;
		
		float posX1 = mJoint1.mPosX;
		float posY1 = mJoint1.mPosY;
		float posX2 = mJoint2.mPosX;
		float posY2 = mJoint2.mPosY;
		
		mVertX1 = posX1+mResShiftX1-mOrthNormX*mWidth1 * mWidthFac;
		mVertY1 = posY1+mResShiftY1-mOrthNormY*mWidth1 * mWidthFac;
		mVertX2 = posX1+mResShiftX1+mOrthNormX*mWidth1 * mWidthFac;
		mVertY2 = posY1+mResShiftY1+mOrthNormY*mWidth1 * mWidthFac;
		mVertX3 = posX2+mResShiftX2-mOrthNormX*mWidth2 * mWidthFac;
		mVertY3 = posY2+mResShiftY2-mOrthNormY*mWidth2 * mWidthFac;
		mVertX4 = posX2+mResShiftX2+mOrthNormX*mWidth2 * mWidthFac;
		mVertY4 = posY2+mResShiftY2+mOrthNormY*mWidth2 * mWidthFac;
	}
	
	public void setWidth(float width1,float width2) {
		mWidth1 = width1;
		mWidth2 = width2;
	}
	
	public void setWidth(float width) {
		setWidth(width,width);
	}
	
	public void setWidthByJoints() {
		setWidth(mJoint1.mRadius,mJoint2.mRadius);
	}
	
	public void addWidth(float w) {
		mWidth1 += w;
		mWidth2 += w;
	}
	
	public void multWidth(float factor) {
		mWidth1 *= factor;
		mWidth2 *= factor;
	}

	public void setShift(float shiftX1, float shiftY1, float shiftX2, float shiftY2) {
		mShiftX1 = shiftX1;
		mShiftY1 = shiftY1;
		mShiftX2 = shiftX2;
		mShiftY2 = shiftY2;
	}
	
	public void applyConstraint() { }

	public void setShiftX(float shift) {
		mShiftX1 = shift;
		mShiftX2 = shift;
	}
	
	public void addShiftX(float shift) {
		mShiftX1 += shift;
		mShiftX2 += shift;
	}
	
	public void setShiftY(float shift) {
		mShiftY1 = shift;
		mShiftY2 = shift;
	}
	
	public void setShiftY(float upperShift,float lowerShift) {
		mShiftY1 = upperShift;
		mShiftY2 = lowerShift;
	}
	
	public void addShiftY(float shift) {
		mShiftY1 += shift;
		mShiftY2 += shift;
	}

	public void addShift(float x1, float y1, float x2, float y2) {
		mShiftX1 += x1;
		mShiftY1 += y1;
		mShiftX2 += x2;
		mShiftY2 += y2;
	}

	public void addContour(float x1, float y1, float x2, float y2) {
		mContourX1 += x1;
		mContourY1 += y1;
		mContourX2 += x2;
		mContourY2 += y2;
		mContourX3 += x1;
		mContourY3 += y1;
		mContourX4 += x2;
		mContourY4 += y2;
	}

	public void scale(float f) {
		mShiftX1 *= f;
		mShiftY1 *= f;
		mShiftX2 *= f;
		mShiftY2 *= f;
		mWidth1 *= f;
		mWidth2 *= f;
	}

	public void addLength(float length) {
		mShiftY2 -= length;
	}

	public void setContourBottom(float val) {
		mContourY3 = val;
		mContourY4 = val;
	}
	
}
