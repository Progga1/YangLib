package yang.graphics.defaults.meshes.armature;

import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;

public class LimbNeutralData {

	public Point3f mPosition = new Point3f();
	public Vector3f mRight = new Vector3f(1,0,0);
	public Vector3f mUp = new Vector3f(0,1,0);
	public Vector3f mForward = new Vector3f(0,0,1);
	public Vector3f mRightDir = new Vector3f(1,0,0);
	public Vector3f mUpDir = new Vector3f(0,1,0);
	public Vector3f mForwardDir = new Vector3f(0,0,1);
	public float mRightDistance = 1;
	public float mUpDistance = 1;
	public float mForwardDistance = 1;

	public YangMatrix mTransform = new YangMatrix();
	public YangMatrix mInvTransform = new YangMatrix();

	public void finish() {

		mForwardDir.set(mForward);
		mForwardDistance = mForwardDir.normalize();
		mUpDir.set(mUp);
		mUpDistance = mUpDir.normalize();
		mRightDir.set(mRight);
		mRightDistance = mRightDir.normalize();

		mTransform.loadIdentity();
		mTransform.translate(mPosition);
		mTransform.setColumn(0, mRight);
		mTransform.setColumn(1, mUp);
		mTransform.setColumn(2, mForward);

		mTransform.asInverted(mInvTransform.mValues);
	}

}
