package yang.graphics.skeletons.animations;

import yang.graphics.skeletons.SkeletonCarrier;
import yang.util.YangList;

public class AnimationSystem<CarrierType extends SkeletonCarrier,AnimationType extends Animation<CarrierType>>{

	public YangList<AnimationType> mAnimations;
	protected AnimationType mStartNode;

	protected AnimationSystem(AnimationType startNode) {
		mStartNode = startNode;
		mAnimations = new YangList<AnimationType>();
	}

	public AnimationType getStartNode() {
		return mStartNode;
	}

	protected void addAnimations(AnimationType... animations) {
		for(final AnimationType animation:animations) {
			mAnimations.add(animation);
		}
	}

}
