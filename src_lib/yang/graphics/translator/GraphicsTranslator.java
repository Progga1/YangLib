package yang.graphics.translator;

import java.nio.ByteBuffer;

import yang.graphics.AbstractGFXLoader;
import yang.graphics.DrawListener;
import yang.graphics.FloatColor;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.buffers.UniversalVertexBuffer;
import yang.graphics.programs.BasicProgram;
import yang.graphics.programs.GLProgramFactory;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.textures.TextureData;
import yang.graphics.textures.TextureHolder;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.TextureSettings;
import yang.graphics.translator.glconsts.GLMasks;
import yang.graphics.translator.glconsts.GLOps;
import yang.math.objects.matrix.YangMatrix;
import yang.math.objects.matrix.YangMatrixCameraOps;
import yang.model.ScreenInfo;
import yang.model.TransformationFactory;
import yang.model.enums.ByteFormat;
import yang.util.NonConcurrentList;

public abstract class GraphicsTranslator implements TransformationFactory,GLProgramFactory,ScreenInfo {

	public final static int T_TRIANGLES = 0;
	public final static int T_STRIP = 1;
	public final static int MAX_TEXTURES = 32;
	public static GraphicsTranslator INSTANCE;
	public static GraphicsTranslator appInstance;
	
	//Properties
	public int mScreenWidth;
	public int mScreenHeight;
	public float mRatioX;
	public float mRatioY;
	public float mInvRatioX;
	public float mInvRatioY;
	public long mThreadId;
	
	//State
	protected Texture[] mCurrentTextures;
	public IndexedVertexBuffer mCurrentVertexBuffer;
	protected boolean mFlushDisabled;
	public int mDrawMode;
	public boolean mWireFrames;
	protected DrawListener mCurDrawListener;
	public ScreenInfo mCurrentScreen;
	public float mTimer;
	private long mLstTimestamp = -1;
	
	//Matrices
	public YangMatrixCameraOps mProjScreenTransform;
	public YangMatrix mStaticTransformation;
	
	//Counters
	public int rectCount;
	public int mFlushCount;
	
	//Persistent
	public Texture mNullTexture;
	public AbstractGFXLoader mGFXLoader;
	
	private NonConcurrentList<BasicProgram> mPrograms;
	
	//Helpers
	protected final int[] mTempInt = new int[1];
	protected final int[] mTempInt2 = new int[1];
	
	public abstract void setClearColor(float r, float g, float b,float a);
	public abstract void clear(int mask);
	protected abstract void derivedInitTexture(Texture texture, ByteBuffer buffer, TextureSettings textureSettings);
	public abstract void deleteTextures(int[] ids);
	protected abstract void drawDefaultVertices(int bufferStart, int vertexCount, boolean wireFrames, IndexedVertexBuffer vertexBuffer);
	public abstract void derivedSetAttributeBuffer(int handle,int bufferIndex,IndexedVertexBuffer vertexBuffer);
	public abstract void enableAttributePointer(int handle);
	public abstract void disableAttributePointer(int handle);
	protected abstract void setViewPort(int width,int height);
	protected abstract void derivedInit();
	public abstract void setCullMode(boolean drawClockwise);
	protected abstract void derivedSetScreenRenderTarget();
	protected abstract TextureRenderTarget derivedCreateRenderTarget(Texture texture);
	protected abstract void derivedSetTextureRenderTarget(TextureRenderTarget renderTarget);
	public abstract void setDepthFunction(boolean less);
	public abstract void generateMipMap();
	protected abstract void bindTexture(int texId, int level);
	public abstract void readPixels(int x,int y,int width,int height,int channels,ByteFormat byteFormat,ByteBuffer pixels);
	protected abstract boolean checkErrorInst(String message,boolean pre);
	public abstract void setBlendFunction(int sourceFactor,int destFactor);
	public abstract void setStencilFunction(int function,int ref,int mask);
	public abstract void setStencilOperation(int fail,int zFail,int zPass);
	public abstract void enable(int glConstant);
	public abstract void disable(int glConstant);
	public abstract void setScissorRectI(int x,int y,int width,int height);
	
	//TODO: glColorMask, glDepthMask, glStencilMask, glScissor, glEnable(GL_SCISSOR_TEST)
	
