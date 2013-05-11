package yang.graphics.translator;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import yang.graphics.DrawListener;
import yang.graphics.FloatColor;
import yang.graphics.buffers.DrawBatch;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.programs.AbstractProgram;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.textures.TextureHolder;
import yang.math.YangMatrix;
import yang.math.YangMatrixRectOps;
import yang.model.DebugYang;



public abstract class AbstractGraphics<ShaderType extends AbstractProgram> implements DrawListener {

	//Constants
	public final static float PI = 3.1415926535f;
	public final static int MAX_DYNAMIC_VERTICES = 100000;
	
	//Matrices
	public YangMatrixRectOps mInterWorldTransf1;
	public YangMatrixRectOps mInterWorldTransf2;
	protected YangMatrixRectOps mInterTexTransf;
	protected YangMatrixRectOps mIdentity;
	public TextureCoordinatesQuad mTexIdentity;
	public YangMatrixRectOps mWorldTransform;
	protected YangMatrix mProjectionTransform;
	public YangMatrix mCameraProjectionMatrix;
	protected YangMatrix mResultTransformationMatrix;
	public YangMatrix mCurProjTransform;
	protected float[] invGameProjection;
	
	//State
	protected boolean mBatchRecording;
	protected boolean mWorldTransformEnabled;
	public ShaderType mCurrentProgram;
	protected float mBold;
	public float[] mCurColor;
	public float[] mCurAddColor;
	public float[] mAmbientColor = new float[4];
	
	//Persistent attributes
	public GraphicsTranslator mTranslator;
	
	//Counters
	protected float mTime;
	
	//Buffers
	public IndexedVertexBuffer mDynamicSpriteVertexBuffer;
	public IndexedVertexBuffer mCurrentVertexBuffer;	//Always the same reference as mTranslator.mCurrentVertexBuffer but generic type
	
	protected abstract IndexedVertexBuffer createVertexBuffer(boolean dynamicVertices,boolean dynamicIndices,int maxIndices,int maxVertices);
	protected void derivedInit() { }
	public abstract ShaderType getDefaultProgram();
	
	public AbstractGraphics(GraphicsTranslator translator) {
		if (DebugYang.showStart) DebugYang.showStackTrace("4", 1);
		mTranslator = translator;
	}
	
	public void init() {
		
		mTranslator.preCheck("Init");
		if (DebugYang.showStart) DebugYang.showStackTrace("5", 1);
		
		mWorldTransformEnabled = false;
		mProjectionTransform = mTranslator.createTransformationMatrix();
		mInterWorldTransf1 = new YangMatrixRectOps();
		mInterWorldTransf2 = new YangMatrixRectOps();
		mWorldTransform = new YangMatrixRectOps();
		mWorldTransform.loadIdentity();
		mCameraProjectionMatrix = mTranslator.createTransformationMatrix();
		mResultTransformationMatrix = mTranslator.createTransformationMatrix();
		mInterTexTransf = new YangMatrixRectOps();
		mIdentity = new YangMatrixRectOps();
		mIdentity.loadIdentity();
//		mIdentity.refreshRect2D();
		mTexIdentity = mTranslator.createTexCoords();
		mTexIdentity.init(0, 0, 1);
		mTranslator.checkErrorInst("Matrices");
		invGameProjection = new float[16];
		mCurProjTransform = mProjectionTransform;
		mCurProjTransform.loadIdentity();
		mCurColor = new float[4];
		mCurAddColor = new float[4];
		mTime = 0;
		mBold = 0;
		mDynamicSpriteVertexBuffer = createVertexBuffer(true,true,MAX_DYNAMIC_VERTICES*6/4,MAX_DYNAMIC_VERTICES);
		mBatchRecording = false;
		
		derivedInit();
		restart();
	}

	
	public void restart() {
		setColor(1,1,1);
		setAddColor(0,0,0);
	}
	
	public final void activate() {
		assert mTranslator.preCheck("activate");
		mTranslator.flush();
		mTranslator.setDrawListener(this);
		if(mCurrentProgram!=null) {
			disableBuffers();
			mCurrentProgram = null;
		}
		setVertexBuffer(mDynamicSpriteVertexBuffer);
		setDefaultProgram();
		mTranslator.rebindTexture(0);
		assert mTranslator.checkErrorInst("Activate");
	}
	
	public final void bindTexture(Texture texture,int level) {
		mTranslator.bindTexture(texture, level);
	}
	
	public final void bindTexture(Texture texture) {
		mTranslator.bindTexture(texture, 0);
	}
	
	public void bindTextureInHolder(TextureHolder textureHolder) {
		mTranslator.bindTextureInHolder(textureHolder);
	}
	
