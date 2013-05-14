package yang.graphics.defaults.programs;

import yang.graphics.translator.AbstractGFXLoader;

public class LightmapCreatorProgram extends ShadowProgram {
	
	public static String VERTEX_SHADER = 
			"uniform mat4 projTransform;\n"
			+"uniform mat4 worldTransform;\n"
			+"uniform mat4 depthMapTransform;\n"
			+"uniform vec4 ambientColor;\n"
			+"uniform float time;\n"
			+"\n"
			+"attribute vec4 vPosition;\n"
			+"attribute vec2 vTexture;\n"
			+"attribute vec4 vColor;\n"
			+"attribute vec4 vNormal;\n"
			+"\n"
			+"varying vec2 texCoord;\n"
			+"varying vec4 color;\n"
			+"varying vec4 depthMapPosition;\n"
			+"varying vec4 worldPosition;\n"
			+"varying vec4 normal;\n"
			+"\n"
			+"void main() {\n"
			+"	worldPosition = worldTransform * vPosition;\n"
			+"	depthMapPosition = depthMapTransform * worldPosition;\n"
			+"	gl_Position = projTransform * worldPosition;\n"
			+"	normal = vNormal;\n"
			+"	texCoord = vTexture;\n"
			+"	color = vColor * ambientColor;\n"
			+"}";

	
	public static String FRAGMENT_SHADER = 
			"#ANDROID precision mediump float;\n"
			+"uniform sampler2D texSampler;\n"
			+"uniform sampler2D depthSampler;\n"
			+"uniform vec4 lightDir;\n"
			+"float sampleFac = 1.0/7.0;\n"
			+"float sampleShift = 0.0015;\n"
			+"float sampleShiftMid = sampleShift*0.707;\n"
			+"\n"
			+"uniform vec4 lightProperties;\n"
			+"varying vec4 normal;\n"
			+"varying vec2 texCoord;\n"
			+"varying vec4 color;\n"
			+"varying vec2 screenPos;\n"
			+"varying vec4 depthMapPosition;\n"
			+"varying vec4 worldPosition;\n"
			+"\n"
			+"void main()\n"
			+"{\n"
			+"	vec4 texValue = texture2D(texSampler, vec2(texCoord.x,texCoord.y));\n"
			+"	/*vec4 texValue = texture2D(depthSampler, vec2(depthMapPosition.x,depthMapPosition.y));*/\n"
			+"	\n"
			+"	float lgt = 0.0;\n"
			+"	\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x,depthMapPosition.y)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac;\n"
			+"		\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x+sampleShift,depthMapPosition.y)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.5;\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x,depthMapPosition.y+sampleShift)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.5;\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x-sampleShift,depthMapPosition.y)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.5;\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x,depthMapPosition.y-sampleShift)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.5;\n"
			+"		\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x+sampleShiftMid,depthMapPosition.y+sampleShiftMid)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.5;\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x-sampleShiftMid,depthMapPosition.y+sampleShiftMid)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.5;\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x+sampleShiftMid,depthMapPosition.y-sampleShiftMid)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.5;\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x-sampleShiftMid,depthMapPosition.y-sampleShiftMid)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.5;\n"
			+"		\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x+sampleShift*2.0,depthMapPosition.y)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.25;\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x,depthMapPosition.y+sampleShift*2.0)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.25;\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x-sampleShift*2.0,depthMapPosition.y)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.25;\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x,depthMapPosition.y-sampleShift*2.0)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.25;\n"
			+"		\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x+sampleShiftMid*2.0,depthMapPosition.y+sampleShiftMid*2.0)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.25;\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x-sampleShiftMid*2.0,depthMapPosition.y+sampleShiftMid*2.0)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.25;\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x+sampleShiftMid*2.0,depthMapPosition.y-sampleShiftMid*2.0)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.25;\n"
			+"	if(depthMapPosition.z > texture2D(depthSampler, vec2(depthMapPosition.x-sampleShiftMid*2.0,depthMapPosition.y-sampleShiftMid*2.0)).r)\n"
			+"		lgt += dot(normalize(normal),lightDir)*sampleFac*0.25;\n"
			+"		\n"
			+"	lgt = "+LIGHT_COMPUTATION+"\n"
			+"\n"
			+"	gl_FragColor = vec4(lgt,lgt,lgt,1.0);\n"
			+"}\n";

	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return VERTEX_SHADER;
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return FRAGMENT_SHADER;
	}
	
}
