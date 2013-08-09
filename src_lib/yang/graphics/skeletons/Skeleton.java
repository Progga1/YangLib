package yang.graphics.skeletons;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.skeletons.constraints.Constraint;
import yang.graphics.skeletons.constraints.DistanceConstraint;
import yang.graphics.skeletons.defaults.DefaultSkeletonCarrier;
import yang.graphics.skeletons.elements.Bone;
import yang.graphics.skeletons.elements.Joint;
import yang.graphics.skeletons.pose.Pose;
import yang.graphics.textures.TextureHolder;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.model.Rect;
import yang.util.NonConcurrentList;

//TODO: bone-arrays

public abstract class Skeleton {

	public static int DEFAULT_ACCURACY = 16;
	static final float[] COLOR_BLACK = {0,0,0,1};
	public static final TextureFilter DEFAULTFILTER = TextureFilter.LINEAR_MIP_LINEAR;
	public static Texture CURSOR_TEXTURE;
	
	public float mContourFactor = 0.015f;
	
	public boolean mSkeletonAngleConstraints = true;
	public float mFloorFriction = 0.98f;
	public TextureHolder mTextureHolder;
	public TextureHolder mContourTextureHolder;
	public boolean mConstraintsActivated;
	protected DefaultGraphics<?> mGraphics;
	protected GraphicsTranslator mTranslator;
	public NonConcurrentList<Joint> mJoints;
	public NonConcurrentList<NonConcurrentList<Bone>> mLayersList;
	public Bone[][] mLayers;
	public Bone[][] mFrontToBackLayers;
	public NonConcurrentList<Bone> mBones;
	protected float[] mSkeletonColor;
	protected float[] mContourColor;
	protected float[] mSuppData;
	public SkeletonCarrier mCarrier;
	public NonConcurrentList<Constraint> mConstraints;
	public float mSkeletonOffsetX;
	public float mSkeletonOffsetY;
	public float mRotation;
	public float mRotAnchorX;
	public float mRotAnchorY;
	public Pose<?> mCurrentPose;
	public Rect mBoundariesRect;
	private boolean mInitialized;
	public float mConstantForceX;
	public float mConstantForceY;
	public float mLowerLimit;
	public float mLimitForceInwards;
	public float mLimitForceOutwards;
	public int mAccuracy;
	public boolean mDrawContour;
	public boolean mDrawFill;
	public float mScale = 1;
	public float mPosZ = 0;
	private int mCurJointId = 0;
	
	public DrawBatch mMesh;
	protected IndexedVertexBuffer mVertexBuffer;
	protected boolean mUpdateColor;
	protected boolean mUpdateTexCoords;
	protected int mVertexCount;	
	
	protected float[] mInterColor = new float[4];
	
	public Skeleton() {
		mJoints = new NonConcurrentList<Joint>();
		mBones = new NonConcurrentList<Bone>();
		mLayersList = new NonConcurrentList<NonConcurrentList<Bone>>();
		mConstraints = new NonConcurrentList<Constraint>();
		mTextureHolder = null;
		mContourTextureHolder = null;
		mSkeletonColor = new float[4];
		mConstraintsActivated = true;
		mContourColor = new float[]{0,0,0,0};
		mSuppData = new float[]{0,0,0,0};
		setModColor(1,1,1);
		mSkeletonOffsetX = 0;
		mSkeletonOffsetY = 0;
		mRotation = 0;
		mInitialized = false;
		mAccuracy = DEFAULT_ACCURACY;
		mConstantForceX = 0;
		mConstantForceY = 0;
		mLimitForceInwards = 20f;
		mLimitForceOutwards = 10f;
		mLowerLimit = Float.MIN_VALUE;
		mDrawContour = true;
	}
	
	protected abstract void build();
	
	public void recalculateConstraints() {
		for(Joint joint:mJoints) {
			joint.recalculate();
		}
		for(Constraint constraint:mConstraints)
			constraint.recalculate();
	}
	
