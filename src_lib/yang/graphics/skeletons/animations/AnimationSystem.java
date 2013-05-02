package yang.graphics.skeletons.animations;

import yang.graphics.skeletons.SkeletonCarrier;
import yang.util.NonConcurrentList;

public class AnimationSystem<CarrierType extends SkeletonCarrier,AnimationType extends Animation<CarrierType>>{

	public NonConcurrentList<AnimationType> mAnimations;
	protected AnimationType mStartNode;
	
	protected AnimationSystem(AnimationType startNode) {
		mStartNode = startNode;
		mAnimations = new NonConcurrentList<AnimationType>();
	}
	
	public AnimationType getStartNode() {
		return mStartNode;
	}
	
}
