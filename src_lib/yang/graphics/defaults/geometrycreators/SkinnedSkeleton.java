package yang.graphics.defaults.geometrycreators;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.meshes.armature.YangArmature;
import yang.graphics.defaults.meshes.armature.YangArmaturePosture;
import yang.graphics.defaults.meshes.loaders.YangMesh;
import yang.physics.massaggregation.MassAggregation;

public class SkinnedSkeleton {

	public boolean mWireFrames = false;
	public DefaultGraphics<?> mGraphics;
	public YangArmature mArmature;
	public YangArmaturePosture mArmaturePose;
	public MassAggregation mSkeleton;
	public boolean mCulling = true;
	public YangMesh mMesh;
	public boolean mAutoRefresh = true;

	public SkinnedSkeleton(YangMesh mesh,MassAggregation skeleton) {
		mMesh = mesh;
		if(mMesh!=null) {
			mGraphics = mesh.mGraphics;
			mWireFrames = mesh.mWireFrames;
			mMesh.mAutoSkinningUpdate = false;
		}
		mSkeleton = skeleton;
		if(mSkeleton!=null) {
			mArmature = new YangArmature();
			mArmature.initBySkeleton(skeleton);
			mArmaturePose = new YangArmaturePosture(mArmature);
			if(mMesh!=null && !mMesh.hasArmatureWeights())
				mMesh.generateArmatureWeights(mArmature);
		}
	}

	public SkinnedSkeleton(YangMesh mesh) {
		this(mesh,null);

	}

	public SkinnedSkeleton setGraphics(DefaultGraphics<?> graphics) {
		mGraphics = graphics;
		return this;
	}

	public void draw() {
		if(mMesh==null)
			return;
		mMesh.mWireFrames = mWireFrames;
		mGraphics.mTranslator.switchCulling(!mWireFrames && mCulling);
//		if(mMesh.mBlockTextures)
			mGraphics.mTranslator.bindTexture(null);
		if(mSkeleton!=null) {
			if(mAutoRefresh)
				refresh();
			mMesh.mCurArmature = mArmaturePose;

			mGraphics.setGlobalTransformEnabled(true);
			mGraphics.mWorldTransform.stackPush();
			mGraphics.mWorldTransform.multiplyRight(mSkeleton.mTransform);
			mGraphics.mWorldTransform.scale(mSkeleton.getScale());

			if(mMesh.isDynamic())
				mMesh.drawDynamic();
			else
				mMesh.drawStatic();

			mGraphics.mWorldTransform.stackPop();
		}else{
			mMesh.draw();
		}
		mMesh.mWireFrames = false;
	}

	public void refresh() {
		if(mArmaturePose!=null) {
			mArmaturePose.refreshMatrices(mSkeleton);
			if(mMesh!=null && !mMesh.mAutoSkinningUpdate)
				mMesh.updateSkinningVertices(mArmaturePose);
		}
	}

}
