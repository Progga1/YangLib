package yang.graphics.defaults.meshes.armature;

import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;

public class YangArmaturePosture {

	private YangArmature mArmature;
	public YangMatrix[] mTransforms;
	private Quaternion mTempQuat = new Quaternion();
	private Vector3f mTempVec = new Vector3f();

	private Point3f mTempPosition = new Point3f();
	private Vector3f mTempRight = new Vector3f();
	private Vector3f mTempUp = new Vector3f();
	private Vector3f mTempForward = new Vector3f();
	private YangMatrix mTempTransform = new YangMatrix();

	public YangArmaturePosture(YangArmature armature) {
		mArmature = armature;
		int l = mArmature.getBoneCount();
		mTransforms = new YangMatrix[l];
		for(int i=0;i<l;i++)
			mTransforms[i] = new YangMatrix();
	}

	public void refreshMatrices(MassAggregation skeleton) {

		int i = 0;
		for(Joint joint:skeleton.mJoints) {
			LimbNeutralData limbData = mArmature.mLimbData[i];
			YangMatrix transform = mTransforms[i];
			Joint parent = joint.mParent;
			if(parent!=null) {
				LimbNeutralData parentLimbData = mArmature.mLimbData[parent.mId];
				transform.loadIdentity();

				if(joint.mUpJoint!=null && joint.mRightJoint!=null) {
					mTempPosition.set(joint.mX,joint.mY,joint.mZ);
					mTempRight.setFromTo(joint,joint.mRightJoint);
					mTempUp.setFromTo(joint,joint.mUpJoint);
					mTempForward.setFromTo(joint.mParent,joint);
					mTempTransform.setFromAxisAndPosition(mTempRight,mTempUp,mTempForward,mTempPosition);

					transform.multiplyRight(mTempTransform);
					transform.multiplyRight(limbData.mInvTransform);
				}else{
					transform.translate(parent.mX,parent.mY,parent.mZ);
					mTempVec.set(joint.mX-parent.mX,joint.mY-parent.mY,joint.mZ-parent.mZ);
					float dist = mTempVec.normalize();
					float scale = dist/limbData.mForwardDistance;
					mTempQuat.setFromToRotation(limbData.mForwardDir, mTempVec);
					transform.multiplyQuaternionRight(mTempQuat);
					transform.scale(scale);
					transform.translate(-parent.mX,-parent.mY,-parent.mZ);
					transform.translate(parent.mX-parentLimbData.mPosition.mX,parent.mY-parentLimbData.mPosition.mY,parent.mZ-parentLimbData.mPosition.mZ);
				}

			}else{
				transform.setTranslation(joint.mX-limbData.mPosition.mX,joint.mY-limbData.mPosition.mY,joint.mZ-limbData.mPosition.mZ);
			}

			i++;
		}
	}

}
