package yang.graphics.defaults.meshcreators;

import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.math.objects.matrix.YangMatrix;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;

public class YangArmature {

	public MassAggregation mSkeleton;
	public YangMatrix[] mTransforms;
	public Vector3f[] mInitialVectors;
	public Point3f[] mInitialPositions;

	private Quaternion mTempQuat = new Quaternion();
	private Vector3f mTempVec = new Vector3f();

	public YangArmature() {

	}

	public YangArmature init(MassAggregation skeleton) {
		mSkeleton = skeleton;
		int l = skeleton.getJointCount();
		mTransforms = new YangMatrix[l];
		mInitialVectors = new Vector3f[l];
		mInitialPositions = new Point3f[l];
		int i = 0;
		for(Joint joint:mSkeleton.mJoints) {
			mInitialVectors[i] = new Vector3f();
			mTransforms[i] = new YangMatrix();
			mInitialPositions[i] = new Point3f();
			i++;
		}
		setInitialValues();
		return this;
	}

	public void setInitialValues() {
		int i = 0;
		for(Joint joint:mSkeleton.mJoints) {
			if(joint.mAngleParent!=null)
				mInitialVectors[i].set(joint.mPosX-joint.mAngleParent.mPosX,joint.mPosY-joint.mAngleParent.mPosY,joint.mPosZ-joint.mAngleParent.mPosZ);
				mInitialVectors[i].normalize();
			i++;
		}
	}

	public void refreshMatrices() {
		int i = 0;
		for(Joint joint:mSkeleton.mJoints) {
			YangMatrix transform = mTransforms[i];
			transform.setTranslation(joint.mWorldPosition);
			transform.translate(joint.mPosX-mInitialPositions[i].mX,joint.mPosY-mInitialPositions[i].mY,joint.mPosZ-mInitialPositions[i].mZ);
			if(joint.mAngleParent!=null) {
				mTempVec.set(joint.mPosX-joint.mAngleParent.mPosX,joint.mPosY-joint.mAngleParent.mPosY,joint.mPosZ-joint.mAngleParent.mPosZ);
				mTempVec.normalize();
				mTempQuat.setFromToRotation(mInitialVectors[i], mTempVec);
				transform.multiplyQuaternionRight(mTempQuat);
			}
			transform.translateNegative(joint.mWorldPosition);
			i++;
		}
	}

}