	public static String errorCodeToString(int code) {
		switch(code) {
		case 1280: return "GL_INVALID_ENUM";
		case 1281: return "GL_INVALID_VALUE";
		case 1282: return "GL_INVALID_OPERATION";
		case 1283: return "GL_STACK_OVERFLOW";
		case 1284: return "GL_STACK_UNDERFLOW";
		case 1285: return "GL_OUT_OF_MEMORY";
		case 1286: return "GL_INVALID_FRAMEBUFFER_OPERATION";
		default: return "CODE: "+code+"/0x" + Integer.toHexString(code);
		}
	}
	
	public boolean preCheck(String message) {
		if(mThreadId!=Thread.currentThread().getId()) {
			throw new RuntimeException("Non-GL-thread");
		}
		return checkErrorInst(message,false);
	}
	
	public boolean checkErrorInst(String message) {
		if(mThreadId!=Thread.currentThread().getId()) {
			throw new RuntimeException("Non-GL-thread");
		}
		return checkErrorInst(message,false);
	}
	
	public static int byteFormatBytes(ByteFormat byteFormat) {
		switch(byteFormat) {
		case BYTE: return 1;
		case SHORT: return 2;
		case INT: return 4;
		case FLOAT: return 4;
		default: return 1;
		}
	}
	
	public GraphicsTranslator() {
		INSTANCE = this;
		mCurrentTextures = new Texture[MAX_TEXTURES];
		mProjScreenTransform = new YangMatrixCameraOps();
		mStaticTransformation = createTransformationMatrix();
		mStaticTransformation.loadIdentity();
		mFlushDisabled = false;
		rectCount = 0;
		mFlushCount = 0;
		mDrawMode = T_TRIANGLES;
		mWireFrames = false;
		mPrograms = new NonConcurrentList<BasicProgram>();
		mCurDrawListener = null;
		appInstance = this;
		mCurrentScreen = this;
	}
	
	public YangMatrix createTransformationMatrix() {
		return new YangMatrix();
	}
	
	public final void start() {
		mThreadId = Thread.currentThread().getId();
		assert preCheck("Start graphics translator");
		final int DIM = 2;
		final int BYTES = DIM*DIM*4;
		ByteBuffer buf = ByteBuffer.allocateDirect(BYTES);

		for(int i=0;i<BYTES;i++) {
			buf.put((byte)255);
		}
		buf.rewind();
		mNullTexture = createTexture(buf, DIM,DIM, new TextureSettings());
		assert checkErrorInst("Create null texture");
		
		switchCulling(false);
		setCullMode(false);
		
		derivedInit();
		assert checkErrorInst("Start graphics translator");
	}
	
	public void initTexture(Texture texture, ByteBuffer buffer, TextureSettings textureSettings, boolean finish) {
		derivedInitTexture(texture,buffer,textureSettings);
		if(finish)
			texture.finish();
	}
	
	public void initTexture(Texture texture, ByteBuffer buffer, TextureSettings textureSettings) {
		initTexture(texture,buffer,textureSettings,true);
	}
	
	public final void bindTexture(Texture texture,int level) {
		assert checkErrorInst("PRE bind texture");
		if(texture!=mCurrentTextures[level] && (texture!=null || mCurrentTextures[level]!=mNullTexture))
		{
			flush();
			if(texture==null)
				texture = mNullTexture;
			mCurrentTextures[level] = texture;
			bindTexture(texture.getId(),level);
		}
		assert checkErrorInst("bind texture");
	}
	
	public final void bindTexture(Texture texture) {
		bindTexture(texture,0);
	}
	
	public void bindTextureInHolder(TextureHolder textureHolder) {
		if(textureHolder==null)
			bindTexture(null);
		else
			bindTexture(textureHolder.getTexture(mGFXLoader));
	}
	
	public final void rebindTexture(int level) {
		if(mCurrentTextures[level]==null)
			mCurrentTextures[level] = mNullTexture;
		bindTexture(mCurrentTextures[level].getId(),0);
	}
	
	public final void rebindTextures() {
		for(int i=0;i<MAX_TEXTURES;i++) {
			if(mCurrentTextures[i]==null)
				return;
			bindTexture(mCurrentTextures[i].getId(),0);
		}
	}
	
	public void unbindTexture(int level) {
		mCurrentTextures[level] = null;
	}
	
	public void unbindTextures() {
		for(int i=0;i<MAX_TEXTURES;i++) {
			if(mCurrentTextures[i]==null)
				return;
			mCurrentTextures[i] = null;
			
		}
	}
	
	public int getSurfaceWidth() {
		return mScreenWidth;
	}
	
	public int getSurfaceHeight() {
		return mScreenHeight;
	}
	
	public float getSurfaceRatioX() {
		return mRatioX;
	}
	
	public float getSurfaceRatioY() {
		return mRatioY;
	}
	
