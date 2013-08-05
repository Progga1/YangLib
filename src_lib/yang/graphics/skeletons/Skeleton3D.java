package yang.graphics.skeletons;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.meshcreators.LineDrawer3D;
import yang.graphics.model.FloatColor;
import yang.graphics.skeletons.elements.Joint;
import yang.math.Geometry;
import yang.math.objects.Vector3f;
import yang.math.objects.matrix.YangMatrix;
import yang.math.objects.matrix.YangMatrixCameraOps;

public class Skeleton3D {

	public static FloatColor jointColor = new FloatColor(0.9f,0.2f,0.2f);
	public static FloatColor hoverColor = new FloatColor(1,0.3f,0.04f);
	public static FloatColor selectedColor = new FloatColor(1,0,0);
	
	public JointEditData[] mJointData = new JointEditData[256];
	public Default3DGraphics mGraphics3D;
	public Skeleton mSkeleton;
	public LineDrawer3D mLineDrawer;
	private YangMatrixCameraOps mProjectMatrix = new YangMatrixCameraOps();
	
	private Vector3f tempVec1 = new Vector3f();
	public Joint mHoverJoint = null;
	
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
		
		for(Joint joint:mSkeleton.mJoints) {
			if(joint==mHoverJoint) {
				mGraphics3D.setColor(hoverColor);
			}else{
				mGraphics3D.setColor(jointColor);
			}
			mGraphics3D.drawSphere(24,16, joint.mPosX,joint.mPosY,joint.mPosZ, joint.getOutputRadius(), 2,1);
		}
		
	}
	
	public Skeleton3D initLines() {
		return initLines(16,0.03f);
	}

	public Joint pickJoint(float x,float y,float zoom) {
		mGraphics3D.getToScreenTransform(mProjectMatrix);
		float minDist = Float.MAX_VALUE;
		
		Joint result = null;
		float radFac = 1f/zoom;
		for(Joint joint:mSkeleton.mJoints) {
			mProjectMatrix.apply3D(joint.mPosX,joint.mPosY,joint.mPosZ,tempVec1);
			//mGraphics2D.drawRectCentered(mReprojectPos.mX, mReprojectPos.mY, joint.getOutputRadius()*2/mZoom);
			float dist = Geometry.getDistance(x-tempVec1.mX, y-tempVec1.mY);
			float rad = joint.getOutputRadius()*radFac;
			if(dist<=rad && dist<=minDist) {
				result = joint;
			}
		}
		return result;
	}
	
}
