package yang.graphics.skeletons.defaults;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.geometrycreators.grids.GridCreator;
import yang.graphics.model.FloatColor;
import yang.math.objects.matrix.YangMatrix;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.constraints.ColliderConstraint;
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
	public float mJointRadius = 0.1f;

	//Drawing
	public GridCreator<?> mGridDrawer;

	private int mColCount,mRowCount;

	public MassAggregation create(int countX,int countY,YangMatrix transform) {
		if(transform==null)
			transform = YangMatrix.IDENTITY;
		mColCount = countX;
		mRowCount = countY;
		mJoints = new Joint[countY][countX];
		if(countX<2 || countY<2)
			throw new RuntimeException("countX and countY must be larger or equal 2.");

		if(mTargetSkeleton==null) {
			mTargetSkeleton = new MassAggregation();
		}

		float ratio = (float)countX/countY;

		for(int j=0;j<countY;j++) {
			float y = (float)j/(countY-1);
			Joint prevJoint = null;
			for(int i=0;i<countX;i++) {
				Joint newJoint = new Joint(mJointNamePrefix+j+"-"+i);
				float x = (float)i/(countX-1) * ratio;
				transform.apply3D(x,y,0, newJoint);
				mJoints[j][i] = newJoint;
				newJoint.mRadius = mJointRadius;
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

	public int getColumnCount() {
		return mColCount;
	}

	public int getRowCount() {
		return mRowCount;
	}

	public void addCollider(Joint collJoint) {
		for(Joint[] row:mJoints) {
			for(Joint joint:row) {
				mTargetSkeleton.addConstraint(new ColliderConstraint(collJoint,joint));
			}
		}
	}

	public void setRowFixed(int row,boolean fixed) {
		Joint[] jointRow = mJoints[row];
		for(Joint joint:jointRow) {
			joint.mFixed = fixed;
		}
	}

	public void setColumnFixed(int column,boolean fixed) {
		for(int i=0;i<mRowCount;i++) {
			mJoints[i][column].mFixed = fixed;
		}
	}

	//--------DRAWING--------

	public int getVertexCount() {
		return mColCount*mRowCount;
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

	public void drawDefault(FloatColor color) {
		int indexStart = mGridDrawer.mGraphics.mCurrentVertexBuffer.getCurrentIndexWriteCount();
		mGridDrawer.begin(mColCount,mRowCount, 1,1);
		putPositions();
		mGridDrawer.putGridColor(color.mValues);
		mGridDrawer.putGridSuppData(FloatColor.BLACK.mValues);
		mGridDrawer.putGridTextureNormalRect();
		if(mGridDrawer.mGraphics instanceof Default3DGraphics) {
			((Default3DGraphics)mGridDrawer.mGraphics).fillNormals(indexStart);
		}
	}

	public void drawDefault() {
		drawDefault(FloatColor.WHITE);
	}

}
