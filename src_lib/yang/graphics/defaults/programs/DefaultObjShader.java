package yang.graphics.defaults.programs;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.programs.subshaders.AmbientSubShader;
import yang.graphics.defaults.programs.subshaders.ColorFactorSubShader;
import yang.graphics.defaults.programs.subshaders.DiffuseLightSubShader;
import yang.graphics.defaults.programs.subshaders.EmissiveSubShader;
import yang.graphics.defaults.programs.subshaders.dataproviders.CameraPerVertexVectorSubShader;
import yang.graphics.defaults.programs.subshaders.dataproviders.DiffuseMatSubShader;
import yang.graphics.defaults.programs.subshaders.dataproviders.NormalSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.defaults.programs.subshaders.realistic.LightSubShader;
import yang.graphics.defaults.programs.subshaders.realistic.SpecularLightSubShader;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.BasicSubShader;
import yang.graphics.programs.permutations.ShaderPermutations;
import yang.graphics.programs.permutations.SubShader;

public class DefaultObjShader extends ShaderPermutations {

	public LightProperties mLightProperties;
	public SpecularMatProperties mSpecMatProperties;

	public DefaultObjShader(Default3DGraphics graphics3D,LightProperties lightProperties,FloatColor ambientColor,SubShader[] additionalShaders) {
		super(graphics3D.mTranslator);
		mLightProperties = lightProperties;
		final SubShader[] subShaders = new SubShader[]{
				new BasicSubShader(true,true,true),new NormalSubShader(true,true),
				new DiffuseMatSubShader(null),
				new LightSubShader(lightProperties),
				};
		super.addSubShaders(subShaders);
		super.addSubShaders(additionalShaders);
		super.addSubShader(new AmbientSubShader(ambientColor));
		super.addSubShader(new ColorFactorSubShader(graphics3D.mColorFactor));
		super.initPermutations();
	}

	public DefaultObjShader(Default3DGraphics graphics3D,LightProperties lightProperties,FloatColor ambientColor) {
		this(graphics3D,lightProperties,ambientColor,new SubShader[]{new DiffuseLightSubShader(),new CameraPerVertexVectorSubShader(graphics3D),new SpecularLightSubShader(null), new EmissiveSubShader(null)});
	}

	public DefaultObjShader(Default3DGraphics graphics3D) {
		this(graphics3D,new LightProperties(),new FloatColor(0.1f,0.1f,0.1f));
	}

}
