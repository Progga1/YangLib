package yang.graphics.translator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.buffers.UniversalVertexBuffer;
import yang.graphics.listeners.DrawListener;
import yang.graphics.listeners.SurfaceListener;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.BasicProgram;
import yang.graphics.programs.GLProgramFactory;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.textures.TextureData;
import yang.graphics.textures.TextureHolder;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.glconsts.GLBlendFuncs;
import yang.graphics.translator.glconsts.GLMasks;
import yang.graphics.translator.glconsts.GLOps;
import yang.math.objects.matrix.YangMatrix;
import yang.math.objects.matrix.YangMatrixCameraOps;
import yang.model.ScreenInfo;
import yang.model.TransformationFactory;
import yang.model.enums.ByteFormat;
import yang.model.state.GraphicsState;
import yang.util.NonConcurrentList;

public abstract class GraphicsTranslator implements TransformationFactory,GLProgramFactory,ScreenInfo {

	public final static int T_TRIANGLES = 0;
	public final static int T_STRIP = 1;
	public final static int MAX_TEXTURES = 32;
	public static GraphicsTranslator INSTANCE;
	public static GraphicsTranslator appInstance;
	public static int FPS_REFRESH_FRAMES = 20;
	
	//Properties
	public String mDriverKey;
	public int mScreenWidth;
	public int mScreenHeight;
	public float mRatioX;
	public float mRatioY;
	public float mInvRatioX;
	public float mInvRatioY;
	public long mThreadId;
	private NonConcurrentList<SurfaceListener> mScreenListeners;
	public float mMinRatioX = 1;
	public float mMaxTime = 60;
	
	//State
	protected Texture[] mCurrentTextures;
	public IndexedVertexBuffer mCurrentVertexBuffer;
	public boolean mFlushDisabled;
	public int mDrawMode;
	public boolean mWireFrames;
	public DrawListener mCurDrawListener;
	public ScreenInfo mCurrentScreen;
	public float mTimer;
	public float mShaderTimer;
	private long mLstTimestamp;
	public float mCurFrameDeltaTime = 0;
	public float mFPS = 0;
	private long mFPSStartTime = 0;
	public long mFrameCount = 0;
	public boolean mForceWireFrames = false;
	private GraphicsState mSavedState;
	
	//Matrices
	public YangMatrixCameraOps mProjScreenTransform;
	public YangMatrix mStaticTransformation;
	
	//Counters
	public int mPolygonCount;
	public int mDynamicPolygonCount;
	public int mBatchPolygonCount;
	public int mDrawCount;
	public int mFlushCount;
	public int mBatchCount;
	public int mTexBindCount;
	public int mShaderSwitchCount;

	
	//Persistent
	public Texture mWhiteTexture;
	public Texture mBlackTexture;
	private Texture mNoTexture;
	public AbstractGFXLoader mGFXLoader;
	private NonConcurrentList<TextureRenderTarget> mRenderTargets;
	private NonConcurrentList<BasicProgram> mPrograms;
	private ShortBuffer mWireFrameIndexBuffer;
	
	//Helpers
	protected final int[] mTempInt = new int[1];
	protected final int[] mTempInt2 = new int[1];
	protected final int[] mTempIntArray = new int[128];
	public int mRestartCount = 0;
	
	public abstract void setClearColor(float r, float g, float b,float a);
	public abstract void clear(int mask);
	protected abstract void genTextures(int[] target,int count);
	protected abstract void setTextureData(int texId,int width,int height, ByteBuffer buffer, TextureProperties textureSettings);
	public abstract void deleteTextures(int[] ids);
	protected abstract void drawDefaultVertices(int bufferStart, int vertexCount, boolean wireFrames, ShortBuffer indexBuffer);
	public abstract void derivedSetAttributeBuffer(int handle,int bufferIndex,IndexedVertexBuffer vertexBuffer);
	public abstract void enableAttributePointer(int handle);
	public abstract void disableAttributePointer(int handle);
	protected abstract void setViewPort(int width,int height);
	public abstract void setCullMode(boolean drawClockwise);
	protected abstract void derivedSetScreenRenderTarget();
	public abstract TextureRenderTarget derivedCreateRenderTarget(Texture texture);
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
	
