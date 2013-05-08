package yang.graphics.programs;

import yang.graphics.AbstractGFXLoader;
import yang.graphics.FloatColor;

public class BasicProgram extends AbstractProgram {

	public static int COLOR_TEXTURE_LEVEL = 0;
	
	public final static String VERTEX_SHADER = 
			"uniform mat4 projTransform;\r\n" +
			"uniform float time;\r\n" +
			"uniform vec4 ambientColor;\r\n" +
			"attribute vec4 vPosition;\r\n" +
			"attribute vec2 vTexture;\r\n" +
			"attribute vec4 vColor;\r\n" +
			"varying vec2 texCoord;\r\n" +
			"varying vec4 color;\r\n" +
			"\r\n" +
			"void main() {\r\n" +
			"	gl_Position = projTransform * vPosition;\r\n" +
			"	texCoord = vTexture;\r\n" +
			"	color = vColor * ambientColor;\r\n" +
			"	#PREMULT color = vec4(color.r*color.a,color.g*color.a,color.b*color.a,color.a);\r\n" +
			"}\r\n";
	
	public final static String VERTEX_SHADER_SCREENPOS = 
			"uniform mat4 projTransform;\r\n" +
			"uniform float time;\r\n" +
			"uniform vec4 ambientColor;\r\n" +
			"attribute vec4 vPosition;\r\n" +
			"attribute vec2 vTexture;\r\n" +
			"attribute vec4 vColor;\r\n" +
			"varying vec2 texCoord;\r\n" +
			"varying vec4 color;\r\n" +
			"varying vec4 screenPos;\r\n" +
			"\r\n" +
			"void main() {\r\n" +
			"	gl_Position = projTransform * vPosition;\r\n" +
			"	texCoord = vTexture;\r\n" +
			"	screenPos = gl_Position;\r\n" +
			"	color = vColor * ambientColor;\r\n" +
			"	#PREMULT color = vec4(color.r*color.a,color.g*color.a,color.b*color.a,color.a);\r\n" +
			"}\r\n";
	
	public final static String FRAGMENT_SHADER = 
			"#ANDROID precision mediump float;\r\n" +
			"uniform sampler2D texSampler;\r\n" +
			"varying vec2 texCoord;\r\n" +
			"varying vec4 color;\r\n" +
			"\r\n" +
			"void main() {\r\n" +
			"	gl_FragColor = texture2D(texSampler, vec2(texCoord.x,texCoord.y)) * color;\r\n" +
			"}\r\n";
	
	public int mWorldTransformHandle = -1;
	public int mProjHandle = -1;
	public int mPositionHandle = -1;
	public int mTextureHandle = -1;
	public int mColorHandle = -1;
	public int mAddColorHandle = -1;
	public int mTexSamplerHandle = -1;
	public int mAmbientHandle = -1;
	public int mTimeHandle = -1;
	public boolean mHasTextureCoords = false;
	public boolean mHasColor = false;
	public boolean mHasAddColor = false;
	public boolean mHasWorldTransform = false;
	public boolean mHasAmbientColor = false;
	
	@Override
	protected void initHandles() {
		mPositionHandle = mProgram.getAttributeLocation("vPosition");
		mWorldTransformHandle = mProgram.getUniformLocation("worldTransform");
		mProjHandle = mProgram.getUniformLocation("projTransform");
		mTextureHandle = mProgram.getAttributeLocation("vTexture");
		mColorHandle = mProgram.getAttributeLocation("vColor");
		mAddColorHandle = mProgram.getAttributeLocation("vAddColor");
		mAmbientHandle = mProgram.getUniformLocation("ambientColor");
		mTimeHandle = mProgram.getUniformLocation("time");
		mTexSamplerHandle = mProgram.getUniformLocation("texSampler");
	}
	
	public void postInit() {
		mHasTextureCoords = (mTextureHandle>=0);
		mHasWorldTransform = (mWorldTransformHandle>=0);
		mHasColor = (mColorHandle>=0);
		mHasAddColor = (mAddColorHandle>=0);
		mHasAmbientColor = (mAmbientHandle>=0);
	}

	public void setProjection(float[] mvpMatrix) {
		mProgram.setUniformMatrix(mProjHandle, mvpMatrix);
	}
	
	public void setWorldTransform(float[] worldMatrix) {
		if(mHasWorldTransform)
			mProgram.setUniformMatrix(mWorldTransformHandle, worldMatrix);
	}
	
	public void setAmbientColor(float[] color) {
		mProgram.setUniform4f(mAmbientHandle, color[0], color[1], color[2], color[3]);
	}
	
	public void setAmbientColor(float r,float g,float b) {
		mProgram.setUniform4f(mAmbientHandle, r, g, b, 1);
	}
	
	public void setAmbientColor(float r,float g,float b,float a) {
		mProgram.setUniform4f(mAmbientHandle, r, g, b, a);
	}
	
	public void setAmbientColor(FloatColor color) {
		mProgram.setUniform4f(mAmbientHandle, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	public void setTime(float time) {
		mProgram.setUniformFloat(mTimeHandle, time);
	}

	public void activate() {
		mProgram.activate();
		mProgram.setUniformInt(mTexSamplerHandle, 0);
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