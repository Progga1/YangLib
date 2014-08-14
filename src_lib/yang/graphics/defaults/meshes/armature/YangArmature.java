package yang.graphics.defaults.meshes.armature;

import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;

public class YangArmature {

	public LimbNeutralData[] mLimbData;

	public YangArmature() {

	}

	public YangArmature init(int boneCount) {
		int l = boneCount;
		mLimbData = new LimbNeutralData[l];
		for(int i=0;i<l;i++) {
			mLimbData[i] = new LimbNeutralData();
		}
		return this;
	}

	public YangArmature initBySkeleton(MassAggregation skeleton) {
		init(skeleton.getJointCount());
		setInitialValues(skeleton);
		return this;
	}

	public void setInitialValues(MassAggregation skeleton) {
		int i = 0;
		for(Joint joint:skeleton.mJoints) {
			LimbNeutralData limbData = mLimbData[i];
			limbData.mPosition.set(joint.mX,joint.mY,joint.mZ);
			if(joint.mParent!=null) {
				limbData.mForward.set(joint.mX-joint.mParent.mX,joint.mY-joint.mParent.mY,joint.mZ-joint.mParent.mZ);
				limbData.mForwardDistance = limbData.mForward.normalize();
			}
			i++;
		}
	}

	public int getBoneCount() {
		return mLimbData.length;
	}



}
