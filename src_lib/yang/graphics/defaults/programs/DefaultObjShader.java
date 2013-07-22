package yang.graphics.defaults.programs;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.programs.subshaders.AmbientSubShader;
import yang.graphics.defaults.programs.subshaders.CameraVectorSubShader;
import yang.graphics.defaults.programs.subshaders.DiffuseLightSubShader;
import yang.graphics.defaults.programs.subshaders.LightSubShader;
import yang.graphics.defaults.programs.subshaders.MtDiffuseSubShader;
import yang.graphics.defaults.programs.subshaders.NormalSubShader;
import yang.graphics.defaults.programs.subshaders.SpecularLightSubShader;
import yang.graphics.defaults.programs.subshaders.ToonSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.BasicSubShader;
import yang.graphics.programs.permutations.ShaderPermutations;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;

public class DefaultObjShader extends ShaderPermutations {

	public LightProperties mLightProperties;
	public SpecularMatProperties mSpecMatProperties;
	
	public DefaultObjShader(Default3DGraphics graphics3D,FloatColor ambientColor,LightProperties lightProperties,SubShader diffuseShader) {
		super();
		mLightProperties = lightProperties;
		SubShader[] subShaders = new SubShader[]{
				new BasicSubShader(true,true,true),new NormalSubShader(true,true),
				new MtDiffuseSubShader(null),
				new LightSubShader(lightProperties),diffuseShader,
				new CameraVectorSubShader(graphics3D.mCameraMatrix.mMatrix),new SpecularLightSubShader(null),
				new AmbientSubShader(ambientColor)
				};
		super.initPermutations(subShaders);System.out.println(this);
	}
	
	public DefaultObjShader(Default3DGraphics graphics3D) {
		this(graphics3D,new FloatColor(0.1f,0.1f,0.1f),new LightProperties(),new DiffuseLightSubShader());
	}
	
}
