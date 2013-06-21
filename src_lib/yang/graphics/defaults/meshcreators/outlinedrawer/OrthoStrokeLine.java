package yang.graphics.defaults.meshcreators.outlinedrawer;

import yang.math.MathFunc;
import yang.math.objects.Point2f;

public class OrthoStrokeLine extends Point2f {

	public float mDeltaX;
	public float mDeltaY;
	public boolean mDeleted;
	
	public OrthoStrokeLine setX(float x,float y,float distance) {
		super.set(x,y);
		mDeleted = false;
		mDeltaX = distance;
		mDeltaY = 0;
		return this;
	}
	
	public OrthoStrokeLine setY(float x,float y,float distance) {
		super.set(x,y);
		mDeleted = false;
		mDeltaX = 0;
		mDeltaY = distance;
		return this;
	}
	
	public int getDirX() {
		return MathFunc.signZero(mDeltaX);
	}
	
	public int getDirY() {
		return MathFunc.signZero(mDeltaY);
	}

	public int getStartPointMask() {
		if(mDeltaX!=0) {
			if(mDeltaX>0)
				return OrthoStrokeProperties.RIGHT;
			else
				return OrthoStrokeProperties.LEFT;
		}
		if(mDeltaY!=0) {
			if(mDeltaY>0)
				return OrthoStrokeProperties.UP;
			else
				return OrthoStrokeProperties.DOWN;
		}
		return 0;
	}
	
	public int getEndPointMask() {
		if(mDeltaX!=0) {
			if(mDeltaX>0)
				return OrthoStrokeProperties.LEFT;
			else
				return OrthoStrokeProperties.RIGHT;
		}
		if(mDeltaY!=0) {
			if(mDeltaY>0)
				return OrthoStrokeProperties.DOWN;
			else
				return OrthoStrokeProperties.UP;
		}
		return 0;
	}

	public float getEndX() {
		return mPosX+mDeltaX;
	}
	
	public float getEndY() {
		return mPosY+mDeltaY;
	}

	public float getLeft() {
		if(mDeltaX>=0)
			return mPosX;
		else
			return mPosX+mDeltaX;
	}
	
	public float getRight() {
		if(mDeltaX>=0)
			return mPosX+mDeltaX;
		else
			return mPosX;
	}
	
	public float getTop() {
		if(mDeltaY>=0)
			return mPosY;
		else
			return mPosY+mDeltaY;
	}
	
	public float getBottom() {
		if(mDeltaY>=0)
			return mPosY+mDeltaY;
		else
			return mPosY;
	}
	
}
