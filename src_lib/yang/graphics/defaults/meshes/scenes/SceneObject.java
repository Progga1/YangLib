package yang.graphics.defaults.meshes.scenes;

import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.util.YangList;

public class SceneObject {

	public String mName;
	public Point3f mTranslation = Point3f.ZERO.clone();
	public Quaternion mOrientation = Quaternion.IDENTITY.clone();
	public Vector3f mScaling = Vector3f.ONE.clone();
	public boolean mVisibility;

	public SceneObject mParent = null;
	public YangList<SceneObject> mChildren = new YangList<SceneObject>();

	public SceneObject() {
		reset();
	}

	public void reset() {
		mTranslation.setZero();
		mOrientation.setIdentity();
		mScaling.setOne();
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


	@Override
	public String toString() {
		return mName+": "+mTranslation;
	}

}
