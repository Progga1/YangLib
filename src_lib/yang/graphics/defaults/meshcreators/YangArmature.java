package yang.graphics.defaults.meshcreators;

import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.math.objects.matrix.YangMatrix;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;

public class YangArmature {

	public MassAggregation mTemplateSkeleton;
	public YangMatrix[] mTransforms;
	public Vector3f[] mInitialVectors;
	public Point3f[] mInitialPositions;

	private Quaternion mTempQuat = new Quaternion();
	private Vector3f mTempVec = new Vector3f();

	public YangArmature() {

	}

	public YangArmature init(MassAggregation skeleton) {
		mTemplateSkeleton = skeleton;
		int l = skeleton.getJointCount();
		mTransforms = new YangMatrix[l];
		mInitialVectors = new Vector3f[l];
		mInitialPositions = new Point3f[l];
		int i = 0;
		for(Joint joint:mTemplateSkeleton.mJoints) {
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
		for(Joint joint:mTemplateSkeleton.mJoints) {
			mInitialPositions[i].set(joint.mPosX,joint.mPosY,joint.mPosZ);
			if(joint.mAngleParent!=null) {
				mInitialVectors[i].set(joint.mPosX-joint.mAngleParent.mPosX,joint.mPosY-joint.mAngleParent.mPosY,joint.mPosZ-joint.mAngleParent.mPosZ);
				mInitialVectors[i].normalize();
			}
			i++;
		}
	}

	public void refreshMatrices(MassAggregation skeleton) {
		int i = 0;
		for(Joint joint:skeleton.mJoints) {
			YangMatrix transform = mTransforms[i];
			Joint parent = joint.mAngleParent;
			if(parent!=null) {
				transform.loadIdentity();
				//transform.translate(joint.mAngleParent.mWorldPosition);
				transform.translate(parent.mPosX,parent.mPosY,parent.mPosZ);
				mTempVec.set(joint.mPosX-parent.mPosX,joint.mPosY-parent.mPosY,joint.mPosZ-parent.mPosZ);
				mTempVec.normalize();
				mTempQuat.setFromToRotation(mInitialVectors[i], mTempVec);
				transform.multiplyQuaternionRight(mTempQuat);
				transform.translate(-parent.mPosX,-parent.mPosY,-parent.mPosZ);
				transform.translate(parent.mPosX-mInitialPositions[parent.mId].mX,parent.mPosY-mInitialPositions[parent.mId].mY,parent.mPosZ-mInitialPositions[parent.mId].mZ);
			}else{
				transform.setTranslation(joint.mPosX-mInitialPositions[i].mX,joint.mPosY-mInitialPositions[i].mY,joint.mPosZ-mInitialPositions[i].mZ);
			}

			i++;
		}
	}

	public void refreshMatrices() {
		refreshMatrices(mTemplateSkeleton);
	}

}
