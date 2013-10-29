package yang.graphics.skeletons;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.textures.TextureCoordBounds;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.physics.massaggregation.Skeleton2D;
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
	public DefaultGraphics<?> mGraphics;


	//GFX data
	public DrawBatch mMesh;
	protected IndexedVertexBuffer mVertexBuffer;
	protected float[] mSkeletonColor;
	protected float[] mContourColor;
	protected float[] mSuppData;
	public YangList<YangList<CartoonBone>> mLayersList;
	public CartoonBone[] mCartoonBones;
	public CartoonBone[][] mLayers;
	public CartoonBone[][] mFrontToBackLayers;

	//State
	protected boolean mUpdateColor;
	protected boolean mUpdateTexCoords;
	protected int mVertexCount;
	protected float[] mInterColor;

	public CartoonSkeleton2D() {
		super();
		mLayersList = new YangList<YangList<CartoonBone>>();
		mSkeletonColor = new float[4];
		mContourColor = new float[4];
		mSuppData = new float[4];
		mInterColor = new float[4];
		setFillColor(1,1,1);
		mDrawContour = true;
		mDrawFill = true;
		mInitialized = false;

	}

	public void init(DefaultGraphics<?> graphics,SkeletonCarrier carrier) {
		if(mInitialized)
			return;
		mCarrier = carrier;

		mGraphics = graphics;
		mTranslator = mGraphics.mTranslator;

		mCurJointId = 0;
		build();

		finishUpdate();
		refreshVisualData();

		mInitialized = true;
	}

	public void init(DefaultGraphics<?> graphics) {
		init(graphics,NEUTRAL_CARRIER);
	}

	public void drawEditing(SkeletonEditing skeletonEditing) {
		super.drawEditing(mGraphics,skeletonEditing);
	}

	public void texCoordsIntoRect(float rectLeft,float rectTop,float rectWidth,float rectHeight) {
		for(JointConnection bone:mBones) {
			if(bone instanceof CartoonBone)
				((CartoonBone)bone).texCoordsIntoRect(rectLeft,rectTop,rectWidth,rectHeight);
		}
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
		for(CartoonBone bone:mCartoonBones)
			bone.mVisible = visible;
	}

	public void refreshVisualData() {
		for(CartoonBone bone:mCartoonBones)
			bone.refreshVisualVars();
	}

	public <ConnectionType extends JointConnection> ConnectionType addSpringBone(ConnectionType bone,int layer,float constraintDistanceStrength) {
		super.addSpringBone(bone, constraintDistanceStrength);
		if(bone instanceof CartoonBone) {
			while(layer>mLayersList.size()-1)
				mLayersList.add(new YangList<CartoonBone>());
			mLayersList.get(layer).add((CartoonBone)bone);
		}
		return bone;
	}

	public <ConnectionType extends JointConnection> ConnectionType addBone(ConnectionType bone,int layer) {
		return addSpringBone(bone,layer,mDefaultBoneSpring);
	}

	@Override
	public <ConnectionType extends JointConnection> ConnectionType addSpringBone(ConnectionType bone) {
		return addSpringBone(bone,0,mDefaultBoneSpring);
	}

	public void draw() {

		if(mMesh==null) {
			//FIRST DRAW
			mVertexCount = 0;
			for(YangList<CartoonBone> layer:mLayersList) {
				//Contour
				for(CartoonBone bone:layer) {
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
			for(CartoonBone[] layer:mLayers) {
				//Contour
				if(mDrawContour)
					for(CartoonBone bone:layer) {
						if(bone.mCelShading) {
							mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, DefaultGraphics.BLACK,4);
							mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mContourColor,4);
						}
					}
				//Fill
				mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, DefaultGraphics.WHITE,4*layer.length);
				mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mSuppData,4*layer.length);
				//mInterColor[3] += zInc;
				//mInterColor[3] += 0.05f;
			}
			mUpdateColor = false;
		}

		//--UPDATE TEXTURE COORDINATES--
		if(mUpdateTexCoords) {
			mVertexBuffer.setDataPosition(DefaultGraphics.ID_TEXTURES, 0);
			for(CartoonBone[] layer:mLayers) {
				//Contour
				if(mDrawContour) {
					for(CartoonBone bone:layer) {
						if(bone.mCelShading) {
							mVertexBuffer.putArray(DefaultGraphics.ID_TEXTURES,bone.getTextureCoordinates().mAppliedCoordinates);
						}
					}
				}
				//Fill
				for(CartoonBone bone:layer) {
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

		for(CartoonBone[] layer:mLayers) {
			//Contour
			if(mDrawContour) {
				for(CartoonBone bone:layer) {
					if(bone.mCelShading) {
						if(bone.mVisible) {
							float contourOrthoX = bone.mNormDirY * mContourFactor;
							float contourOrthoY = -bone.mNormDirX * mContourFactor;
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
			for(CartoonBone bone:layer) {
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

		mMesh.draw();

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

	protected void finishUpdate() {
		int l = mLayersList.size();
		mLayers = new CartoonBone[l][];
		mFrontToBackLayers = new CartoonBone[l][];
		mCartoonBones = new CartoonBone[mBones.size()];
		int k=0;
		int i=0;
		for(YangList<CartoonBone> layer:mLayersList) {
			CartoonBone[] layerArray = new CartoonBone[layer.size()];
			int c=0;
			for(CartoonBone bone:layer) {
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

	public Texture getDefaultTexture(AbstractGFXLoader gfxLoader) {
		return null;
	}

}