package yang.graphics.defaults.programs;

import yang.graphics.programs.Basic3DProgram;
import yang.graphics.translator.AbstractGFXLoader;

public class LightProgram extends Basic3DProgram implements LightInterface {

	public final static String LIGHT_COMPUTATION = "clamp((lgt+lightProperties[2])*lightProperties[3],lightProperties[0],lightProperties[1]);";
	
	public static String VERTEX_SHADER = 
			"uniform mat4 projTransform;\n"
			+"uniform mat4 worldTransform;\n"
			+"\n"
			+"uniform float time;\n"
			+"uniform vec4 lightDir;\n"
			+"uniform vec4 lightProperties;\n"
			+"\n"
			+"attribute vec4 vPosition;\n"
			+"attribute vec2 vTexture;\n"
			+"attribute vec4 vColor;\n"
			+"attribute vec4 vNormal;\n"
			+"\n"
			+"varying vec2 texCoord;\n"
			+"varying vec4 color;\n"
			+"varying vec2 screenPos;\n"
			+"\n"
			+"void main() {\n"
			+"	gl_Position = projTransform * worldTransform * vPosition;\n"
			+"	screenPos = vec2(gl_Position.x*0.5+0.5,gl_Position.y*0.5+0.5);\n"
			+"	texCoord = vTexture;\n"
			+"  float lgt = dot(worldTransform*vNormal,lightDir);\n"
			+"	lgt = "+LIGHT_COMPUTATION+"\n"
			+"	color = vec4(vColor.r*lgt,vColor.g*lgt,vColor.b*lgt,vColor.a);\n"
			+"}";
	
	public static String FRAGMENT_SHADER = 
			"#ANDROID precision mediump float;\n"
			+"uniform sampler2D texSampler;\n"
			+"uniform vec4 ambientColor;\n"
			+"\n"
			+"varying vec2 texCoord;\n"
			+"varying vec4 color;\n"
			+"varying vec2 screenPos;\n"
			+"\n"
			+"void main()\n"
			+"{\n"
			+"	gl_FragColor = texture2D(texSampler, vec2(texCoord.x,texCoord.y)) * color * ambientColor;\n"
			+"}";
	
	public int mLightHandle;
	public int mLightPropertiesHandle;
	
	@Override
	protected void initHandles() {
		super.initHandles();
		mLightHandle = mProgram.getUniformLocation("lightDir");
		mLightPropertiesHandle = mProgram.getUniformLocation("lightProperties");
	}
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return VERTEX_SHADER;
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return FRAGMENT_SHADER;
	}
	
	public void setLightDirection(float[] dir) {
		if(mLightHandle>=0)
			mProgram.setUniform4f(mLightHandle, -dir[0], -dir[1], -dir[2], dir[3]);
	}
	
	public void setLightDirection(float x,float y,float z) {
		if(mLightHandle>=0)
			mProgram.setUniform4f(mLightHandle, -x,-y,-z,0);
	}
	
	public void setLightProperties(float minLight,float maxLight,float addLight,float lightFactor) {
		if(mLightPropertiesHandle>=0)
			mProgram.setUniform4f(mLightPropertiesHandle, minLight, maxLight, addLight, lightFactor);
	}

}
