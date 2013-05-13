package yang.graphics.skeletons;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.skeletons.defaults.HumanSkeleton;
import yang.graphics.skeletons.elements.Bone;
import yang.util.NonConcurrentList;

public abstract class ContourSkeleton extends HumanSkeleton{
	
	public ContourSkeleton() {
		super();
	}
	
	public void draw() {	

		mGraphics.switchZBuffer(true);
		
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

		System.arraycopy(mSuppData, 0, mInterColor, 0, 4);
		mInterColor[3] = 0.5f;
		final float zInc = 0.01f;
		
		//--UPDATE COLOR--
		if(mUpdateColor) {
			
			mVertexBuffer.setDataPosition(DefaultGraphics.ID_COLORS,0);
			mVertexBuffer.setDataPosition(DefaultGraphics.ID_SUPPDATA, 0);
			
			for(Bone[] layer:mFrontToBackLayers) {
				//Fill
				for(Bone bone:layer) {
					mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, new float[]{1,1,1,0.0f},4);
					mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mInterColor,4);
					//mInterColor[3] += zInc;
				}
				//mInterColor[3] += 0.04f;
				
				//Contour
				if(mDrawContour)
				for(Bone bone:layer) {
					if(bone.mCelShading) {
						mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, DefaultGraphics.BLACK,4);
						mVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, mInterColor,4);
					}
				}
				
				//mInterColor[3] -= 0.08f;
			}
			mUpdateColor = false;
		}
		
		//--UPDATE TEXTURE COORDINATES--
		if(mUpdateTexCoords) {
			mVertexBuffer.setDataPosition(DefaultGraphics.ID_TEXTURES, 0);
			for(Bone[] layer:mFrontToBackLayers) {
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

		
		for(Bone[] layer:mFrontToBackLayers) {
			
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
		}
		
		mGraphics2D.setShaderProgram(mShader);
		mGraphics2D.bindTextureInHolder(mTextureHolder);
		
		mMesh.draw();
		
		mGraphics.switchZBuffer(false);
	}
}