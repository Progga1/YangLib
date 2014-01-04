package yang.graphics.defaults.geometrycreators.outlinedrawer;

import yang.math.objects.Point2f;
import yang.util.Util;

public class OrthoStrokePatch extends Point2f {

	public int mInterLines = 0;
	public boolean mDeleted = false;

	public OrthoStrokePatch() {

	}

	public OrthoStrokePatch reset(float x,float y) {
		mInterLines = 0;
		mDeleted = false;
		return (OrthoStrokePatch)super.set(x,y);
	}

	public OrthoStrokePatch reset() {
		return reset(0,0);
	}

	@Override
	public String toString() {
		return "x,y="+mX+","+mY+"; mask="+Util.intToBin(mInterLines, 8);
	}
}
