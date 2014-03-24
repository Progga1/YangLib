package yang.model;

import yang.math.objects.YangMatrix;

public interface SurfaceParameters {

	public int getSurfaceWidth();
	public int getSurfaceHeight();
	public float getSurfaceRatioX();
	public float getSurfaceRatioY();
	public YangMatrix getViewPostTransform();

}
