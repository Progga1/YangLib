package yang.graphics.defaults.meshcreators;

import yang.graphics.defaults.meshcreators.loaders.OBJLoader;
import yang.graphics.translator.AbstractGraphics;
import yang.physics.massaggregation.MassAggregation;

public class SkinnedMesh {

	public boolean mWireFrames = false;
	public AbstractGraphics<?> mGraphics;
	public YangArmature mArmature;
	public MassAggregation mSkeleton;
	public OBJLoader mMesh;
	public boolean mAutoRefresh = true;

	public SkinnedMesh(OBJLoader mesh,MassAggregation skeleton) {
		mMesh = mesh;
		mGraphics = mesh.mGraphics;
		mSkeleton = skeleton;
		if(mSkeleton!=null) {
			mArmature = new YangArmature();
			mArmature.init(skeleton);
			if(!mMesh.hasArmatureWeights())
				mMesh.createArmatureWeights(mArmature);
		}
	}

	public SkinnedMesh(OBJLoader mesh) {
		this(mesh,null);

	}

	public void draw() {
		boolean preWireframes = mGraphics.mTranslator.mForceWireFrames;
		mGraphics.mTranslator.mForceWireFrames = mWireFrames;
		if(mSkeleton!=null) {
			if(mAutoRefresh)
				mArmature.refreshMatrices(mSkeleton);
			mMesh.mCurArmature = mArmature;

			mGraphics.setGlobalTransformEnabled(true);
			mGraphics.mWorldTransform.stackPush();
			mGraphics.mWorldTransform.set(mSkeleton.mTransform);
			mGraphics.mWorldTransform.scale(mSkeleton.getScale());

			mMesh.drawDynamic();

			mGraphics.mWorldTransform.stackPop();
		}else{
			mMesh.draw();
		}
		mGraphics.mTranslator.mForceWireFrames = preWireframes;
	}

	public void refresh() {
		if(mArmature!=null)
			mArmature.refreshMatrices(mSkeleton);
	}

}
