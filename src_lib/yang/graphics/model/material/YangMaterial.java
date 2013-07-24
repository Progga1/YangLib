package yang.graphics.model.material;

import yang.graphics.model.FloatColor;
import yang.graphics.translator.Texture;

public class YangMaterial {

	public String mName;
	public Texture mDiffuseTexture = null;
	public FloatColor mDiffuseColor = FloatColor.WHITE.clone();
	public FloatColor mSpecularColor = new FloatColor(FloatColor.ZERO);
	public FloatColor mAmbientColor = FloatColor.GRAY.clone();
	public FloatColor mEmissiveColor = FloatColor.BLACK.clone();
	public float mSpecularCoefficient = 10;
	
	@Override
	public String toString() {
		String result = "Diffuse: "+mDiffuseColor+"\n"+"Ambient: "+mDiffuseColor+"\n"+"Specular: "+mSpecularColor+"\n"+"Emissive: "+mEmissiveColor+"\n";
		if(mDiffuseTexture!=null)
			result = "Texture: "+mDiffuseTexture+"\n"+result;
		if(mName!=null)
			result = "Name: '"+mName+"'\n"+result;
		return result;
	}
	
}
