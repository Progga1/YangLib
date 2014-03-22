package yang.graphics.translator;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.listeners.DrawListener;
import yang.graphics.listeners.SurfaceListener;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.AbstractProgram;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.textures.TextureHolder;
import yang.math.MatrixOps;
import yang.math.objects.Quadruple;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;
import yang.model.DebugYang;



public abstract class AbstractGraphics<ShaderType extends AbstractProgram> implements DrawListener,SurfaceListener {

	//Constants
	public final static float PI = 3.1415926535f;
	public static float METERS_PER_UNIT = 1;
	public static int MAX_DYNAMIC_VERTICES = 200000;
	public static int WORLD_TRANSFORM_STACK_CAPACITY = 128;

	//Matrices
	public YangMatrix mInterTransf1;
	public YangMatrix mInterTransf2;
	protected YangMatrix mInterTexTransf;
	protected YangMatrix mIdentity;
	public TextureCoordinatesQuad mTexIdentity;
	//Camera
	public YangMatrix mWorldTransform;
	public YangMatrix mViewProjectionTransform;
	public YangMatrix mCameraProjectionMatrix;
	protected YangMatrix mResultTransformationMatrix;
	public YangMatrix mCurViewProjTransform;
	public float[] mNormalTransform;
	protected YangMatrix mInvViewProjectionTransform;
	protected YangMatrix mStereoScreenTransform;

	//State
	public boolean mBatchRecording;
	protected boolean mWorldTransformEnabled;
	public ShaderType mCurrentProgram;
	protected float mBold;
	public float[] mCurColor;
	public float[] mCurSuppData;
	public float mStereoScreenDistance = 0.15f;

	//Persistent attributes
	public GraphicsTranslator mTranslator;
	public boolean mAutoUpdateAmbientColor = true;
	private int[] mBufferElementSizes;
	private float[][] mNeutralBufferElements;

	//Counters
	protected float mTime;

	//Buffers
	public IndexedVertexBuffer mDynamicVertexBuffer;
	public IndexedVertexBuffer mCurrentVertexBuffer;	//Always the same reference as mTranslator.mCurrentVertexBuffer but generic type

	//Abstract methods
	public abstract ShaderType getDefaultProgram();
	protected abstract int[] getBufferElementSizes();
	protected abstract float[][] getNeutralBufferElements();

	//Optional methods
	protected void derivedInit() { }

	public AbstractGraphics(GraphicsTranslator translator) {
		if (DebugYang.showStart) DebugYang.showStackTrace("4", 1);
		mTranslator = translator;
		mTranslator.addScreenListener(this);
		mDynamicVertexBuffer = null;

	}

	/**
	 * Use only, if dynamic buffer should be initialized before calling init()
	 */
	public void initDynamicBuffer() {
		if(mDynamicVertexBuffer==null)
			mDynamicVertexBuffer = mTranslator.createUninitializedVertexBuffer(true,true,MAX_DYNAMIC_VERTICES*6/4,MAX_DYNAMIC_VERTICES);
	}

	public void init() {

		mTranslator.preCheck("Init");
		if (DebugYang.showStart) DebugYang.showStackTrace("5", 1);

		mWorldTransformEnabled = false;
		mViewProjectionTransform = new YangMatrix();
		mInterTransf1 = new YangMatrix();
		mInterTransf2 = new YangMatrix();
		mWorldTransform = new YangMatrix(WORLD_TRANSFORM_STACK_CAPACITY);
		mWorldTransform.loadIdentity();
		mStereoScreenTransform = new YangMatrix();
		mCameraProjectionMatrix = new YangMatrix();
		mResultTransformationMatrix = new YangMatrix();
		mNormalTransform = new float[9];
		mInterTexTransf = new YangMatrix();
		mIdentity = new YangMatrix();
		mIdentity.loadIdentity();
		mTexIdentity = new TextureCoordinatesQuad();
		mTexIdentity.init(0, 0, 1);
		mTranslator.checkErrorInst("Matrices");
		mInvViewProjectionTransform = new YangMatrix();
		mInvViewProjectionTransform.loadIdentity();
		mCurViewProjTransform = mViewProjectionTransform;
		mCurViewProjTransform.loadIdentity();
		mCurColor = new float[4];
		mCurSuppData = new float[4];
		mTime = 0;
		mBold = 0;
		mBufferElementSizes = getBufferElementSizes();
		mNeutralBufferElements = getNeutralBufferElements();
		//mDynamicSpriteVertexBuffer = createVertexBuffer(true,true,MAX_DYNAMIC_VERTICES*6/4,MAX_DYNAMIC_VERTICES);
		initDynamicBuffer();
		mDynamicVertexBuffer.init(mBufferElementSizes, mNeutralBufferElements);
		mBatchRecording = false;

		derivedInit();
		restart();
	}

	public IndexedVertexBuffer createVertexBuffer(boolean dynamicVertices, boolean dynamicIndices, int maxIndices,int maxVertices) {
		final IndexedVertexBuffer vertexBuffer = mTranslator.createUninitializedVertexBuffer(dynamicVertices,dynamicIndices,maxIndices,maxVertices);
		vertexBuffer.init(mBufferElementSizes,mNeutralBufferElements);
		return vertexBuffer;
	}

