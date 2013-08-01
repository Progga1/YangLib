package yang.graphics.defaults.programs;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.programs.subshaders.AmbientSubShader;
import yang.graphics.defaults.programs.subshaders.CameraPerVertexVectorSubShader;
import yang.graphics.defaults.programs.subshaders.DiffuseLightSubShader;
import yang.graphics.defaults.programs.subshaders.EmissiveSubShader;
import yang.graphics.defaults.programs.subshaders.MtDiffuseSubShader;
import yang.graphics.defaults.programs.subshaders.NormalSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.defaults.programs.subshaders.realistic.LightSubShader;
import yang.graphics.defaults.programs.subshaders.realistic.SpecularLightSubShader;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.BasicSubShader;
import yang.graphics.programs.permutations.ShaderPermutations;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.util.Camera3D;

public class DefaultObjShader extends ShaderPermutations {

	public LightProperties mLightProperties;
	public SpecularMatProperties mSpecMatProperties;
	
	public DefaultObjShader(Default3DGraphics graphics3D,LightProperties lightProperties,FloatColor ambientColor,SubShader[] additionalShaders) {
		super(graphics3D.mTranslator);
		mLightProperties = lightProperties;
		SubShader[] subShaders = new SubShader[]{
				new BasicSubShader(true,true,true),new NormalSubShader(true,true),
				new MtDiffuseSubShader(null),
				new LightSubShader(lightProperties),
				};
		super.addSubShaders(subShaders);
		super.addSubShaders(additionalShaders);
		super.addSubShader(new AmbientSubShader(ambientColor));
		super.initPermutations();
	}
	
	public DefaultObjShader(Default3DGraphics graphics3D,Camera3D camera,LightProperties lightProperties,FloatColor ambientColor) {
		this(graphics3D,lightProperties,ambientColor,new SubShader[]{new DiffuseLightSubShader(),new CameraPerVertexVectorSubShader(camera),new SpecularLightSubShader(null)});
	}
	
	public DefaultObjShader(Default3DGraphics graphics3D,Camera3D camera) {
		this(graphics3D,camera,new LightProperties(),new FloatColor(0.1f,0.1f,0.1f));
	}
	
}
