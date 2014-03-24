package yang.graphics.camera.projection;

import yang.math.objects.YangMatrix;


public class OrthogonalProjection extends Projection {

	public final static float DEFAULT_NEAR = -1;
	public final static float DEFAULT_FAR = 1;

	public static void getTransform(YangMatrix target,float left, float right, float top, float bottom, float near, float far) {
		final float dx = 1/(right - left);
		final float dy = 1/(top - bottom);
		final float dz = 1/(far - near);

		target.setRow(0, 2*dx,    0,    0, -(right+left)*dx);
		target.setRow(1, 0,    2*dy,    0, -(top+bottom)*dy);
		target.setRow(2, 0,    0, -2*dz, -(far+near)*dz);
		target.setRow(3, 0,    0,    0,    1);
	}

	public void setOrthogonalProjection(YangMatrix target,float left, float right, float top, float bottom) {
		getTransform(target,left,right,top,bottom,DEFAULT_NEAR,DEFAULT_FAR);
	}

	private float mSize = 1;

	public OrthogonalProjection() {
		super();
	}

	@Override
	public void reset() {
		super.reset();
		mSize = 1;
	}

	@Override
	public void refresh() {
		float w = mRatioX*mSize*0.5f;
		float h = mRatioY*mSize*0.5f;
		OrthogonalProjection.getTransform(mTransform,-w,w, h,-h, mNear,mFar);
	}

	public void set(float near,float far,float size) {
		mNear = near;
		mFar = far;
		mSize = size;
		refresh();
	}

	public void set(float near,float far) {
		set(near,far,1);
		refresh();
	}

}
