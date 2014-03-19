package yang.graphics.camera;

import yang.math.objects.matrix.YangMatrix;
import yang.model.ScreenInfo;

public abstract class YangCamera {

	public YangMatrix mResultTransform;
	public YangMatrix mInvResultTransform;

	protected ScreenInfo mScreen;
	protected float mNear,mFar;

	public abstract void reset();
	public abstract void refreshResultTransform();

	public YangCamera() {
		reset();
	}

}
