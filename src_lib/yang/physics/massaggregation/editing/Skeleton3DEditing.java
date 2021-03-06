package yang.physics.massaggregation.editing;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.geometrycreators.LineDrawer3D;
import yang.graphics.defaults.geometrycreators.SphereCreator;
import yang.graphics.model.FloatColor;
import yang.math.Geometry;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;
import yang.physics.massaggregation.elements.JointConnection;
import yang.util.YangList;

public class Skeleton3DEditing {

	public static int SPHERE_VERTICES_X = 24;
	public static int SPHERE_VERTICES_Y = 16;
	public static boolean DRAW_PARENT_CONNECTIONS = true;

	public static int MAX_JOINTS = 2048;
	//public static FloatColor jointColor = new FloatColor(0.9f,0.2f,0.2f);
	public static FloatColor jointFixedColor = new FloatColor(0.9f,0.2f,0.2f);
//	public static FloatColor hoverColor = new FloatColor(1,0.3f,0.04f);
	public static FloatColor selectedColor = new FloatColor(1,0.5f,0);
	public static FloatColor jointSelectedAddColor = new FloatColor(0.3f,0.2f,0.2f,0.1f);

	public JointEditData[] mJointData = new JointEditData[MAX_JOINTS];
	public Default3DGraphics mGraphics3D;
	public MassAggregation mSkeleton;
	public LineDrawer3D mLineDrawer;
	public boolean mActive = true;

	private final Vector3f tempVec1 = new Vector3f();
	public Joint mHoverJoint = null;
	private DrawBatch mSphereBatch;

	public JointEditListener mJointEditListener = null;
	public JointDrawCallback mJointDrawCallback = null;

