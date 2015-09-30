package yang.graphics.programs;

import yang.graphics.model.FloatColor;
import yang.graphics.translator.AbstractGFXLoader;

public class BasicProgram extends AbstractProgram {

	public static int COLOR_TEXTURE_LEVEL = 0;
	public int mColorPrecision,mPositionPrecision,mTexCoordPrecision;


	public final static String VERTEX_SHADER =
			"uniform mat4 projTransform;\r\n" +
			"uniform \\COLORP vec4 colorFactor;\r\n" +
			"attribute \\POSITIONP vec4 vPosition;\r\n" +
			"attribute \\TEXCOORDP vec2 vTexture;\r\n" +
			"attribute \\COLORP vec4 vColor;\r\n" +
			"varying \\TEXCOORDP vec2 texCoord;\r\n" +
			"varying \\COLORP vec4 color;\r\n" +
			"\r\n" +
			"void main() {\r\n" +
			"	gl_Position = projTransform * vPosition;\r\n" +
			"	texCoord = vTexture;\r\n" +
			"	color = vColor * colorFactor;\r\n" +
			"	#PREMULT color = vec4(color.r*color.a,color.g*color.a,color.b*color.a,color.a);\r\n" +
			"}\r\n";

	public final static String VERTEX_SHADER_SCREENPOS =
			"uniform mat4 projTransform;\r\n" +
			"uniform \\COLORP vec4 colorFactor;\r\n" +
			"attribute \\POSITIONP vec4 vPosition;\r\n" +
			"attribute \\TEXCOORDP vec2 vTexture;\r\n" +
			"attribute \\COLORP vec4 vColor;\r\n" +
			"varying \\TEXCOORDP vec2 texCoord;\r\n" +
			"varying \\COLORP vec4 color;\r\n" +
			"#MEDIUMP varying vec4 screenPos;\r\n" +
			"\r\n" +
			"void main() {\r\n" +
			"	gl_Position = projTransform * vPosition;\r\n" +
			"	texCoord = vTexture;\r\n" +
			"	screenPos = gl_Position;\r\n" +
			"	color = vColor * colorFactor;\r\n" +
			"	#PREMULT color = vec4(color.r*color.a,color.g*color.a,color.b*color.a,color.a);\r\n" +
			"}\r\n";

	public final static String FRAGMENT_SHADER =
			"uniform \\COLORP sampler2D texSampler;\r\n" +
			"varying \\TEXCOORDP vec2 texCoord;\r\n" +
			"varying \\COLORP vec4 color;\r\n" +
			"\r\n" +
			"void main() {\r\n" +
			"	gl_FragColor = texture2D(texSampler, texCoord) * color;\r\n" +
			"}\r\n";

	public int mProjHandle = -1;
	public int mWorldTransformHandle = -1;
	public int mNormalTransformHandle = -1;
	public int mPositionHandle = -1;
	public int mTextureHandle = -1;
	public int mColorHandle = -1;
	public int mSuppDataHandle = -1;
	public int mTexSamplerHandle = -1;
	public int mColorFactorHandle = -1;
	public int mTimeHandle = -1;
	public boolean mHasTextureCoords = false;
	public boolean mHasColor = false;
	public boolean mHasSuppData = false;
	public boolean mHasWorldTransform = false;
	public boolean mHasColorFactor = false;

	public BasicProgram(int precPosition,int precTexCoord,int precColor) {
		mPositionPrecision = precPosition;
		mTexCoordPrecision = precTexCoord;
		mColorPrecision = precColor;
	}

	public BasicProgram() {
		this(2,2,1);
	}

	protected String getSuppDataIdentifier() {
		return "vSuppData";
	}

	@Override
	protected void initHandles() {
		mPositionHandle = mProgram.getAttributeLocation("vPosition");
		mWorldTransformHandle = mProgram.getUniformLocation("worldTransform");
		mNormalTransformHandle = mProgram.getUniformLocation("normalTransform");
		mProjHandle = mProgram.getUniformLocation("projTransform");
		mTextureHandle = mProgram.getAttributeLocation("vTexture");
		mColorHandle = mProgram.getAttributeLocation("vColor");
		mSuppDataHandle = mProgram.getAttributeLocation(getSuppDataIdentifier());
		mColorFactorHandle = mProgram.getUniformLocation("colorFactor");
		mTimeHandle = mProgram.getUniformLocation("time");
		mTexSamplerHandle = mProgram.getUniformLocation("texSampler");
	}

	@Override
	protected void preInit() {
		addPrecisionVariable("POSITIONP",mPositionPrecision);
		addPrecisionVariable("TEXCOORDP",mTexCoordPrecision);
		addPrecisionVariable("COLORP",mColorPrecision);
	}

	@Override
	public void postInit() {
		mHasTextureCoords = (mTextureHandle>=0);
		mHasWorldTransform = (mWorldTransformHandle>=0);
		mHasColor = (mColorHandle>=0);
		mHasSuppData = (mSuppDataHandle>=0);
		mHasColorFactor = (mColorFactorHandle>=0);
	}

	public void setProjection(float[] mvpMatrix) {
		assert mGraphics.preCheck("Set projection");
		mProgram.setUniformMatrix4f(mProjHandle, mvpMatrix);
		assert mGraphics.checkErrorInst("Set projection");
	}

	public void setWorldTransform(float[] worldMatrix) {
		if(mHasWorldTransform)
			mProgram.setUniformMatrix4f(mWorldTransformHandle, worldMatrix);
	}

	public void setNormalTransform(float[] normalMatrix) {
		if(mNormalTransformHandle>=0)
			mProgram.setUniformMatrix3f(mNormalTransformHandle, normalMatrix);
	}

	public void setColorFactor(float[] color) {
		mProgram.setUniform4f(mColorFactorHandle, color[0], color[1], color[2], color[3]);
	}

	public void setColorFactor(float r,float g,float b) {
		mProgram.setUniform4f(mColorFactorHandle, r, g, b, 1);
	}

	public void setColorFactor(float r,float g,float b,float a) {
		mProgram.setUniform4f(mColorFactorHandle, r, g, b, a);
	}

	public void setColorFactor(FloatColor color) {
		mProgram.setUniform4f(mColorFactorHandle, color.mValues);
	}

	public void setTime(float time) {
		mProgram.setUniformFloat(mTimeHandle, time);
	}

	@Override
	public void activate() {
		super.activate();
		assert mGraphics.checkErrorInst("Activate");
		mProgram.setUniformInt(mTexSamplerHandle, 0);
		assert mGraphics.checkErrorInst("Set tex handle");
	}

	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return VERTEX_SHADER;
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return FRAGMENT_SHADER;
	}

}
