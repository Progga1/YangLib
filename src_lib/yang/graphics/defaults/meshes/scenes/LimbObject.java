package yang.graphics.defaults.meshes.scenes;

import yang.util.YangList;

public class LimbObject extends SceneObject {

	public float mLimbLength = 1;

	protected YangList<LimbObject> mLimbChildren = new YangList<LimbObject>();

	public float getMinAdjescentLimbLength() {
		float result = mLimbLength;
		for(LimbObject child:mLimbChildren) {
			if(child.mLimbLength<result)
				result = child.mLimbLength;
		}
		return result;
	}

	@Override
	public void setParent(SceneObject parent) {
		super.setParent(parent);
		if(parent instanceof LimbObject) {
			((LimbObject)parent).mLimbChildren.add(this);
		}
	}

}
