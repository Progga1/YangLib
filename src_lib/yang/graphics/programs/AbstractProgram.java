package yang.graphics.programs;

import java.util.HashMap;
import java.util.Map.Entry;

import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.GraphicsTranslator;
import yang.util.Util;

public abstract class AbstractProgram {

	public static final int PRECISION_LOW = 0;
	public static final int PRECISION_MEDIUM = 1;
	public static final int PRECISION_HIGH = 2;

	public GLProgram mProgram;
	public int mTextureLevelCount;
	public boolean mInitialized = false;
	public HashMap<String,String> mVariables = new HashMap<String,String>(16);

	protected GraphicsTranslator mGraphics;
	protected AbstractGFXLoader mGFXLoader;

	private String mFragSource,mVertSource;

	protected void initHandles() {

	}

	protected void preInit() {

	}

	protected void postInit() {

	}

	private final AbstractProgram init(GraphicsTranslator graphics,String vertexShaderCode,String fragmentShaderCode) {
		mTextureLevelCount = 0;
		mGraphics = graphics;
		mGFXLoader = graphics.mGFXLoader;
		mProgram = graphics.createProgram();
		preInit();
		for(Entry<String,String> entry:mVariables.entrySet()) {
			String key = "\\"+entry.getKey();
			vertexShaderCode = vertexShaderCode.replace(key, entry.getValue());
			fragmentShaderCode = fragmentShaderCode.replace(key, entry.getValue());
		}
		mProgram.compile(vertexShaderCode, fragmentShaderCode,this);

		initHandles();
		postInit();

		mInitialized = true;
		return this;
	}

	public final AbstractProgram init(GraphicsTranslator graphics) {
		mVertSource = getVertexShader(graphics.mGFXLoader);
		mFragSource = getFragmentShader(graphics.mGFXLoader);
		return init(graphics,mVertSource,mFragSource);
	}

	public final void addPrecisionVariable(String name,int precision) {
		if(!mProgram.hasPrecision()) {
			mVariables.put(name,"");
			return;
		}
		switch(precision) {
		case PRECISION_LOW: mVariables.put(name,"lowp");break;
		case PRECISION_MEDIUM: mVariables.put(name,"mediump");break;
		case PRECISION_HIGH: mVariables.put(name,"highp");break;
		default: throw new RuntimeException("Invalid precision: "+precision);
		}
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

	@Override
	public String toString() {
		return "--------VERTEX-SHADER--------\n\n"+Util.stringToLineNumbersString(mVertSource)+"\n\n--------FRAGMENT-SHADER--------\n\n"+Util.stringToLineNumbersString(mFragSource)+"\n";
	}


}
