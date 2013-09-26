package yang.graphics.stereovision;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.programs.AbstractProgram;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.GraphicsTranslator;

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
		mStereoVertexBuffer.putRect3D(DefaultGraphics.ID_POSITIONS, -1,1, 0,-1, 0);
		mStereoVertexBuffer.putRect3D(DefaultGraphics.ID_POSITIONS, 0,1, 1,-1, 0);
		mStereoVertexBuffer.putArrayMultiple(DefaultGraphics.ID_TEXTURES, DefaultGraphics.RECT_TEXTURECOORDS,2);
//		mStereoVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, FloatColor.WHITE.mValues,2*4);
//		mStereoVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, FloatColor.WHITE.mValues,2*4);
		mStereoVertexBuffer.finishUpdate();
		
		mStereoLeftRenderTarget = graphics.createRenderTarget(resolution,resolution, new TextureProperties(TextureWrap.MIRROR,TextureFilter.LINEAR));
		mStereoRightRenderTarget = graphics.createRenderTarget(resolution,resolution, new TextureProperties(TextureWrap.MIRROR,TextureFilter.LINEAR));
	}
	
	public void draw() {
		mGraphics.switchZBuffer(false);
		
		//Save
		IndexedVertexBuffer buf = mGraphics.mCurrentVertexBuffer;
		AbstractProgram prevShader = mGraphics.mCurrentProgram;
		
		
		mGraphics.disableBuffers();
		mShader.activate();
		assert mGraphics.checkErrorInst("1");
		mGraphics.mCurrentVertexBuffer = mStereoVertexBuffer;
		mGraphics.enableAttributePointer(mShader.mPositionHandle);
		mGraphics.enableAttributePointer(mShader.mTexCoordsHandle);
		mGraphics.setAttributeBuffer(mShader.mPositionHandle, DefaultGraphics.ID_POSITIONS);
		mGraphics.setAttributeBuffer(mShader.mTexCoordsHandle, DefaultGraphics.ID_TEXTURES);
		mGraphics.bindTextureNoFlush(mStereoLeftRenderTarget.mTargetTexture, 0);
		mGraphics.drawBufferDirectly(mStereoVertexBuffer, 0,6, GraphicsTranslator.T_TRIANGLES);
		mGraphics.bindTextureNoFlush(mStereoRightRenderTarget.mTargetTexture, 0);
		mGraphics.drawBufferDirectly(mStereoVertexBuffer, 6,6, GraphicsTranslator.T_TRIANGLES);
		
		assert mGraphics.checkErrorInst("2");
		mGraphics.disableAttributePointer(mShader.mPositionHandle);
		mGraphics.disableAttributePointer(mShader.mTexCoordsHandle);
		
		//reset
		mGraphics.mCurrentVertexBuffer = buf;
		prevShader.activate();
		mGraphics.enableBuffers();
		mGraphics.bindBuffers();

		assert mGraphics.checkErrorInst("3");
	}
	
}