	public void setBonesVisible(boolean visible) {
		for(Bone bone:mBones) {
			bone.mVisible = visible;
		}
	}
	
	public void init(DefaultGraphics<?> graphics,SkeletonCarrier carrier) {
		mCarrier = carrier;
		carrier.setSkeleton(this);
		mGraphics = graphics;
		mTranslator = mGraphics.mTranslator;
		
		mDrawFill = true;
		
		mCurJointId = 0;
		build();
		
		mBoundariesRect = new Rect();
		refreshBoundariesRect();
		mInitialized = true;
		
		finish();
	}
	
	protected void finish() {
		int l = mLayersList.size();
		mLayers = new Bone[l][];
		mFrontToBackLayers = new Bone[l][];
		int k=0;
		for(NonConcurrentList<Bone> layer:mLayersList) {
			Bone[] layerArray = new Bone[layer.size()];
			int c=0;
			for(Bone bone:layer) {
				layerArray[c] = bone;
				c++;
			}
			mLayers[k] = layerArray;
			mFrontToBackLayers[l-1-k] = layerArray;
			k++;
		}
	}
	
	public void setTexture(TextureHolder texture) {
		mTextureHolder = texture;
		mContourTextureHolder = texture;
	}
	
	public void setTexture(String filename,TextureProperties settings) {
		setTexture(new TextureHolder(filename,settings));
	}
	
	public void setTexture(String filename,TextureFilter filter) {
		setTexture(filename,new TextureProperties(TextureWrap.CLAMP,filter));
	}
	
	public void refreshBoundariesRect() {
		mBoundariesRect.set(100000,-100000,-100000,100000);
		for(Joint joint:mJoints) {
			if(joint.mPosX<mBoundariesRect.mLeft)
				mBoundariesRect.mLeft = joint.mPosX;
			if(joint.mPosX>mBoundariesRect.mRight)
				mBoundariesRect.mRight = joint.mPosX;
			if(joint.mPosY>mBoundariesRect.mTop)
				mBoundariesRect.mTop = joint.mPosY;
			if(joint.mPosY<mBoundariesRect.mBottom)
				mBoundariesRect.mBottom = joint.mPosY;
		}
	}
	
	public void init(DefaultGraphics<?> graphics) {
		init(graphics,new DefaultSkeletonCarrier());
	}
	
	public void addJoint(Joint bone) {
		mJoints.add(bone);
	}
	
	public void addConstraint(Constraint constraint) {
		mConstraints.add(constraint);
	}
	
	public void addBone(Bone bone,int layer,float constraintDistanceStrength) {
		while(layer>mLayersList.size()-1)
		{
			mLayersList.add(new NonConcurrentList<Bone>());
		}
		mLayersList.get(layer).add(bone);
		mBones.add(bone);
		if(constraintDistanceStrength>0)
			addConstraint(new DistanceConstraint(bone,constraintDistanceStrength));
	}

	public void addBone(Bone bone,int layer) {
		addBone(bone,layer,10);
	}

	public Joint getBoneByName(String name) {
		name = name.toUpperCase();
		for(Joint bone:mJoints) {
			if(bone.mName.equals(name))
				return bone;
		}
		return null;
	}
	
	public float getJointWorldX(Joint joint) {
		return mCarrier.getWorldX() + (mSkeletonOffsetX + joint.mPosX*mCarrier.getLookDirection())*mCarrier.getScale()*mScale;
	}
	
	public float getJointWorldY(Joint joint) {
		return mCarrier.getWorldY() + (mSkeletonOffsetY + joint.mPosY)*mCarrier.getScale()*mScale;
	}
	
	public void loadTexture() {
		mContourTextureHolder.getTexture(mTranslator.mGFXLoader);
	}
	