	public void restart() {
		setColor(1,1,1);
		setSuppData(0,0,0);
	}

	@Override
	public void onRestartGraphics() {
		if(mCurrentProgram!=null) {
			final ShaderType program = mCurrentProgram;
			mCurrentProgram = null;
			setShaderProgram(program);
		}

	}

	@Override
	public final void activate() {
		assert mTranslator.preCheck("activate");
		if(mTranslator.mCurDrawListener==this)
			return;
		mTranslator.flush();
		mTranslator.setDrawListener(this);
		if(mCurrentProgram!=null) {
			disableBuffers();
			mCurrentProgram = null;
		}
		setVertexBuffer(mDynamicVertexBuffer);
		setDefaultProgram();
		mTranslator.rebindTexture(0);
		assert mTranslator.checkErrorInst("Activate");
	}

	public void bindTextureInHolder(TextureHolder textureHolder) {
		mTranslator.bindTextureInHolder(textureHolder);
	}

	public boolean refreshNormalTransform() {
		if(mWorldTransformEnabled) {
			if(!mWorldTransform.asNormalTransform3f(mNormalTransform))
				return false;
		}else{
			MatrixOps.identity3f(mNormalTransform);
		}
		return true;
	}

	public boolean setShaderProgram(ShaderType program) {
		if(program!=mCurrentProgram) {
			if(mCurrentProgram!=null) {
				flush();
				disableBuffers();
			}
			mCurrentProgram = program;
			mTranslator.mShaderSwitchCount++;
			program.activate();
			enableBuffers();

			return true;
		}else
			return false;
	}

	public void setDefaultProgram() {
		setShaderProgram(getDefaultProgram());
	}

	public void setVertexBuffer(IndexedVertexBuffer vertexBuffer) {
		assert mTranslator.preCheck("Set vertex buffer");
		flush();
		mCurrentVertexBuffer = vertexBuffer;
		mTranslator.setVertexBuffer(vertexBuffer);
		assert mTranslator.checkErrorInst("Set vertex buffer");
	}

	public void resetVertexBuffer() {
		setVertexBuffer(mDynamicVertexBuffer);
	}

	public void startBatchRecording(IndexedVertexBuffer vertexBuffer) {
		mBatchRecording = true;
		mTranslator.mFlushDisabled = true;
		setVertexBuffer(vertexBuffer);
	}

	public void startBatchRecording(DrawBatch batch) {
		startBatchRecording(batch.mVertexBuffer);
	}

	public void startBatchRecording(int maxIndices,int maxVertices,boolean dynamicIndices,boolean dynamicVertices) {
		startBatchRecording(createVertexBuffer(dynamicVertices,dynamicIndices,maxIndices,maxVertices));
	}

	public void startBatchRecording(int maxIndices, int maxVertices) {
		startBatchRecording(maxIndices,maxVertices,false,false);
	}

	public void startBatchRecording(int maxIndices) {
		startBatchRecording(maxIndices,maxIndices);
	}

	public DrawBatch finishBatchRecording() {
		mTranslator.mCurrentVertexBuffer.finishUpdate();
		final DrawBatch result = new DrawBatch(this,mCurrentVertexBuffer);
		resetVertexBuffer();
		mBatchRecording = false;
		mTranslator.mFlushDisabled = false;
		return result;
	}

	public void switchGameCoordinates(boolean enable) {
		if((enable && mCurViewProjTransform == mViewProjectionTransform) || (!enable && mCurViewProjTransform == mTranslator.mProjScreenTransform))
			return;
		flush();
		if(enable) {
			mCurViewProjTransform = mViewProjectionTransform;
		}else{
			mCurViewProjTransform = mTranslator.mProjScreenTransform;
		}
	}

	//---Color---
	public void setColor(float r, float g, float b) {
		mCurColor[0] = r;
		mCurColor[1] = g;
		mCurColor[2] = b;
		mCurColor[3] = 1;
	}

	public void setColor(float r, float g, float b, float a) {
		mCurColor[0] = r;
		mCurColor[1] = g;
		mCurColor[2] = b;
		mCurColor[3] = a;
	}

	public void setColor(float brightness) {
		mCurColor[0] = brightness;
		mCurColor[1] = brightness;
		mCurColor[2] = brightness;
		mCurColor[3] = 1;
	}

	public void setColor(float[] color) {
		mCurColor[0] = color[0];
		mCurColor[1] = color[1];
		mCurColor[2] = color[2];
		mCurColor[3] = color[3];
	}

	public float[] getCurrentColor(){
		return mCurColor;
	}

	public void setColor(Vector3f color) {
		mCurColor[0] = color.mX;
		mCurColor[1] = color.mY;
		mCurColor[2] = color.mZ;
		mCurColor[3] = 1;
	}

	public void setColor(Vector3f color, float a) {
		mCurColor[0] = color.mX;
		mCurColor[1] = color.mY;
		mCurColor[2] = color.mZ;
		mCurColor[3] = a;
	}