	public float toNormX(float x) {
		return (x - mScreenWidth / 2) / mScreenWidth * 2 * mRatioX;
	}
	
	public float toNormY(float y) {
		return -(y - mScreenHeight / 2) / mScreenHeight * 2 * mRatioY;
	}
	
	public IndexedVertexBuffer createVertexBuffer(boolean dynamicVertices,boolean dynamicIndices,int maxIndices,int maxVertices) {
		return new UniversalVertexBuffer(dynamicVertices,dynamicIndices,maxIndices,maxVertices);
	}
	
	public TextureCoordinatesQuad createTexCoords() {
		return new TextureCoordinatesQuad();
	}
	
	public final TextureCoordinatesQuad createTexCoords(int x1, int y1, int x2, int y2, int textureWidth, int textureHeight) {
		return createTexCoords().initI(x1,y1,x2,y2,textureWidth,textureHeight);
	}
	
	public final TextureCoordinatesQuad createTexCoords(float x1,float y1,float x2,float y2) {
		return createTexCoords().init(x1,y1,x2,y2);
	}
	
	public final TextureCoordinatesQuad createTexCoords(float x1, float y1, float widthAndHeight) {
		return createTexCoords().init(x1,y1,widthAndHeight);
	}
	
	public Texture createEmptyTexture(int width,int height,TextureSettings settings) {
		Texture texture = new Texture(this);
		ByteBuffer emptyBuffer = ByteBuffer.allocateDirect(width*height*settings.mChannels);
		texture.set(emptyBuffer, width, height, settings);
		return texture;
	}
	
	public Texture createTexture() {
		return new Texture(this);
	}
	
	public Texture createTexture(ByteBuffer source, int width, int height, TextureSettings settings) {
		source.rewind();
		return new Texture(this,source,width,height,settings);
	}
	
	public Texture createTexture(TextureData textureData, TextureSettings settings) {
		settings.mChannels = textureData.mChannels;
		return createTexture(textureData.mData,textureData.mWidth,textureData.mHeight,settings);
	}
	
	public void setDrawListener(DrawListener drawListener) {
		mCurDrawListener = drawListener;
	}
	
	public void addProgram(BasicProgram program) {
		assert preCheck("Add program");
		program.init(this);
		mPrograms.add(program);
		assert checkErrorInst("Add program");
	}
	
	public void restartPrograms() {
		for(BasicProgram program:mPrograms) {
			program.restart();
		}
	}
	
	public void flush() {
		assert checkErrorInst("PRE flush");
		if(mFlushDisabled || mCurrentVertexBuffer==null)
			return;
		int vertexCount = mCurrentVertexBuffer.getCurrentIndexWriteCount();
		if(vertexCount>0) {
			mCurDrawListener.onPreDraw();
			rectCount += mCurrentVertexBuffer.getCurrentIndexWriteCount()/6;
			mCurrentVertexBuffer.finishUpdate();
			mCurrentVertexBuffer.reset();
			drawVertices(0,vertexCount,mDrawMode);
			mFlushCount++;
		}
		assert checkErrorInst("Flush");
	}
	
	public final void measureTime() {
		long curTime = System.currentTimeMillis();
		if(mLstTimestamp>0) {
			mTimer += (curTime-mLstTimestamp)*0.001f;
		}else
			mTimer = 0;
		mLstTimestamp = curTime;
	}
	
	public void beginFrame() {
		measureTime();
		rectCount = 0;
		mFlushCount = 0;
	}
	
	public void endFrame() {
		flush();
	}
	
	public final void clear(float r, float g, float b) {
		setClearColor(r,g,b,1);
		clear(GLMasks.COLOR_BUFFER_BIT);
	}
	
	public final void clear(float r, float g, float b,float a,int additionalMask) {
		setClearColor(r,g,b,a);
		clear(GLMasks.COLOR_BUFFER_BIT | additionalMask);
	}
	
	public void clear(FloatColor color,int additionalMask) {
		clear(color.mValues[0], color.mValues[1], color.mValues[2],color.mValues[3],additionalMask);
	}

	public void setAttributeBuffer(int handle,int bufferIndex) {
		if(!mCurrentVertexBuffer.bindBuffer(handle,bufferIndex)) {
			derivedSetAttributeBuffer(handle,bufferIndex,mCurrentVertexBuffer);
		}
	}
	
