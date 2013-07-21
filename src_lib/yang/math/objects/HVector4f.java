package yang.math.objects;

public class HVector4f extends Quadruple {
	
	public static final HVector4f UP = new HVector4f(0,1,0,1);
	public static final HVector4f DOWN = new HVector4f(0,-1,0,1);
	public static final HVector4f RIGHT = new HVector4f(1,0,0,1);
	public static final HVector4f LEFT = new HVector4f(-1,0,0,1);
	public static final HVector4f FORWARD = new HVector4f(0,0,1,1);
	public static final HVector4f BACKWARD = new HVector4f(0,0,-1,1);
	public static int X = 0;
	public static int Y = 1;
	public static int Z = 2;
	public static int W = 3;
	
	public HVector4f(float v1,float v2,float v3,float v4) {
		set(v1,v2,v3,v4);
	}
	
	public HVector4f(float[] values) {
		set(values);
	}
	
	public float getMagnitude() {
		if(mValues[W]!=1) {
			if(mValues[W]==0)
				return Float.MAX_VALUE;
			else
				return (float)Math.sqrt(mValues[X]*mValues[X]+mValues[Y]*mValues[Y]+mValues[Z]*mValues[Z]);
		}else
			return (float)Math.sqrt(mValues[X]*mValues[X]+mValues[Y]*mValues[Y]+mValues[Z]*mValues[Z]);
	}
	
	public void normalizeW() {
		float w = mValues[W];
		if(w==0) {
			mValues[X] = Float.MAX_VALUE;
			mValues[Y] = Float.MAX_VALUE;
			mValues[Z] = Float.MAX_VALUE;
		}else{
			mValues[X] /= w;
			mValues[Y] /= w;
			mValues[Z] /= w;
		}
	}
	
	public void add(Quadruple values) {
		mValues[X] += values.mValues[X];
		mValues[Y] += values.mValues[Y];
		mValues[Z] += values.mValues[Z];
	}
	
	public void subtract(Quadruple values) {
		mValues[X] -= values.mValues[X];
		mValues[Y] -= values.mValues[Y];
		mValues[Z] -= values.mValues[Z];
	}
	
	public float dot(HVector4f vector) {
		return mValues[X]*vector.mValues[X] + mValues[Y]*vector.mValues[Y] + mValues[Z]*vector.mValues[Z] + mValues[W]*vector.mValues[W];
	}
	
	public HVector4f clone() {
		return new HVector4f(mValues);
	}

	public void setAlphaBeta(float alpha, float beta, float distance) {
		mValues[0] = (float)(Math.sin(alpha)*Math.cos(beta)) * distance;
		mValues[1] = (float)(Math.sin(beta)) * distance;
		mValues[2] = (float)(Math.cos(alpha)*Math.cos(beta)) * distance;
	}
	
	public void setAlphaBeta(float alpha, float beta) {
		setAlphaBeta(alpha,beta,1);
	}
}
