package yang.graphics.model.material;

import java.io.IOException;
import java.io.InputStream;

import yang.graphics.model.FloatColor;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.AbstractGFXLoader;
import yang.util.NonConcurrentList;
import yang.util.filereader.TokenReader;

public class YangMaterialSet {

	private static String[] KEYWORDS = {"newmtl","Ka","Kd","Ks","Ns","illum","map_Kd","map_Ks"};
	
	public NonConcurrentList<YangMaterial> mMaterials;
	
	public static TextureProperties diffuseTextureProperties = new TextureProperties(TextureWrap.REPEAT,TextureFilter.LINEAR_MIP_LINEAR);
	public static TextureProperties specularTextureProperties = new TextureProperties(TextureWrap.REPEAT,TextureFilter.LINEAR_MIP_LINEAR);
	
	private TokenReader reader;
	private AbstractGFXLoader mGFXLoader;
	
	public YangMaterialSet(AbstractGFXLoader gfxLoader) {
		mGFXLoader = gfxLoader;
		mMaterials = new NonConcurrentList<YangMaterial>();
	}
	
	private void readColor(FloatColor targetColor) throws IOException {
		targetColor.mValues[0] = reader.readFloat(false);
		targetColor.mValues[1] = reader.readFloat(false);
		targetColor.mValues[2] = reader.readFloat(false);
		float alpha = reader.readFloat(false);
		if(alpha!=TokenReader.ERROR_FLOAT) {
			targetColor.mValues[3] = alpha;
		}
	}
	
	public void loadFromStream(InputStream inputStream) throws IOException {
		reader = new TokenReader(inputStream);
		YangMaterial curMat = null;
		
		while(!reader.eof()) {
			reader.nextWord(true);
			String filename;
			switch(reader.pickWord(KEYWORDS)) {
			case 0:
				String name = reader.readString(false);
				curMat = new YangMaterial();
				curMat.mName = name;
				mMaterials.add(curMat);
				break;
			case 1:
				readColor(curMat.mAmbientColor);
				break;
			case 2:
				readColor(curMat.mDiffuseColor);
				break;
			case 3:
				readColor(curMat.mSpecularProps.mColor);
				break;
			case 4:
				curMat.mSpecularProps.mExponent = reader.readFloat(false);
				break;
			case 6:
				filename = reader.readString(false);
				curMat.mDiffuseTexture = mGFXLoader.getImage(filename,diffuseTextureProperties);
				break;
			case 7:
				filename = reader.readString(false);
				curMat.mSpecularProps.mTexture = mGFXLoader.getImage(filename,specularTextureProperties);
				break;
			}
			reader.toLineEnd();
		}

	}
	
	@Override
	public String toString() {
		return mMaterials.toString();
	}

	public YangMaterial getMaterial(String materialName) {
		for(YangMaterial mat:mMaterials) {
			if(materialName.equals(mat.mName)) {
				return mat;
			}
		}
		return null;
	}
	
}
