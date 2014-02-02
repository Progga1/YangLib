package yang.graphics.defaults.meshes.loaders;

import yang.graphics.model.material.YangMaterial;

public class YangMaterialSection {

	public int mStartIndex;
	public int mEndIndex;
	public int mEdgeStartIndex;
	public int mEdgeEndIndex;
	public YangMaterial mMaterial;

	public YangMaterialSection(int startIndex,int edgeStartIndex,YangMaterial material) {
		mStartIndex = startIndex;
		mMaterial = material;
	}

	@Override
	public String toString() {
		return "From-to: "+mStartIndex+"-"+mEndIndex+"\n"+mMaterial.toString();
	}

}
