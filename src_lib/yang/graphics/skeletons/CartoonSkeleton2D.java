package yang.graphics.skeletons;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;
import yang.graphics.skeletons.animations.Animation;
import yang.graphics.textures.TextureCoordBounds;
import yang.graphics.textures.TextureHolder;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.physics.massaggregation.Skeleton2D;
import yang.physics.massaggregation.SkeletonEditing;
import yang.physics.massaggregation.elements.Joint;
import yang.physics.massaggregation.elements.JointConnection;
import yang.util.YangList;

public class CartoonSkeleton2D extends Skeleton2D {

	//Properties
	public float mContourFactor = 0.015f;
	public boolean mDrawContour;
	public boolean mDrawFill;

	//Persistent
	private boolean mInitialized;
	public GraphicsTranslator mTranslator;
	public DefaultGraphics<?> mG;
	protected int mVertexCount;
	protected int mIndexCount;

	//GFX data
	public DrawBatch mMesh;
	protected float[] mContourColor;
	protected float[] mSuppData;
	public YangList<YangList<CartoonBone>> mLayersList;
	public CartoonBone[] mCartoonBones;
	public CartoonBone[][] mLayers;
	public CartoonBone[][] mFrontToBackLayers;
	public TextureHolder mTextureHolder = null,mContourTextureHolder = null;
	public FloatColor mContourColorFactor = FloatColor.BLACK.clone();

	//State
	protected boolean mUpdateColor;
	protected boolean mUpdateTexCoords;
	protected float[] mInterColor;
	public float mShiftX,mShiftY,mShiftZ;

	public CartoonSkeleton2D() {
		super();
		mLayersList = new YangList<YangList<CartoonBone>>();
		mContourColor = new float[4];
		mSuppData = new float[4];
		mInterColor = new float[4];
//		setFillColor(1,1,1);
		mDrawContour = true;
		mDrawFill = true;
		mInitialized = false;

	}

	public void init(DefaultGraphics<?> graphics,SkeletonCarrier carrier) {
		if(mInitialized)
			return;
		mCarrier = carrier;

		mG = graphics;
		mTranslator = mG.mTranslator;

		mCurJointId = 0;
		build();

		finishUpdate();
		refreshVisualData();

		mInitialized = true;
	}

	public void init(DefaultGraphics<?> graphics) {
		init(graphics,NEUTRAL_CARRIER);
	}

	@Override
	public void drawDebug2D(DefaultGraphics<?> graphics,SkeletonEditing skeletonEditing) {
		drawDebug2D(this,graphics,skeletonEditing,mShiftX,mShiftY,mLookDirection);
	}

	public void drawEditing2D(SkeletonEditing skeletonEditing) {
		drawDebug2D(mG,skeletonEditing);
	}

	public void texCoordsIntoRect(float rectLeft,float rectTop,float rectWidth,float rectHeight) {
		for(final JointConnection bone:mBones) {
			if(bone instanceof CartoonBone)
				((CartoonBone)bone).texCoordsIntoRect(rectLeft,rectTop,rectWidth,rectHeight);
		}
		mUpdateTexCoords = true;
	}

	public void texCoordsIntoRect(TextureCoordBounds bounds) {
		if(bounds==null)
			return;
		texCoordsIntoRect(bounds.mValues[0],bounds.mValues[1],bounds.mValues[2],bounds.mValues[3]);
	}

	public boolean isInitialized() {
		return mInitialized;
	}

	public void setBonesVisible(boolean visible) {
		for(final CartoonBone bone:mCartoonBones)
			bone.mVisible = visible;
	}

	public void refreshVisualData() {
		for(final CartoonBone bone:mCartoonBones)
			bone.refreshVisualVars();
	}

	public JointConnection addSpringBone(JointConnection bone,int layer,float constraintDistanceStrength) {
		super.addSpringBone(bone, constraintDistanceStrength);
		if(bone instanceof CartoonBone) {
			while(layer>mLayersList.size()-1)
				mLayersList.add(new YangList<CartoonBone>());
			mLayersList.get(layer).add((CartoonBone)bone);
		}
		return bone;
	}

	public JointConnection addConnection(JointConnection bone,int layer) {
		return addSpringBone(bone,layer,0);
	}

	public JointConnection addSpringBone(JointConnection bone,int layer) {
		return addSpringBone(bone,layer,mDefaultBoneSpring);
	}

	@Override
	public JointConnection addSpringBone(JointConnection bone) {
		return addSpringBone(bone,0,mDefaultBoneSpring);
	}

