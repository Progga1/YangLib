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
		for(int i=0;i<skeleton.mJoints.size();i++) {
			mInitialVectors[i] = new Vector3f();
			mTransforms[i] = new YangMatrix();
			mInitialPositions[i] = new Point3f();
		}
		setInitialValues();
		return this;
	}

	public void setInitialValues() {
		int i = 0;
		for(Joint joint:mTemplateSkeleton.mJoints) {
			mInitialPositions[i].set(joint.mX,joint.mY,joint.mZ);
			if(joint.mAngleParent!=null) {
				mInitialVectors[i].set(joint.mX-joint.mAngleParent.mX,joint.mY-joint.mAngleParent.mY,joint.mZ-joint.mAngleParent.mZ);
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
				transform.translate(parent.mX,parent.mY,parent.mZ);
				mTempVec.set(joint.mX-parent.mX,joint.mY-parent.mY,joint.mZ-parent.mZ);
				mTempVec.normalize();
				mTempQuat.setFromToRotation(mInitialVectors[i], mTempVec);
				transform.multiplyQuaternionRight(mTempQuat);
				transform.translate(-parent.mX,-parent.mY,-parent.mZ);
				transform.translate(parent.mX-mInitialPositions[parent.mId].mX,parent.mY-mInitialPositions[parent.mId].mY,parent.mZ-mInitialPositions[parent.mId].mZ);
			}else{
				transform.setTranslation(joint.mX-mInitialPositions[i].mX,joint.mY-mInitialPositions[i].mY,joint.mZ-mInitialPositions[i].mZ);
			}

			i++;
		}
	}

	public void refreshMatrices() {
		refreshMatrices(mTemplateSkeleton);
	}

}
