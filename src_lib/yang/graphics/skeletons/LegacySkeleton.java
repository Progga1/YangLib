package yang.graphics.skeletons;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.programs.BasicProgram;
import yang.graphics.skeletons.constraints.Constraint;
import yang.graphics.skeletons.constraints.DistanceConstraint;
import yang.graphics.skeletons.defaults.DefaultSkeletonCarrier;
import yang.graphics.skeletons.elements.Bone;
import yang.graphics.skeletons.elements.Joint;
import yang.graphics.skeletons.pose.Pose;
import yang.graphics.textures.TextureHolder;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.model.Rect;
import yang.util.NonConcurrentList;

//TODO: bone-arrays

public abstract class LegacySkeleton {

	public static int DEFAULT_ACCURACY = 16;
	static final float[] COLOR_BLACK = {0,0,0,1};
	public static final TextureFilter DEFAULTFILTER = TextureFilter.NEAREST;
	public static Texture CURSOR_TEXTURE;
	
	public float mContourFactor = 0.02f;
	
	public float mFloorFriction = 0.98f;
	public TextureHolder mTextureHolder;
	public TextureHolder mContourTextureHolder;
	public boolean mConstraintsActivated;
	protected Default2DGraphics mGraphics2D;
	protected GraphicsTranslator mGraphics;
	public NonConcurrentList<Joint> mJoints;
	public NonConcurrentList<NonConcurrentList<Bone>> mLayers;
	public NonConcurrentList<Bone> mBones;
	protected float[] mSkeletonColor;
	protected float[] mContourColor;
	protected float[] mSuppData;
	public SkeletonCarrier mCarrier;
	public BasicProgram mShader;
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
	
	public DrawBatch mMesh;
	protected IndexedVertexBuffer mVertexBuffer;
	private boolean mUpdateColor;
	private boolean mUpdateTexCoords;
	protected int mVertexCount;	
	
