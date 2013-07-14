package yang.graphics.defaults.meshcreators.loaders;

import yang.graphics.model.material.YangMaterial;

public class OBJMaterialSection {

	public int mStartIndex;
	public int mEndIndex;
	public YangMaterial mMaterial;
	
	public OBJMaterialSection(int startIndex,YangMaterial material) {
		mStartIndex = startIndex;
		mMaterial = material;
	}
	
	@Override
	public String toString() {
		return "From-to: "+mStartIndex+"-"+mEndIndex+"\n"+mMaterial.toString();
	}
	
}
