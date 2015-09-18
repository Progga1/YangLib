package yang.graphics.defaults.meshes.scenes;

import yang.graphics.model.TransformationData;
import yang.math.objects.YangMatrix;
import yang.util.YangList;

public class SceneObject extends TransformationData {

	public String mName;
	public boolean mVisibility;
	public YangMatrix mGlobalTransform = new YangMatrix();

	public SceneObject mParent = null;
	protected YangList<SceneObject> mChildren = new YangList<SceneObject>();

	public SceneObject() {
		reset();
	}

	public void reset() {
		super.loadIdentity();
		mVisibility = true;
	}

	public void setParent(SceneObject parent) {
		if(mParent!=null) {
			mParent.mChildren.remove(this);
		}
		mParent = parent;
		if(parent!=null) {
			mParent.mChildren.add(this);
		}
	}

	public void addChild(SceneObject child) {
		child.setParent(this);
	}


	public Iterable<SceneObject> getChildren() {
		return mChildren;
	}

	public String hierarchyToString() {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for(SceneObject child:mChildren) {
			if(!first)
				result.append(',');
			result.append(child.hierarchyToString());
			first = false;
		}
		if(first)
			return mName;
		else
			return mName+"("+result.toString()+")";
	}

	@Override
	public String toString() {
		return mName+": "+mPosition;
	}

}
