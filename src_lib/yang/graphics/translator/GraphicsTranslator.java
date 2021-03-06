package yang.graphics.translator;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import yang.events.Keys;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.buffers.UniversalVertexBuffer;
import yang.graphics.camera.projection.OrthogonalProjection;
import yang.graphics.listeners.DrawListener;
import yang.graphics.listeners.SurfaceListener;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.AbstractProgram;
import yang.graphics.programs.BasicProgram;
import yang.graphics.programs.GLProgramFactory;
import yang.graphics.textures.TextureData;
import yang.graphics.textures.TextureHolder;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.glconsts.GLBlendFuncs;
import yang.graphics.translator.glconsts.GLDrawModes;
import yang.graphics.translator.glconsts.GLMasks;
import yang.graphics.translator.glconsts.GLOps;
import yang.graphics.translator.glconsts.GLTex;
import yang.math.objects.Bounds;
import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;
import yang.model.DebugYang;
import yang.model.SurfaceParameters;
import yang.model.TransformationFactory;
import yang.model.enums.ByteFormat;
import yang.util.YangList;

public abstract class GraphicsTranslator implements TransformationFactory,GLProgramFactory,SurfaceParameters {

	public static int MAX_NESTED_RENDERTARGETS = 128;

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
	private final YangList<SurfaceListener> mScreenListeners;
	public float mMinRatioX = 1;
	public float mMaxTime = 60;
	private int mMaxTextureId = -1;

	//Debug camera
	private YangMatrix mDebugPostCameraTransform = new YangMatrix();
	private YangMatrix mDebugPostCameraTransformRef = null;
	private Point3f mDebugCamShift = new Point3f();
	private Quaternion mDebugCamOrientation = new Quaternion();

	//State
	private boolean mInitialized = false;
	public Texture[] mCurrentTextures;
	public IndexedVertexBuffer mCurrentVertexBuffer;
	public boolean mFlushDisabled;
	public int mDrawMode;
	public DrawListener mCurDrawListener;
	public SurfaceParameters mCurrentSurface;
	public float mTimer;
	public float mShaderTimer;
	private long mLstTimestamp;
	public float mCurFrameDeltaTime = 0;
	public float mFPS = 0;
	private long mFPSStartTime = 0;
	public long mFrameCount = 0;
	public boolean mForceWireFrames = false;
	private long mTargetTime = 0;
	private final TextureRenderTarget[] mRenderTargetStack = new TextureRenderTarget[MAX_NESTED_RENDERTARGETS];
	private int mRenderTargetStackPos = -1;
	public float mMinForceWireFrameRenderTargetDepth = -1;

	//Matrices
	public YangMatrix mProjScreenTransform;
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
	final Texture mNoTexture;
	public AbstractGFXLoader mGFXLoader;
	private final YangList<TextureRenderTarget> mRenderTargets;
	private final YangList<Texture> mRegisteredTextures;
	private final YangList<AbstractProgram> mPrograms;
	private IntBuffer mWireFrameIndexBuffer;
	private long mMinDrawFrameIntervalNanos;

	//Helpers
	protected final int[] mTempInt = new int[1];
	protected final int[] mTempInt2 = new int[1];
	protected final int[] mTempIntArray = new int[128];
	protected final int[] mScreenBoundsInt = new int[4];
	public int mRestartCount = 0;
	public AbstractProgram mCurrentProgram = null;
	public YangMatrix mSensorOrientationMatrix = new YangMatrix();

	public abstract void setClearColor(float r, float g, float b,float a);
	public abstract void clear(int mask);
	protected abstract void genTextures(int[] target,int count);
	public abstract void derivedSetTextureData(int texId,int width,int height,ByteBuffer buffer,TextureProperties properties);
	public abstract void setTextureRectData(int texId,int level,int offsetX,int offsetY,int width,int height,int channels, ByteBuffer data);
	public abstract void setTextureParameter(int pName,int param);
	public abstract void deleteTextures(int[] ids);
	protected abstract void drawDefaultVertices(int bufferStart, int vertexCount, int mode, Buffer indexBuffer,boolean intIndices);
	public abstract void derivedSetAttributeBuffer(int handle,int bufferIndex,IndexedVertexBuffer vertexBuffer);
	public abstract void enableAttributePointer(int handle);
	public abstract void disableAttributePointer(int handle);
	protected abstract void setViewPort(int width,int height);
	public abstract void setCullMode(boolean drawClockwise);
	protected abstract void derivedSetScreenRenderTarget();
	public abstract void lineWidth(float width);
	public abstract void initRenderTarget(TextureRenderTarget target);
	protected abstract void derivedSetTextureRenderTarget(TextureRenderTarget renderTarget);
	public abstract void setDepthFunction(boolean less,boolean equal);
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
	public abstract void switchZWriting(boolean enable);
	public abstract void polygonOffset(float factor,float units);
	public abstract void depthRange(float zNear,float zFar);
	protected abstract void deleteBuffers(int[] bufIds);
	protected abstract void deleteFrameBuffers(int[] bufIds);

