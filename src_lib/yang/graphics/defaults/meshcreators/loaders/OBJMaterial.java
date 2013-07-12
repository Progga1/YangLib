package yang.graphics.defaults.meshcreators.loaders;

import yang.graphics.model.material.YangMaterial;

public class OBJMaterial {

	public int mStartIndex;
	public int mEndIndex;
	public YangMaterial mMaterial;
	
	@Override
	public String toString() {
		
		return "From-to: "+mStartIndex+"-"+mEndIndex+"\n"+mMaterial.toString();
	}
	
}
