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
				return 1 << 1;
			else
				return 1 << 3;
		}else{
			if(mDeltaY>0)
				return 1 << 0;
			else
				return 1 << 2;
		}
	}
	
	public int getEndPointMask() {
		if(mDeltaX!=0) {
			if(mDeltaX>0)
				return 1 << 3;
			else
				return 1 << 1;
		}else{
			if(mDeltaY>0)
				return 1 << 2;
			else
				return 1 << 0;
		}
	}
	
}
