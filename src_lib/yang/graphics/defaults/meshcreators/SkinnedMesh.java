package yang.graphics.defaults.meshcreators;

import yang.graphics.defaults.meshcreators.loaders.OBJLoader;
import yang.physics.massaggregation.MassAggregation;

public class SkinnedMesh {

	public YangArmature mArmature;
	public MassAggregation mSkeleton;
	public OBJLoader mMesh;

	public SkinnedMesh(MassAggregation skeleton,OBJLoader mesh) {
		mMesh = mesh;
		mSkeleton = skeleton;
		mArmature = new YangArmature();
		mArmature.init(skeleton);
		if(!mMesh.hasArmatureWeights())
			mMesh.createArmatureWeights(mArmature);
	}

	public void draw() {
		mArmature.refreshMatrices(mSkeleton);
		mMesh.mCurArmature = mArmature;
		mMesh.drawDynamic();
	}

}
