package yang.graphics.defaults.programs;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.programs.subshaders.AmbientSubShader;
import yang.graphics.defaults.programs.subshaders.CameraVectorSubShader;
import yang.graphics.defaults.programs.subshaders.DiffuseLightSubShader;
import yang.graphics.defaults.programs.subshaders.LightSubShader;
import yang.graphics.defaults.programs.subshaders.NormalSubShader;
import yang.graphics.defaults.programs.subshaders.SpecularLightSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.BasicSubShader;
import yang.graphics.programs.permutations.ShaderPermutations;
import yang.graphics.programs.permutations.SubShader;

public class DefaultObjShader extends ShaderPermutations {

	public LightProperties mLightProperties;
	public SpecularMatProperties mSpecMatProperties;
	
	public DefaultObjShader(Default3DGraphics graphics3D,LightProperties lightProperties) {
		super();
		mLightProperties = new LightProperties();
		SubShader[] subShaders = new SubShader[]{
				new BasicSubShader(true,true,true),new NormalSubShader(true,false),
				new LightSubShader(mLightProperties),new DiffuseLightSubShader(),
				new CameraVectorSubShader(graphics3D.mCameraMatrix.mMatrix),new SpecularLightSubShader(null),
				new AmbientSubShader(new FloatColor(graphics3D.mAmbientColor))
				};
		super.initPermutations(subShaders);
	}
	
	public DefaultObjShader(Default3DGraphics graphics3D) {
		this(graphics3D,new LightProperties());
	}
	
}
