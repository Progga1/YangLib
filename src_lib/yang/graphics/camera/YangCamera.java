package yang.graphics.camera;

import yang.graphics.camera.projection.OrthogonalProjection;
import yang.math.objects.Point3f;

public abstract class YangCamera extends CameraTransformations {

	public boolean mAutoRefreshInverted = true;
	public float mNear = OrthogonalProjection.DEFAULT_NEAR;
	public float mFar = OrthogonalProjection.DEFAULT_FAR;

	public YangCamera() {

	}

	public abstract void calcTransformations();

//	public Point3f normToWorld(float normX,float normY,float normZ) {
//		mInvResultTransform.apply3D(normX,normY,normZ, mTempPoint);
//		return mTempPoint;
//	}
//
//	public Point3f worldToNorm(float worldX,float worldY,float worldZ) {
//		mResultTransform.apply3D(worldX,worldY,worldZ, mTempPoint);
//		return mTempPoint;
//	}

	@Override
	public float getX() {
		return mPosition.mX;
	}

	@Override
	public float getY() {
		return mPosition.mY;
	}

	@Override
	public float getZ() {
		return mPosition.mZ;
	}

	@Override
	public Point3f getPositionReference() {
		return mPosition;
	}

}
