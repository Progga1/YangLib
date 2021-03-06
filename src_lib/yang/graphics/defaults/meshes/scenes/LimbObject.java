package yang.graphics.defaults.meshes.scenes;

import yang.util.YangList;

public class LimbObject extends SceneObject {

	public float mLimbLength = 1;

	protected YangList<LimbObject> mLimbChildren = new YangList<LimbObject>();
	public YangList<MeshDeformer> mDeformers = new YangList<MeshDeformer>();

	public float getMinAdjescentLimbLength() {
		float result = mLimbLength;
		for(LimbObject child:mLimbChildren) {
			if(child.mLimbLength<result)
				result = child.mLimbLength;
		}
		return result;
	}

	public MeshDeformer addDeformer(MeshObject mesh) {
		MeshDeformer deformer = new MeshDeformer(this,mesh);
		mDeformers.add(deformer);
		return deformer;
	}

	public String deformersToString() {
		return mDeformers.toString();
	}

	public void setDeformerIndex(int index) {
		for(MeshDeformer deformer:mDeformers) {
			deformer.mMapIndex = index;
		}
	}

	@Override
	public void setParent(SceneObject parent) {
		super.setParent(parent);
		if(parent instanceof LimbObject) {
			((LimbObject)parent).mLimbChildren.add(this);
		}
	}

}
