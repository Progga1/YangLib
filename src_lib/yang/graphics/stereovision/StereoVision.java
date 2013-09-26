package yang.graphics.stereovision;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.AbstractProgram;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.glconsts.GLMasks;
import yang.model.ScreenInfo;

public class StereoVision {

	public int mResolution;
	private GraphicsTranslator mGraphics;
	private StereoShader mShader;
	public IndexedVertexBuffer mStereoVertexBuffer = null;
	public TextureRenderTarget mStereoLeftRenderTarget = null;
	public TextureRenderTarget mStereoRightRenderTarget = null;
	
	public void init(GraphicsTranslator graphics,int resolution) {
		mGraphics = graphics;
		mShader = mGraphics.addProgram(new StereoShader());
		mStereoVertexBuffer = DefaultGraphics.createVertexBuffer(graphics,true,true, 2*6, 2*4);
		mStereoVertexBuffer.putQuadIndicesMultiple(2);

		mStereoVertexBuffer.putRect3D(DefaultGraphics.ID_POSITIONS, -1,-1, 0,1, 0);
		mStereoVertexBuffer.putRect3D(DefaultGraphics.ID_POSITIONS, 0,-1, 1,1, 0);
		mStereoVertexBuffer.putArrayMultiple(DefaultGraphics.ID_TEXTURES, DefaultGraphics.RECT_TEXTURECOORDS_INV,2);
		mStereoVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, FloatColor.WHITE.mValues,2*4);
		mStereoVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, FloatColor.WHITE.mValues,2*4);
		mStereoVertexBuffer.finishUpdate();
		
		mStereoLeftRenderTarget = graphics.createRenderTarget(resolution,resolution, new TextureProperties(TextureWrap.MIRROR,TextureFilter.LINEAR));
		mStereoRightRenderTarget = graphics.createRenderTarget(resolution,resolution, new TextureProperties(TextureWrap.MIRROR,TextureFilter.LINEAR));
		
		surfaceChanged(graphics);
	}
	
	public void draw() {
		//Save state
		IndexedVertexBuffer buf = mGraphics.mCurrentVertexBuffer;
		AbstractProgram prevShader = mGraphics.mCurrentProgram;
		
		//Activate stereo state
		mGraphics.disableBuffers();
		mShader.activate();
		mStereoVertexBuffer.reset();
		mGraphics.setVertexBuffer(mStereoVertexBuffer);
		mGraphics.enableAttributePointer(mShader.mPositionHandle);
		mGraphics.enableAttributePointer(mShader.mTexCoordsHandle);
		mGraphics.setAttributeBuffer(mShader.mPositionHandle, DefaultGraphics.ID_POSITIONS);
		mGraphics.setAttributeBuffer(mShader.mTexCoordsHandle, DefaultGraphics.ID_TEXTURES);
		mGraphics.setAttributeBuffer(mShader.mPositionHandle, DefaultGraphics.ID_POSITIONS);
		mGraphics.setAttributeBuffer(mShader.mTexCoordsHandle, DefaultGraphics.ID_TEXTURES);
		
		//Draw
		mGraphics.clear(0.2f,0,0, GLMasks.DEPTH_BUFFER_BIT);
		mGraphics.bindTextureNoFlush(mStereoLeftRenderTarget.mTargetTexture, 0);
		mGraphics.drawBufferDirectly(mStereoVertexBuffer, 0,6, GraphicsTranslator.T_TRIANGLES);
		mGraphics.bindTextureNoFlush(mStereoRightRenderTarget.mTargetTexture, 0);
		mGraphics.drawBufferDirectly(mStereoVertexBuffer, 6,6, GraphicsTranslator.T_TRIANGLES);
		
		//reset
		mGraphics.disableAttributePointer(mShader.mPositionHandle);
		mGraphics.disableAttributePointer(mShader.mTexCoordsHandle);
		mGraphics.setVertexBuffer(buf);
		prevShader.activate();
		mGraphics.enableBuffers();

		assert mGraphics.checkErrorInst("3");
	}
	
	public void surfaceChanged(ScreenInfo screenInfo) {
		if(mStereoLeftRenderTarget==null)
			return;
		mStereoLeftRenderTarget.enforceRatio(screenInfo.getSurfaceRatioX(),screenInfo.getSurfaceRatioY());
		mStereoRightRenderTarget.enforceRatio(screenInfo.getSurfaceRatioX(),screenInfo.getSurfaceRatioY());
	}
	
}
