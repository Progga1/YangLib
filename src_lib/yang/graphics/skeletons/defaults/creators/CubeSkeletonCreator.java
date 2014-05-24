package yang.graphics.skeletons.defaults.creators;

import yang.math.objects.YangMatrix;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;
import yang.physics.massaggregation.elements.JointConnection;

public class CubeSkeletonCreator {

	public MassAggregation mSkeleton;
	public String mNamePrefix = "CUBE_";

	public Joint mLeftBottomFrontJoint,mRightBottomFrontJoint,mLeftTopFrontJoint,mRightTopFrontJoint, mLeftBottomBackJoint,mRightBottomBackJoint,mLeftTopBackJoint,mRightTopBackJoint;
	public Joint[] mJoints = new Joint[8];

	public CubeSkeletonCreator() {

	}

	public MassAggregation create(YangMatrix transform) {

		if(mSkeleton==null) {
			mSkeleton = new MassAggregation();
		}

		mLeftBottomBackJoint = mSkeleton.addJoint(mNamePrefix+"LEFT_BOTTOM_BACK",null, 0,0,0);
		mRightBottomBackJoint = mSkeleton.addJoint(mNamePrefix+"RIGHT_BOTTOM_BACK",null, 1,0,0);
		mLeftTopBackJoint = mSkeleton.addJoint(mNamePrefix+"LEFT_TOP_BACK",null, 0,1,0);
		mRightTopBackJoint = mSkeleton.addJoint(mNamePrefix+"RIGHT_TOP_BACK",null, 1,1,0);
		mLeftBottomFrontJoint = mSkeleton.addJoint(mNamePrefix+"LEFT_BOTTOM_FRONT",null, 0,0,1);
		mRightBottomFrontJoint = mSkeleton.addJoint(mNamePrefix+"RIGHT_BOTTOM_FRONT",null, 1,0,1);
		mLeftTopFrontJoint = mSkeleton.addJoint(mNamePrefix+"LEFT_TOP_FRONT",null, 0,1,1);
		mRightTopFrontJoint = mSkeleton.addJoint(mNamePrefix+"RIGHT_TOP_FRONT",null, 1,1,1);

		mJoints[0] = mLeftBottomBackJoint;
		mJoints[1] = mRightBottomBackJoint;
		mJoints[2] = mRightTopBackJoint;
		mJoints[3] = mLeftTopBackJoint;
		mJoints[4] = mLeftTopFrontJoint;
		mJoints[5] = mRightTopFrontJoint;
		mJoints[6] = mRightBottomFrontJoint;
		mJoints[7] = mLeftBottomFrontJoint;

		for(Joint joint:mJoints) {
			joint.applyTransform(transform);
		}

//		mSkeleton.createRigidBody(mJoints,"",10);
		final float SPRING_STRENGTH = 5;
		for(int i=0;i<7;i++) {
			mSkeleton.addSpringBone(new JointConnection("CUBE_BONE",mJoints[i],mJoints[i+1]), SPRING_STRENGTH);
		}
		mSkeleton.addSpringBone(new JointConnection("CUBE_BONE",mLeftTopBackJoint,mLeftBottomBackJoint), SPRING_STRENGTH);
		mSkeleton.addSpringBone(new JointConnection("CUBE_BONE",mLeftTopFrontJoint,mLeftBottomFrontJoint), SPRING_STRENGTH);
		mSkeleton.addSpringBone(new JointConnection("CUBE_BONE",mRightTopFrontJoint,mRightTopBackJoint), SPRING_STRENGTH);
		mSkeleton.addSpringBone(new JointConnection("CUBE_BONE",mRightBottomFrontJoint,mRightBottomBackJoint), SPRING_STRENGTH);
		mSkeleton.addSpringBone(new JointConnection("CUBE_BONE",mLeftBottomFrontJoint,mLeftBottomBackJoint), SPRING_STRENGTH);
		mSkeleton.addSpringBone(new JointConnection("CUBE_BONE_DIAG",mRightTopFrontJoint,mLeftBottomBackJoint), SPRING_STRENGTH);
		mSkeleton.addSpringBone(new JointConnection("CUBE_BONE_DIAG",mLeftTopFrontJoint,mRightBottomBackJoint), SPRING_STRENGTH);
		mSkeleton.addSpringBone(new JointConnection("CUBE_BONE_DIAG",mRightBottomFrontJoint,mLeftTopBackJoint), SPRING_STRENGTH);
		mSkeleton.addSpringBone(new JointConnection("CUBE_BONE_DIAG",mLeftBottomFrontJoint,mRightTopBackJoint), SPRING_STRENGTH);

		return mSkeleton;
	}

	public MassAggregation create(MassAggregation target,YangMatrix transform) {
		mSkeleton = target;
		create(transform);
		return mSkeleton;
	}

	public YangMatrix create(MassAggregation target,float centerX,float centerY,float centerZ, float width,float height,float depth) {
		YangMatrix transform = new YangMatrix();
		transform.setTranslation(centerX,centerY,centerZ);
		transform.scale(width,height,depth);
		transform.translate(-0.5f,-0.5f,-0.5f);
		create(target,transform);
		return transform;
	}

	public void getRigidBodyTransform(YangMatrix targetTransform) {
		MassAggregation.getRigidTransform(targetTransform,mLeftBottomBackJoint,mRightBottomBackJoint,mLeftTopBackJoint,mLeftBottomFrontJoint);
	}

}
