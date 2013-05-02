package yang.graphics.defaults;

import yang.graphics.skeletons.Skeleton;
import yang.graphics.skeletons.animations.Animation;
import yang.graphics.skeletons.animations.AnimationPlayer;

public class DefaultAnimationPlayer extends AnimationPlayer<Animation<?>>{

	public DefaultAnimationPlayer(Skeleton skeleton, Animation<?> startAnimation) {
		super(skeleton, startAnimation);
	}

}
