package yang.graphics.skeletons.defaults;

import yang.graphics.skeletons.ConnectionShapes;
import yang.math.MathConst;

public class LinearCycleConnectionShape extends ConnectionShapes {

	public LinearCycleConnectionShape(int segments) {
		super(segments+1);
		int samples = segments+1;
		int l = samples*2;
		float d = 1f/(samples-1)*0.5f;
		for(int i=0;i<l;i+=2) {
			float x = i*d;
			mPositions1[i] = i*d;
			mPositions1[i+1] = 0;
//			float sx = (x*2-1);
//			mPositions1[i] = x;
//			mPositions1[i+1] = (1-sx*sx)*0.2f;
			float f = x*2*MathConst.PI;
			float r = 1/(MathConst.PI);
			mPositions2[i] = -(float)Math.sin(f)*r;
			mPositions2[i+1] = r-(float)Math.cos(f)*r;
			mScales1[i] = 0;
			mScales1[i+1] = 1;
			f = (f*0.9f+0.05f*2*MathConst.PI);
			mScales2[i] = -(float)Math.sin(f);
			mScales2[i+1] = -(float)Math.cos(f);
		}
	}



}
