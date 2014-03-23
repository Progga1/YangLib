package yang.graphics.camera;

import yang.graphics.camera.projection.OrthogonalProjection;
import yang.math.objects.Point3f;

public abstract class YangCamera {

	protected Point3f mPosition = new Point3f();
	protected float mNear = OrthogonalProjection.DEFAULT_NEAR;
	protected float mFar = OrthogonalProjection.DEFAULT_FAR;

	public boolean mAutoRefreshInverted = true;

	public YangCamera() {

	}

	public abstract void calcTransformations(CameraProjection target);

//	public Point3f normToWorld(float normX,float normY,float normZ) {
//		mInvResultTransform.apply3D(normX,normY,normZ, mTempPoint);
//		return mTempPoint;
//	}
//
//	public Point3f worldToNorm(float worldX,float worldY,float worldZ) {
//		mResultTransform.apply3D(worldX,worldY,worldZ, mTempPoint);
//		return mTempPoint;
//	}

	public float getX() {
		return mPosition.mX;
	}

	public float getY() {
		return mPosition.mY;
	}

	public float getZ() {
		return mPosition.mZ;
	}

	public Point3f getPositionReference() {
		return mPosition;
	}

}
