package yang.graphics.skeletons.defaults;

import yang.graphics.skeletons.ConnectionShapes;
import yang.math.MathConst;

public class LinearCycleConnectionShape extends ConnectionShapes {

	public LinearCycleConnectionShape(int segments) {
		super(segments);
		int l = segments*2;
		float d = 1f/(segments-1);
		for(int i=0;i<l;i+=2) {
			float x = i*d;
			mShape1[i] = i*d;
			mShape1[i+1] = 0;
			float f = x*2*MathConst.PI;
			float r = 1/(2*MathConst.PI);
			mShape2[i] = (float)Math.sin(f)*r;
			mShape2[i+1] = r-(float)Math.cos(f)*r;
		}
	}

}