	protected void postInit() { }
	
	//TODO: glColorMask, glDepthMask
	
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
		mPolygonCount = 0;
		mFlushCount = 0;
		mDrawMode = T_TRIANGLES;
		mWireFrames = false;
		mPrograms = new NonConcurrentList<BasicProgram>();
		mCurDrawListener = null;
		appInstance = this;
		mCurrentScreen = this;
		mNoTexture = new Texture(this);
		mScreenListeners = new NonConcurrentList<SurfaceListener>();
		mRenderTargets = new NonConcurrentList<TextureRenderTarget>();
	}
	
	public void addScreenListener(SurfaceListener listener) {
		mScreenListeners.add(listener);
	}
	
	public YangMatrix createTransformationMatrix() {
		return new YangMatrix();
	}
	
	private void start() {
		mThreadId = Thread.currentThread().getId();
		assert preCheck("Start graphics translator");
		final int DIM = 2;
		final int BYTES = DIM*DIM*4;
		ByteBuffer buf = ByteBuffer.allocateDirect(BYTES);
		for(int i=0;i<BYTES;i++)
			buf.put((byte)255);
		if(mWhiteTexture==null)
			mWhiteTexture = createTexture(buf, DIM,DIM, new TextureProperties(TextureWrap.CLAMP,TextureFilter.NEAREST));
		else
			mWhiteTexture.update(buf);
		buf = ByteBuffer.allocateDirect(BYTES);
		for(int i=0;i<BYTES;i++)
			if(i%4==3)
				buf.put((byte)255);
			else
				buf.put((byte)0);
		if(mBlackTexture==null)
			mBlackTexture = createTexture(buf, DIM,DIM, new TextureProperties(TextureWrap.CLAMP,TextureFilter.NEAREST));
		else
			mBlackTexture.update(buf);
		assert checkErrorInst("Create def textures");
		
		enable(GLOps.BLEND);
		setBlendFunction(GLBlendFuncs.ONE,GLBlendFuncs.ONE_MINUS_SRC_ALPHA);
		switchCulling(false);
		setCullMode(false);
		mLstTimestamp = -1;
	}
	
	public final void init() {
		start();
		
		postInit();
		assert checkErrorInst("Start graphics translator");
	}
	
	public void restart() {
		start();
		for(BasicProgram program:mPrograms) {
			program.restart();
		}
		for(TextureRenderTarget renderTarget:mRenderTargets) {
			renderTarget.mTargetTexture.update(null);
			derivedCreateRenderTarget(renderTarget.mTargetTexture);
		}
		mRestartCount++;
	}
	
	
	public int genTexture() {
		genTextures(mTempInt,1);
		return mTempInt[0];
	}
	
	protected void setTextureData(Texture targetTexture,ByteBuffer data) {
		setTextureData(targetTexture.getId(),targetTexture.getWidth(),targetTexture.getHeight(),data,targetTexture.mSettings);
	}
	
	protected void initTexture(Texture targetTexture, ByteBuffer buffer) {
		if(buffer!=null)
			buffer.order(ByteOrder.nativeOrder());
		targetTexture.setId(genTexture());
		setTextureData(targetTexture.getId(),targetTexture.getWidth(),targetTexture.getHeight(),buffer,targetTexture.mSettings);
		if(buffer!=null)
			targetTexture.finish();
	}
	
	public final void bindTexture(Texture texture,int level) {
		assert checkErrorInst("PRE bind texture");
		if(texture!=mCurrentTextures[level] && (texture!=null || mCurrentTextures[level]!=mWhiteTexture)) {
			flush();
			if(texture==null)
				texture = mWhiteTexture;
			mCurrentTextures[level] = texture;
			mTexBindCount++;
			bindTexture(texture.getId(),level);
		}
		assert checkErrorInst("bind texture");
	}
	
	public final void bindTextureNoFlush(Texture texture,int level) {
		assert checkErrorInst("PRE bind texture");
		if(texture!=mCurrentTextures[level] && (texture!=null || mCurrentTextures[level]!=mWhiteTexture)) {
			if(texture==null)
				texture = mWhiteTexture;
			mCurrentTextures[level] = texture;
			mTexBindCount++;
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
			mCurrentTextures[level] = mWhiteTexture;
		mTexBindCount++;
		bindTexture(mCurrentTextures[level].getId(),0);
	}
	
	public final void rebindTextures() {
		for(int i=0;i<MAX_TEXTURES;i++) {
			if(mCurrentTextures[i]==null)
				return;
			mTexBindCount++;
			bindTexture(mCurrentTextures[i].getId(),0);
		}
	}
	
	public void unbindTexture(int level) {
		mCurrentTextures[level] = mNoTexture;
	}
	
	public void unbindTextures() {
		for(int i=0;i<MAX_TEXTURES;i++) {
			if(mCurrentTextures[i]==null)
				return;
			mCurrentTextures[i] = mNoTexture;
			
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
	
	public IndexedVertexBuffer createUninitializedVertexBuffer(boolean dynamicVertices,boolean dynamicIndices,int maxIndices,int maxVertices) {
		return new UniversalVertexBuffer(dynamicVertices,dynamicIndices,maxIndices,maxVertices);
	}
	
	public TextureCoordinatesQuad createTexCoords() {
		return new TextureCoordinatesQuad();
	}
	
	public final TextureCoordinatesQuad createTexCoords(int x1, int y1, int x2, int y2, int textureWidth, int textureHeight) {
		return createTexCoords().init(x1,y1,x2,y2,textureWidth,textureHeight);
	}
	
	public final TextureCoordinatesQuad createTexCoords(float x1,float y1,float x2,float y2) {
		return createTexCoords().init(x1,y1,x2,y2);
	}
	
	public final TextureCoordinatesQuad createTexCoords(float x1, float y1, float widthAndHeight) {
		return createTexCoords().init(x1,y1,widthAndHeight);
	}
	
	public Texture createEmptyTexture(int width,int height,TextureProperties settings) {
		Texture texture = new Texture(this);
		texture.set(null, width, height, settings);
		return texture;
	}
	
	public Texture createTexture() {
		return new Texture(this);
	}
	
	public Texture createTexture(ByteBuffer source, int width, int height, TextureProperties settings) {
		source.rewind();
		Texture result = new Texture(this,source,width,height,settings);
		return result;
	}
	
	public Texture createTexture(TextureData textureData, TextureProperties settings) {
		settings.mChannels = textureData.mChannels;
		return createTexture(textureData.mData,textureData.mWidth,textureData.mHeight,settings);
	}
	
	public void setDrawListener(DrawListener drawListener) {
		mCurDrawListener = drawListener;
	}
	
	public <ShaderType extends BasicProgram> ShaderType addProgram(ShaderType program) {
		assert preCheck("Add program");
		program.init(this);
		mPrograms.add(program);
		assert checkErrorInst("Add program");
		return program;
	}
	
	public <ShaderType extends BasicProgram> ShaderType addProgram(Class<ShaderType> program) {
		
		try {
			return addProgram(program.newInstance());
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void flush() {
		assert checkErrorInst("PRE flush");
		if(mFlushDisabled || mCurrentVertexBuffer==null)
			return;
		int vertexCount = mCurrentVertexBuffer.getCurrentIndexWriteCount();
		if(vertexCount>0) {
			prepareDraw();
			assert preCheck("Prepare draw");
			mCurrentVertexBuffer.finishUpdate();
			assert preCheck("Finish update");
			drawVertices(0,vertexCount,mDrawMode);
			mFlushCount++;
			mDynamicPolygonCount += vertexCount/3;
		}
		assert checkErrorInst("Flush");
	}
	
	public void prepareDraw() {
		assert preCheck("Prepare draw vertices");
		mCurDrawListener.onPreDraw();
		assert preCheck("Draw vertices finish update");
		mCurrentVertexBuffer.reset();
		mCurDrawListener.bindBuffers();
	}
	
	public final void measureTime() {
		long curTime = System.currentTimeMillis();
		if(mLstTimestamp>0) {
			mCurFrameDeltaTime = (curTime-mLstTimestamp)*0.001f;
			mTimer += mCurFrameDeltaTime;
			mShaderTimer += mCurFrameDeltaTime;
			if(mShaderTimer>mMaxTime)
				mShaderTimer-=mMaxTime;
			if(mFrameCount%FPS_REFRESH_FRAMES==0) {
				if(mFPSStartTime>0)
					mFPS = 1f/((curTime-mFPSStartTime)*0.001f)*FPS_REFRESH_FRAMES;
				mFPSStartTime = curTime;
			}
		}else{
			mShaderTimer = 0;
			mTimer = 0;
			mFPSStartTime = -1;
		}
		mLstTimestamp = curTime;
	}
	
	public void beginFrame() {
		mFrameCount++;
		measureTime();
		mPolygonCount = 0;
		mDynamicPolygonCount = 0;
		mBatchPolygonCount = 0;
		mDrawCount = 0;
		mFlushCount = 0;
		mBatchCount = 0;
		mTexBindCount = 0;
		mShaderSwitchCount = 0;
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
	
	public final void clear(float r, float g, float b,int additionalMask) {
		setClearColor(r,g,b,1);
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
		mPolygonCount += vertexCount/3;
		mDrawCount++;
		ShortBuffer indexBuffer = mCurrentVertexBuffer.mIndexBuffer;
		indexBuffer.position(bufferStart);	

		if(mForceWireFrames) {
			int cap = indexBuffer.capacity();
			if(mWireFrameIndexBuffer==null || mWireFrameIndexBuffer.capacity()<cap*2)
				mWireFrameIndexBuffer = ByteBuffer.allocateDirect(cap*2*2).order(ByteOrder.nativeOrder()).asShortBuffer();
			mWireFrameIndexBuffer.position(0);
			for(int i=0;i<vertexCount;i+=3) {
				short first = indexBuffer.get();
				mWireFrameIndexBuffer.put(first);
				short to =  indexBuffer.get();
				mWireFrameIndexBuffer.put(to);
				mWireFrameIndexBuffer.put(to);
				to = indexBuffer.get();
				mWireFrameIndexBuffer.put(to);
				mWireFrameIndexBuffer.put(to);
				mWireFrameIndexBuffer.put(first);
			}
			indexBuffer.position(0);
			indexBuffer = mWireFrameIndexBuffer;
			indexBuffer.position(0);
			bufferStart = 0;
			vertexCount = vertexCount*2;
		}
		if(!mCurrentVertexBuffer.draw(bufferStart, vertexCount, mode)) {
			drawDefaultVertices(bufferStart,vertexCount,mForceWireFrames,indexBuffer);
		}
	}
	
	public void setVertexBuffer(IndexedVertexBuffer vertexBuffer) {
		assert preCheck("Set vertex buffer");
		mCurrentVertexBuffer = vertexBuffer;
		vertexBuffer.setAsCurrent();
	}
	
	public void setSurfaceSize(int width, int height) {
		this.mScreenWidth = width;
		this.mScreenHeight = height;
		this.mRatioX = (float) width / height;
		if(mRatioX<mMinRatioX){
			this.mRatioY = 1/mRatioX;
			mRatioX = 1;
		}else
			mRatioY = 1;
		mInvRatioX = 1/mRatioX;
		mInvRatioY = 1/mRatioY;
		mProjScreenTransform.setOrthogonalProjection(-mRatioX, mRatioX, mRatioY, -mRatioY);
		mProjScreenTransform.refreshInverted();
		setViewPort(width,height);
		for(SurfaceListener surfaceListener:mScreenListeners) {
			surfaceListener.onSurfaceSizeChanged(width, height);
		}
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
	
	public TextureRenderTarget createRenderTarget(int width,int height,TextureProperties textureSettings) {
		Texture texture = createEmptyTexture(width,height,textureSettings);
		TextureRenderTarget result = derivedCreateRenderTarget(texture);
		mRenderTargets.add(result);
		return result;
	}
	
	public void setScreenRenderTarget() {
		flush();
		mCurrentScreen = this;
		setViewPort(mScreenWidth,mScreenHeight);
		derivedSetScreenRenderTarget();
		unbindTextures();
		assert checkErrorInst("Set screen render target");
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
		mShaderTimer = 0;
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