	public void draw() {	

		if(mMesh==null) {
			//FIRST DRAW
			mVertexCount = 0;
			for(NonConcurrentList<Bone> layer:mLayersList) {
				//Contour
				for(Bone bone:layer) {
					if(mDrawContour && bone.mCelShading)
						mVertexCount += 4;
					mVertexCount += 4;
				}
			}
			int indexCount = mBones.size()*6*2;
			mVertexBuffer = mGraphics.createVertexBuffer(true, false, indexCount, mVertexCount);
			mVertexBuffer.setIndexPosition(0);
			for(short i=0;i<mVertexCount;i+=4)
				mVertexBuffer.beginQuad(false,i);
			mVertexBuffer.mFinishedIndexCount = indexCount;
			mVertexBuffer.mFinishedVertexCount = mVertexCount;
			mVertexBuffer.reset();
			mMesh = new DrawBatch(mGraphics,mVertexBuffer);
			mUpdateColor = true;
			mUpdateTexCoords = true;
		}
//
//		System.arraycopy(mSuppData, 0, mInterColor, 0, 4);
//		mInterColor[3] = 0;
//		final float zInc = 0.01f;
		
		//--UPDATE COLOR--
		if(mUpdateColor) {
			
			mVertexBuffer.setDataPosition(DefaultGraphics.ID_COLORS,0);
			mVertexBuffer.setDataPosition(DefaultGraphics.ID_SUPPDATA, 0);
			for(Bone[] layer:mLayers) {
				//Contour
				if(mDrawContour)
					for(Bone bone:layer) {
						if(bone.mCelShading) {
							mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, DefaultGraphics.BLACK,4);
							mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mContourColor,4);
						}
					}
				//Fill
				for(Bone _:layer) {
					mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, DefaultGraphics.WHITE,4);
					mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mSuppData,4);
					//mInterColor[3] += zInc;
				}
				//mInterColor[3] += 0.05f;
			}
			mUpdateColor = false;
		}
		
		//--UPDATE TEXTURE COORDINATES--
		if(mUpdateTexCoords) {
			mVertexBuffer.setDataPosition(DefaultGraphics.ID_TEXTURES, 0);
			for(Bone[] layer:mLayers) {
				//Contour
				if(mDrawContour) {
					for(Bone bone:layer) {
						if(bone.mCelShading) {
							mVertexBuffer.putArray(DefaultGraphics.ID_TEXTURES,bone.getTextureCoordinates().mAppliedCoordinates);
						}
					}
				}
				//Fill
				for(Bone bone:layer) {
					mVertexBuffer.putArray(DefaultGraphics.ID_TEXTURES,bone.getTextureCoordinates().mAppliedCoordinates);
				}
			}
			mUpdateTexCoords = false;
		}
		
		//--UPDATE POSITIONS--
		mVertexBuffer.setDataPosition(DefaultGraphics.ID_POSITIONS, 0);
		float worldPosX = mCarrier.getWorldX() + mSkeletonOffsetX;
		float worldPosY = mCarrier.getWorldY() + mSkeletonOffsetY;
		float scale = mCarrier.getScale()*mScale;
		int mirrorFac = mCarrier.getLookDirection();
		
		for(Bone[] layer:mLayers) {
			//Contour
			if(mDrawContour) {
				for(Bone bone:layer) {
					if(bone.mCelShading) {
						if(bone.mVisible) {
							float contourOrthoX = bone.mOrthNormX * mContourFactor;
							float contourOrthoY = bone.mOrthNormY * mContourFactor;
							float contourNormX = bone.mNormDirX * mContourFactor;
							float contourNormY = bone.mNormDirY * mContourFactor;
							mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX4*scale + contourOrthoX*bone.mContourX3 + contourNormX*bone.mContourY4) * mirrorFac, worldPosY + bone.mVertY4*scale + contourOrthoY*bone.mContourX3 + contourNormY*bone.mContourY4, mPosZ);
							mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX3*scale - contourOrthoX*bone.mContourX4 + contourNormX*bone.mContourY3) * mirrorFac, worldPosY + bone.mVertY3*scale - contourOrthoY*bone.mContourX4 + contourNormY*bone.mContourY3, mPosZ);
							mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX2*scale + contourOrthoX*bone.mContourX1 - contourNormX*bone.mContourY1) * mirrorFac, worldPosY + bone.mVertY2*scale + contourOrthoY*bone.mContourX1 - contourNormY*bone.mContourY1, mPosZ);
							mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX1*scale - contourOrthoX*bone.mContourX2 - contourNormX*bone.mContourY2) * mirrorFac, worldPosY + bone.mVertY1*scale - contourOrthoY*bone.mContourX2 - contourNormY*bone.mContourY2, mPosZ);
						}else{
							mVertexBuffer.putArray(DefaultGraphics.ID_POSITIONS,DefaultGraphics.FLOAT_ZERO_12);
						}
					}
				}
			}
			
			//Fill
			for(Bone bone:layer) {
				if(bone.mVisible) {
					mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX4*scale * mirrorFac , worldPosY + bone.mVertY4*scale, mPosZ);
					mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX3*scale * mirrorFac , worldPosY + bone.mVertY3*scale, mPosZ);
					mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX2*scale * mirrorFac , worldPosY + bone.mVertY2*scale, mPosZ);
					mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX1*scale * mirrorFac , worldPosY + bone.mVertY1*scale, mPosZ);
				}else{
					mVertexBuffer.putArray(DefaultGraphics.ID_POSITIONS,DefaultGraphics.FLOAT_ZERO_12);
				}
			}
		}

		mGraphics.bindTextureInHolder(mTextureHolder);
		
		mMesh.draw();
		
	}
	
	public void drawEditing(SkeletonEditing skeletonEditing) {
		Joint markedJoint;
		if(skeletonEditing==null)
			markedJoint = null;
		else
			markedJoint = skeletonEditing.mMarkedJoint;
		float worldPosX = mCarrier.getWorldX() + mSkeletonOffsetX;
		float worldPosY = mCarrier.getWorldY() + mSkeletonOffsetY;
		float scale = mCarrier.getScale()*mScale;
		int mirrorFac = mCarrier.getLookDirection();

		mGraphics.setDefaultProgram();
		mTranslator.bindTexture(CURSOR_TEXTURE);
		
		for(Joint joint:mJoints) 
			if(joint.mEnabled){
				float alpha = (markedJoint==joint)?1:0.6f;
				if(joint.mFixed)
					mGraphics.setColor(1, 0, 0, alpha);
				else
					mGraphics.setColor(0.8f,0.8f,0.8f,alpha);
				mGraphics.drawRectCentered(worldPosX + joint.mPosX*scale * mirrorFac, worldPosY + joint.mPosY*scale, joint.getOutputRadius()*2);
			}
		
		mTranslator.bindTexture(null);
		mGraphics.setColor(0.8f, 0.1f, 0,0.8f);
		for(Joint joint:mJoints)
			if(joint.mEnabled && joint.mAngleParent!=null){
				mGraphics.drawLine(
						worldPosX + joint.mPosX*scale * mirrorFac, worldPosY + joint.mPosY*scale, 
						worldPosX + joint.mAngleParent.mPosX*scale * mirrorFac, worldPosY + joint.mAngleParent.mPosY*scale,
						0.015f
						);
		}
		
	}
	
	public float toJointX(float x) {
		return (x-mCarrier.getWorldX())*mCarrier.getScale();
	}
	
	public float toJointY(float y) {
		return (y-mCarrier.getWorldY())*mCarrier.getScale();
	}
	
	public Joint pickJoint(float x,float y) {
		float minDist = 1000000;
		Joint resJoint = null;
		for(Joint joint:mJoints) {
			float dX = (x-this.getJointWorldX(joint));
			float dY = (y-this.getJointWorldY(joint));
			float dist = dX*dX + dY*dY;
			if(dist < joint.getOutputRadius()*joint.getOutputRadius()*4.5f && dist<minDist) {
				minDist = dist;
				resJoint = joint;
			}
		}
		return resJoint;
	}
	
	public void refreshVisualVars() {
		for(Bone connection:mBones)
			connection.refreshVisualVars();
	}
	
	public void applyConstraints(float deltaTime) {
		
		float uDeltaTime = deltaTime/mAccuracy;
		float worldY = mCarrier.getWorldY();
		for(int i=0;i<mAccuracy;i++) {
			
			//Init force
			for(Joint Joint:mJoints) {
				Joint.mForceX = mConstantForceX;
				Joint.mForceY = mConstantForceY;
				
				if(Joint.mPosY+worldY<mLowerLimit) {
					float uForce = (Joint.mVelY<0)?mLimitForceInwards:mLimitForceOutwards;
					Joint.mForceY += (mLowerLimit-Joint.mPosY)*uForce;
					Joint.mVelX *= mFloorFriction;
				}
			}
			
			//Apply constraints
			if(mConstraintsActivated)
				for(Constraint constraint:mConstraints) {
					constraint.apply();
				}
			
			if(mConstraintsActivated)
				for(Joint joint:mJoints) {
					joint.applyConstraint();
				}
			
			for(Joint joint:mJoints) {
				joint.physicalStep(uDeltaTime);
			}
		
		}
		
	}

	public void setOffset(float x, float y) {
		mSkeletonOffsetX = x;
		mSkeletonOffsetY = y;
	}
	
	public void setModColor(float r,float g,float b,float a) {
		mSkeletonColor[0] = r;
		mSkeletonColor[1] = g;
		mSkeletonColor[2] = b;
		mSkeletonColor[3] = a;
		mUpdateColor = true;
	}
	
	public void setModColor(float r,float g,float b) {
		setModColor(r,g,b,1);
	}
	
	public void setModColor(float brightness) {
		mSkeletonColor[0] = brightness;
		mSkeletonColor[1] = brightness;
		mSkeletonColor[2] = brightness;
		mSkeletonColor[3] = 1;
		mUpdateColor = true;
	}

	public void setContourColor(float r, float g, float b) {
		mContourColor[0] = r;
		mContourColor[1] = g;
		mContourColor[2] = b;
		mUpdateColor = true;
	}
	
	public void setContourColor(float brightness) {
		mContourColor[0] = brightness;
		mContourColor[1] = brightness;
		mContourColor[2] = brightness;
		mUpdateColor = true;
	}
	
	public void setSuppData(float r,float g,float b) {
		mSuppData[0] = r;
		mSuppData[1] = g;
		mSuppData[2] = b;
		mUpdateColor = true;
	}

	public void setSuppData(float brightness) {
		mSuppData[0] = brightness;
		mSuppData[1] = brightness;
		mSuppData[2] = brightness;
		mUpdateColor = true;
	}
	
	public void setRotationAnchor(float anchorX,float anchorY) {
		mRotAnchorX = anchorX;
		mRotAnchorY = anchorY;
	}
	
	public void resetRotationAnchor() {
		mRotAnchorX = 0;
		mRotAnchorY = 0;
	}

	public void reApplyPose() {
		if(mCurrentPose!=null)
			mCurrentPose.applyPose(this);
	}

	public boolean isInitialized() {
		return mInitialized;
	}
	
	@SuppressWarnings("unchecked")
	public <ConstraintType extends Constraint> ConstraintType getBoneConstraint(Bone bone,Class<ConstraintType> type) {
		for(Constraint constraint:mConstraints) {
			if((constraint.getClass()==type) && (constraint.containsBone(bone)))
				return (ConstraintType)constraint;
		}
		return null;
	}

	public void setFriction(float friction) {
		for(Joint joint:mJoints) {
			joint.mFriction = friction;
		}
	}

	public void updatedTextureCoords() {
		this.mUpdateTexCoords = true;
	}

	public int getNextJointId() {
		return mCurJointId++;
	}
	
}