	public void draw() {

		if(mMesh==null) {
			//FIRST DRAW
			mVertexCount = 0;
			for(final YangList<CartoonBone> layer:mLayersList) {
				//Contour
				for(final CartoonBone bone:layer) {
					if(mDrawContour && bone.mCelShading) {
						mVertexCount += bone.mVertexCount;
						mIndexCount += bone.mIndexCount;
					}
					mVertexCount += bone.mVertexCount;
					mIndexCount += bone.mIndexCount;
				}
			}
			mVertexBuffer = mG.createVertexBuffer(true, false, mIndexCount, mVertexCount);
			mVertexBuffer.setIndexPosition(0);
			short i = 0;
			for(final YangList<CartoonBone> layer:mLayersList) {
				//Contour
				for(final CartoonBone bone:layer) {
					bone.mVertexBuffer = mVertexBuffer;
					if(mDrawContour && bone.mCelShading) {
						bone.putIndices(i,true);
						i += bone.mVertexCount;
					}
				}

				//Fill
				for(final CartoonBone bone:layer) {
					bone.putIndices(i,false);
					i += bone.mVertexCount;
				}
			}
			mVertexBuffer.mFinishedIndexCount = mIndexCount;
			mVertexBuffer.mFinishedVertexCount = mVertexCount;
			mVertexBuffer.reset();
			mMesh = new DrawBatch(mG,mVertexBuffer);
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
			for(final CartoonBone[] layer:mLayers) {
				//Contour
				if(mDrawContour)
					for(final CartoonBone bone:layer) {
						if(bone.mCelShading) {
							mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, mContourColorFactor.mValues,bone.mVertexCount);
							mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mContourColor,bone.mVertexCount);
						}
					}
				//Fill
				for(final CartoonBone bone:layer) {
					mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, bone.mColor.mValues,bone.mVertexCount);
					mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mSuppData,bone.mVertexCount);
				}
			}
			mUpdateColor = false;
		}

		//--UPDATE TEXTURE COORDINATES--
		if(mUpdateTexCoords) {
			mVertexBuffer.setDataPosition(DefaultGraphics.ID_TEXTURES, 0);
			for(final CartoonBone[] layer:mLayers) {
				//Contour
				if(mDrawContour) {
					for(final CartoonBone bone:layer) {
						if(bone.mCelShading) {
							bone.putTextureCoordBuffer(true);
						}
					}
				}
				//Fill
				for(final CartoonBone bone:layer) {
					bone.putTextureCoordBuffer(false);
				}
			}
			mUpdateTexCoords = false;
		}

		//--UPDATE POSITIONS--
		mVertexBuffer.setDataPosition(DefaultGraphics.ID_POSITIONS, 0);
		final float worldPosX = mCarrier.getWorldX() + mShiftX;
		final float worldPosY = mCarrier.getWorldY() + mShiftY;
		final float scale = mCarrier.getScale()*mScale;
		final int mirrorFac = mLookDirection;

		for(final CartoonBone[] layer:mLayers) {
			//Contour
			if(mDrawContour) {
				for(final CartoonBone bone:layer) {
					if(bone.mCelShading) {
						if(bone.mVisible) {
							final float contourOrthoX = bone.mNormDirY * mContourFactor;
							final float contourOrthoY = -bone.mNormDirX * mContourFactor;
							final float contourNormX = bone.mNormDirX * mContourFactor;
							final float contourNormY = bone.mNormDirY * mContourFactor;
							mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX[0]*scale + contourOrthoX*bone.mContourX3 + contourNormX*bone.mContourY4) * mirrorFac, worldPosY + bone.mVertY[0]*scale + contourOrthoY*bone.mContourX3 + contourNormY*bone.mContourY4, mShiftZ);
							mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX[1]*scale - contourOrthoX*bone.mContourX4 + contourNormX*bone.mContourY3) * mirrorFac, worldPosY + bone.mVertY[1]*scale - contourOrthoY*bone.mContourX4 + contourNormY*bone.mContourY3, mShiftZ);
							mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX[2]*scale + contourOrthoX*bone.mContourX1 - contourNormX*bone.mContourY1) * mirrorFac, worldPosY + bone.mVertY[2]*scale + contourOrthoY*bone.mContourX1 - contourNormY*bone.mContourY1, mShiftZ);
							mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX[3]*scale - contourOrthoX*bone.mContourX2 - contourNormX*bone.mContourY2) * mirrorFac, worldPosY + bone.mVertY[3]*scale - contourOrthoY*bone.mContourX2 - contourNormY*bone.mContourY2, mShiftZ);
						}else{
							mVertexBuffer.putArray(DefaultGraphics.ID_POSITIONS,DefaultGraphics.FLOAT_ZERO_12);
						}
					}
				}
			}

			//Fill
			for(final CartoonBone bone:layer) {
				if(bone.mVisible) {
//					mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX4*scale * mirrorFac , worldPosY + bone.mVertY4*scale, mShiftZ);
//					mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX3*scale * mirrorFac , worldPosY + bone.mVertY3*scale, mShiftZ);
//					mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX2*scale * mirrorFac , worldPosY + bone.mVertY2*scale, mShiftZ);
//					mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX1*scale * mirrorFac , worldPosY + bone.mVertY1*scale, mShiftZ);
					int l = bone.mVertX.length;
					for(int i=0;i<l;i++) {
						mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX[i]*scale * mirrorFac , worldPosY + bone.mVertY[i]*scale, mShiftZ);
					}
				}else{
					mVertexBuffer.putArray(DefaultGraphics.ID_POSITIONS,DefaultGraphics.FLOAT_ZERO_12);
				}
			}
		}

		if(mTextureHolder!=null) {
			mG.bindTextureInHolder(mTextureHolder);
		}