	public float mAlpha = 1;
	private FloatColor mJointColor1 = new FloatColor(),mJointColor2 = new FloatColor();
	public boolean mVisible = true;

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
		mLineDrawer.mDefaultLineWidth = lineWidth;
		return this;
	}

	public void refreshSkeletonData() {
		for(final Joint joint:mSkeleton.mJoints) {
			mJointData[joint.mId].set(joint,this);
		}
	}

	public void draw() {
		if(mLineDrawer==null)
			initLines(8,0.1f);
		if(!mVisible)
			return;
		mGraphics3D.mTranslator.switchZBuffer(true);

		int indStart = mGraphics3D.mCurrentVertexBuffer.getCurrentIndexWriteCount();

		if(mSphereBatch==null) {
			final SphereCreator sphere = new SphereCreator(mGraphics3D);
			sphere.initBatch(SPHERE_VERTICES_X,SPHERE_VERTICES_Y, 1,1);
			sphere.beginDraw();
			sphere.putAllIndices();
			sphere.putPositions();
			mSphereBatch = sphere.finishBatch();
		}

//		for(Bone bone:mSkeleton.mBones) {
//			Joint joint1 = bone.mJoint1;
//			Joint joint2 = bone.mJoint2;
//			mLineDrawer.drawLine(joint1.mPosX,joint1.mPosY,joint1.mPosZ, joint2.mPosX,joint2.mPosY,joint2.mPosZ);
//		}

		mGraphics3D.setGlobalTransformEnabled(true);
		mGraphics3D.mWorldTransform.loadIdentity();
		//mGraphics3D.mWorldTransform.scale(mSkeleton.mScale);

		if(DRAW_PARENT_CONNECTIONS) {
			for(Joint joint:mSkeleton.mJoints) {
				if(joint==null)
					continue;
				JointEditData jointData = mJointData[joint.mId];

				if(jointData.mVisible && joint.mParent!=null){
					final Joint parent = joint.mParent;
					mJointColor1.set(1,1,1,mAlpha);
					float r;
					if(mJointDrawCallback!=null) {
						mJointDrawCallback.getJointLineColor(jointData,mJointColor1);
						r = mJointDrawCallback.getJointRadius(jointData);
					}else
						r = jointData.getOutputRadius();
					mJointColor1.clamp();

					mLineDrawer.drawLine(joint.mWorldPosition, parent.mWorldPosition, r*0.25f,parent.getOutputRadius()*0.25f);
					mLineDrawer.mCylinder.putColor(mJointColor1.mValues);
				}
			}
		}

		for(JointConnection bone:mSkeleton.mBones) {
//			if(!bone.connectsChildParent()) {
				Joint joint1 = bone.mJoint1;
				Joint joint2 = bone.mJoint2;
				if(joint1==null || joint2==null)
					continue;
				JointEditData jointData1 = getJointEditData(joint1);
				JointEditData jointData2 = getJointEditData(joint2);
				if(jointData1.mVisible && jointData2.mVisible) {
					mJointColor1.set(1,1,1,mAlpha);
					mJointColor2.set(1,1,1,mAlpha);

					float r1;
					float r2;
					if(mJointDrawCallback!=null) {
						mJointDrawCallback.getJointLineColor(jointData1,mJointColor1);
						mJointDrawCallback.getJointLineColor(jointData2,mJointColor2);
						r1 = mJointDrawCallback.getJointRadius(jointData1);
						r2 = mJointDrawCallback.getJointRadius(jointData2);
					}else{
						r1 = jointData1.getOutputRadius();
						r2 = jointData2.getOutputRadius();

					}
					mJointColor1.clamp();
					mJointColor2.clamp();

					mLineDrawer.drawLine(joint1.mWorldPosition, joint2.mWorldPosition, r1*0.5f,r2*0.5f);
					mLineDrawer.mCylinder.putStartEndColors(mJointColor1.mValues,mJointColor2.mValues);
				}
//			}
		}

		mGraphics3D.fillNormals(indStart);
		mGraphics3D.fillBuffers();

		for(Joint joint:mSkeleton.mJoints) {
			JointEditData jointData = mJointData[joint.mId];
			if(joint==null || !jointData.mVisible)
				continue;
			float radius;
			if(joint.mFixed)
				mJointColor1.set(jointFixedColor);
			else
				mJointColor1.set(jointData.mDefaultColor);
			if(jointData.mSelectionGroup>=0)
				mJointColor1.add(jointSelectedAddColor);
			if(mJointDrawCallback!=null) {
				mJointDrawCallback.getJointColor(jointData,mJointColor1);
				radius = mJointDrawCallback.getJointRadius(jointData);
			}else
				radius = jointData.getOutputRadius();
			mJointColor1.clamp();
			mGraphics3D.setColor(mJointColor1);
			if(jointData.mSelectionGroup<0 && joint==mHoverJoint)
				mGraphics3D.multColor(1.3f);

			mGraphics3D.setColorFactor(mGraphics3D.getCurrentColor());
			mGraphics3D.mColorFactor[3] *= mAlpha;
			mGraphics3D.mWorldTransform.stackPush();
			mGraphics3D.mWorldTransform.translate(joint.mWorldPosition);
			mGraphics3D.mWorldTransform.scale(radius);
			mSphereBatch.draw();
			mGraphics3D.mWorldTransform.stackPop();
		}
		mGraphics3D.setGlobalTransformEnabled(false);
		mGraphics3D.setColorFactor(1);
	}

	public Skeleton3DEditing initLines() {
		return initLines(16,0.03f);
	}

	public Joint pickJoint3D(Point3f pickPos,float pickRadius,float radiusFactor,boolean onlySelectable) {
		Joint result = null;
		mSkeleton.mInvTransform.apply3D(pickPos.mX,pickPos.mY,pickPos.mZ,tempVec1);
		tempVec1.scale(1/mSkeleton.mScale);
		float minDist = Float.MAX_VALUE;
		for(final Joint joint:mSkeleton.mJoints) {
			JointEditData jointData = mJointData[joint.mId];
			if(!onlySelectable || jointData.mSelectable) {
				final float dist = tempVec1.getDistance(joint.mX,joint.mY,joint.mZ);
				if(dist<minDist && dist<pickRadius+joint.getOutputRadius()*radiusFactor) {
					minDist = dist;
					result = joint;
				}
			}
		}
		return result;
	}

	public Joint pickJoint3D(Point3f pickPos,float pickRadius,float radiusFactor) {
		return pickJoint3D(pickPos,pickRadius,radiusFactor,false);
	}

	public Joint pickJoint2D(float x,float y,float zoom,float radiusFactor) {
		mGraphics3D.prepareProjection();
		float minDist = Float.MAX_VALUE;

		Joint result = null;
		for(final Joint joint:mSkeleton.mJoints) {
			final float rad = mGraphics3D.getProjectedPositionAndRadius(tempVec1, joint.mX,joint.mY,joint.mZ, joint.getOutputRadius()*radiusFactor);
			final float dist = Geometry.getDistance(x-tempVec1.mX, y-tempVec1.mY);
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

	public void setJointSelected(Joint joint,int group,int depth) {
		final JointEditData data = mJointData[joint.mId];
//		if(index>=0)
//			mSelection[index] = data;
//		else if(data.mSelectionIndex>=0)
//			mSelection[data.mSelectionIndex] = null;
		if(data.mSelectionDepth==-1 || data.mSelectionDepth>=depth) {
			if(group<0) {
				joint.endDrag();
				if(mJointEditListener!=null)
					mJointEditListener.onDeselectJoint(data);
				data.mSelectionGroup = -1;
				data.mSelectionDepth = -1;
				if(data.mParentConnection!=null) {
					//data.mParentConnection.mApplyToJoint1 = true;
				}

			}else{
				data.mSelectionGroup = group;
				data.mSelectionDepth = depth;
				if(mJointEditListener!=null)
					mJointEditListener.onSelectJoint(data);
				data.mLstSelectTime = mGraphics3D.mTranslator.mTimer;
//				if(data.mParentConnection!=null) {
//					if(data.mParentConnection.mBone.mJoint1==joint)
//						data.mParentConnection.mApplyToJoint2 = false;
//					else
//						data.mParentConnection.mApplyToJoint1 = false;
//				}
				joint.startDrag();
			}
		}
		if(depth>-1) {
			for(Joint child:joint.mChildren)
				setJointSelected(child,group,depth+1);
		}
	}

	public void setJointSelected(Joint joint,int group,boolean recursive) {
		setJointSelected(joint,group,recursive?0:-1);
	}

	public void setJointSelected(Joint joint,int group) {
		setJointSelected(joint,group,false);
	}

	public void unselectJoint(Joint joint) {
		setJointSelected(joint,-1);
	}

	public void unselectJointGroup(int group) {
		for(final Joint joint:mSkeleton.mJoints) {
			final JointEditData data = mJointData[joint.mId];
			if(data.mSelectionGroup==group)
				unselectJoint(joint);
		}
	}

	public void unselectAllJoints() {
		for(final Joint joint:mSkeleton.mJoints) {
			setJointSelected(joint,-1);
			joint.endDrag();
		}
	}

	public boolean isSelected(Joint joint) {
		return mJointData[joint.mId].mSelectionGroup>=0;
	}

	public int getSelectionCount() {
		int result = 0;
		for(final Joint joint:mSkeleton.mJoints) {
			if(mJointData[joint.mId].mSelectionGroup>=0)
				result++;
		}
		return result;
	}

	public YangList<Joint> getJoints() {
		return mSkeleton.mJoints;
	}

	public void setPrevPositions() {
		for(Joint joint:mSkeleton.mJoints) {
			mJointData[joint.mId].setPrevPos();
		}
	}

	public void setJointEditListener(JointEditListener listener) {
		mJointEditListener = listener;
	}

	public void setSelectable(boolean selectable) {
		for(Joint joint:mSkeleton.mJoints) {
			mJointData[joint.mId].mSelectable = selectable;
		}
	}

	public void setJointRadiusOutputFactor(float factor) {
		for(JointEditData jointData:mJointData)
			jointData.mOutputRadiusFactor = factor;

	}

}
