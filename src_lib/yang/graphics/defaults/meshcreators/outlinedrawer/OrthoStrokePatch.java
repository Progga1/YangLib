package yang.graphics.defaults.meshcreators.outlinedrawer;

import yang.math.objects.Point2f;

public class OrthoStrokePatch extends Point2f {

	public int mInterLines = 0;
	
	public OrthoStrokePatch() {
		
	}
	
	public OrthoStrokePatch reset(float x,float y) {
		mInterLines = 0;
		return (OrthoStrokePatch)super.set(x,y);
	}
}
