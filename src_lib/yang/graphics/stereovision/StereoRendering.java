package yang.graphics.stereovision;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.programs.MinimumTexShader;
import yang.graphics.programs.AbstractProgram;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.glconsts.GLDrawModes;
import yang.graphics.translator.glconsts.GLMasks;
import yang.model.SurfaceParameters;

public class StereoRendering extends StereoVision {

	public static float RATIO_FAC = 1.6f;
	public static TextureWrap WRAP_MODE = TextureWrap.CLAMP;
	public static boolean LENS_DISTORTION = true;
	public static boolean LENS_ABERRATION = false;

	public int mResolution;
	private GraphicsTranslator mGraphics;
	private MinimumTexShader mMinimumShader;
	public LensDistortionShader mLensDistortionShader;
	public LensAberrationShader mLensAberrationShader;
	public LensDistortionShader mUsedDistortionShader;
	public IndexedVertexBuffer mStereoVertexBuffer = null;
	public TextureRenderTarget mStereoLeftRenderTarget = null;
	public TextureRenderTarget mStereoRightRenderTarget = null;
	public boolean mDuplicateLeft = false;
	public float mLensShift = 0.118f;

	public void setLensParameters(float x,float y,float z,float w) {
		mLensDistortionShader.mLensParameters[0] = x;
		mLensDistortionShader.mLensParameters[1] = y;
		mLensDistortionShader.mLensParameters[2] = z;
		mLensDistortionShader.mLensParameters[3] = w;
	}

	public void init(GraphicsTranslator graphics,int resolution) {
		mGraphics = graphics;
		mMinimumShader = mGraphics.addProgram(new MinimumTexShader());
		mLensDistortionShader = mGraphics.addProgram(new LensDistortionShader());
		mLensAberrationShader = mGraphics.addProgram(new LensAberrationShader());
		mStereoVertexBuffer = mGraphics.createUninitializedVertexBuffer(true, true, 2*6, 2*4);
		mStereoVertexBuffer.init(new int[]{3,2}, null);
		mStereoVertexBuffer.putQuadIndicesMultiple(2);

		mStereoVertexBuffer.putRect3D(DefaultGraphics.ID_POSITIONS, -1,-1, 0,1, 0);
		mStereoVertexBuffer.putRect3D(DefaultGraphics.ID_POSITIONS, 0,-1, 1,1, 0);
		mStereoVertexBuffer.putArrayMultiple(DefaultGraphics.ID_TEXTURES, DefaultGraphics.RECT_TEXTURECOORDS_FLIP_X,2);
//		mStereoVertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, FloatColor.WHITE.mValues,2*4);
//		mStereoVertexBuffer.putArrayMultiple(DefaultGraphics.ID_SUPPDATA, FloatColor.WHITE.mValues,2*4);
		mStereoVertexBuffer.finishUpdate();

		mStereoLeftRenderTarget = graphics.createRenderTarget(resolution,resolution, new TextureProperties(WRAP_MODE,TextureFilter.LINEAR),false);
		mStereoRightRenderTarget = graphics.createRenderTarget(resolution,resolution, new TextureProperties(WRAP_MODE,TextureFilter.LINEAR),false);

		mStereoLeftRenderTarget.mPostCameraTransform = mLeftResultTransform;
		mStereoRightRenderTarget.mPostCameraTransform = mRightResultTransform;
		surfaceChanged(graphics);
	}

	public void draw() {
		//Save state
		final IndexedVertexBuffer buf = mGraphics.mCurrentVertexBuffer;
		final AbstractProgram prevShader = mGraphics.mCurrentProgram;

		mUsedDistortionShader = LENS_ABERRATION?mLensAberrationShader:mLensDistortionShader;
		MinimumTexShader stereoShader;
		if(LENS_DISTORTION) {
			stereoShader = mUsedDistortionShader;
		}else
			stereoShader = mMinimumShader;
		//Activate stereo state
		mGraphics.disableBuffers();
		stereoShader.activate();
		mStereoVertexBuffer.reset();
		mGraphics.setVertexBuffer(mStereoVertexBuffer);
		mGraphics.enableAttributePointer(stereoShader.mPositionHandle);
		mGraphics.enableAttributePointer(stereoShader.mTexCoordsHandle);
		mGraphics.setAttributeBuffer(stereoShader.mPositionHandle, DefaultGraphics.ID_POSITIONS);
		mGraphics.setAttributeBuffer(stereoShader.mTexCoordsHandle, DefaultGraphics.ID_TEXTURES);
//		mGraphics.setAttributeBuffer(stereoShader.mPositionHandle, DefaultGraphics.ID_POSITIONS);
//		mGraphics.setAttributeBuffer(stereoShader.mTexCoordsHandle, DefaultGraphics.ID_TEXTURES);

		//Draw
		mGraphics.clear(0,0,0, GLMasks.DEPTH_BUFFER_BIT);
		mGraphics.bindTextureNoFlush(mStereoLeftRenderTarget.mTargetTexture, 0);
		if(LENS_DISTORTION)
			mUsedDistortionShader.setShiftX(-mLensShift);
		mGraphics.drawBufferDirectly(mStereoVertexBuffer, 0,6, GLDrawModes.TRIANGLES);
		if(LENS_DISTORTION)
			mUsedDistortionShader.setShiftX(mLensShift);
		if(!mDuplicateLeft)
			mGraphics.bindTextureNoFlush(mStereoRightRenderTarget.mTargetTexture, 0);
		mGraphics.drawBufferDirectly(mStereoVertexBuffer, 6,6, GLDrawModes.TRIANGLES);

		//reset
		mGraphics.disableAttributePointer(stereoShader.mPositionHandle);
		mGraphics.disableAttributePointer(stereoShader.mTexCoordsHandle);
		mGraphics.setVertexBuffer(buf);
		prevShader.activate();
		mGraphics.enableBuffers();

		assert mGraphics.checkErrorInst("3");
	}

	public void surfaceChanged(SurfaceParameters screenInfo) {
		if(mStereoLeftRenderTarget==null)
			return;
		mStereoLeftRenderTarget.fakeDimensions(screenInfo.getSurfaceWidth()/2,screenInfo.getSurfaceHeight(),screenInfo.getSurfaceRatioX()/2*RATIO_FAC,1);
		mStereoRightRenderTarget.fakeDimensions(screenInfo.getSurfaceWidth()/2,screenInfo.getSurfaceHeight(),screenInfo.getSurfaceRatioX()/2*RATIO_FAC,1);
	}

}