	public TextureDisplay createTextureDisplay(boolean undecorated) { return null; }
	protected void postInit() { }
	public void setSystemCursorEnabled(boolean enabled){ }
	public int getNumberOfScreens() { return 1; }
	public int getMainScreenId() { return 0; }

	//TODO reset program and texture
	public void setTempContext(GLHolder holder) { }

	public void resetContext() { }

	//TODO glColorMask

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
		return checkErrorInst(message,true);
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
		mProjScreenTransform = new YangMatrix();
		mStaticTransformation = createTransformationMatrix();
		mStaticTransformation.loadIdentity();
		mFlushDisabled = false;
		mPolygonCount = 0;
		mFlushCount = 0;
		mDrawMode = GLDrawModes.TRIANGLES;
		mPrograms = new YangList<AbstractProgram>();
		mCurDrawListener = null;
		appInstance = this;
		setSurfaceParameters(this);
		mNoTexture = new Texture(this);
		mScreenListeners = new YangList<SurfaceListener>();
		mRenderTargets = new YangList<TextureRenderTarget>();
		mRegisteredTextures = new YangList<Texture>();
		setMaxFPS(0);
	}

	public void deleteRenderTarget(TextureRenderTarget renderTarget) {
		assert preCheck("Delete render target");
		mTempInt[0] = renderTarget.mFrameBufferId;
		deleteBuffers(mTempInt);
		assert checkErrorInst("Delete frame buffer");
		mTempInt[0] = renderTarget.mDepthBufferId;
		deleteFrameBuffers(mTempInt);
		assert checkErrorInst("Delete depth buffer");
		mRenderTargets.remove(renderTarget);
		mRegisteredTextures.remove(renderTarget.mTargetTexture);
		renderTarget.mTargetTexture.free();
	}

	public void addScreenListener(SurfaceListener listener) {
		mScreenListeners.add(listener);
	}

	@Override
	public YangMatrix createTransformationMatrix() {
		return new YangMatrix();
	}

	private void start() {
		mRenderTargetStackPos = -1;
		mThreadId = Thread.currentThread().getId();
		assert preCheck("Start graphics translator");
		final int DIM = 2;
		final int BYTES = DIM*DIM*4;
		ByteBuffer buf = ByteBuffer.allocateDirect(BYTES);
		for(int i=0;i<BYTES;i++)
			buf.put((byte)255);
		if(mWhiteTexture==null)
			mWhiteTexture = createAndInitTexture(buf, DIM,DIM, new TextureProperties(TextureWrap.CLAMP,TextureFilter.NEAREST));
		else{
			mWhiteTexture.update(buf);
		}
		buf = ByteBuffer.allocateDirect(BYTES);
		for(int i=0;i<BYTES;i++)
			if(i%4==3)
				buf.put((byte)255);
			else
				buf.put((byte)0);
		if(mBlackTexture==null)
			mBlackTexture = createAndInitTexture(buf, DIM,DIM, new TextureProperties(TextureWrap.CLAMP,TextureFilter.NEAREST));
		else{
			mBlackTexture.update(buf);
		}
		assert checkErrorInst("Create def textures");

		enable(GLOps.BLEND);
		setBlendFunction(GLBlendFuncs.ONE,GLBlendFuncs.ONE_MINUS_SRC_ALPHA);
		switchCulling(false);
		setCullMode(false);
		mLstTimestamp = -1;
	}

	public final void init() {
		if(mInitialized)
			return;
		mInitialized = true;
		start();

		postInit();
		assert checkErrorInst("Start graphics translator");
	}

	public void restart() {
		for(final AbstractProgram program:mPrograms) {
			program.restart();
		}
		for(final Texture texture:mRegisteredTextures) {
			texture.generate();
			texture.resetData();
		}
		start();
		for(final TextureRenderTarget renderTarget:mRenderTargets) {
			initRenderTarget(renderTarget);
		}
		mRestartCount++;
	}


	public int genTexture() {
		genTextures(mTempInt,1);
		if(mTempInt[0]>mMaxTextureId)
			mMaxTextureId = mTempInt[0];
		return mTempInt[0];
	}

	public int getMaxTexId() {
		return mMaxTextureId;
	}

	public void setTextureData(int texId,int width,int height, ByteBuffer buffer, TextureProperties textureProperties) {
		derivedSetTextureData(texId,width,height,buffer,textureProperties);

		switch(textureProperties.mWrapX) {
		case CLAMP: setTextureParameter(GLTex.GL_TEXTURE_WRAP_S, GLTex.GL_CLAMP_TO_EDGE); break;
		case REPEAT: setTextureParameter(GLTex.GL_TEXTURE_WRAP_S, GLTex.GL_REPEAT); break;
		case MIRROR: setTextureParameter(GLTex.GL_TEXTURE_WRAP_S, GLTex.GL_MIRRORED_REPEAT); break;
		}
		switch(textureProperties.mWrapY) {
		case CLAMP: setTextureParameter(GLTex.GL_TEXTURE_WRAP_T, GLTex.GL_CLAMP_TO_EDGE); break;
		case REPEAT: setTextureParameter(GLTex.GL_TEXTURE_WRAP_T, GLTex.GL_REPEAT); break;
		case MIRROR: setTextureParameter(GLTex.GL_TEXTURE_WRAP_T, GLTex.GL_MIRRORED_REPEAT); break;
		}
		assert checkErrorInst("Set texture wrap");

		switch(textureProperties.mFilter) {
		case NEAREST:
			setTextureParameter(GLTex.GL_TEXTURE_MAG_FILTER, GLTex.GL_NEAREST);
			setTextureParameter(GLTex.GL_TEXTURE_MIN_FILTER, GLTex.GL_NEAREST);
			break;
		default:
			setTextureParameter(GLTex.GL_TEXTURE_MAG_FILTER, GLTex.GL_LINEAR);
			setTextureParameter(GLTex.GL_TEXTURE_MIN_FILTER, GLTex.GL_LINEAR);
			break;
		case LINEAR_MIP_LINEAR:
			setTextureParameter(GLTex.GL_TEXTURE_MAG_FILTER, GLTex.GL_LINEAR);
			setTextureParameter(GLTex.GL_TEXTURE_MIN_FILTER, GLTex.GL_LINEAR_MIPMAP_LINEAR);
			break;
		case NEAREST_MIP_LINEAR:
			setTextureParameter(GLTex.GL_TEXTURE_MAG_FILTER, GLTex.GL_LINEAR);
			setTextureParameter(GLTex.GL_TEXTURE_MIN_FILTER, GLTex.GL_NEAREST_MIPMAP_LINEAR);
			break;
		}
		assert checkErrorInst("Set texture filter ("+textureProperties.mFilter+")");
	}

	public void setTextureData(Texture targetTexture,ByteBuffer data) {
		setTextureData(targetTexture.getId(),targetTexture.getWidth(),targetTexture.getHeight(),data,targetTexture.mProperties);
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

	@Override
	public int getSurfaceWidth() {
		return mScreenWidth;
	}

	@Override
	public int getSurfaceHeight() {
		return mScreenHeight;
	}

	@Override
	public float getSurfaceRatioX() {
		return mRatioX;
	}

	@Override
	public float getSurfaceRatioY() {
		return mRatioY;
	}

	public float toNormX(float surfaceX) {
		return (surfaceX/mCurrentSurface.getSurfaceWidth()-0.5f)  * 2*mCurrentSurface.getSurfaceRatioX();
	}

	public float toNormY(float surfaceY) {
		return -(surfaceY/mCurrentSurface.getSurfaceHeight()-0.5f) * 2*mCurrentSurface.getSurfaceRatioY();
	}

	public IndexedVertexBuffer createUninitializedVertexBuffer(boolean dynamicVertices,boolean dynamicIndices,int maxIndices,int maxVertices) {
		return new UniversalVertexBuffer(dynamicVertices,dynamicIndices,maxIndices,maxVertices);
	}

	public Texture createSingleColorTexture(int width,int height,TextureProperties texProperties,FloatColor fillColor) {
		final Texture texture = createTexture();
		if(fillColor==null) {
			texture.initCompletely(null, width, height, texProperties);
		}else{
			texture.initCompletely(TextureData.createSingleColorBuffer(width,height,texProperties,fillColor), width, height, texProperties);
		}

		return texture;
	}

	public Texture createEmptyTexture(int width,int height,TextureProperties texProperties) {
		final Texture texture = createTexture();
		texture.initCompletely(null, width,height, texProperties);
		return texture;
	}

	public Texture createSingleColorTexture(FloatColor color) {
		return createSingleColorTexture(2,2,new TextureProperties(TextureWrap.CLAMP,TextureFilter.NEAREST),color);
	}

	public void registerTexture(Texture texture) {
		if(!mRegisteredTextures.contains(texture))
			mRegisteredTextures.add(texture);
	}

	public void unregisterTexture(Texture texture) {
		mRegisteredTextures.remove(texture);
	}

	public Texture createTexture(TextureProperties properties) {
		final Texture result = new Texture(this,properties);
		registerTexture(result);
		return result;
	}

	public Texture createTexture() {
		return createTexture(null);
	}

	public Texture createAndInitTexture(ByteBuffer source, int width, int height, TextureProperties settings) {
		source.rewind();
		final Texture result = createTexture();
		result.initCompletely(source,width,height,settings);
		return result;
	}

	public Texture createAndInitTexture(TextureData textureData, TextureProperties properties) {
		properties.mChannels = textureData.mChannels;
		return createAndInitTexture(textureData.mData,textureData.mWidth,textureData.mHeight,properties);
	}

	public void setDrawListener(DrawListener drawListener) {
		mCurDrawListener = drawListener;
	}

	public <ShaderType extends AbstractProgram> ShaderType addProgram(ShaderType program) {
		assert preCheck("Add program");
		program.init(this);
		mPrograms.add(program);
		assert checkErrorInst("Add program");
		return program;
	}

	public <ShaderType extends BasicProgram> ShaderType addProgram(Class<ShaderType> program) {

		try {
			return addProgram(program.newInstance());
		} catch (final InstantiationException e) {
			throw new RuntimeException(e);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <ShaderType extends AbstractProgram> ShaderType getProgram(Class<ShaderType> programClass) {
		for(AbstractProgram program:mPrograms) {
			if(program.getClass()==programClass)
				return (ShaderType)program;
		}
		return null;
	}

	public void flush() {
		assert checkErrorInst("PRE flush");
		if(mFlushDisabled || mCurrentVertexBuffer==null)
			return;
		final int vertexCount = mCurrentVertexBuffer.getCurrentIndexWriteCount();
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

	public void setMaxFPS(int fps) {
		if(fps<=0)
			fps = Integer.MAX_VALUE;
		else
			DebugYang.FPS_BAR_MAX_FRAMES = fps;
		mMinDrawFrameIntervalNanos = 1000000000/fps;
	}

	public final void measureTime() {
		mTargetTime += mMinDrawFrameIntervalNanos;
		long curTime = System.nanoTime();
		if(mLstTimestamp>0) {
			if(mTargetTime<curTime)
				mTargetTime=curTime;
			else{
				try {
					Thread.sleep((long) ((mTargetTime-curTime)*0.000001));
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				curTime = System.nanoTime();
			}

			final float TO_SEC = 0.000000001f;

			mCurFrameDeltaTime = (curTime-mLstTimestamp)*TO_SEC;
//			if(mCurFrameDeltaTime<mMinDrawFrameInterval) {
//				try {
//					Thread.sleep((long) ((mMinDrawFrameInterval-mCurFrameDeltaTime)*1000));
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				mCurFrameDeltaTime = mMinDrawFrameInterval;
//				curTime = System.nanoTime();
//			}
			mTimer += mCurFrameDeltaTime;
			mShaderTimer += mCurFrameDeltaTime;
			if(mShaderTimer>mMaxTime)
				mShaderTimer-=mMaxTime;
			if(mFrameCount%FPS_REFRESH_FRAMES==0) {
				if(mFPSStartTime>0)
					mFPS = 1f/((curTime-mFPSStartTime)*TO_SEC)*FPS_REFRESH_FRAMES;
				mFPSStartTime = curTime;
			}
		}else{
			mShaderTimer = 0;
			mTimer = 0;
			mFPSStartTime = -1;
			mTargetTime = curTime;
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
		assert preCheck("clear");
		setClearColor(r,g,b,1);
		clear(GLMasks.COLOR_BUFFER_BIT);
	}

	public final void clear(float r, float g, float b,float a,int additionalMask) {
		assert preCheck("clear");
		setClearColor(r,g,b,a);
		clear(GLMasks.COLOR_BUFFER_BIT | additionalMask);
	}

	public final void clear(FloatColor color) {
		clear(color.mValues[0],color.mValues[1],color.mValues[2],color.mValues[3],0);
	}

	public final void clear(float r, float g, float b,int additionalMask) {
		assert preCheck("clear");
		setClearColor(r,g,b,1);
		clear(GLMasks.COLOR_BUFFER_BIT | additionalMask);
	}

	public void clear(FloatColor color,int additionalMask) {
		assert preCheck("clear");
		clear(color.mValues[0], color.mValues[1], color.mValues[2],color.mValues[3],additionalMask);
	}
	
	public void clear(FloatColor color,float alpha,int additionalMask) {
		assert preCheck("clear");
		clear(color.mValues[0], color.mValues[1], color.mValues[2],alpha,additionalMask);
	}

	public void setAttributeBuffer(int handle,int bufferIndex) {
		if(!mCurrentVertexBuffer.bindBuffer(handle,bufferIndex)) {
			derivedSetAttributeBuffer(handle,bufferIndex,mCurrentVertexBuffer);
		}
	}

	//TODO glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

	public void drawVertices(int bufferStart, int vertexCount,int mode) {
		assert preCheck("Draw vertices");
		mPolygonCount += vertexCount/3;
		mDrawCount++;
		Buffer indexBuffer = mCurrentVertexBuffer.getActiveIndexBuffer();
		indexBuffer.position(bufferStart);

		boolean forceWireFrames = mode==GLDrawModes.TRIANGLES && mForceWireFrames && mRenderTargetStackPos>=mMinForceWireFrameRenderTargetDepth;
		if(forceWireFrames) {
			final int cap = indexBuffer.capacity();
			if(mWireFrameIndexBuffer==null || mWireFrameIndexBuffer.capacity()<cap*2)
				mWireFrameIndexBuffer = ByteBuffer.allocateDirect(cap*4*2).order(ByteOrder.nativeOrder()).asIntBuffer();
			mWireFrameIndexBuffer.position(0);
			if(mCurrentVertexBuffer.isIntMode()) {
				IntBuffer sIndexBuffer = (IntBuffer)indexBuffer;
				for(int i=0;i<vertexCount;i+=3) {
					final int first = sIndexBuffer.get();
					mWireFrameIndexBuffer.put(first);
					int to =  sIndexBuffer.get();
					mWireFrameIndexBuffer.put(to);
					mWireFrameIndexBuffer.put(to);
					to = sIndexBuffer.get();
					mWireFrameIndexBuffer.put(to);
					mWireFrameIndexBuffer.put(to);
					mWireFrameIndexBuffer.put(first);
				}
			}else{
				ShortBuffer sIndexBuffer = (ShortBuffer)indexBuffer;
				for(int i=0;i<vertexCount;i+=3) {
					final short first = sIndexBuffer.get();
					mWireFrameIndexBuffer.put(first);
					short to =  sIndexBuffer.get();
					mWireFrameIndexBuffer.put(to);
					mWireFrameIndexBuffer.put(to);
					to = sIndexBuffer.get();
					mWireFrameIndexBuffer.put(to);
					mWireFrameIndexBuffer.put(to);
					mWireFrameIndexBuffer.put(first);
				}
			}

			indexBuffer.position(0);
			indexBuffer = mWireFrameIndexBuffer;
			indexBuffer.position(0);
			bufferStart = 0;
			vertexCount = vertexCount*2;
			mode = GLDrawModes.LINELIST;
		}
		if(!mCurrentVertexBuffer.draw(bufferStart, vertexCount, mode)) {
			drawDefaultVertices(bufferStart,vertexCount, mode,indexBuffer,mCurrentVertexBuffer.isIntMode() || forceWireFrames);
		}
	}

	public void drawBuffer(IndexedVertexBuffer buffer,int bufferStart,int vertexCount,int mode) {
		final IndexedVertexBuffer prevBuffer = mCurrentVertexBuffer;
		setVertexBuffer(buffer);
		prepareDraw();
		drawVertices(bufferStart,vertexCount,mode);
		setVertexBuffer(prevBuffer);
	}

	public void drawBufferDirectly(IndexedVertexBuffer buffer,int bufferStart,int vertexCount,int mode) {
		final IndexedVertexBuffer prevBuffer = mCurrentVertexBuffer;
		mCurrentVertexBuffer = buffer;
		drawVertices(bufferStart,vertexCount,mode);
		mCurrentVertexBuffer = prevBuffer;
	}

	public void drawBuffer(IndexedVertexBuffer buffer) {
		drawBuffer(buffer,0,buffer.getIndexCount(),GLDrawModes.TRIANGLES);
	}

	public void setVertexBuffer(IndexedVertexBuffer vertexBuffer) {
		assert preCheck("Set vertex buffer");
		mCurrentVertexBuffer = vertexBuffer;
		vertexBuffer.setAsCurrent();
	}

	private void refreshProjScreenTransform() {
		final float rx = mCurrentSurface.getSurfaceRatioX();
		final float ry = mCurrentSurface.getSurfaceRatioY();
		OrthogonalProjection.getTransform(mProjScreenTransform,-rx,rx, ry,-ry, -1,1);
		mProjScreenTransform.refreshInverted();
	}

	public void setSurfaceSize(int width, int height) {
		setViewPort(width,height);

		this.mScreenWidth = width;
		this.mScreenHeight = height;
		this.mRatioX = (float) width / height;

		if(mRatioX<mMinRatioX){
			mRatioY = mMinRatioX/mRatioX;
			mRatioX = mMinRatioX;
		}else
			mRatioY = 1;

		mInvRatioX = 1/mRatioX;
		mInvRatioY = 1/mRatioY;

		refreshProjScreenTransform();

		for(final SurfaceListener surfaceListener:mScreenListeners) {
			surfaceListener.onSurfaceSizeChanged(width, height);
		}
	}

	protected void updateTexture(Texture texture, ByteBuffer source, int left,int top, int width,int height) {

	}

	protected void deleteTexture(int id) {
		assert preCheck("Delete texture");
		mTempInt[0] = id;
		deleteTextures(mTempInt);
		assert checkErrorInst("Delete texture");
	}

	public void deleteAllTextures() {
		for(int i=0;i<=mMaxTextureId;i++) {
			//setTextureData(i, 2, 2, buf, new TextureProperties());
			deleteTexture(i);
		}
	}

	public TextureRenderTarget createRenderTarget(Texture texture,boolean useScreenParameters) {
		final TextureRenderTarget result = new TextureRenderTarget(texture);
		initRenderTarget(result);
		result.setUseScreenParameters(useScreenParameters);
		mRenderTargets.add(result);
		return result;
	}

	public TextureRenderTarget createRenderTarget(int width,int height,TextureProperties textureSettings,boolean useScreenParameters) {
		final Texture texture = createEmptyTexture(width,height,textureSettings);
		return createRenderTarget(texture, useScreenParameters);
	}

	public TextureRenderTarget createRenderTarget(int width,int height,TextureProperties textureSettings) {
		return createRenderTarget(width, height, textureSettings,false);
	}

	public TextureRenderTarget createRenderTarget(int width,int height) {
		return createRenderTarget(width,height,new TextureProperties(TextureWrap.CLAMP,TextureFilter.LINEAR));
	}

	public TextureRenderTarget createRenderTarget(int widthAndHeight) {
		return createRenderTarget(widthAndHeight,widthAndHeight);
	}

	public void resizeRenderTarget(TextureRenderTarget renderTarget, int width,int height) {
		assert preCheck("Delete render target");
		mTempInt[0] = renderTarget.mFrameBufferId;
		deleteBuffers(mTempInt);
		assert checkErrorInst("Delete frame buffer");
		mTempInt[0] = renderTarget.mDepthBufferId;
		deleteFrameBuffers(mTempInt);
		assert checkErrorInst("Delete depth buffer");
		renderTarget.mTargetTexture.resize(width,height);
		initRenderTarget(renderTarget);
		assert checkErrorInst("Resize render target");
	}

	private void setSurfaceParameters(SurfaceParameters surface) {
		if(surface==mCurrentSurface)
			return;
		mCurrentSurface = surface;
		refreshProjScreenTransform();
	}

	private void setRenderTarget(TextureRenderTarget renderTarget) {
		flush();
		setSurfaceParameters(renderTarget.mSurfaceParameters);
		setViewPort(renderTarget.mTargetTexture.mWidth,renderTarget.mTargetTexture.mHeight);
		derivedSetTextureRenderTarget(renderTarget);
		unbindTextures();
		assert checkErrorInst("Set texture render target");
	}

	public void setTextureRenderTarget(TextureRenderTarget renderTarget) {
		mRenderTargetStack[++mRenderTargetStackPos] = renderTarget;
		if(renderTarget.mKeepSurfaceParameters)
			renderTarget.mSurfaceParameters = mCurrentSurface;
		setRenderTarget(renderTarget);
	}

	public void setTextureRenderTargetOnlySurfaceValues(TextureRenderTarget renderTarget) {
		flush();
		mRenderTargetStack[++mRenderTargetStackPos] = renderTarget;
		setSurfaceParameters(renderTarget);
	}

	public void leaveTextureRenderTarget() {
		flush();
		if(mRenderTargetStackPos>0) {
			setRenderTarget(mRenderTargetStack[mRenderTargetStackPos-1]);
			mRenderTargetStackPos--;
			unbindTextures();
		}else{
			mRenderTargetStackPos = -1;
			setSurfaceParameters(this);
//			if(mStereo)
//				setViewPort(mScreenWidth*2,mScreenHeight);
//			else
				setViewPort(mScreenWidth,mScreenHeight);
			derivedSetScreenRenderTarget();
			unbindTextures();
			assert checkErrorInst("Set screen render target");
		}
	}

	/**
	 * Ignores the render target stack and sets the screen as render target. Call only, if absolutely necessary!
	 */
	public void enforceScreenRenderTarget() {
		mRenderTargetStackPos = 0;
		leaveTextureRenderTarget();
	}

	public YangMatrix getStaticTransformation() {
		mStaticTransformation.loadIdentity();
		return mStaticTransformation;
	}

	public void readPixels(ByteBuffer target,int channels,ByteFormat byteFormat) {
		readPixels(0,0,mCurrentSurface.getSurfaceWidth(),mCurrentSurface.getSurfaceHeight(),channels,byteFormat,target);
	}

	public void readPixels(ByteBuffer target) {
		readPixels(0,0,mCurrentSurface.getSurfaceWidth(),mCurrentSurface.getSurfaceHeight(),4,ByteFormat.UNSIGNED_BYTE,target);
	}

//	public ByteBuffer createByteBufferWithScreenData(int channels,ByteFormat byteFormat) {
//		final ByteBuffer screenData = ByteBuffer.allocateDirect(mScreenWidth*mScreenHeight*channels*byteFormatBytes(byteFormat));
//		readPixels(screenData,channels,byteFormat);
//		return screenData;
//	}
//
//	public ByteBuffer createByteBufferWithScreenData() {
//		return createByteBufferWithScreenData(4,ByteFormat.UNSIGNED_BYTE);
//	}

	public void resetTimer() {
		mTimer = 0;
		mShaderTimer = 0;
		mLstTimestamp = -1;
	}

	public void setScissorRectNormalized(float x,float y,float width,float height) {
		x = x*0.5f/mCurrentSurface.getSurfaceRatioX()+0.5f;
		y = y*0.5f/mCurrentSurface.getSurfaceRatioY()+0.5f;
		final int w = mCurrentSurface.getSurfaceWidth();
		final int h = mCurrentSurface.getSurfaceHeight();
		setScissorRectI((int)(x*w),(int)(y*h),(int)(width*0.5f/mCurrentSurface.getSurfaceRatioX()*w),(int)(height*0.5f/mCurrentSurface.getSurfaceRatioY()*h));
		enable(GLOps.SCISSOR_TEST);
	}

	public void setScissorRectNormalized(Bounds mBounds) {
		assert preCheck("Set scissor");
		setScissorRectNormalized(mBounds.mValues[0],mBounds.mValues[1],mBounds.mValues[2]-mBounds.mValues[0],mBounds.mValues[3]-mBounds.mValues[1]);
		assert checkErrorInst("Set scissor");
	}

	public void switchScissor(boolean enabled) {
		assert preCheck("Switch scissor");
		if(enabled)
			enable(GLOps.SCISSOR_TEST);
		else
			disable(GLOps.SCISSOR_TEST);
		assert checkErrorInst("Switch scissor");
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

	public boolean isScreenRenderTarget() {
		return mRenderTargetStackPos==-1;
	}

	public void disableBuffers() {
		mCurDrawListener.disableBuffers();
	}

	public void enableBuffers() {
		mCurDrawListener.enableBuffers();
	}
	public void bindBuffers() {
		mCurDrawListener.bindBuffers();
	}

	public int getRenderTargetStackLevel() {
		return mRenderTargetStackPos;
	}

	private void refreshDebugCamera() {
		mDebugPostCameraTransform.setTranslation(mDebugCamShift);
		mDebugPostCameraTransform.multiplyQuaternionRight(mDebugCamOrientation);
	}

	public void setDebugPostCameraPosition(Point3f position) {
		mDebugCamShift.set(position);
		refreshDebugCamera();
	}

	public void setDebugPostCameraOrientation(Quaternion orientation) {
		mDebugCamOrientation.set(orientation);
		refreshDebugCamera();
	}

	public void setDebugPostCameraOrientation(YangMatrix orientation) {
		mDebugCamOrientation.setFromMatrix(orientation.mValues);
		refreshDebugCamera();
	}

	public void moveDebugPostCamera(Vector3f shift,Quaternion orientation) {
		if(shift!=null)
			mDebugCamShift.add(shift);
		if(orientation!=null)
			mDebugCamOrientation.mult(orientation);
		refreshDebugCamera();
	}

	public void setDebugPostCameraEnabled(boolean enabled) {
		mDebugPostCameraTransformRef = enabled?mDebugPostCameraTransform:null;
	}

	@Override
	public YangMatrix getViewPostTransform() {
		return mDebugPostCameraTransformRef;
	}

	public int getDefaultMetaBaseKey() {
		return Keys.F1;
	}

	public final TextureDisplay createTextureDisplay(Texture texture,boolean undecorated) {
		TextureDisplay result = createTextureDisplay(undecorated);
		result.setTexture(texture);
		GLHolder glHolder = result.getGLHolder();
		if(texture!=null)
			glHolder.setBounds(0,0, texture.mWidth,texture.mHeight);
		glHolder.run();
		return result;
	}

	public final TextureDisplay createTextureDisplay(Texture texture,String title) {
		TextureDisplay result = createTextureDisplay(texture,false);
		result.getGLHolder().setTitle(title);
		return result;
	}

	public final TextureDisplay createTextureDisplay(Texture texture) {
		return createTextureDisplay(texture,false);
	}

	public final TextureDisplay createTextureDisplayFullscreen(Texture texture,String title,int screenId) {
		TextureDisplay result = createTextureDisplay(texture,true);
		if(result==null)
			return null;
		GLHolder glHolder = result.getGLHolder();
		glHolder.setTitle(title);
		glHolder.setAlwaysOnTop(true);
		glHolder.setFullscreen(screenId);
		return result;
	}

	public int[] getScreenBounds(int screenId) {
		if(screenId!=0)
			return null;
		else{
			mScreenBoundsInt[0] = 0;
			mScreenBoundsInt[1] = 1;
			mScreenBoundsInt[2] = mScreenWidth;
			mScreenBoundsInt[3] = mScreenHeight;
			return mScreenBoundsInt;
		}
	}

	public int getScreenWidth(int screenId) {
		return getScreenBounds(screenId)[2];
	}

	public int getScreenHeight(int screenId) {
		return getScreenBounds(screenId)[3];
	}

	public int getScreenWidth() {
		return getScreenWidth(getMainScreenId());
	}

	public int getScreenHeight() {
		return getScreenHeight(getMainScreenId());
	}

	public GLHolder getMainDisplay() {
		return null;
	}

	public int normXToScreenX(float x) {
		return (int)((x/getSurfaceRatioX()+1)*0.5f*getSurfaceWidth());
	}

	public int normYToScreenY(float y) {
		return (int)((1-y/getSurfaceRatioY())*0.5f*getSurfaceHeight());
	}

}
