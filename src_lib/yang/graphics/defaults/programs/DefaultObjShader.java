package yang.graphics.defaults.programs;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.programs.subshaders.AmbientSubShader;
import yang.graphics.defaults.programs.subshaders.CameraPerVertexVectorSubShader;
import yang.graphics.defaults.programs.subshaders.DiffuseLightSubShader;
import yang.graphics.defaults.programs.subshaders.LightSubShader;
import yang.graphics.defaults.programs.subshaders.MtDiffuseSubShader;
import yang.graphics.defaults.programs.subshaders.NormalSubShader;
import yang.graphics.defaults.programs.subshaders.SpecularLightSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.BasicSubShader;
import yang.graphics.programs.permutations.ShaderPermutations;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.util.Camera3D;

public class DefaultObjShader extends ShaderPermutations {

	public LightProperties mLightProperties;
	public SpecularMatProperties mSpecMatProperties;
	
	public DefaultObjShader(Default3DGraphics graphics3D,Camera3D camera,FloatColor ambientColor,LightProperties lightProperties,SubShader diffuseShader) {
		super(graphics3D.mTranslator);
		mLightProperties = lightProperties;
		SubShader[] subShaders = new SubShader[]{
				new BasicSubShader(true,true,true),new NormalSubShader(true,true),
				new MtDiffuseSubShader(null),
				new LightSubShader(lightProperties),diffuseShader,
				new CameraPerVertexVectorSubShader(camera),new SpecularLightSubShader(null),
				//new ToonOutlineSubShader(new FloatWrapper(0.3f)),
				new AmbientSubShader(ambientColor)
				};
		super.initPermutations(subShaders);System.out.println(this);
	}
	
	public DefaultObjShader(Default3DGraphics graphics3D,Camera3D camera) {
		this(graphics3D,camera,new FloatColor(0.1f,0.1f,0.1f),new LightProperties(),new DiffuseLightSubShader());
	}
	
}