	public void setColor(FloatColor color) {
		mCurColor[0] = color.mValues[0];
		mCurColor[1] = color.mValues[1];
		mCurColor[2] = color.mValues[2];
		mCurColor[3] = color.mValues[3];
	}


	public void setColor(FloatColor color, float alphaFactor) {
		mCurColor[0] = color.mValues[0];
		mCurColor[1] = color.mValues[1];
		mCurColor[2] = color.mValues[2];
		mCurColor[3] = color.mValues[3]*alphaFactor;
	}

	public void setColor(Quadruple color) {
		mCurColor[0] = color.mValues[0];
		mCurColor[1] = color.mValues[1];
		mCurColor[2] = color.mValues[2];
		mCurColor[3] = color.mValues[3];
	}

	public void multColor(float brightness) {
		if(brightness<1) {
			mCurColor[0] *= brightness;
			mCurColor[1] *= brightness;
			mCurColor[2] *= brightness;
		}else if(brightness>1) {
			float b = brightness-1;
			mCurColor[0] += (1-mCurColor[0])*b;
			mCurColor[1] += (1-mCurColor[1])*b;
			mCurColor[2] += (1-mCurColor[2])*b;
		}
	}

	public void addColor(float r,float g,float b,float a) {
		mCurColor[0] += r;
		mCurColor[1] += g;
		mCurColor[2] += b;
		mCurColor[3] += a;
	}

	public void addColor(FloatColor color) {
		mCurColor[0] += color.mValues[0];
		mCurColor[1] += color.mValues[1];
		mCurColor[2] += color.mValues[2];
		mCurColor[3] += color.mValues[3];
	}


	/**
	 * weight=0 => color1
	 * weight=1 => color2
	 */
	public void setColorWeighted(FloatColor color1,FloatColor color2,float weight) {
		final float dWeight = 1-weight;
		mCurColor[0] = color1.mValues[0]*dWeight + color2.mValues[0]*weight;
		mCurColor[1] = color1.mValues[1]*dWeight + color2.mValues[1]*weight;
		mCurColor[2] = color1.mValues[2]*dWeight + color2.mValues[2]*weight;
		mCurColor[3] = color1.mValues[3]*dWeight + color2.mValues[3]*weight;
	}

	//---Add-Color---
	public void setSuppData(float r, float g, float b) {
		mCurSuppData[0] = r;
		mCurSuppData[1] = g;
		mCurSuppData[2] = b;
		mCurSuppData[3] = 0;
	}

	public void setSuppData(float r, float g, float b, float a) {
		mCurSuppData[0] = r;
		mCurSuppData[1] = g;
		mCurSuppData[2] = b;
		mCurSuppData[3] = a;
	}

	public void setSuppData(float[] data) {
		mCurSuppData[0] = data[0];
		mCurSuppData[1] = data[1];
		mCurSuppData[2] = data[2];
		mCurSuppData[3] = data[3];
	}

	public void setSuppData(Quadruple data) {
		mCurSuppData[0] = data.mValues[0];
		mCurSuppData[1] = data.mValues[1];
		mCurSuppData[2] = data.mValues[2];
		mCurSuppData[3] = data.mValues[3];
	}

	public float[] getCurrentSuppData(){
		return mCurSuppData;
	}


	//---Screen-data---
	public float getScreenRight() {
		return mTranslator.mCurrentSurface.getSurfaceRatioX();
	}

	public float getScreenBottom() {
		return -mTranslator.mCurrentSurface.getSurfaceRatioY();
	}

	public float getScreenLeft() {
		return -mTranslator.mCurrentSurface.getSurfaceRatioX();
	}

	public float getScreenTop() {
		return mTranslator.mCurrentSurface.getSurfaceRatioY();
	}

	public float getScreenCenterX() {
		return 0;
	}

	public float getScreenCenterY() {
		return 0;
	}

	public float getTime() {
		return mTime;
	}

	public void setWhite() {
		setColor(1,1,1,1);
	}

	public void setBlack() {
		setColor(0,0,0,1);
	}

	public void setAddBlack() {
		setSuppData(0,0,0,0);
	}

	public void setGlobalTransformEnabled(boolean enabled) {
		mTranslator.flush();
		mWorldTransformEnabled = enabled;
	}

	public void resetGlobalTransform() {
		mTranslator.flush();
		mWorldTransform.loadIdentity();
	}

	public boolean isActivated() {
		return mTranslator.mCurDrawListener == this;
	}

	public IndexedVertexBuffer getCurrentVertexBuffer() {
		assert isActivated() : "Graphics not activated";
		return mCurrentVertexBuffer;
	}

	public void flush() {
		mTranslator.flush();
	}

	public void fillBuffers() {
		mCurrentVertexBuffer.fillBuffers();
	}

	@Override
	public void onSurfaceSizeChanged(int width, int height) {

	}

	protected float get2DStereoShift(float eyeDistance) {
		return (1f/(eyeDistance+1))*mTranslator.mCameraShiftX;
	}

}
