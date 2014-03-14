package yang.graphics.skeletons.defaults;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.geometrycreators.grids.GridCreator;
import yang.graphics.model.FloatColor;
import yang.math.objects.matrix.YangMatrix;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;
import yang.physics.massaggregation.elements.JointConnection;

public class JointGridCreator {

	public float mJointMass = 1.5f;
	public MassAggregation mTargetSkeleton;
	public String mJointNamePrefix = "grid_";
	public String mBoneNamePrefix = "grid_";
	public Joint[][] mJoints;
	public float mStrength = 10;
	public float mFriction = 0.96f;

	//Drawing
	public GridCreator<?> mGridDrawer;

	private int mCountX,mCountY;

	public MassAggregation create(int countX,int countY,YangMatrix transform) {
		if(transform==null)
			transform = YangMatrix.IDENTITY;
		mCountX = countX;
		mCountY = countY;
		mJoints = new Joint[countY][countX];
		if(countX<2 || countY<2)
			throw new RuntimeException("countX and countY must be larger or equal 2.");

		if(mTargetSkeleton==null) {
			mTargetSkeleton = new MassAggregation();
		}


		for(int j=0;j<countY;j++) {
			float y = (float)j/(countY-1);
			Joint prevJoint = null;
			for(int i=0;i<countX;i++) {
				Joint newJoint = new Joint(mJointNamePrefix+j+"-"+i);
				float x = (float)i/(countX-1);
				transform.apply3D(x,y,0, newJoint);
				mJoints[j][i] = newJoint;
				newJoint.mRadius = 0.01f;
				mTargetSkeleton.addJoint(newJoint);
				if(i>0)
					mTargetSkeleton.addSpringBone(new JointConnection(mBoneNamePrefix+j+"-"+(i-1)+"_"+j+"-"+i, prevJoint,newJoint), mStrength);
				if(j>0)
					mTargetSkeleton.addSpringBone(new JointConnection(mBoneNamePrefix+(j-1)+"-"+i+"_"+j+"-"+i, mJoints[j-1][i],newJoint), mStrength);
				prevJoint = newJoint;
			}
		}

		return mTargetSkeleton;
	}

	public Joint getJoint(int indexX,int indexY) {
		return mJoints[indexY][indexX];
	}

	public int getCountX() {
		return mCountX;
	}

	public int getCountY() {
		return mCountY;
	}

	//--------DRAWING--------

	public int getVertexCount() {
		return mCountX*mCountY;
	}

	public void initGraphics(DefaultGraphics<?> graphics) {
		mGridDrawer = new GridCreator<DefaultGraphics<?>>(graphics);
	}

	public void putPositions() {
		if(mGridDrawer==null)
			throw new RuntimeException("Graphics not initialized");
		IndexedVertexBuffer vBuffer = mGridDrawer.mGraphics.mCurrentVertexBuffer;
		for(Joint[] row:mJoints) {
			for(Joint joint:row) {
				vBuffer.putVec3(DefaultGraphics.ID_POSITIONS, joint.mWorldPosition);
			}
		}
	}

	public void drawDefault() {
		int indPos = mGridDrawer.mGraphics.mCurrentVertexBuffer.getCurrentVertexWriteCount();
		mGridDrawer.begin(mCountX,mCountY, 1,1);
		putPositions();
		mGridDrawer.putGridColor(FloatColor.WHITE.mValues);
		mGridDrawer.putGridSuppData(FloatColor.BLACK.mValues);
		mGridDrawer.putGridTextureNormalRect();
		if(mGridDrawer.mGraphics instanceof Default3DGraphics) {
			((Default3DGraphics)mGridDrawer.mGraphics).fillNormals(indPos);
		}
	}

}
