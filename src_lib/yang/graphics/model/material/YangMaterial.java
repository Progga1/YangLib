package yang.graphics.model.material;

import yang.graphics.defaults.programs.subshaders.properties.EmissiveMatProperties;
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
	public EmissiveMatProperties mEmissiveProps;

	public YangMaterial() {
		mSpecularProps = new SpecularMatProperties();
		mEmissiveProps = new EmissiveMatProperties();
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

	@Override
	public YangMaterial clone() {
		YangMaterial result = new YangMaterial();
		result.mName = mName;
		result.mDiffuseColor.set(mDiffuseColor);
		result.mAmbientColor.set(mAmbientColor);
		result.mEmissiveColor.set(mEmissiveColor);
		result.mDiffuseTexture = mDiffuseTexture;
		result.mSpecularProps = mSpecularProps.clone();
		result.mEmissiveProps = mEmissiveProps.clone();
		return result;
	}

}