	public LegacySkeleton() {
		mJoints = new NonConcurrentList<Joint>();
		mBones = new NonConcurrentList<Bone>();
		mLayers = new NonConcurrentList<NonConcurrentList<Bone>>();
		mConstraints = new NonConcurrentList<Constraint>();
		mTextureHolder = null;
		mContourTextureHolder = null;
		mSkeletonColor = new float[4];
		mConstraintsActivated = true;
		mContourColor = new float[]{0,0,0,0};
		mSuppData = new float[]{0,0,0,0};
		setModColor(1,1,1);
		mShader = null;
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
	
	public void init(SkeletonCarrier carrier) {
		mCarrier = carrier;
		//carrier.setSkeleton(this);
		mGraphics2D = carrier.getGraphics();
		if(mShader==null)
			mShader = mGraphics2D.mAdditiveModulateProgram;
		mGraphics = mGraphics2D.mTranslator;
		
		mDrawFill = true;
		
		build();
		
		mBoundariesRect = new Rect();
		refreshBoundariesRect();
		mInitialized = true;
	}
	
	public void setTexture(TextureHolder texture) {
		mTextureHolder = texture;
		mContourTextureHolder = texture;
	}
	
	public void setTexture(String filename,TextureProperties settings) {
		setTexture(new TextureHolder(filename,settings));
	}
	
	public void setTexture(String filename,TextureFilter filter) {
		setTexture(filename,new TextureProperties(filter));
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
	
	public void init(Default2DGraphics graphics2D) {
		init(new DefaultSkeletonCarrier(graphics2D));
	}
	
	public void addJoint(Joint bone) {
		mJoints.add(bone);
	}
	
	public void addConstraint(Constraint constraint) {
		mConstraints.add(constraint);
	}
	
	public void addBone(Bone bone,int layer,float constraintDistanceStrength) {
		while(layer>mLayers.size()-1)
		{
			mLayers.add(new NonConcurrentList<Bone>());
		}
		mLayers.get(layer).add(bone);
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
		return mCarrier.getWorldX() + (mSkeletonOffsetX + joint.mPosX*mCarrier.getLookDirection())*mCarrier.getScale();
	}
	
	public float getJointWorldY(Joint joint) {
		return mCarrier.getWorldY() + (mSkeletonOffsetY + joint.mPosY)*mCarrier.getScale();
	}
	
	public void loadTexture() {
		mContourTextureHolder.getTexture(mGraphics.mGFXLoader);
	}
	
	public void draw() {	

		if(mMesh==null) {
			//FIRST DRAW
			mVertexCount = 0;
			for(NonConcurrentList<Bone> layer:mLayers) {
				//Contour
				for(Bone bone:layer) {
					if(mDrawContour && bone.mCelShading)
						mVertexCount += 4;
					mVertexCount += 4;
				}
			}
			int indexCount = mBones.size()*6*2;
			mVertexBuffer = mGraphics2D.createVertexBuffer(true, false, indexCount, mVertexCount);
			mVertexBuffer.setIndexPosition(0);
			for(short i=0;i<mVertexCount;i+=4)
				mVertexBuffer.beginQuad(false,i);
			mVertexBuffer.mFinishedIndexCount = indexCount;
			mVertexBuffer.mFinishedVertexCount = mVertexCount;
			mVertexBuffer.reset();
			mMesh = new DrawBatch(mGraphics2D,mVertexBuffer);
			mUpdateColor = true;
			mUpdateTexCoords = true;
		}

		//--UPDATE COLOR--
		if(mUpdateColor) {
			
			mVertexBuffer.setDataPosition(DefaultGraphics.ID_COLORS,0);
			mVertexBuffer.setDataPosition(DefaultGraphics.ID_SUPPDATA, 0);
			for(NonConcurrentList<Bone> layer:mLayers) {
				//Contour
				if(mDrawContour)
					for(Bone bone:layer) {
						if(bone.mCelShading) {
							mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, DefaultGraphics.BLACK,4);
							mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mContourColor,4);
						}
					}
				//Fill
				for(Bone bone:layer) {
					mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, DefaultGraphics.WHITE,4);
					mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mSuppData,4);
				}
			}
			mUpdateColor = false;
		}
		
		//--UPDATE TEXTURE COORDINATES--
		if(mUpdateTexCoords) {
			mVertexBuffer.setDataPosition(DefaultGraphics.ID_TEXTURES, 0);
			for(NonConcurrentList<Bone> layer:mLayers) {
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
		float scale = mCarrier.getScale();
		int mirrorFac = mCarrier.getLookDirection();

		
		for(NonConcurrentList<Bone> layer:mLayers) {
			//Contour
			if(mDrawContour) {
				for(Bone bone:layer) {
					if(bone.mCelShading) {
						if(bone.mVisible) {
							float contourOrthoX = bone.mOrthNormX * mContourFactor;
							float contourOrthoY = bone.mOrthNormY * mContourFactor;
							float contourNormX = bone.mNormDirX * mContourFactor;
							float contourNormY = bone.mNormDirY * mContourFactor;
							
							mVertexBuffer.putVec2(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX4*scale + contourOrthoX*bone.mContourX3 + contourNormX*bone.mContourY4) * mirrorFac, worldPosY + bone.mVertY4*scale + contourOrthoY*bone.mContourX3 + contourNormY*bone.mContourY4);
							mVertexBuffer.putVec2(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX3*scale - contourOrthoX*bone.mContourX4 + contourNormX*bone.mContourY3) * mirrorFac, worldPosY + bone.mVertY3*scale - contourOrthoY*bone.mContourX4 + contourNormY*bone.mContourY3);
							mVertexBuffer.putVec2(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX2*scale + contourOrthoX*bone.mContourX1 - contourNormX*bone.mContourY1) * mirrorFac, worldPosY + bone.mVertY2*scale + contourOrthoY*bone.mContourX1 - contourNormY*bone.mContourY1);
							mVertexBuffer.putVec2(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX1*scale - contourOrthoX*bone.mContourX2 - contourNormX*bone.mContourY2) * mirrorFac, worldPosY + bone.mVertY1*scale - contourOrthoY*bone.mContourX2 - contourNormY*bone.mContourY2);
					
						}else{
							//mVertexBuffer.putDegeneratedQuad2D(DefaultGraphics.ID_POSITIONS);
							mVertexBuffer.putVec8(DefaultGraphics.ID_POSITIONS, 0,0,0,0,0,0,0,0);
						}
					}
				}
			}
			
			//Fill
			for(Bone bone:layer) {
				if(bone.mVisible) {
					mVertexBuffer.putVec2(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX4*scale * mirrorFac , worldPosY + bone.mVertY4*scale);
					mVertexBuffer.putVec2(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX3*scale * mirrorFac , worldPosY + bone.mVertY3*scale);
					mVertexBuffer.putVec2(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX2*scale * mirrorFac , worldPosY + bone.mVertY2*scale);
					mVertexBuffer.putVec2(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX1*scale * mirrorFac , worldPosY + bone.mVertY1*scale);
				}else{
					//mVertexBuffer.putDegeneratedQuad2D(DefaultGraphics.ID_POSITIONS);
					mVertexBuffer.putVec8(DefaultGraphics.ID_POSITIONS, 0,0,0,0,0,0,0,0);
				}
			}
		}
		
		mGraphics2D.setShaderProgram(mShader);
		mGraphics2D.bindTextureInHolder(mTextureHolder);
		
		mMesh.draw();
		
		if(false) {
			for(NonConcurrentList<Bone> layer:mLayers) {
	//		for(int i=0;i<mLayers.size();i++) {
	//			NonConcurrentList<Bone> layer = mLayers.get(i);
			
				mGraphics.bindTextureInHolder(mContourTextureHolder);
				if(mDrawContour) {
					for(Bone bone:layer) {
	//				for(int j=0;j<layer.size();j++) {
	//					Bone bone = layer.get(j);
						if(bone.mCelShading && bone.mVisible) {
							float contourOrthoX = bone.mOrthNormX * mContourFactor;
							float contourOrthoY = bone.mOrthNormY * mContourFactor;
							float contourNormX = bone.mNormDirX * mContourFactor;
							float contourNormY = bone.mNormDirY * mContourFactor;
							
							mGraphics2D.beginQuad(false);
							mGraphics2D.putPositionQuad(
									worldPosX + (bone.mVertX4*scale + contourOrthoX*bone.mContourX3 + contourNormX*bone.mContourY4) * mirrorFac, worldPosY + bone.mVertY4*scale + contourOrthoY*bone.mContourX3 + contourNormY*bone.mContourY4, 
									worldPosX + (bone.mVertX3*scale - contourOrthoX*bone.mContourX4 + contourNormX*bone.mContourY3) * mirrorFac, worldPosY + bone.mVertY3*scale - contourOrthoY*bone.mContourX4 + contourNormY*bone.mContourY3, 
									worldPosX + (bone.mVertX2*scale + contourOrthoX*bone.mContourX1 - contourNormX*bone.mContourY1) * mirrorFac, worldPosY + bone.mVertY2*scale + contourOrthoY*bone.mContourX1 - contourNormY*bone.mContourY1, 
									worldPosX + (bone.mVertX1*scale - contourOrthoX*bone.mContourX2 - contourNormX*bone.mContourY2) * mirrorFac, worldPosY + bone.mVertY1*scale - contourOrthoY*bone.mContourX2 - contourNormY*bone.mContourY2
								);
							mGraphics2D.putTextureArray(bone.getTextureCoordinates().mAppliedCoordinates);
							mGraphics2D.putColorRect(COLOR_BLACK);
							mGraphics2D.putSuppDataRect(mContourColor);
							
						}
					}
				}
	
				mGraphics.bindTextureInHolder(mTextureHolder);
				if(mDrawFill) {
					for(Bone bone:layer) {
	//				for(int j=0;j<layer.size();j++) {
	//					Bone bone = layer.get(j);
						if(bone.mVisible) {
							mGraphics2D.beginQuad(false);
							mGraphics2D.putPositionQuad(
									worldPosX + bone.mVertX4*scale * mirrorFac , worldPosY + bone.mVertY4*scale, 
									worldPosX + bone.mVertX3*scale * mirrorFac , worldPosY + bone.mVertY3*scale, 
									worldPosX + bone.mVertX2*scale * mirrorFac , worldPosY + bone.mVertY2*scale, 
									worldPosX + bone.mVertX1*scale * mirrorFac , worldPosY + bone.mVertY1*scale
								);
							mGraphics2D.putTextureArray(bone.getTextureCoordinates().mAppliedCoordinates);
							mGraphics2D.putColorRect(mSkeletonColor);
							mGraphics2D.putSuppDataRect(mSuppData);
							
						}
					}
				}
			}
		}
		
	}
	
	public void drawEditing(SkeletonEditing skeletonEditing) {
		Joint markedJoint;
		if(skeletonEditing==null)
			markedJoint = null;
		else
			markedJoint = skeletonEditing.mMarkedJoint;
		float worldPosX = mCarrier.getWorldX() + mSkeletonOffsetX;
		float worldPosY = mCarrier.getWorldY() + mSkeletonOffsetY;
		float scale = mCarrier.getScale();
		int mirrorFac = mCarrier.getLookDirection();

		mGraphics2D.setDefaultProgram();
		mGraphics.bindTexture(CURSOR_TEXTURE);
		
		for(Joint joint:mJoints) 
			if(joint.mEnabled){
				float alpha = (markedJoint==joint)?1:0.6f;
				if(joint.mFixed)
					mGraphics2D.setColor(1, 0, 0, alpha);
				else
					mGraphics2D.setColor(0.8f,0.8f,0.8f,alpha);
				mGraphics2D.drawRectCentered(worldPosX + joint.mPosX*scale * mirrorFac, worldPosY + joint.mPosY*scale, joint.getOutputRadius()*2);
			}
		
		mGraphics.bindTexture(null);
		mGraphics2D.setColor(0.8f, 0.1f, 0,0.8f);
		for(Joint joint:mJoints)
			if(joint.mEnabled && joint.mAngleParent!=null){
				mGraphics2D.drawLine(
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
//		if(mCurrentPose!=null)
//			mCurrentPose.applyPose(this);
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
	
}