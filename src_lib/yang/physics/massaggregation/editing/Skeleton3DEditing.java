package yang.physics.massaggregation.editing;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.meshcreators.LineDrawer3D;
import yang.graphics.defaults.meshcreators.SphereCreator;
import yang.graphics.model.FloatColor;
import yang.math.Geometry;
import yang.math.objects.Vector3f;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;
import yang.util.NonConcurrentList;

public class Skeleton3DEditing {

	public static int SPHERE_VERTICES_X = 24;
	public static int SPHERE_VERTICES_Y = 16;
	
	public static int MAX_JOINTS = 256;
	public static FloatColor jointColor = new FloatColor(0.9f,0.2f,0.2f);
	public static FloatColor hoverColor = new FloatColor(1,0.3f,0.04f);
	public static FloatColor selectedColor = new FloatColor(1,0.5f,0);
	
	public JointEditData[] mJointData = new JointEditData[MAX_JOINTS];
	//public JointEditData[] mSelection = new JointEditData[MAX_JOINTS];
	public Default3DGraphics mGraphics3D;
	public MassAggregation mSkeleton;
	public LineDrawer3D mLineDrawer;
	
	private Vector3f tempVec1 = new Vector3f();
	public Joint mHoverJoint = null;
	private DrawBatch mSphereBatch;
	
	public Skeleton3DEditing(Default3DGraphics graphics3D,MassAggregation skeleton) {
		mGraphics3D = graphics3D;
		mSkeleton = skeleton;
		mSkeleton.m3D = true;
		for(int i=0;i<MAX_JOINTS;i++) {
			mJointData[i] = new JointEditData();
		}
		refreshSkeletonData();
	}
	
	public Skeleton3DEditing initLines(int cylinderSamples,float lineWidth) {
		mLineDrawer = new LineDrawer3D(mGraphics3D);
		mLineDrawer.setSamples(cylinderSamples);
		mLineDrawer.mLineWidth = lineWidth;
		return this;
	}
	
	public void refreshSkeletonData() {
		for(Joint joint:mSkeleton.mJoints) {
			mJointData[joint.mId].set(joint);
		}
	}
	
	public void draw() {
		
		if(mSphereBatch==null) {
			SphereCreator sphere = new SphereCreator(mGraphics3D);
			sphere.beginBatch(SPHERE_VERTICES_X,SPHERE_VERTICES_Y, 1,1);
			sphere.putPositions();
			mSphereBatch = sphere.finishBatch();
		}
		
//		for(Bone bone:mSkeleton.mBones) {
//			Joint joint1 = bone.mJoint1;
//			Joint joint2 = bone.mJoint2;
//			mLineDrawer.drawLine(joint1.mPosX,joint1.mPosY,joint1.mPosZ, joint2.mPosX,joint2.mPosY,joint2.mPosZ);
//		}
		mGraphics3D.setWhite();
		for(Joint joint:mSkeleton.mJoints)
			if(joint.mEnabled && joint.mAngleParent!=null){
				Joint parent = joint.mAngleParent;
				mLineDrawer.drawLine(joint.mPosX,joint.mPosY,joint.mPosZ, parent.mPosX,parent.mPosY,parent.mPosZ, joint.getOutputRadius()*0.5f,parent.getOutputRadius()*0.5f);
			}
		mGraphics3D.fillNormals(0);
		mGraphics3D.fillBuffers();
		
		mGraphics3D.setGlobalTransformEnabled(true);
		for(Joint joint:mSkeleton.mJoints) {
			JointEditData data = mJointData[joint.mId];
			
			if(data.mSelectionIndex>=0)
				mGraphics3D.setColor(selectedColor);
			else if(joint==mHoverJoint)
				mGraphics3D.setColor(hoverColor);
			else
				mGraphics3D.setColor(jointColor);
			mGraphics3D.setColorFactor(mGraphics3D.getCurrentColor());
			mGraphics3D.mWorldTransform.loadIdentity();
			mGraphics3D.mWorldTransform.translate(joint.mPosX,joint.mPosY,joint.mPosZ);
			mGraphics3D.mWorldTransform.scale(joint.getOutputRadius());
			mSphereBatch.draw();
		}
		mGraphics3D.setGlobalTransformEnabled(false);
		mGraphics3D.setColorFactor(1);
		
	}
	
	public Skeleton3DEditing initLines() {
		return initLines(16,0.03f);
	}

	public Joint pickJoint(float x,float y,float zoom,float radiusFactor) {
		mGraphics3D.prepareProjection();
		float minDist = Float.MAX_VALUE;
		
		Joint result = null;
		for(Joint joint:mSkeleton.mJoints) {
			float rad = mGraphics3D.getProjectedPositionAndRadius(tempVec1, joint.mPosX,joint.mPosY,joint.mPosZ, joint.getOutputRadius()*radiusFactor);
			float dist = Geometry.getDistance(x-tempVec1.mX, y-tempVec1.mY);
			if(dist<=rad && dist<=minDist) {
				result = joint;
				minDist = dist;
			}
		}
		return result;
	}
	
	public JointEditData getJointEditData(Joint joint) {
		return mJointData[joint.mId];
	}

	public void setJointSelected(Joint joint,int index) {
		JointEditData data = mJointData[joint.mId];
//		if(index>=0)
//			mSelection[index] = data;
//		else if(data.mSelectionIndex>=0)
//			mSelection[data.mSelectionIndex] = null;
		joint.endDrag();
		data.mSelectionIndex = index;
	}
	
	public void unselectJoint(Joint joint) {
		setJointSelected(joint,-1);
	}

	public void unselectJoint(int index) {
		for(Joint joint:mSkeleton.mJoints) {
			JointEditData data = mJointData[joint.mId];
			if(data.mSelectionIndex==index)
				unselectJoint(joint);
		}
	}
	
	public void unselectAllJoints() {
		for(Joint joint:mSkeleton.mJoints) {
			setJointSelected(joint,-1);
			joint.endDrag();
		}
	}
	
	public boolean isSelected(Joint joint) {
		return mJointData[joint.mId].mSelectionIndex>=0;
	}
	
	public int getSelectionCount() {
		int result = 0;
		for(Joint joint:mSkeleton.mJoints) {
			if(mJointData[joint.mId].mSelectionIndex>=0)
				result++;
		}
		return result;
	}

	public NonConcurrentList<Joint> getJoints() {
		return mSkeleton.mJoints;
	}
	
}