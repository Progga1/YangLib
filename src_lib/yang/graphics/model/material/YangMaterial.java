package yang.graphics.model.material;

import yang.graphics.model.FloatColor;
import yang.graphics.translator.Texture;

public class YangMaterial {

	public String mName;
	public Texture mTexture = null;
	public FloatColor mDiffuseColor = FloatColor.WHITE.clone();
	public FloatColor mSpecularColor = FloatColor.BLACK.clone();
	public FloatColor mAmbientColor = FloatColor.GRAY.clone();
	public FloatColor mEmissiveColor = FloatColor.BLACK.clone();
	
	@Override
	public String toString() {
		String result = "Diffuse: "+mDiffuseColor+"\n"+"Ambient: "+mDiffuseColor+"\n"+"Specular: "+mSpecularColor+"\n"+"Emissive: "+mEmissiveColor+"\n";
		if(mTexture!=null)
			result = "Texture: "+mTexture+"\n"+result;
		if(mName!=null)
			result = "Name: '"+mName+"'\n"+result;
		return result;
	}
	
}
