package yang.graphics.skeletons;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.skeletons.elements.Bone;
import yang.graphics.skeletons.elements.Joint;
import yang.graphics.textures.TextureHolder;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.physics.massaggregation.MassAggregation;
import yang.util.NonConcurrentList;

public class Skeleton2D extends MassAggregation {

	//Properties
	public float mContourFactor = 0.015f;
	public boolean mDrawContour;
	public boolean mDrawFill;
	
	//Persistent
	public static Texture CURSOR_TEXTURE;
	public TextureHolder mTextureHolder;
	public TextureHolder mContourTextureHolder;
	
	//GFX data
	public DrawBatch mMesh;
	protected IndexedVertexBuffer mVertexBuffer;
	protected float[] mSkeletonColor;
	protected float[] mContourColor;
	protected float[] mSuppData;
	public Bone[][] mLayers;
	public Bone[][] mFrontToBackLayers;
	
	//State
	protected boolean mUpdateColor;
	protected boolean mUpdateTexCoords;
	protected int mVertexCount;
	protected float[] mInterColor;
	public float mRotation;
	public float mRotAnchorX;
	public float mRotAnchorY;
	public int mLookDirection;
	
	public Skeleton2D() {
		super();
		m3D = false;
		mTextureHolder = null;
		mContourTextureHolder = null;
		mRotation = 0;
		mSkeletonColor = new float[4];
		mContourColor = new float[4];
		mSuppData = new float[4];
		mInterColor = new float[4];
		setFillColor(1,1,1);
		mDrawContour = true;
		mDrawFill = true;
		mLookDirection = 1;
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
		float worldPosX = mCarrier.getWorldX() + mShiftX;
		float worldPosY = mCarrier.getWorldY() + mShiftY;
		float scale = mCarrier.getScale()*mScale;
		int mirrorFac = mLookDirection;
		
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
							mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX4*scale + contourOrthoX*bone.mContourX3 + contourNormX*bone.mContourY4) * mirrorFac, worldPosY + bone.mVertY4*scale + contourOrthoY*bone.mContourX3 + contourNormY*bone.mContourY4, mShiftZ);
							mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX3*scale - contourOrthoX*bone.mContourX4 + contourNormX*bone.mContourY3) * mirrorFac, worldPosY + bone.mVertY3*scale - contourOrthoY*bone.mContourX4 + contourNormY*bone.mContourY3, mShiftZ);
							mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX2*scale + contourOrthoX*bone.mContourX1 - contourNormX*bone.mContourY1) * mirrorFac, worldPosY + bone.mVertY2*scale + contourOrthoY*bone.mContourX1 - contourNormY*bone.mContourY1, mShiftZ);
							mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + (bone.mVertX1*scale - contourOrthoX*bone.mContourX2 - contourNormX*bone.mContourY2) * mirrorFac, worldPosY + bone.mVertY1*scale - contourOrthoY*bone.mContourX2 - contourNormY*bone.mContourY2, mShiftZ);
						}else{
							mVertexBuffer.putArray(DefaultGraphics.ID_POSITIONS,DefaultGraphics.FLOAT_ZERO_12);
						}
					}
				}
			}
			
			//Fill
			for(Bone bone:layer) {
				if(bone.mVisible) {
					mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX4*scale * mirrorFac , worldPosY + bone.mVertY4*scale, mShiftZ);
					mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX3*scale * mirrorFac , worldPosY + bone.mVertY3*scale, mShiftZ);
					mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX2*scale * mirrorFac , worldPosY + bone.mVertY2*scale, mShiftZ);
					mVertexBuffer.putVec3(DefaultGraphics.ID_POSITIONS,worldPosX + bone.mVertX1*scale * mirrorFac , worldPosY + bone.mVertY1*scale, mShiftZ);
				}else{
					mVertexBuffer.putArray(DefaultGraphics.ID_POSITIONS,DefaultGraphics.FLOAT_ZERO_12);
				}
			}
		}

		mGraphics.bindTextureInHolder(mTextureHolder);
		
		mMesh.draw();
		
	}
	
	public void drawEditing(SkeletonEditing skeletonEditing) {
		drawEditing(this,skeletonEditing,mGraphics,mShiftX,mShiftY,mLookDirection);
	}
	
	public static void drawEditing(MassAggregation massAggregation,SkeletonEditing skeletonEditing,DefaultGraphics<?> graphics,float offsetX,float offsetY,int lookDirection) {
		Joint markedJoint;
		if(skeletonEditing==null)
			markedJoint = null;
		else
			markedJoint = skeletonEditing.mMainMarkedJoint;
		float worldPosX = massAggregation.mCarrier.getWorldX() + offsetX;
		float worldPosY = massAggregation.mCarrier.getWorldY() + offsetY;
		float scale = massAggregation.mCarrier.getScale()*massAggregation.mScale;
		int mirrorFac = lookDirection;

		GraphicsTranslator translator = graphics.mTranslator;
		massAggregation.mGraphics.setDefaultProgram();
		translator.bindTexture(CURSOR_TEXTURE);
		
		for(Joint joint:massAggregation.mJoints) 
			if(joint.mEnabled){
				float alpha = (markedJoint==joint)?1:0.6f;
				if(joint.mFixed)
					graphics.setColor(1, 0, 0, alpha);
				else
					graphics.setColor(0.8f,0.8f,0.8f,alpha);
				graphics.drawRectCentered(worldPosX + joint.mPosX*scale * mirrorFac, worldPosY + joint.mPosY*scale, joint.getOutputRadius()*2);
			}
		
		translator.bindTexture(null);
		graphics.setColor(0.8f, 0.1f, 0,0.8f);
		for(Joint joint:massAggregation.mJoints)
			if(joint.mEnabled && joint.mAngleParent!=null){
				graphics.drawLine(
						worldPosX + joint.mPosX*scale * mirrorFac, worldPosY + joint.mPosY*scale, 
						worldPosX + joint.mAngleParent.mPosX*scale * mirrorFac, worldPosY + joint.mAngleParent.mPosY*scale,
						0.015f
						);
		}
		
	}
	
	public Joint pickJoint2D(float x,float y) {
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
	
	public void setFillColor(float r,float g,float b,float a) {
		mSkeletonColor[0] = r;
		mSkeletonColor[1] = g;
		mSkeletonColor[2] = b;
		mSkeletonColor[3] = a;
		mUpdateColor = true;
	}
	
	public void setFillColor(float r,float g,float b) {
		setFillColor(r,g,b,1);
	}
	
	public void setFillColor(float brightness) {
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
	
	public void setTexture(TextureHolder texture) {
		mTextureHolder = texture;
		mContourTextureHolder = texture;
	}
	
	public void setTexture(String filename,TextureFilter filter) {
		setTexture(filename,new TextureProperties(TextureWrap.CLAMP,filter));
	}
	
	public void setTexture(String filename,TextureProperties settings) {
		setTexture(new TextureHolder(filename,settings));
	}
	
	public void loadTexture() {
		mTextureHolder.getTexture(mTranslator.mGFXLoader);
		mContourTextureHolder.getTexture(mTranslator.mGFXLoader);
	}
	
	public void updatedTextureCoords() {
		this.mUpdateTexCoords = true;
	}
	
	public void setRotationAnchor(float anchorX,float anchorY) {
		mRotAnchorX = anchorX;
		mRotAnchorY = anchorY;
	}
	
	public void resetRotationAnchor() {
		mRotAnchorX = 0;
		mRotAnchorY = 0;
	}
	
	@Override
	public float getJointWorldX(Joint joint) {
		return mCarrier.getWorldX() + (mShiftX + joint.mPosX*mLookDirection)*mCarrier.getScale()*mScale;
	}
	
	@Override
	protected void finish() {
		super.finish();
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
}