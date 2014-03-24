package yang.graphics.defaults.meshes.armature;

import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;

public class YangArmaturePose {

	private YangArmature mArmature;
	public YangMatrix[] mTransforms;
	private Quaternion mTempQuat = new Quaternion();
	private Vector3f mTempVec = new Vector3f();

	public YangArmaturePose(YangArmature armature) {
		mArmature = armature;
		int l = mArmature.getBoneCount();
		mTransforms = new YangMatrix[l];
		for(int i=0;i<l;i++)
			mTransforms[i] = new YangMatrix();
	}

	public void refreshMatrices(MassAggregation skeleton) {
		Vector3f[] initialVectors = mArmature.mInitialVectors;
		Point3f[] initialPositions = mArmature.mInitialPositions;

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
				mTempQuat.setFromToRotation(initialVectors[i], mTempVec);
				transform.multiplyQuaternionRight(mTempQuat);
				transform.translate(-parent.mX,-parent.mY,-parent.mZ);
				transform.translate(parent.mX-initialPositions[parent.mId].mX,parent.mY-initialPositions[parent.mId].mY,parent.mZ-initialPositions[parent.mId].mZ);
			}else{
				transform.setTranslation(joint.mX-initialPositions[i].mX,joint.mY-initialPositions[i].mY,joint.mZ-initialPositions[i].mZ);
			}

			i++;
		}
	}

}
