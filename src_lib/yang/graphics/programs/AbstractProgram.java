package yang.graphics.programs;

import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.GraphicsTranslator;

public abstract class AbstractProgram {

	public GLProgram mProgram;
	public int mTextureLevelCount;
	public boolean mInitialized = false;
	
	protected GraphicsTranslator mGraphics;
	protected AbstractGFXLoader mGFXLoader;
	
	protected void initHandles() {
		
	}
	
	protected void postInit() {
		
	}
	
	public final AbstractProgram init(GraphicsTranslator graphics,String vertexShaderCode,String fragmentShaderCode) {
		mTextureLevelCount = 0;
		mGraphics = graphics;
		mGFXLoader = graphics.mGFXLoader;
		mProgram = graphics.createProgram();
		mProgram.compile(vertexShaderCode, fragmentShaderCode,this);
		
		initHandles();
		postInit();
		
		mInitialized = true;
		return this;
	}
	
	public final AbstractProgram init(GraphicsTranslator graphics) {
		return init(graphics,getVertexShader(graphics.mGFXLoader),getFragmentShader(graphics.mGFXLoader));
	}
	
	public void restart() {
		init(mGraphics);
	}

	protected abstract String getVertexShader(AbstractGFXLoader gfxLoader);
	protected abstract String getFragmentShader(AbstractGFXLoader gfxLoader);

	public GLProgram getProgram() {
		return mProgram;
	}

	public void activate() {
		mGraphics.mCurrentProgram = this;
		mProgram.activate();
	}
	
	public void prepareDraw() {
		
	}
	
}
