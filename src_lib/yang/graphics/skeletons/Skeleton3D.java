package yang.graphics.skeletons;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.meshcreators.LineDrawer3D;
import yang.graphics.model.FloatColor;
import yang.graphics.skeletons.elements.Bone;
import yang.graphics.skeletons.elements.Joint;

public class Skeleton3D {

	public Default3DGraphics mGraphics3D;
	public Skeleton mSkeleton;
	public LineDrawer3D mLineDrawer;
	
	public Skeleton3D(Default3DGraphics graphics3D,Skeleton skeleton) {
		mGraphics3D = graphics3D;
		mSkeleton = skeleton;
	}
	
	public Skeleton3D initLines(int cylinderSamples,float lineWidth) {
		mLineDrawer = new LineDrawer3D(mGraphics3D);
		mLineDrawer.setSamples(cylinderSamples);
		mLineDrawer.mLineWidth = lineWidth;
		return this;
	}
	
	public void draw() {
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
		mGraphics3D.setColor(FloatColor.RED);
		for(Joint joint:mSkeleton.mJoints) {
			mGraphics3D.drawSphere(24,16, joint.mPosX,joint.mPosY,joint.mPosZ, joint.getOutputRadius()*0.9f, 2,1);
		}
		
	}
	
	public Skeleton3D initLines() {
		return initLines(16,0.03f);
	}
	
}