	public void drawVertices(int bufferStart, int vertexCount,int mode) {
		assert preCheck("Draw vertices");
		mCurDrawListener.onPreDraw();
		mCurDrawListener.bindBuffers();
		if(!mCurrentVertexBuffer.draw(bufferStart, vertexCount, mode)) {
			drawDefaultVertices(bufferStart,vertexCount,mWireFrames,mCurrentVertexBuffer);
		}
	}
	
	public void setVertexBuffer(IndexedVertexBuffer vertexBuffer) {
		assert preCheck("Set vertex buffer");
		mCurrentVertexBuffer = vertexBuffer;
		vertexBuffer.setAsCurrent();
	}
	
	public void setScreenSize(int width, int height) {
		this.mScreenWidth = width;
		this.mScreenHeight = height;
		this.mRatioX = (float) width / height;
		if(mRatioX<1){
			this.mRatioY = 1/mRatioX;
			mRatioX = 1;
		}else
			mRatioY = 1;
		mInvRatioX = 1/mRatioX;
		mInvRatioY = 1/mRatioY;
		mProjScreenTransform.setOrthogonalProjection(-mRatioX, mRatioX, mRatioY, -mRatioY);
		mProjScreenTransform.refreshInverted();
		setViewPort(width,height);
	}
	
	public static YangMatrix newTransformationMatrix() {
		return appInstance.createTransformationMatrix();
	}
	
	protected void updateTexture(Texture texture, ByteBuffer source, int left,int top, int width,int height) {
		
	}
	
	public void deleteTexture(int id) {
		mTempInt[0] = id;
		deleteTextures(mTempInt);
	}
	
	public TextureRenderTarget createRenderTarget(int width,int height,TextureSettings textureSettings) {
		Texture texture = createEmptyTexture(width,height,textureSettings);
		return derivedCreateRenderTarget(texture);
	}
	
	public void setScreenRenderTarget() {
		flush();
		mCurrentScreen = this;
		setViewPort(mScreenWidth,mScreenHeight);
		derivedSetScreenRenderTarget();
	}
	
	public void setTextureRenderTarget(TextureRenderTarget renderTarget) {
		flush();
		mCurrentScreen = renderTarget;
		setViewPort(renderTarget.mTargetTexture.mWidth,renderTarget.mTargetTexture.mHeight);
		derivedSetTextureRenderTarget(renderTarget);
		unbindTextures();
		assert checkErrorInst("Set texture render target");
	}
	
	public YangMatrix getStaticTransformation() {
		mStaticTransformation.loadIdentity();
		return mStaticTransformation;
	}
	
	public void readPixels(ByteBuffer pixels,int channels,ByteFormat byteFormat) {
		readPixels(0,0,mScreenWidth,mScreenHeight,channels,byteFormat,pixels);
	}
	
	public void readPixels(ByteBuffer pixels) {
		readPixels(0,0,mScreenWidth,mScreenHeight,4,ByteFormat.BYTE,pixels);
	}
	
	public ByteBuffer makeScreenshot(int channels,ByteFormat byteFormat) {
		ByteBuffer screenData = ByteBuffer.allocateDirect(mScreenWidth*mScreenHeight*channels*byteFormatBytes(byteFormat));
		readPixels(screenData,channels,byteFormat);
		return screenData;
	}
	
	public ByteBuffer makeScreenshot() {
		return makeScreenshot(4,ByteFormat.UNSIGNED_BYTE);
	}

	public void resetTimer() {
		mTimer = 0;
		mLstTimestamp = -1;
	}
	
	public void setScissorRectNormalized(float x,float y,float width,float height) {
		x = x*0.5f*mInvRatioX+0.5f;
		y = y*0.5f*mInvRatioY+0.5f;
		setScissorRectI((int)(x*mScreenWidth),(int)(y*mScreenHeight),(int)(width*0.5f*mInvRatioX*mScreenWidth),(int)(height*0.5f*mInvRatioY*mScreenHeight));
	}
	
	public void switchScissor(boolean enabled) {
		if(enabled)
			enable(GLOps.SCISSOR_TEST);
		else
			disable(GLOps.SCISSOR_TEST);
	}
	
	public void switchZBuffer(boolean enabled) {
		if(enabled)
			enable(GLOps.DEPTH_TEST);
		else
			disable(GLOps.DEPTH_TEST);
	}
	
	public void switchCulling(boolean enabled) {
		if(enabled)
			enable(GLOps.CULL_FACE);
		else
			disable(GLOps.CULL_FACE);
	}
	
	public void switchStencilTest(boolean enabled) {
		if(enabled)
			enable(GLOps.STENCIL_TEST);
		else
			disable(GLOps.STENCIL_TEST);
	}
	
}