//		mTranslator.bindTexture(null);
		mMesh.draw();

	}

	public Joint pickJoint2D(float x,float y) {
		float minDist = 1000000;
		Joint resJoint = null;
		for(final Joint joint:mJoints) {
			final float dX = (x-this.getJointWorldX(joint));
			final float dY = (y-this.getJointWorldY(joint));
			final float dist = dX*dX + dY*dY;
			if(dist < joint.getOutputRadius()*joint.getOutputRadius()*4.5f && dist<minDist) {
				minDist = dist;
				resJoint = joint;
			}
		}
		return resJoint;
	}

//	public void setFillColor(float r,float g,float b,float a) {
//		mSkeletonColor[0] = r;
//		mSkeletonColor[1] = g;
//		mSkeletonColor[2] = b;
//		mSkeletonColor[3] = a;
//		mUpdateColor = true;
//	}
//
//	public void setFillColor(float r,float g,float b) {
//		setFillColor(r,g,b,1);
//	}
//
//	public void setFillColor(float brightness) {
//		mSkeletonColor[0] = brightness;
//		mSkeletonColor[1] = brightness;
//		mSkeletonColor[2] = brightness;
//		mSkeletonColor[3] = 1;
//		mUpdateColor = true;
//	}

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

	protected void finishUpdate() {
		final int l = mLayersList.size();
		mLayers = new CartoonBone[l][];
		mFrontToBackLayers = new CartoonBone[l][];
		mCartoonBones = new CartoonBone[mBones.size()];
		int k=0;
		int i=0;
		for(final YangList<CartoonBone> layer:mLayersList) {
			final CartoonBone[] layerArray = new CartoonBone[layer.size()];
			int c=0;
			for(final CartoonBone bone:layer) {
				layerArray[c] = bone;
				mCartoonBones[i] = bone;
				c++;
				i++;
			}
			mLayers[k] = layerArray;
			mFrontToBackLayers[l-1-k] = layerArray;
			k++;
		}
	}

	public void updateTexCoords() {
		mUpdateTexCoords = true;
	}

	public void updateColor() {
		mUpdateColor = true;
	}

	public Texture getDefaultTexture(AbstractGFXLoader gfxLoader) {
		return null;
	}

	public void setOffset(float x, float y) {
		mShiftX = x;
		mShiftY = y;
	}

	@Override
	public float getJointWorldX(Joint joint) {
		return mCarrier.getWorldX() + (mShiftX + joint.mX*mLookDirection)*mCarrier.getScale()*mScale;
	}

	@Override
	public float getJointWorldY(Joint joint) {
		return mCarrier.getWorldY() + (mShiftY + joint.mY)*mCarrier.getScale()*mScale;
	}

	@Override
	public float getJointWorldZ(Joint joint) {
		return mCarrier.getWorldZ() + (mShiftZ + joint.mZ)*mCarrier.getScale()*mScale;
	}

	public void loadTexture() {
		mTextureHolder.getTexture(mTranslator.mGFXLoader);
	}

	public void setJointAnimationsEnabled(Animation<?> animation) {
		int c = 0;
		for(Joint joint:mJoints) {
			if(joint.mAnimate) {
				joint.mAnimDisabled = !animation.isJointAnimated(c);
				c++;
			}
		}
	}

	public void scaleBone(CartoonBone bone,Joint peakJoint,float scaleFactorX,float scaleFactorY) {
		bone.scale(scaleFactorX,scaleFactorY);
		peakJoint.scaleDistance(bone.mJoint1==peakJoint?bone.mJoint2:bone.mJoint1,scaleFactorY);
	}

	public void scaleBone(CartoonBone bone,Joint peakJoint,float scaleFactor) {
		scaleBone(bone,peakJoint,scaleFactor,scaleFactor);
	}

	public void setFillColor(float r,float g,float b,float a) {
		for(CartoonBone bone:mCartoonBones) {
			bone.mColor.set(r,g,b,a);
		}
	}

}