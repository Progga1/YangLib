package yang.graphics.defaults.meshes.armature;

import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;

public class YangArmature {

	public Vector3f[] mInitialVectors;
	public Point3f[] mInitialPositions;
	public float[] mInitialDistances;

	public YangArmature() {

	}

	public YangArmature init(int boneCount) {
		int l = boneCount;
		mInitialVectors = new Vector3f[l];
		mInitialPositions = new Point3f[l];
		mInitialDistances = new float[l];
		for(int i=0;i<l;i++) {
			mInitialVectors[i] = new Vector3f();
			mInitialPositions[i] = new Point3f();
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
			mInitialPositions[i].set(joint.mX,joint.mY,joint.mZ);
			if(joint.mAngleParent!=null) {
				mInitialVectors[i].set(joint.mX-joint.mAngleParent.mX,joint.mY-joint.mAngleParent.mY,joint.mZ-joint.mAngleParent.mZ);
				mInitialDistances[i] = mInitialVectors[i].normalize();
			}
			i++;
		}
	}

	public int getBoneCount() {
		return mInitialPositions.length;
	}



}