	public boolean setShaderProgram(ShaderType program) {
		if(program!=mCurrentProgram) {
			flush();
			if(mCurrentProgram!=null)
				disableBuffers();
			mCurrentProgram = program;
			program.activate();
			enableBuffers();
			setColor(1, 1, 1);
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
		setVertexBuffer(mDynamicSpriteVertexBuffer);
	}
	
	public void startBatchRecording(int maxIndices,int maxVertices,boolean dynamicIndices,boolean dynamicVertices) {
		mBatchRecording = true;
		mTranslator.mFlushDisabled = true;
		setVertexBuffer(createVertexBuffer(dynamicVertices,dynamicIndices,maxIndices,maxVertices));
	}
	
	public void startBatchRecording(int maxIndices, int maxVertices) {
		startBatchRecording(maxIndices,maxVertices,false,false);
	}
	
	public void startBatchRecording(int maxIndices) {
		startBatchRecording(maxIndices,maxIndices);
	}
	
	public DrawBatch finishBatchRecording() {
		mTranslator.mCurrentVertexBuffer.finishUpdate();
		DrawBatch result = new DrawBatch(this,mCurrentVertexBuffer);
		resetVertexBuffer();
		mBatchRecording = false;
		mTranslator.mFlushDisabled = false;
		return result;
	}
	
	public void switchGameCoordinates(boolean enable) {
		if((enable && mCurProjTransform == mProjectionTransform) || (!enable && mCurProjTransform == mTranslator.mProjScreenTransform))
			return;
		flush();
		if(enable) {
			mCurProjTransform = mProjectionTransform;
		}else{
			mCurProjTransform = mTranslator.mProjScreenTransform;
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
		mCurColor[0] = color.x;
		mCurColor[1] = color.y;
		mCurColor[2] = color.z;
		mCurColor[3] = 1;
	}
	
	public void setColor(Vector3f color, float a) {
		mCurColor[0] = color.x;
		mCurColor[1] = color.y;
		mCurColor[2] = color.z;
		mCurColor[3] = a;
	}
	
	public void setColor(Vector4f color) {
		mCurColor[0] = color.x;
		mCurColor[1] = color.y;
		mCurColor[2] = color.z;
		mCurColor[3] = color.w;
	}
	
	public void setColor(FloatColor color) {
		mCurColor[0] = color.mValues[0];
		mCurColor[1] = color.mValues[1];
		mCurColor[2] = color.mValues[2];
		mCurColor[3] = color.mValues[3];
	}
	
	/**
	 * weight=0 => color1
	 * weight=1 => color2
	 */
	public void setColorWeighted(FloatColor color1,FloatColor color2,float weight) {
		float dWeight = 1-weight;
		mCurColor[0] = color1.mValues[0]*dWeight + color2.mValues[0]*weight;
		mCurColor[1] = color1.mValues[1]*dWeight + color2.mValues[1]*weight;
		mCurColor[2] = color1.mValues[2]*dWeight + color2.mValues[2]*weight;
		mCurColor[3] = color1.mValues[3]*dWeight + color2.mValues[3]*weight;
	}
	
	//---Add-Color---
	public void setAddColor(float r, float g, float b) {
		mCurAddColor[0] = r;
		mCurAddColor[1] = g;
		mCurAddColor[2] = b;
		mCurAddColor[3] = 0;
	}

	public void setAddColor(float r, float g, float b, float a) {
		mCurAddColor[0] = r;
		mCurAddColor[1] = g;
		mCurAddColor[2] = b;
		mCurAddColor[3] = a;
	}
	
	public void setAddColor(float[] color) {
		mCurAddColor[0] = color[0];
		mCurAddColor[1] = color[1];
		mCurAddColor[2] = color[2];
		mCurAddColor[3] = color[3];
	}
	
	public float[] getCurrentAddColor(){
		return mCurAddColor;
	}
	
	
	//---Screen-data---
	public float getScreenRight() {
		return mTranslator.mRatioX;
	}
	
	public float getScreenBottom() {
		return -mTranslator.mRatioY;
	}
	
	public float getScreenLeft() {
		return -mTranslator.mRatioX;
	}

	public float getScreenTop() {
		return mTranslator.mRatioY;
	}
	
	public float getScreenCenterX() {
		return 0;
	}
	
	public float getScreenCenterY() {
		return 0;
	}
	
	public void switchWireFrames(boolean enabled) {
		flush();
		mTranslator.mWireFrames = enabled;
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
		setAddColor(0,0,0,0);
	}
	
	public void setGlobalTransformEnabled(boolean enabled) {
		mTranslator.flush();
		mWorldTransformEnabled = enabled;
	}

	public void resetGlobalTransform() {
		mTranslator.flush();
		mWorldTransform.loadIdentity();
	}
	
	public IndexedVertexBuffer getCurrentVertexBuffer() {
		return mCurrentVertexBuffer;
	}
	
	public void flush() {
		mTranslator.flush();
	}
	
	public void fillBuffers() {
		mCurrentVertexBuffer.fillBuffers();
	}
	
}
