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
	
	public float getDistance() {
		if(mDeltaX!=0)
			return mDeltaX;
		else
			return mDeltaY;
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
		return mX+mDeltaX;
	}
	
	public float getEndY() {
		return mY+mDeltaY;
	}

	public float getLeft() {
		if(mDeltaX>=0)
			return mX;
		else
			return mX+mDeltaX;
	}
	
	public float getRight() {
		if(mDeltaX>=0)
			return mX+mDeltaX;
		else
			return mX;
	}
	
	public float getTop() {
		if(mDeltaY>=0)
			return mY;
		else
			return mY+mDeltaY;
	}
	
	public float getBottom() {
		if(mDeltaY>=0)
			return mY+mDeltaY;
		else
			return mY;
	}
	
}
