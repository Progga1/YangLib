package yang.graphics.model.material;

import java.io.IOException;
import java.io.InputStream;

import yang.graphics.model.FloatColor;
import yang.util.NonConcurrentList;
import yang.util.filereader.TokenReader;

public class YangMaterialSet {

	private static String[] KEYWORDS = {"newmtl","Ka","Kd","Ks","Ns","illum"};
	
	public NonConcurrentList<YangMaterial> mMaterials;
	
	private TokenReader reader;
	
	public YangMaterialSet() {
		mMaterials = new NonConcurrentList<YangMaterial>();
	}
	
	private void readColor(FloatColor targetColor) throws IOException {
		targetColor.mValues[0] = reader.readFloat();
		targetColor.mValues[1] = reader.readFloat();
		targetColor.mValues[2] = reader.readFloat();
		float alpha = reader.readFloat();
		if(alpha!=TokenReader.ERROR_FLOAT) {
			targetColor.mValues[3] = alpha;
		}
		reader.skipLine();
	}
	
	public void loadFromStream(InputStream inputStream) throws IOException {
		reader = new TokenReader(inputStream);
		YangMaterial curMat = null;
		
		while(!reader.eof()) {
			reader.nextWord();
			switch(reader.pickWord(KEYWORDS)) {
			case 0:
				curMat = new YangMaterial();
				mMaterials.add(curMat);
				break;
			case 1:
				readColor(curMat.mAmbientColor);
				break;
			}
		}
	}
	
	@Override
	public String toString() {
		return mMaterials.toString();
	}
	
}
