package yang.math.objects;

public class HVector4 extends Quadruple {
	
	public static int X = 0;
	public static int Y = 1;
	public static int Z = 2;
	public static int W = 3;
	
	public HVector4(float v1,float v2,float v3,float v4) {
		set(v1,v2,v3,v4);
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
	
}
