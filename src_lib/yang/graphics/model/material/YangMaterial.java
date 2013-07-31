package yang.graphics.model.material;

import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.model.FloatColor;
import yang.graphics.translator.Texture;

public class YangMaterial {

	public String mName;
	public FloatColor mDiffuseColor = FloatColor.WHITE.clone();
	public FloatColor mAmbientColor = FloatColor.GRAY.clone();
	public FloatColor mEmissiveColor = FloatColor.BLACK.clone();
	public Texture mDiffuseTexture = null;
	public SpecularMatProperties mSpecularProps;

	public YangMaterial() {
		mSpecularProps = new SpecularMatProperties();
	}
	
	@Override
	public String toString() {
		String result = "Diffuse: "+mDiffuseColor+"\n"+"Ambient: "+mDiffuseColor+"\n"+"Specular: "+mSpecularProps+"\n"+"Emissive: "+mEmissiveColor+"\n";
		if(mDiffuseTexture!=null)
			result = "Texture: "+mDiffuseTexture+"\n"+result;
		if(mName!=null)
			result = "Name: '"+mName+"'\n"+result;
		return result;
	}
	
}
